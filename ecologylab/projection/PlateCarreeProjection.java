/**
 * 
 */
package ecologylab.projection;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double ;

import ecologylab.projection.Projection.RotationConstraintMode;
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
 * PlateCarreeProjection sets scale in terms of virtual world points : degrees latitude/longitude.
 * 
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 * 
 */
public class PlateCarreeProjection extends Projection
{
	/**
	 * The matrix that performs the transformation from real-world coordinates to virtual world coordinates.
	 */
	protected AffineTransform	transformMatrix;

	private double					scaleFactor;

	public PlateCarreeProjection(GPSDatum physicalWorldPoint1, GPSDatum physicalWorldPoint2, double scaleFactor,
			RotationConstraintMode rotConstMode) throws SameCoordinatesException
	{
		super(physicalWorldPoint1, physicalWorldPoint2, scaleFactor, rotConstMode);

		debug("Using plate carree projection.");
	}

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
	public PlateCarreeProjection(GPSDatum physicalWorldPoint1, GPSDatum physicalWorldPoint2, double virtualWorldWidth,
			double virtualWorldHeight, RotationConstraintMode rotConstMode) throws SameCoordinatesException
	{
		super(physicalWorldPoint1, physicalWorldPoint2, virtualWorldWidth, virtualWorldHeight, rotConstMode);

		debug("Using plate carree projection.");
	}

	public static void main(String[] args) throws SameCoordinatesException
	{
		PlateCarreeProjection p = new PlateCarreeProjection(new GPSDatum(0, -100), new GPSDatum(30, -70), 400, 200,
				Projection.RotationConstraintMode.CARDINAL_DIRECTIONS);

		GPSDatum[] ds =
		{ new GPSDatum(15, -85), new GPSDatum(0, -100), new GPSDatum(30, -70), new GPSDatum(29.95, -95.67) };

		for (GPSDatum d : ds)
		{
			Point2D.Double  transformedPoint = (Double) p.projectIntoVirtual(d);

			System.out.print(d.getLon() + ", " + d.getLat());
			System.out.print(" > ");
			System.out.println(transformedPoint.x + ", " + transformedPoint.y);

			// System.out.print(" > ");
			// Point2D.Double  transformedBackPoint = (Double) p.projectIntoReal(transformedPoint);
			// System.out.println(transformedBackPoint.x + ", " + transformedBackPoint.y);
		}

	}

	/**
	 * @see ecologylab.projection.Projection#getScale()
	 */
	@Override public double getScale()
	{
		return this.scaleFactor;
	}

	/**
	 * Adjusts internal variables to match the modes, based on the current parameters. This method should be called
	 * whenever any states are changed.
	 */
	@Override protected void configure()
	{
		if (this.transformMatrix == null)
		{
			this.transformMatrix = new AffineTransform();
		}
		
		switch (this.rotConstMode)
		{
		case CARDINAL_DIRECTIONS:
			// we need to redfine NE and SW so that they correspond to a rectangle with the same aspect ratio as the two
			// virtual world coordinates

			switch (this.scaleConstMode)
			{
			case CONSTANT_SCALE_FACTOR:
				// TODO
				break;
			case CONSTANT_SIZE:
				// we need to maintain a constant size and aspect ratio for the virtual world
				
				// first determine the scaling factor
				// need the longest dimension of the virtual world
				if (this.virtualWorldHeight > this.virtualWorldWidth)
				{ // then our "bounding box" specified by the real world coordinates is constraining us on height more than width
					this.scaleFactor = this.virtualWorldHeight / this.realWorldHeight;
				}
				else
				{
					this.scaleFactor = this.virtualWorldWidth / this.realWorldWidth;
				}

				double centerRealX = this.physicalWorldPointNE.getLon() - (this.realWorldWidth / 2.0);
				double centerRealY = this.physicalWorldPointNE.getLat() - (this.realWorldHeight / 2.0);

				double translateToOriginX = Point2D.distance(centerRealX, 0, 0, 0) * (centerRealX > 0 ? -1.0 : 1.0);
				double translateToOriginY = Point2D.distance(0, centerRealY, 0, 0) * (centerRealY > 0 ? -1.0 : 1.0);

				debug("distance to origin: " + centerRealX + ", " + centerRealY);

				this.transformMatrix.setToIdentity();

				this.transformMatrix.scale(scaleFactor, -1.0 * scaleFactor);
				this.transformMatrix.translate(translateToOriginX, translateToOriginY);

				break;
			}

			break;
		case ANCHOR_POINTS:
			// TODO
			break;
		}
	}

	/**
	 * Projects the given GPSDatum's coordinates into the virtual space, using some type of projection.
	 * 
	 * This version takes an instantiated Point2D, so as not to expend resources instantating a new one.
	 * 
	 * Subclasses may override this method, if they are not making affine transformations.
	 * 
	 * @param origPoint
	 * @param destPoint
	 * @return destPoint containing the virtual space point for origPoint.
	 */
	@Override protected Point2D.Double projectIntoVirtualImpl(GPSDatum origPoint, Point2D.Double destPoint)
	{
		return (Double) this.transformMatrix.transform(origPoint.getPointRepresentation(), destPoint);
	}

	@Override protected GPSDatum projectIntoRealImpl(Point2D.Double origPoint, GPSDatum destDatum)
	{
		try
		{
			Point2D.Double inversePoint = (Double) this.transformMatrix.inverseTransform(origPoint, null);
			destDatum.setLon(inversePoint.getX());
			destDatum.setLat(inversePoint.getY());
		}
		catch (NoninvertibleTransformException e)
		{
			e.printStackTrace();
		}

		return destDatum;
	}

	/**
	 * @return the transformMatrix
	 */
	public AffineTransform getTransformMatrix()
	{
		return transformMatrix;
	}
}
