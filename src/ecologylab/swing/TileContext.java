package ecologylab.swing;

import java.awt.image.BufferedImage;

import com.vividsolutions.jts.geom.Coordinate;

class TileContext
{
	boolean done;
	boolean cancle;
	
	private static int count = 1;
	
	public Coordinate center = new Coordinate();
	public int myCount;
	
	private BufferedImage image;

	public TileContext(int pixels)
	{
		image = new BufferedImage(pixels, pixels, BufferedImage.TYPE_INT_ARGB);
		reset();
		myCount = count++;
	}
	
	public synchronized void reset()
	{
		done = false;
		cancle = false;
		center.x = center.y = center.z = 0;
	}
	
	public synchronized void markDone()
	{
		done = true;
	}
	
	public synchronized boolean isDone()
	{
		return done;
	}
	
	public synchronized void cancle()
	{
		cancle = true;
	}
	
}