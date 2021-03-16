package ecologylab.standalone.ImageGeotagger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.ImageWriteException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.jpeg.exifRewrite.ExifRewriter;
import org.apache.sanselan.formats.tiff.TiffField;
import org.apache.sanselan.formats.tiff.TiffImageMetadata;
import org.apache.sanselan.formats.tiff.constants.ExifTagConstants;
import org.apache.sanselan.formats.tiff.constants.GPSTagConstants;
import org.apache.sanselan.formats.tiff.constants.TagInfo;
import org.apache.sanselan.formats.tiff.constants.TiffConstants;
import org.apache.sanselan.formats.tiff.constants.TiffFieldTypeConstants;
import org.apache.sanselan.formats.tiff.constants.TiffTagConstants;
import org.apache.sanselan.formats.tiff.write.TiffOutputDirectory;
import org.apache.sanselan.formats.tiff.write.TiffOutputField;
import org.apache.sanselan.formats.tiff.write.TiffOutputSet;

import ecologylab.sensor.location.compass.CompassDatum;
import ecologylab.sensor.location.gps.data.GPSDatum;
import ecologylab.services.messages.LocationDataResponse;
import ecologylab.standalone.GeoClient;
import ecologylab.standalone.ImageGeotagger.DirectoryMonitor.AppendGPSImgDirMonitor;

public class ImageProcessor implements Runnable
{
	protected File												imageFile;

	private Thread												t;

	private GeoClient											client;

	private GPSDatum											gpsData;

	private CompassDatum									compassData;

	private final AppendGPSImgDirMonitor	monitor;

	private long													offsetMillis;

	public ImageProcessor(File image, GeoClient client, long offsetMillis,
			AppendGPSImgDirMonitor monitor)
	{
		this.imageFile = image;
		this.client = client;
		this.monitor = monitor;
		this.offsetMillis = offsetMillis;
	}

	public ImageProcessor(File image,
			GPSDatum gpsData,
			CompassDatum compassData,
			AppendGPSImgDirMonitor monitor)
	{
		this.imageFile = image;
		this.gpsData = gpsData;
		this.compassData = compassData;

		this.monitor = monitor;

	}

	public void processImage()
	{
		t = new Thread(this);
		t.start();
	}

