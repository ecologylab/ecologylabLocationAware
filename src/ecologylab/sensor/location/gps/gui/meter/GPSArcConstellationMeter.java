/**
 * 
 */
package ecologylab.sensor.location.gps.gui.meter;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

import javax.swing.JPanel;

import ecologylab.rendering.Ellipse2DDoubleTweener;
import ecologylab.sensor.location.gps.GPSDeviceProfile;
import ecologylab.sensor.location.gps.data.GPSDatum;
import ecologylab.sensor.location.gps.data.GPSDatum.DopType;

/**
 * Draws a meter indicating GPS uncertainty.
 * 
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 */
public class GPSArcConstellationMeter extends JPanel
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
																				-10, -10, 20, 20);

	private Ellipse2D.Double			satellite			= new Ellipse2D.Double(
																				-1, -1, 2, 2);

	AffineTransform						saveXForm;

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
		this.profile = profile;
	}

	/**
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override protected void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;

		this.saveXForm = g2.getTransform();

		// we are working in a space 14 clicks high, and 22 wide
		int width = 22;
		int height = 14;

		// TODO WORKING HERE
		int gpsQual = datum.getGpsQual();
		
		double accuracy;
		char units;

		double dop;
		DopType dopType;

		dopType = datum.getDopType();

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
			
		}
		else
		{ // no profile, no units
			units = ' ';
			accuracy = dop;
		}

		// first, place the DoP indicator in the center, near the bottom
		g2.translate(11, 10);

		g2.setTransform(saveXForm);
	}
}
