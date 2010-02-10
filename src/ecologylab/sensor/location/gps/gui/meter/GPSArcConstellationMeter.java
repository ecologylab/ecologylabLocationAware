/**
 * 
 */
package ecologylab.sensor.location.gps.gui.meter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

import javax.swing.JFrame;

import ecologylab.generic.Generic;
import ecologylab.rendering.panel.GraphicsTransformPanel;
import ecologylab.rendering.tweener.Ellipse2DDoubleTweener;
import ecologylab.sensor.location.gps.GPSDeviceProfile;
import ecologylab.sensor.location.gps.data.GPSConstants;
import ecologylab.sensor.location.gps.data.GPSDatum;
import ecologylab.sensor.location.gps.data.GPSDatum.DopType;

/**
 * Draws a meter indicating GPS uncertainty.
 * 
 * @author Zachary O. Toups (zach@ecologylab.net)
 */
public class GPSArcConstellationMeter extends GraphicsTransformPanel implements
		GPSConstants
{
	private static final long			serialVersionUID	= -563188957766504027L;

	private GPSDatum						datum;

	private GPSDeviceProfile			profile;

	private Ellipse2DDoubleTweener	dopIndicator		= new Ellipse2DDoubleTweener(
																				10,
																				// ellipse size 2
																				// for the smallest
																				-1, -1, 2, 2,
																				// size 20 for the
																				// largest
																				-7, -7, 14, 14);

	private Ellipse2D.Double			satellite			= new Ellipse2D.Double(
																				-1, -1, 2, 2);

	/**
	 * Maximum error based on profile. Determined for a DOP of 10 with GPS (not
	 * DGPS) accuracy.
	 */
	private double							maximumError;

	/**
	 * 
	 */
	public GPSArcConstellationMeter(GPSDatum datum)
	{
		this(datum, null);
	}

	/**
	 * @param datum
	 * @param profile
	 *           (Optional) the profile describing the GPS device in use.
	 */
	public GPSArcConstellationMeter(GPSDatum datum, GPSDeviceProfile profile)
	{
		this.datum = datum;

		this.setProfile(profile);
	}

	public void setProfile(GPSDeviceProfile newProfile)
	{
		this.profile = newProfile;

		if (this.profile != null)
		{
			this.maximumError = 10 * this.profile.getGpsAccuracy();
		}
		else
		{
			this.maximumError = 10;
		}
	}

	@Override protected void paintComponentImpl(Graphics2D g2)
	{
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		
		// we are working in a space 14 clicks high, and 22 wide
		double width = 22;
		double height = 14;

		// scale
		g2.scale(this.getWidth() / width, this.getHeight() / height);

		g2.clearRect(0, 0, (int) width, (int) height);

		int gpsQual = datum.getGpsQual();

		double currentError = 0;
		char units;

		double dop;
		DopType dopType;

		dopType = datum.getDopType();

		if (gpsQual == GPS_QUAL_NO)
		{ // no GPS, no display!
			return;
		}

		switch (dopType)
		{
		case PDOP:
			dop = datum.getPdop();
			break;
		case HDOP:
			dop = datum.getHdop();
			break;
		case VDOP:
			dop = datum.getVdop();
			break;
		default:
			dop = -1;
			break;
		}

		// determine the accuracy in meters
		if (this.profile != null)
		{
			units = 'm';

			switch (gpsQual)
			{ // can't be GPS_QUAL_NO
			case (GPS_QUAL_DGPS):
				currentError = this.profile.getDgpsAccuracy() * dop;
				break;
			case (GPS_QUAL_GPS):
				currentError = this.profile.getGpsAccuracy() * dop;
				break;
			}
		}
		else
		{ // no profile, no units
			units = ' ';
			currentError = dop;
		}

		// now have accuracy for center display size
		// first, place the DoP indicator in the center, near the bottom

		// center the transform
		g2.translate(11, 10);

		// push the transform
		this.pushTransform(g2.getTransform());

		g2.setColor(Color.BLACK);

		g2.fill(this.dopIndicator
				.getState((int) ((currentError / this.maximumError) * 10)));

		g2.setTransform(this.peekTransform());

		// draw possible satellite spots
		g2.translate(5.5, 5);

		g2.rotate(.765 * Math.PI, -5.5, -5);

		int numSats = this.datum.getNumSats();

		for (int i = 0; i < 13; i++)
		{
			if (numSats > i)
			{
				g2.setColor(Color.BLACK);
			}
			else
			{
				g2.setColor(Color.LIGHT_GRAY);
			}

			g2.fill(new Ellipse2D.Double(-.5, -.5, 1, 1));
			g2.rotate(Math.PI * 1.0 / 12.0, -5.5, -5);
		}
		
		// draw the current error text
		g2.setTransform(this.peekTransform());
		
		String errorText = Double.toString(currentError);
		// rounding
		errorText = (errorText.length() > 3 ? errorText.substring(0, 3) : errorText) +units;

		g2.setFont(new Font("Arial", Font.PLAIN, 2));
		double errorTextWidth = g2.getFontMetrics().stringWidth(errorText);
				
		g2.setColor(Color.BLUE);
		g2.drawString(errorText, (int)-(errorTextWidth/2.0), 0);
	}
	
	public static void main(String[] args)
	{
		JFrame f = new JFrame("test GPS arc constellation");

		GPSDatum g = new GPSDatum();

		GPSArcConstellationMeter c = new GPSArcConstellationMeter(g, null);

		g.gpsQual = GPS_QUAL_DGPS;
		g.pdop = 1.5f;

		c.setPreferredSize(new Dimension(220, 140));
		f.setSize(new Dimension(300, 200));

		f.add(c);
		f.setVisible(true);

		System.out.println("pdop: " + g.pdop);

		Generic.sleep(1000);

		c.repaint();
		g.pdop = 2.0f;
		g.numSats = 10;
		System.out.println("pdop: " + g.pdop);

		Generic.sleep(1000);

		c.repaint();
		g.pdop = 3.0f;
		g.numSats = 14;
		System.out.println("pdop: " + g.pdop);
		Generic.sleep(1000);

		c.repaint();
		g.pdop = 4.0f;
		g.numSats = 4;
		System.out.println("pdop: " + g.pdop);
		Generic.sleep(1000);

		c.repaint();
		g.pdop = 5.0f;
		System.out.println("pdop: " + g.pdop);
		Generic.sleep(1000);

		c.repaint();
		g.pdop = 6.0f;
		System.out.println("pdop: " + g.pdop);
		Generic.sleep(1000);

		c.repaint();
		g.pdop = 7.0f;
		System.out.println("pdop: " + g.pdop);
		Generic.sleep(1000);

		c.repaint();
		g.pdop = 6.0f;
		System.out.println("pdop: " + g.pdop);
		g.numSats = 3;
		Generic.sleep(1000);

		c.repaint();
		g.pdop = 5.0f;
		System.out.println("pdop: " + g.pdop);
		Generic.sleep(1000);

		c.repaint();
		g.pdop = 4.0f;
		System.out.println("pdop: " + g.pdop);
		g.numSats = 9;
		Generic.sleep(1000);
		
		c.repaint();
	}

	/**
	 * @param datum the datum to set
	 */
	public void setDatum(GPSDatum datum)
	{
		this.datum = datum;
	}
}