	@Override
	public synchronized void run()
	{
		System.out.println("Processing file: " + imageFile.getName());

		/* Read metadata from image file */
		IImageMetadata metadata = null;
		JpegImageMetadata jpegMetadata = null;
		TiffImageMetadata exif = null;
		TiffOutputSet outputSet = null;

		/* Initialize metadata from existing image metadata */
		try
		{
			// there seems to be a non-deterministic bug where this call thinks it has read the whole file
			// when, in fact, it has not. This code will run until it gets metadata back, dammit, or until
			// it has tried 100 times.
			int countdown = 100000;
			while (metadata == null && countdown > 0)
			{
				try
				{
					metadata = Sanselan.getMetadata(imageFile);
				}
				catch (ImageReadException e)
				{ // ignore
					System.err.println("WARNING: Failed to read metadata on " + imageFile.getName()
							+ "; trying " + (countdown - 1) + " more times.");
				}

				countdown--;
			}

			if (metadata == null || !(metadata instanceof JpegImageMetadata))
				return;

			jpegMetadata = (JpegImageMetadata) metadata;

			exif = jpegMetadata.getExif();

			if (exif == null)
				return;

			outputSet = exif.getOutputSet();

			if (client != null)
			{
				System.out.println("Getting location from service.");
				if (client.connected())
				{
					// get the time for the image and produce a Calendar object from it
					// assumes camera is set to UTC
					String dateTimeUTC = jpegMetadata.findEXIFValue(TiffTagConstants.TIFF_TAG_DATE_TIME)
							.getStringValue();

					// Calendar imageTime = Calendar.getInstance();
					DateFormat df = new SimpleDateFormat("yyyy:MM:dd kk:mm:ss");
					java.util.Date d = df.parse(dateTimeUTC);

					// imageTime.setTimeInMillis(d.getTime() + offsetMillis);

					long timeInMillis = d.getTime() - offsetMillis;

					System.out.println("image time: " + d.getTime() + "; offset: " + offsetMillis
							+ "; image time - offset = " + timeInMillis);

					if (timeInMillis == -1)
						throw new RuntimeException("-1 is an invalid time");

					LocationDataResponse locationData = client.updateLocation(timeInMillis);
					gpsData = locationData.gpsData;
					compassData = locationData.compassData;
				}
				else
				{
					System.out.println("Client isn't connected!");
					return;
				}
			}

			/* Write location metadata to the existing metadata */
			if (outputSet != null)
				try
				{
					if (gpsData != null)
						outputSet.setGPSInDegrees(gpsData.getLon(), gpsData.getLat());

					if (compassData != null)
						setCompassData(outputSet);
				}
				catch (ImageWriteException e)
				{
					e.printStackTrace();
					return;
				}
		}
		catch (ImageReadException | IOException | ImageWriteException | ParseException e)
		{
			e.printStackTrace();
			return;
		}

		OutputStream os = null;
		File dst = null;

		// create output stream to temp file for dst
		try
		{
			dst = File.createTempFile("temp-" + System.currentTimeMillis(), ".jpg");
			os = new FileOutputStream(dst);
			os = new BufferedOutputStream(os);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return;
		}

		// write/update EXIF metadata to output stream of temp file
		try
		{
			new ExifRewriter().updateExifMetadataLossless(imageFile, os, outputSet);
		}
		catch (ImageReadException | ImageWriteException | IOException e)
		{
			e.printStackTrace();
			return;
		}
		finally
		{
			if (os != null)
				try
				{
					os.close();
				}
				catch (IOException e)
				{
				}
		}

		// finally copy the temp file to original
		try
		{
			copyFile(dst, imageFile);
			if (this.monitor != null)
			{
				this.monitor.addCompassDatum(compassData);
				this.monitor.addGPSDatum(gpsData);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void setCompassData(TiffOutputSet outputSet)
	{
		TiffOutputDirectory dir = outputSet.getGPSDirectory();

		ByteOrder order = (outputSet.byteOrder == TiffConstants.BYTE_ORDER_BIG_ENDIAN) ? ByteOrder.BIG_ENDIAN
				: ByteOrder.LITTLE_ENDIAN;

		/* create heading data */
		ByteBuffer rationalBuffer = ByteBuffer.allocate(8);

		long heading = (long) (compassData.getHeading() * 100);

		System.out.println("Heading num: " + heading);
		rationalBuffer.put(intToUnsignedByteArray(heading, order));
		rationalBuffer.put(intToUnsignedByteArray(100, order));
		rationalBuffer.flip();

		byte[] buf = new byte[8];
		rationalBuffer.get(buf);

		TiffOutputField outField = new TiffOutputField(GPSTagConstants.GPS_TAG_GPS_IMG_DIRECTION,
				TiffFieldTypeConstants.FIELD_TYPE_RATIONAL,
				1,
				buf);

		/* Remove Previous Data if Necessary */
		TiffOutputField imageDirPre = outputSet.findField(GPSTagConstants.GPS_TAG_GPS_IMG_DIRECTION);
		if (imageDirPre != null)
		{
			outputSet.removeField(GPSTagConstants.GPS_TAG_GPS_IMG_DIRECTION);
		}

		dir.add(outField);

		/* Create Heading Reference Data */
		String refVal = GPSTagConstants.GPS_TAG_GPS_IMG_DIRECTION_REF_VALUE_MAGNETIC_NORTH;
		TiffOutputField outField2 = new TiffOutputField(TiffOutputField.GPS_TAG_GPS_IMG_DIRECTION_REF,
				TiffFieldTypeConstants.FIELD_TYPE_ASCII,
				refVal.length(),
				refVal.getBytes());

		/* Delete Previous Field */
		TiffOutputField imageDirRefPre = outputSet
				.findField(GPSTagConstants.GPS_TAG_GPS_IMG_DIRECTION_REF);
		if (imageDirRefPre != null)
		{
			outputSet.removeField(GPSTagConstants.GPS_TAG_GPS_IMG_DIRECTION_REF);
		}

		dir.add(outField2);
	}

	public static void copyFile(File in, File out) throws IOException
	{
		FileChannel inChannel = new FileInputStream(in).getChannel();
		FileChannel outChannel = new FileOutputStream(out).getChannel();
		try
		{
			inChannel.transferTo(0, inChannel.size(), outChannel);
		}
		catch (IOException e)
		{
			throw e;
		}
		finally
		{
			if (inChannel != null)
				inChannel.close();
			if (outChannel != null)
				outChannel.close();
		}
	}

	/* Converts a long into a byte array representing a 32 bit unsigned integer */
	public byte[] intToUnsignedByteArray(long x, ByteOrder order)
	{
		Long anUnsignedInt = x;

		byte[] buf = new byte[4];

		if (order.equals(java.nio.ByteOrder.nativeOrder()))
		{
			buf[3] = (byte) ((anUnsignedInt & 0xFF000000L) >> 24);
			buf[2] = (byte) ((anUnsignedInt & 0x00FF0000L) >> 16);
			buf[1] = (byte) ((anUnsignedInt & 0x0000FF00L) >> 8);
			buf[0] = (byte) (anUnsignedInt & 0x000000FFL);
		}
		else
		{
			buf[0] = (byte) ((anUnsignedInt & 0xFF000000L) >> 24);
			buf[1] = (byte) ((anUnsignedInt & 0x00FF0000L) >> 16);
			buf[2] = (byte) ((anUnsignedInt & 0x0000FF00L) >> 8);
			buf[3] = (byte) (anUnsignedInt & 0x000000FFL);
		}

		return buf;
	}

	public static void main(String[] args) throws ImageReadException
	{
		File imageFile = new File(
				// "/Users/ztoups/Dropbox/workspaceGit/photoNav/config/photoLibrary/IMG_0290.JPG");
				"/Users/ztoups/Desktop/DSCN0945.JPG");

		System.out.println("Processing file: " + imageFile.getName());

		/* Read metadata from image file */
		IImageMetadata metadata = null;
		JpegImageMetadata jpegMetadata = null;
		TiffImageMetadata exif = null;
		TiffOutputSet outputSet = null;

		/* Initialize metadata from existing image metadata */
		try
		{
			metadata = Sanselan.getMetadata(imageFile);

			if (metadata == null || !(metadata instanceof JpegImageMetadata))
				return;

			jpegMetadata = (JpegImageMetadata) metadata;

			exif = jpegMetadata.getExif();

			if (exif == null)
				return;

			outputSet = exif.getOutputSet();
		}
		catch (ImageReadException | IOException | ImageWriteException e)
		{
			e.printStackTrace();
			return;
		}

		// get the time for the image and produce a Calendar object from it
		printTagValue(jpegMetadata, TiffTagConstants.TIFF_TAG_DATE_TIME);
		printTagValue(jpegMetadata,
				ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
	}

	private static void printTagValue(final JpegImageMetadata jpegMetadata,
			final TagInfo tagInfo)
	{
		final TiffField field = jpegMetadata.findEXIFValue(tagInfo);
		if (field == null)
		{
			System.out.println(tagInfo.name + ": " + "Not Found.");
		}
		else
		{
			System.out.println(tagInfo.name + ": "
					+ field.getValueDescription());
		}
	}

	// public static void main(String[] args) throws IOException
	// {
	// GPSDatum gData = new GPSDatum();
	// gData.setLat(30.620785);
	// gData.setLon(-96.335091);
	//
	// CompassDatum cData = new CompassDatum(202, 0, 0, 0);
	//
	// JFileChooser chooser = new JFileChooser();
	// chooser.setDialogType(JFileChooser.FILES_ONLY);
	//
	// FileFilter filter = new ExtensionFilter("jpg");
	// chooser.setFileFilter(filter);
	//
	// int returnVal = chooser.showOpenDialog(null);
	//
	// if (returnVal == JFileChooser.APPROVE_OPTION)
	// {
	// ImageProcessor proc = new ImageProcessor(chooser.getSelectedFile(), gData, cData, null);
	// proc.processImage();
	// }
	// }
}
