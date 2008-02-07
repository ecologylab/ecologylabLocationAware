/**
 * 
 */
package ecologylab.sensor.gps.listener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Logs pure NMEA sentences to a return-delimited file.
 * 
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 * 
 */
public class GPSDataLogger implements GPSDataListener
{
	File	targetFile;
	
	OutputStream outStream;

	public GPSDataLogger(File targetFile) throws IOException
	{
		this.targetFile = targetFile;

		if (!this.targetFile.exists())
		{
			if (!this.targetFile.createNewFile())
			{
				throw new IOException("Could not create " + targetFile.getCanonicalPath());
			}
			
			if (!this.targetFile.canWrite())
			{
				throw new IOException("Cannot write to file " + targetFile.getCanonicalPath());
			}
			
			if (!this.targetFile.isFile())
			{
				throw new IOException(targetFile.getCanonicalPath()+" is not a file.");
			}
		}
		
		outStream = new FileOutputStream(this.targetFile);
	}

	/** 
	 * @see ecologylab.sensor.gps.data.GPSDataListener#readGPSData(java.lang.String)
	 */
	public void readGPSData(String gpsDataString)
	{
		try
		{
			outStream.write(('$'+gpsDataString+"\r\n").getBytes());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
