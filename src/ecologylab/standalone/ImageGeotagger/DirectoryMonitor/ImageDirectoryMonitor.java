package ecologylab.standalone.ImageGeotagger.DirectoryMonitor;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion.Static;

import ecologylab.generic.StartAndStoppable;
import ecologylab.standalone.GeoClient;
import ecologylab.standalone.ImageGeotagger.ImageProcessor;

public class ImageDirectoryMonitor implements Runnable, StartAndStoppable
{
	
	private static class JpegFilter implements FilenameFilter
	{

		@Override
		public boolean accept(File dir, String name)
		{
			if(name.length() >= 4)
			{
				if(name.length() >= 5)
				{
					String extension5 = name.substring(name.length()-5, name.length());
					if(extension5.equalsIgnoreCase(".jpeg"))
					{
						return true;
					}
				}
				String extension = name.substring(name.length()-4, name.length());
				return extension.equalsIgnoreCase(".jpg");
			}
			return false;
		}
	
	}
	
	private File directoryToMonitor;
	
	private GeoClient client;
	
	/**
	 * Interval at which the directory is polled for new files.
	 */
	private int pollingInterval = 1000;
	
	private static JpegFilter filter = new JpegFilter();
	
	private Set<String> processedFiles = new HashSet<String>();
	
	private Thread t;
	
	private boolean running = false;
	
	public ImageDirectoryMonitor(File directory, GeoClient client) throws Exception
	{
		if(!directory.isDirectory())
		{
			throw new Exception(directory.getName() + " is not a directory!");
		}
		
		this.directoryToMonitor = directory;
		
		seedProcessedFiles();
		
		this.client = client;
	}

	private void seedProcessedFiles()
	{
		String[] files = directoryToMonitor.list(filter);
		
		for(String file : files)
		{
			processedFiles.add(file);
		}
	}
	
	private Set<File> waitForNewImages() throws InterruptedException
	{
		Set<File> newFiles = new HashSet<File>();
		
		while(running)
		{
			File[] files = directoryToMonitor.listFiles(filter);
			
			for(File file : files)
			{
				if(!processedFiles.contains(file.getName()))
				{
					processedFiles.add(file.getName());
					newFiles.add(file);
				}
			}
						
			if(!newFiles.isEmpty())
			{
				return newFiles;
			}
			else
			{
				Thread.sleep(pollingInterval);
			}
		}
		
		return null;
	}
	
	@Override
	public void run()
	{
		while(running)
		{
			try
			{
				Set<File> newFiles = waitForNewImages();
				for(File file : newFiles)
				{
					processFile(file);
				}
			}
			catch(InterruptedException e)
			{
				if(!running)
				{
					return;
				}
			}
		}
	}

	private void processFile(File file)
	{
		ImageProcessor processor = new ImageProcessor(file, client);
		
		processor.processImage();
	}

	@Override
	public synchronized void start()
	{
		if(!running)
		{
			running = true;
			t = new Thread(this);
			t.start();
		}
	}

	@Override
	public synchronized void stop()
	{
		if(running)
		{
			running = false;
			t.interrupt();
		}	
	}
}
