package ecologylab.standalone.ImageGeotagger;

import java.io.File;

import javax.swing.JFileChooser;

import ecologylab.standalone.GeoClient;
import ecologylab.standalone.ImageGeotagger.DirectoryMonitor.AppendGPSImgDirMonitor;
import ecologylab.standalone.ImageGeotagger.DirectoryMonitor.ImgDirMonitor;

public class ImageGeotagger
{
	public static AppendGPSImgDirMonitor startMonitor(GeoClient client)
	{

		AppendGPSImgDirMonitor monitor = null;

		if (client != null)
		{
			System.out.println("Client succesfully connected to geo service!");

			/* Select Directory To Monitor */
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle("Choose Image Directory");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			int returnVal = chooser.showOpenDialog(null);

			if (returnVal == JFileChooser.APPROVE_OPTION)
			{

				try
				{
					File selectedFile = chooser.getSelectedFile();

					// set up clock synch
					long clockOffset = ClockSynchWindow.getOffsetFromSynchWindow(selectedFile, client);

					monitor = new AppendGPSImgDirMonitor(selectedFile, client, clockOffset);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}

				monitor.start();
			}
			else
			{
				client.disconnect();
				System.exit(0);
			}

		}
		else
		{
			System.err.println("Could not connect to GPS service.");
		}
		return monitor;
	}

	public static void main(String[] args) throws Exception
	{
		GeoClient client = new GeoClient();

		if (client.connect())
		{
			System.out.println("Client succesfully connected to geo service!");

			/* Select Directory To Monitor */
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			int returnVal = chooser.showOpenDialog(null);

			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				File selectedFile = chooser.getSelectedFile();

				// set up clock synch
				long clockOffset = ClockSynchWindow.getOffsetFromSynchWindow(selectedFile, client);
				
				ImgDirMonitor monitor = new AppendGPSImgDirMonitor(selectedFile, client, clockOffset);
				monitor.start();
			}
			else
			{
				client.disconnect();
				System.exit(0);
			}

		}
	}
}
