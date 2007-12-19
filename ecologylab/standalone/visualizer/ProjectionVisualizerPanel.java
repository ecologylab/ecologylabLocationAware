/**
 * 
 */
package ecologylab.standalone.visualizer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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
	public ProjectionVisualizerPanel(GPSDatum centerPoint, Projection currentProjection)
	{
		Dimension d = new Dimension(200, 200);
		this.setPreferredSize(d);
		this.setMinimumSize(d);
		this.setMaximumSize(d);

		this.centerPoint = centerPoint;
		this.currentPosition = centerPoint;
		this.currentProjection = currentProjection;

		// use the center point to create a projection that is about .25 minutes expanded in each direction
		GPSDatum neCorner = new GPSDatum(centerPoint.getLat() + .04, centerPoint.getLon() + .04);
		Point2D.Double upperRight = new Point2D.Double(200, 0);

		GPSDatum swCorner = new GPSDatum(centerPoint.getLat() - .04, centerPoint.getLon() - .04);
		Point2D.Double lowerLeft = new Point2D.Double(0, 200);

		visualRect = new Rectangle2D.Double(lowerLeft.x, upperRight.y, Point2D.distance(upperRight.x, 0, lowerLeft.x, 0),
				Point2D.distance(0, upperRight.y, 0, lowerLeft.y));

		try
		{
			visualizerProjection = new PlateCarreeProjection(neCorner, swCorner, upperRight, lowerLeft,
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
			g2.drawLine((int) (loc.getX()), (int) (loc.getY()), 100, 100);
		}

		g2.setColor(Color.RED);
		
		GPSDatum temp = new GPSDatum(this.centerPoint.getLat(), this.centerPoint.getLon());
		
		for (int i = 0; i < 100; i++)
		{
			temp.setLat(temp.getLat() + .001);
			temp.setLon(temp.getLon() + .001);
			
			
			Point2D tempP = this.visualizerProjection.projectIntoVirtual(temp);

			g2.fillOval((int)tempP.getX(), (int)tempP.getY(), 1, 1);
		}
		
		this.paintCorners(g2);

		g2.setFont(new Font("Arial", Font.BOLD, 10));
		g2.drawString("o: " + this.centerPoint.getLon() + ", " + centerPoint.getLat(), 0, 10);
		g2.drawString("c: " + this.currentPosition.getLon() + ", " + this.currentPosition.getLat(), 0, 20);
	}

	private void paintCorners(Graphics2D g2)
	{
		Point2D ne = this.visualizerProjection.projectIntoVirtual(this.currentProjection.getPhysicalWorldPointNE());
		Point2D sw = this.visualizerProjection.projectIntoVirtual(this.currentProjection.getPhysicalWorldPointSW());

		g2.setColor(Color.GRAY);

		g2.drawLine((int) ne.getX(), (int) ne.getY(), (int) ne.getX() - 5, (int) ne.getY());
		g2.drawLine((int) ne.getX(), (int) ne.getY(), (int) ne.getX(), (int) ne.getY() + 5);

		g2.drawLine((int) sw.getX(), (int) sw.getY(), (int) sw.getX() + 5, (int) sw.getY());
		g2.drawLine((int) sw.getX(), (int) sw.getY(), (int) sw.getX(), (int) sw.getY() - 5);
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
