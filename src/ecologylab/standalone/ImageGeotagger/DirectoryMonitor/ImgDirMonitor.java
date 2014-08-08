package ecologylab.standalone.ImageGeotagger.DirectoryMonitor;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashSet;
import java.util.Set;

import ecologylab.generic.StartAndStoppable;

public abstract class ImgDirMonitor implements Runnable, StartAndStoppable
{
	protected final File			directoryToMonitor;

	/** Interval at which the directory is polled for new files. */
	private final int					pollingInterval	= 1000;

	private static JpegFilter	filter					= new JpegFilter();

	private final Set<String>	processedFiles	= new HashSet<String>();

	private Thread						t;

	private boolean						running					= false;

	static class JpegFilter implements FilenameFilter
	{
		@Override
		public boolean accept(File dir, String name)
		{
			if (name.length() >= 4)
			{
				if (name.length() >= 5)
				{
					String extension5 = name.substring(name.length() - 5, name.length());
					if (extension5.equalsIgnoreCase(".jpeg"))
					{
						return true;
					}
				}
				String extension = name.substring(name.length() - 4, name.length());
				return extension.equalsIgnoreCase(".jpg");
			}
			return false;
		}
	}

	public ImgDirMonitor(File directory) throws Exception
	{
		if (!directory.isDirectory())
		{
			throw new Exception(directory.getName() + " is not a directory!");
		}

		this.directoryToMonitor = directory;

		seedProcessedFiles();
	}

	protected void seedProcessedFiles()
	{
		String[] files = directoryToMonitor.list(filter);

		for (String file : files)
		{
			processedFiles.add(file);
		}
	}

	private Set<File> waitForNewImages() throws InterruptedException
	{
		Set<File> newFiles = new HashSet<File>();

		while (running)
		{
			File[] files = directoryToMonitor.listFiles(filter);

			for (File file : files)
			{
				if (!processedFiles.contains(file.getName()))
				{
					processedFiles.add(file.getName());
					newFiles.add(file);
				}
			}

			if (!newFiles.isEmpty())
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

	protected abstract void processFile(File file);

	@Override
	public void run()
	{
		while (running)
		{
			try
			{
				Set<File> newFiles = waitForNewImages();
				for (File file : newFiles)
				{
					processFile(file);
				}
			}
			catch (InterruptedException e)
			{
				if (!running)
				{
					return;
				}
			}
		}
	}

	@Override
	public synchronized void start()
	{
		if (!running)
		{
			running = true;
			t = new Thread(this);
			t.start();
		}
	}

	@Override
	public synchronized void stop()
	{
		if (running)
		{
			running = false;
			t.interrupt();
		}
	}

}