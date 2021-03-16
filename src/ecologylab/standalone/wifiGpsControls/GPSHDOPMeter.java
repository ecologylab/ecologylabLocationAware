/*
 * Created on Apr 24, 2006
 */
package ecologylab.standalone.wifiGpsControls;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import ecologylab.sensor.location.gps.data.GPSDatum;

public class GPSHDOPMeter extends JPanel
{
	private static final long	serialVersionUID	= -1762445798326919181L;

	private AffineTransform		saveXForm;

	private Color					color					= new Color(150, 150, 150);

	private GPSDatum				datum;

	public GPSHDOPMeter(GPSDatum datum)
	{
		this.datum = datum;
	}

	/**
	 * 
	 */
	public void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		saveXForm = g2.getTransform();

		int scaleFactor;

		int height = this.getHeight();
		int width = this.getWidth();

		// determine limiting factor
		if (height > (width / 4))
		{
			scaleFactor = width / 40;
		}
		else
		{
			scaleFactor = height / 10;
		}

		g2.setColor(color);

		System.out.println("hdop: "+datum.getHdop());
		
		double hdopDisplay = datum.getHdop()/50;

		Rectangle2D.Double rect = new Rectangle2D.Double(0, 0, this.getWidth()
				* (1.0-hdopDisplay), this.getHeight());
		g2.fill(rect);

		g2.setTransform(saveXForm);
	}
}
