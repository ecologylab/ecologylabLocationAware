/*
 * Created on Apr 24, 2006
 */
package ecologylab.standalone.wifiGpsControls;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;

import ecologylab.sensor.location.gps.data.GPSDatum;

public class GPSMeter extends JPanel
{
	private static final long	serialVersionUID	= -1762445798326919181L;

	private AffineTransform		saveXForm;

	private Color					color					= new Color(150, 150, 150);	
	
	private GPSDatum datum;

	public GPSMeter(GPSDatum datum)
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

		g2.scale(scaleFactor, scaleFactor / 3.0);

		g2.translate(5, 5);

		int numIcons = datum.getNumSats();

		System.out.println("num sats: "+datum.getNumSats());
		
		for (int i = 0; i < numIcons; i++)
		{
			if (i != 0 && i % 4 == 0)
			{
				g2.translate(-40, 10);
			}

			g2.fill(CommonShapes.getGpsSymbolShape());

			g2.translate(10, 0);
		}

		g2.setTransform(saveXForm);
	}
}
