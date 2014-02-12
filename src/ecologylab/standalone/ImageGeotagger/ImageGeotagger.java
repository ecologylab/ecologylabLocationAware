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
					monitor = new AppendGPSImgDirMonitor(chooser.getSelectedFile(), client);
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
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
				
				ImgDirMonitor monitor = new AppendGPSImgDirMonitor(selectedFile, client); // TODO add synch
																																									// offset
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
