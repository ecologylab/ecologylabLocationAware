package ecologylab.standalone.ImageGeotagger.ImageProcessor;

import java.io.File;

public class ImageProcessor implements Runnable
{
	protected File imageFile;
	private Thread t;
	
	public ImageProcessor(File image)
	{
		this.imageFile = image;
	}
	
	public void processImage()
	{
		t = new Thread(this);
		t.start();
	}
	
	@Override
	public void run()
	{
		System.out.println("New file: " + imageFile.getName());
	}
}
