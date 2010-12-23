package ecologylab.swing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
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
		
		Graphics2D g2 = (Graphics2D) image.getGraphics();
		
		
		g2.setBackground(Color.LIGHT_GRAY);
		g2.clearRect(0, 0, image.getWidth(), image.getHeight());
		
		
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
	
	public Graphics2D getGraphics()
	{
		return (Graphics2D) image.getGraphics();
	}
	
	public Image getTile()
	{
		return image;
	}
	
}