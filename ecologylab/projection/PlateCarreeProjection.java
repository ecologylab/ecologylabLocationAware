/**
 * 
 */
package ecologylab.projection;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import ecologylab.sensor.gps.data.GPSDatum;

/**
 * A plate carree projection directly maps X coordinates to longitude and Y coordinates to latitude. This projection is
 * fast, but will only generally be effective for small spaces and/or near the equator. Otherwise, the distortion
 * created by this mapping may be problematic.
 * 
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 * 
 */
public class PlateCarreeProjection extends Projection
{
	/**
	 * @param physicalWorldPoint1
	 * @param physicalWorldPoint2
	 * @param virtualWorldPointUpperLeft
	 * @param virtualWorldPointLowerRight
	 * @param rotConstMode
	 * @throws SameCoordinatesException
	 */
	public PlateCarreeProjection(GPSDatum physicalWorldPoint1, GPSDatum physicalWorldPoint2,
			Double virtualWorldPointUpperLeft, Double virtualWorldPointLowerRight, RotationConstraintMode rotConstMode)
			throws SameCoordinatesException
	{
		super(physicalWorldPoint1, physicalWorldPoint2, virtualWorldPointUpperLeft, virtualWorldPointLowerRight,
				rotConstMode);

		debug("Using plate carree projection.");
	}

	/**
	 * We map latitude to Y and longitude to X, assuming the same distance between each degree of lat/lon.
	 * 
	 * @see ecologylab.projection.Projection#redefineRealWorldCoordinatesForAspectRatio()
	 */
	@Override protected void redefineRealWorldCoordinatesForAspectRatio()
	{
		debug("redefining real world coordinate corners.");

		this.realWorldHeight = this.physicalWorldPointNE.getLat() - this.physicalWorldPointSW.getLat();
		this.realWorldWidth = this.physicalWorldPointNE.getLon() - this.physicalWorldPointSW.getLon();

		double origRealWorldAspectRatio = this.realWorldWidth / this.realWorldHeight;

		if (origRealWorldAspectRatio > this.aspectRatio)
		{ // orig space is too wide, we need to shrink it (making it taller could make it go around the world...then bad
			// things happen :( )

			double targetWidthAdjustment = .5 * realWorldWidth * (1 - (this.aspectRatio / origRealWorldAspectRatio));

			this.physicalWorldPointNE.setLon(this.physicalWorldPointNE.getLon() - (targetWidthAdjustment));
			this.physicalWorldPointSW.setLon(this.physicalWorldPointSW.getLon() + (targetWidthAdjustment));
		}
		else
		{ // make the real world shorter
			double targetHeightAdjustment = .5 * realWorldHeight * (1 - (origRealWorldAspectRatio / this.aspectRatio));

			this.physicalWorldPointNE.setLat(this.physicalWorldPointNE.getLat() - (targetHeightAdjustment));
			this.physicalWorldPointSW.setLat(this.physicalWorldPointSW.getLat() + (targetHeightAdjustment));
		}

		this.realWorldHeight = this.physicalWorldPointNE.getLat() - this.physicalWorldPointSW.getLat();
		this.realWorldWidth = this.physicalWorldPointNE.getLon() - this.physicalWorldPointSW.getLon();

		origRealWorldAspectRatio = this.realWorldWidth / this.realWorldHeight;

		debug("corner points adjusted: ");
		debug("NE: " + this.physicalWorldPointNE.toString());
		debug("SW: " + this.physicalWorldPointSW.toString());

		debug("target aspect ratio: " + this.aspectRatio);
		debug("new aspect ratio: " + origRealWorldAspectRatio);
	}

	/**
	 * @see ecologylab.projection.Projection#configureTransformMatrix()
	 */
	@Override protected void configureTransformMatrix()
	{
		switch (this.rotConstMode)
		{
		case CARDINAL_DIRECTIONS:
			// we're just dealing with a scaling / translation issue here
			double scaleFactorX = this.realWorldWidth / this.virtualWorldWidth;
			double scaleFactorY = this.realWorldHeight / this.virtualWorldHeight;
			
			scaleFactorX = -1.0/scaleFactorX;
			scaleFactorY = -1.0/scaleFactorY;
			
			double scaledX = this.physicalWorldPointNE.getLon() * 1.0;
			double translateX = Point2D.distance(scaledX, 0, this.virtualWorldPointUpperRight.x, 0)
					* (scaledX > this.virtualWorldPointUpperRight.x ? -1.0 : 1.0);

			double scaledY = this.physicalWorldPointNE.getLat() * 1.0;
			double translateY = Point2D.distance(scaledY, 0, this.virtualWorldPointUpperRight.y, 0)
					* (scaledY > this.virtualWorldPointUpperRight.y ? -1.0 : 1.0);

			this.transformMatrix.setToIdentity();
			this.transformMatrix.scale(scaleFactorX, scaleFactorY);
			this.transformMatrix.translate(translateX, translateY);
			break;
		case ANCHOR_POINTS:
			// TODO
			break;
		}
	}

	public static void main(String[] args) throws SameCoordinatesException
	{
		PlateCarreeProjection p = new PlateCarreeProjection(new GPSDatum(28, -100), new GPSDatum(32,
				-80), new Point2D.Double(0, 0), new Point2D.Double(1, 2),
				Projection.RotationConstraintMode.CARDINAL_DIRECTIONS);
		
		GPSDatum[] ds = { new GPSDatum(30, -90), new GPSDatum(28, -100), new GPSDatum(28, -90), new GPSDatum(28, -91) };
		
		for (GPSDatum d : ds)
		{
			Point2D.Double transformedPoint = (Double) p.projectIntoVirtual(d);
			
			System.out.println(d.getLon() + ", " + d.getLat());
			System.out.println("  v  ");
			System.out.println(transformedPoint.x + ", "+ transformedPoint.y);
			System.out.println("  v");
			
			Point2D.Double transformedBackPoint = (Double) p.projectIntoReal(transformedPoint);
			System.out.println(transformedBackPoint.x +", "+transformedBackPoint.y);
		}
		

	}
}
