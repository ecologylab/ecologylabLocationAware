package ecologylab.standalone.ImageGeotagger;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.ImageWriteException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.jpeg.exifRewrite.ExifRewriter;
import org.apache.sanselan.formats.tiff.TiffImageMetadata;
import org.apache.sanselan.formats.tiff.constants.GPSTagConstants;
import org.apache.sanselan.formats.tiff.constants.TiffFieldTypeConstants;
import org.apache.sanselan.formats.tiff.write.TiffOutputDirectory;
import org.apache.sanselan.formats.tiff.write.TiffOutputField;
import org.apache.sanselan.formats.tiff.write.TiffOutputSet;

import ecologylab.oodss.logging.playback.ExtensionFilter;
import ecologylab.sensor.location.compass.CompassDatum;
import ecologylab.sensor.location.gps.data.GPSDatum;
import ecologylab.services.messages.LocationDataResponse;
import ecologylab.standalone.GeoClient;

public class ImageProcessor implements Runnable {
	protected File imageFile;
	private Thread t;

	private GeoClient client;
	private GPSDatum gpsData;
	private CompassDatum compassData;

	public ImageProcessor(File image, GeoClient client) {
		this.imageFile = image;
		this.client = client;
	}

	public ImageProcessor(File image, GPSDatum gpsData, CompassDatum compassData)
	{
		this.imageFile = image;
		this.gpsData = gpsData;
		this.compassData = compassData;
	}
	
	public void processImage() {
		t = new Thread(this);
		t.start();
	}

	@Override
	public void run() {
		System.out.println("Processing file: " + imageFile.getName());

		if (client != null)
		{
			System.out.println("Getting location from service.");
			if (client.connected())
			{
				LocationDataResponse locationData = client.updateLocation();
				gpsData = locationData.gpsData;
				compassData = locationData.compassData;
			}
			else
			{
				System.out.println("Client isn't connected!");
				return;
			}
		}
		
		/* Read metadata from image file */
		IImageMetadata metadata = null;

		/* Initialize metadata from existing image metadata */
		try {
			metadata = Sanselan.getMetadata(imageFile);
		} catch (ImageReadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (metadata == null || !(metadata instanceof JpegImageMetadata))
			return;

		JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;

		TiffImageMetadata exif = jpegMetadata.getExif();

		if (exif == null)
			return;

		TiffOutputSet outputSet = null;
		try {
			outputSet = exif.getOutputSet();
		} catch (ImageWriteException e) {
			e.printStackTrace();
			return;
		}

		/* Write location metadata to the existing metadata */
		if (outputSet != null) {
			try {
				if(gpsData != null)
				{
					outputSet.setGPSInDegrees(gpsData.getLon(), gpsData.getLat());
				}
				if(compassData != null)
				{
					setCompassData(outputSet);
				}
			} catch (ImageWriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		}

		OutputStream os = null;
		File dst = null;

		// create output stream to temp file for dst
		try {
			dst = File.createTempFile("temp-" + System.currentTimeMillis(),
					".jpg");
			os = new FileOutputStream(dst);
			os = new BufferedOutputStream(os);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		// write/update EXIF metadata to output stream of temp file
		try {
			new ExifRewriter().updateExifMetadataLossless(imageFile, os,
					outputSet);
		} catch (ImageReadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (ImageWriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
				}
			}
		}

		// finally copy the temp file to original
		try {
			copyFile(dst, imageFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setCompassData(TiffOutputSet outputSet) 
	{
		TiffOutputDirectory dir = outputSet.getGPSDirectory();

		/* create heading data */
		ByteBuffer rationalBuffer = ByteBuffer.allocate(8);
		rationalBuffer.put(intToUnsignedByteArray((long) (compassData
				.getHeading() * 100)));
		rationalBuffer.put(intToUnsignedByteArray(100));
		rationalBuffer.flip();
		
		byte[] buf = new byte[8];
		rationalBuffer.get(buf);
		
		TiffOutputField outField = new TiffOutputField(
				GPSTagConstants.GPS_TAG_GPS_IMG_DIRECTION,
				TiffFieldTypeConstants.FIELD_TYPE_RATIONAL, 1,
				buf);
		
		/* Remove Previous Data if Necessary */
		TiffOutputField imageDirPre = outputSet
				.findField(GPSTagConstants.GPS_TAG_GPS_IMG_DIRECTION);
		if (imageDirPre != null) {
			outputSet.removeField(GPSTagConstants.GPS_TAG_GPS_IMG_DIRECTION);
		}
		
		dir.add(outField);
		
		/* Create Heading Reference Data */
		String refVal = GPSTagConstants.GPS_TAG_GPS_IMG_DIRECTION_REF_VALUE_MAGNETIC_NORTH;
		TiffOutputField outField2 = new TiffOutputField(
				TiffOutputField.GPS_TAG_GPS_IMG_DIRECTION_REF,
				TiffFieldTypeConstants.FIELD_TYPE_ASCII, refVal.length(),
				refVal.getBytes());
		
		/* Delete Previous Field */
		TiffOutputField imageDirRefPre = outputSet
				.findField(GPSTagConstants.GPS_TAG_GPS_IMG_DIRECTION_REF);
		if (imageDirRefPre != null) {
			outputSet.removeField(GPSTagConstants.GPS_TAG_GPS_IMG_DIRECTION_REF);
		}
		
		dir.add(outField2);
	}

	public static void copyFile(File in, File out) throws IOException {
		FileChannel inChannel = new FileInputStream(in).getChannel();
		FileChannel outChannel = new FileOutputStream(out).getChannel();
		try {
			inChannel.transferTo(0, inChannel.size(), outChannel);
		} catch (IOException e) {
			throw e;
		} finally {
			if (inChannel != null)
				inChannel.close();
			if (outChannel != null)
				outChannel.close();
		}
	}

	/* Converts a long into a byte array representing a 32 bit unsigned integer */
	public byte[] intToUnsignedByteArray(long x) {
		Long anUnsignedInt = x;

		byte[] buf = new byte[4];

		buf[0] = (byte) ((anUnsignedInt & 0xFF000000L) >> 24);
		buf[1] = (byte) ((anUnsignedInt & 0x00FF0000L) >> 16);
		buf[2] = (byte) ((anUnsignedInt & 0x0000FF00L) >> 8);
		buf[3] = (byte) (anUnsignedInt & 0x000000FFL);

		return buf;
	}

	public static void main(String[] args) throws IOException {
		GPSDatum gData = new GPSDatum();
		gData.setLat(30.620785);
		gData.setLon(-96.335091);

		CompassDatum cData = new CompassDatum(202, 0, 0, 0);

		JFileChooser chooser = new JFileChooser();
		chooser.setDialogType(JFileChooser.FILES_ONLY);

		FileFilter filter = new ExtensionFilter("jpg");
		chooser.setFileFilter(filter);

		int returnVal = chooser.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			ImageProcessor proc = new ImageProcessor(chooser.getSelectedFile(),
					gData, cData);
			proc.processImage();
		}
	}
}
