package ecologylab.standalone.ImageGeotagger;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import ecologylab.oodss.logging.playback.ExtensionFilter;
import ecologylab.standalone.GeoClient;
import ecologylab.standalone.ImageGeotagger.DirectoryMonitor.ImageDirectoryMonitor;

public class ImageGeotagger
{
	public static ImageDirectoryMonitor startMonitor(GeoClient client)
	{
		
		ImageDirectoryMonitor monitor = null;
		
		if(client != null)
		{
			System.out.println("Client succesfully connected to geo service!");
			
			/* Select Directory To Monitor */
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			int returnVal = chooser.showOpenDialog(null);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				
				try
				{
					monitor = new ImageDirectoryMonitor(chooser.getSelectedFile(), client);
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
	
	public static void main (String[] args) throws Exception
	{
		GeoClient client = new GeoClient();
		
		if(client.connect())
		{
			System.out.println("Client succesfully connected to geo service!");
			
			/* Select Directory To Monitor */
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			int returnVal = chooser.showOpenDialog(null);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				ImageDirectoryMonitor monitor = new ImageDirectoryMonitor(chooser.getSelectedFile(), client);
				
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
