/**
 * 
 */
package ecologylab.standalone.visualizer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import ecologylab.projection.PlateCarreeProjection;
import ecologylab.projection.Projection;
import ecologylab.projection.SameCoordinatesException;
import ecologylab.sensor.gps.data.GPSDatum;
import ecologylab.sensor.gps.listener.GPSDataUpdatedListener;

/**
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 * 
 */
public class ProjectionVisualizerPanel extends JPanel implements GPSDataUpdatedListener
{
	GPSDatum						centerPoint;

	GPSDatum						currentPosition;

	Projection					currentProjection;

	/**
	 * Allows us to visualize GPS coordinates.
	 */
	PlateCarreeProjection	visualizerProjection;

	GPSDatum						point1;

	GPSDatum						point2;

	boolean						drawing;

	Rectangle2D					visualRect;

	/**
	 * @throws SameCoordinatesException
	 * 
	 */
	public ProjectionVisualizerPanel(GPSDatum centerPoint)
	{
		Dimension d = new Dimension(200, 200);
		this.setPreferredSize(d);
		this.setMinimumSize(d);
		this.setMaximumSize(d);

		this.centerPoint = centerPoint;
		this.currentPosition = centerPoint;

		// use the center point to create a projection that is about .25 minutes expanded in each direction
		GPSDatum neCorner = new GPSDatum(centerPoint.getLat() + .02, centerPoint.getLon() + .02);
		Point2D.Double upperLeft = new Point2D.Double(0, 0);

		GPSDatum swCorner = new GPSDatum(centerPoint.getLat() - .02, centerPoint.getLon() - .02);
		Point2D.Double lowerRight = new Point2D.Double(200, 200);

		visualRect = new Rectangle2D.Double(upperLeft.x, upperLeft.y, Point2D.distance(upperLeft.x, 0, lowerRight.x, 0),
				Point2D.distance(0, upperLeft.y, 0, lowerRight.y));

		try
		{
			visualizerProjection = new PlateCarreeProjection(neCorner, swCorner, upperLeft, lowerRight,
					Projection.RotationConstraintMode.CARDINAL_DIRECTIONS);
		}
		catch (SameCoordinatesException e)
		{
			// this can't happen
			e.printStackTrace();
		}
	}

	/**
	 * @param layout
	 */
	private ProjectionVisualizerPanel(LayoutManager layout)
	{
		super(layout);
	}

	/**
	 * @param isDoubleBuffered
	 */
	private ProjectionVisualizerPanel(boolean isDoubleBuffered)
	{
		super(isDoubleBuffered);
	}

	/**
	 * @param layout
	 * @param isDoubleBuffered
	 */
	private ProjectionVisualizerPanel(LayoutManager layout, boolean isDoubleBuffered)
	{
		super(layout, isDoubleBuffered);
	}

	/**
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override protected void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;

		// clear background
		g2.setColor(Color.BLACK);
		g2.fill(visualRect);

		// draw origin
		g2.setColor(Color.WHITE);
		g2.drawLine(95, 100, 105, 100);
		g2.drawLine(100, 95, 100, 105);

		// draw current position
		g2.setColor(Color.GREEN);
		Point2D loc = this.visualizerProjection.projectIntoVirtual(this.currentPosition);

		if (visualRect.contains(loc))
		{ // draw an x @ the current position
			g2.drawLine((int) (loc.getX() - 5), (int) (loc.getY() - 5), (int) (loc.getX() + 5), (int) (loc.getY() + 5));
			g2.drawLine((int) (loc.getX() + 5), (int) (loc.getY() - 5), (int) (loc.getX() - 5), (int) (loc.getY() + 5));
		}
		else
		{ // draw an arrow offscreen
			g2.drawLine((int) (loc.getX()), (int) (loc.getY()), 0, 0);
		}
	}

	public void centerOnCurrent()
	{
		this.centerPoint = this.currentPosition;
	}

	/**
	 * @see ecologylab.sensor.gps.listener.GPSDataUpdatedListener#gpsDatumUpdated(ecologylab.sensor.gps.data.GPSDatum)
	 */
	public void gpsDatumUpdated(GPSDatum datum)
	{
		this.currentPosition = datum;
	}
}
