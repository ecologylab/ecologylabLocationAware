/**
 * 
 */
package ecologylab.sensor.location.gps.gui.visualization;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.EnumSet;

import javax.swing.JPanel;

import ecologylab.sensor.location.gps.data.GPSDatum;
import ecologylab.sensor.location.gps.data.SVData;
import ecologylab.sensor.location.gps.listener.GPSDataUpdatedListener;

/**
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 * 
 */
public class ConstellationSignalStrength extends JPanel implements
		GPSDataUpdatedListener
{
	GPSDatum				datum;

	AffineTransform	saveXForm;

	/**
	 * 
	 */
	public ConstellationSignalStrength()
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param layout
	 */
	public ConstellationSignalStrength(LayoutManager layout)
	{
		super(layout);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param isDoubleBuffered
	 */
	public ConstellationSignalStrength(boolean isDoubleBuffered)
	{
		super(isDoubleBuffered);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param layout
	 * @param isDoubleBuffered
	 */
	public ConstellationSignalStrength(LayoutManager layout,
			boolean isDoubleBuffered)
	{
		super(layout, isDoubleBuffered);
		// TODO Auto-generated constructor stub
	}

	public void gpsDatumUpdated(GPSDatum datum)
	{
		this.datum = datum;
	}

	/**
	 * Draws a constellation of satellites representing the current status of the
	 * location produced by the GPS device.
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override protected void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		saveXForm = g2.getTransform();

		int scaleFactor;

		int height = this.getHeight();
		int width = this.getWidth();

		// determine limiting factor
		if (height > width)
		{
			scaleFactor = width / 10;
		}
		else
		{
			scaleFactor = height / 10;
		}

		SVData[] sats;

		if (sats != null)
		{
			for (int i = 0; i < 12; i++)
			{
				if (sats[i] != null)
				{ // we need to draw a sat. circle
					int snr = sats[i].getSnr();
					int id = sats[i].getId();
				}
			}
		}

		g2.setColor(Color.WHITE);

		g2.translate(scaleFactor * 5, height / 2);

		g2.scale(scaleFactor, scaleFactor);

		int numIcons = seeker.getGpsSats() / 3;
		if (numIcons > 4)
			numIcons = 4;

		for (int i = 0; i < numIcons; i++)
		{
			g2.fill(CommonShapes.getGpsSymbolShape());

			g2.translate(10, 0);
		}

		g2.setTransform(saveXForm);
	}

	/**
	 * Indicates which GPS update operations this is interested in; in this case,
	 * all of them.
	 */
	private static EnumSet<GPSUpdateInterest>	interestSet	= EnumSet
																					.of(GPSUpdateInterest.LAT_LON);

	public EnumSet<GPSUpdateInterest> getInterestSet()
	{
		return interestSet;
	}
}