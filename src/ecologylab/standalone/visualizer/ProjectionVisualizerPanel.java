/**
 * 
 */
package ecologylab.standalone.visualizer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.EnumSet;

import javax.swing.JPanel;

import ecologylab.projection.PlateCarreeProjection;
import ecologylab.projection.Projection;
import ecologylab.projection.SameCoordinatesException;
import ecologylab.sensor.location.gps.data.GPSConstants;
import ecologylab.sensor.location.gps.data.GPSDatum;
import ecologylab.sensor.location.gps.data.GeoCoordinate;
import ecologylab.sensor.location.gps.listener.GPSDataUpdatedListener;

/**
 * @author Z O. Toups (zach@ecologylab.net)
 * 
 */
public class ProjectionVisualizerPanel extends JPanel implements
		GPSDataUpdatedListener
{
	private static final long	serialVersionUID	= -7543653734798453833L;

	private static final Color	TRANSLUCENT_GREEN	= new Color(Color.GREEN
																		.getRed(), Color.GREEN
																		.getGreen(), Color.GREEN
																		.getBlue(), 128);

	GeoCoordinate							centerPoint;

	GeoCoordinate							currentPosition;

	PlateCarreeProjection		currentProjection;

	/**
	 * Allows us to visualize GPS coordinates.
	 */
	PlateCarreeProjection		visualizerProjection;

	GPSConstants					point1;

	GPSConstants					point2;

	boolean							drawing;

	Rectangle2D						visualRect;

	int								w, h;

	/**
	 * @throws SameCoordinatesException
	 * 
	 */
	public ProjectionVisualizerPanel(GeoCoordinate centerPoint,
			PlateCarreeProjection currentProjection, int width, int height)
	{
		this.w = width;
		this.h = height;

		Dimension d = new Dimension(w, h);
		this.setPreferredSize(d);
		this.setMinimumSize(d);
		this.setMaximumSize(d);

		this.centerPoint = centerPoint;
		this.currentPosition = centerPoint;
		this.currentProjection = currentProjection;

		// use the center point to create a projection that is about .25 minutes
		// expanded in each direction
		GeoCoordinate neCorner = new GPSDatum(centerPoint.getLat() + .3, centerPoint
				.getLon() + .3);

		GeoCoordinate swCorner = new GPSDatum(centerPoint.getLat() - .3, centerPoint
				.getLon() - .3);

		visualRect = new Rectangle2D.Double(-w / 2.0, -h / 2.0, w, h);

		try
		{
			visualizerProjection = new PlateCarreeProjection(neCorner, swCorner,
					w, h, Projection.RotationConstraintMode.CARDINAL_DIRECTIONS);
		}
		catch (SameCoordinatesException e)
		{
			// this can't happen
			e.printStackTrace();
		}
	}

	/**
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override protected void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;

		Point2D.Double center = this.visualizerProjection
				.projectIntoVirtual(this.centerPoint);

		AffineTransform saveXForm = g2.getTransform();

		g2.translate(center.getX() + (this.w / 2.0), center.getY()
				+ (this.h / 2.0));

		// clear background
		g2.setColor(Color.BLACK);
		g2.fill(visualRect);

		// draw origin
		g2.setColor(Color.WHITE);
		g2.drawLine(-5, 0, 5, 0);
		g2.drawLine(0, -5, 0, 5);

		// draw current position
		g2.setColor(Color.RED);
		Point2D.Double loc = this.visualizerProjection
				.projectIntoVirtual(this.currentPosition);

		if (visualRect.contains(loc))
		{ // draw an x @ the current position
			g2.drawLine((int) (loc.getX() - 5), (int) (loc.getY() - 5), (int) (loc
					.getX() + 5), (int) (loc.getY() + 5));
			g2.drawLine((int) (loc.getX() + 5), (int) (loc.getY() - 5), (int) (loc
					.getX() - 5), (int) (loc.getY() + 5));
		}
		else
		{ // draw an arrow offscreen
			g2.drawLine((int) (loc.getX()), (int) (loc.getY()), 100, 100);
		}

		g2.setColor(Color.RED);

		this.paintVirtualWorld(g2, center);
		this.paintCorners(g2);

		g2.setTransform(saveXForm);

		g2.setColor(Color.WHITE);
		g2.setFont(new Font("Arial", Font.BOLD, 10));
		g2.drawString("o: " + this.centerPoint.getLon() + ", "
				+ centerPoint.getLat(), 0, 10);
		g2.drawString("c: " + this.currentPosition.getLon() + ", "
				+ this.currentPosition.getLat(), 0, 20);
	}

	/**
	 * @param g2
	 */
	private void paintVirtualWorld(Graphics2D g2,
			Point2D.Double currentCenterInVisCoords)
	{
		AffineTransform saveXForm = g2.getTransform();

		double vWidth = this.currentProjection.getVirtualWorldWidth();
		double vHeight = this.currentProjection.getVirtualWorldHeight();

		Rectangle2D.Double virtualWorld = new Rectangle2D.Double(-vWidth / 2.0,
				-vHeight / 2.0, vWidth, vHeight);

		AffineTransform modified = g2.getTransform();

		modified.concatenate(visualizerProjection.getTransformMatrix());
		modified.concatenate(currentProjection.getInverseTransformMatrix());

		g2.setTransform(modified);

		// move the rectangle to the center
		g2.setColor(TRANSLUCENT_GREEN);
		g2.fill(virtualWorld);

		g2.setColor(Color.GREEN);
		g2.draw(virtualWorld);

		g2.setTransform(saveXForm);
	}

	private void paintCorners(Graphics2D g2)
	{
		Point2D.Double ne = this.visualizerProjection
				.projectIntoVirtual(this.currentProjection
						.getPhysicalWorldPointNE());
		Point2D.Double sw = this.visualizerProjection
				.projectIntoVirtual(this.currentProjection
						.getPhysicalWorldPointSW());

		g2.setColor(Color.GRAY);

		g2.drawLine((int) ne.getX(), (int) ne.getY(), (int) ne.getX() - 5,
				(int) ne.getY());
		g2.drawLine((int) ne.getX(), (int) ne.getY(), (int) ne.getX(), (int) ne
				.getY() + 5);

		g2.drawLine((int) sw.getX(), (int) sw.getY(), (int) sw.getX() + 5,
				(int) sw.getY());
		g2.drawLine((int) sw.getX(), (int) sw.getY(), (int) sw.getX(), (int) sw
				.getY() - 5);

		g2.setColor(Color.GREEN);

		g2.drawLine((int) ne.getX(), (int) ne.getY(), (int) sw.getX(), (int) sw
				.getY());
	}

	public void centerOnCurrent()
	{
		this.centerPoint = this.currentPosition;
	}

	/**
	 * @see ecologylab.sensor.location.gps.listener.GPSDataUpdatedListener#gpsDatumUpdated(ecologylab.sensor.location.gps.data.GPSDatum)
	 */
	public void gpsDatumUpdated(GPSDatum datum)
	{
		this.currentPosition = datum;
	}

	/**
	 * Indicates which GPS update operations this is interested in; in this case,
	 * all of them.
	 */
	private static EnumSet<GPSUpdateInterest>	interestSet	= EnumSet
																					.of(
																							GPSUpdateInterest.LAT_LON,
																							GPSUpdateInterest.ALT,
																							GPSUpdateInterest.OTHERS);

	public EnumSet<GPSUpdateInterest> getInterestSet()
	{
		return interestSet;
	}
}
