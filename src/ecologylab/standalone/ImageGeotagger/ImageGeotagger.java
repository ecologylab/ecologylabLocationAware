package ecologylab.standalone.ImageGeotagger;

import java.io.File;

import ecologylab.standalone.ImageGeotagger.DirectoryMonitor.ImageDirectoryMonitor;

public class ImageGeotagger
{
	public static void main (String[] args) throws Exception
	{
		ImageDirectoryMonitor monitor = new ImageDirectoryMonitor(new File("/tmp/"));
		
		monitor.start();
	}
}
