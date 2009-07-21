/**
 * 
 */
package ecologylab.sensor.location.gps.listener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import ecologylab.sensor.location.NMEAStringListener;

/**
 * Logs pure NMEA sentences to a return-delimited file.
 * 
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 * 
 */
public class GPSDataLogger implements NMEAStringListener
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
	 * @see ecologylab.sensor.location.NMEAStringListener#processIncomingNMEAString(java.lang.String)
	 */
	public void processIncomingNMEAString(String gpsDataString)
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
