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
 * Essentially, any north-south movement will take place directly along a great circle, any east-west movement will run
 * along a parallel. Distortion will come into play in that as a person moves further north, EW movement in the virtual
 * world will be accelerated: one step east or west will cover more "ground" at high and low latitudes.
 * 
 * Note that, although it is somewhat confusing, this projection will map north to negative Y (because in Java, negative
 * Y is UP) and east to positive X.
 * 
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 * 
 */
public class PlateCarreeProjection extends Projection
{
	/**
	 * @param physicalWorldPoint1
	 * @param physicalWorldPoint2
	 * @param virtualWorldPointUpperRight -
	 *           the upper right of this coordinate system; normally, this will be the point with the greatest X and
	 *           least Y.
	 * @param virtualWorldPointLowerLeft -
	 *           the lower left of the target coordinate system; normally, this will be the point with the least X and
	 *           the greatest Y.
	 * @param rotConstMode
	 * @throws SameCoordinatesException
	 */
	public PlateCarreeProjection(GPSDatum physicalWorldPoint1, GPSDatum physicalWorldPoint2,
			Double virtualWorldPointUpperRight, Double virtualWorldPointLowerLeft, RotationConstraintMode rotConstMode)
			throws SameCoordinatesException
	{
		super(physicalWorldPoint1, physicalWorldPoint2, virtualWorldPointUpperRight, virtualWorldPointLowerLeft,
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

			this.physicalWorldPointNE.setLon(this.physicalWorldPointNE.getLon() - targetWidthAdjustment);
			this.physicalWorldPointSW.setLon(this.physicalWorldPointSW.getLon() + targetWidthAdjustment);
		}
		else
		{ // make the real world shorter
			double targetHeightAdjustment = .5 * realWorldHeight * (1 - (origRealWorldAspectRatio / this.aspectRatio));

			this.physicalWorldPointNE.setLat(this.physicalWorldPointNE.getLat() - targetHeightAdjustment);
			this.physicalWorldPointSW.setLat(this.physicalWorldPointSW.getLat() + targetHeightAdjustment);
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

			scaleFactorX = 1.0 / scaleFactorX;
			scaleFactorY = -1.0 / scaleFactorY;

			double centerRealX = this.physicalWorldPointNE.getLon() - (this.realWorldWidth / 2.0);
			double centerRealY = this.physicalWorldPointNE.getLat() - (this.realWorldHeight / 2.0);

			double centerVirtualX = this.virtualWorldPointUpperRight.x - (this.virtualWorldWidth / 2.0);
			double centerVirtualY = this.virtualWorldPointUpperRight.y + (this.virtualWorldHeight / 2.0);

			double translateToOriginX = Point2D.distance(centerRealX, 0, 0, 0) * (centerRealX > 0 ? -1.0 : 1.0);
			double translateToOriginY = Point2D.distance(0, centerRealY, 0, 0) * (centerRealY > 0 ? -1.0 : 1.0);

			double translateToCenterX = Point2D.distance(centerVirtualX, 0, 0, 0) * (centerVirtualX < 0 ? -1.0 : 1.0);
			double translateToCenterY = Point2D.distance(0, centerVirtualY, 0, 0) * (centerVirtualY < 0 ? -1.0 : 1.0);

			debug("distance to origin: " + centerRealX + ", " + centerRealY);
			debug("distance to center of virtual field: " + centerVirtualX + ", " + centerVirtualY);

			this.transformMatrix.setToIdentity();
			this.transformMatrix.translate(translateToCenterX, translateToCenterY);
			this.transformMatrix.scale(scaleFactorX, scaleFactorY);
			this.transformMatrix.translate(translateToOriginX, translateToOriginY);

			break;
		case ANCHOR_POINTS:
			// TODO
			break;
		}
	}

	public static void main(String[] args) throws SameCoordinatesException
	{
		PlateCarreeProjection p = new PlateCarreeProjection(new GPSDatum(0, -100), new GPSDatum(30, -70),
				new Point2D.Double(200, 0), new Point2D.Double(0, 200),
				Projection.RotationConstraintMode.CARDINAL_DIRECTIONS);

		GPSDatum[] ds =
		{ new GPSDatum(15, -85), new GPSDatum(0, -100), new GPSDatum(30, -70), new GPSDatum(29.95, -95.67) };

		for (GPSDatum d : ds)
		{
			Point2D.Double transformedPoint = (Double) p.projectIntoVirtual(d);

			System.out.print(d.getLon() + ", " + d.getLat());
			System.out.print(" > ");
			System.out.println(transformedPoint.x + ", " + transformedPoint.y);
			// System.out.print(" > ");

			// Point2D.Double transformedBackPoint = (Double) p.projectIntoReal(transformedPoint);
			// System.out.println(transformedBackPoint.x + ", " + transformedBackPoint.y);
		}

	}
}
