/**
 * 
 */
package ecologylab.projection;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import ecologylab.generic.Debug;
import ecologylab.sensor.gps.data.GPSDatum;

/**
 * Using two GPSDatum objects, demarking two corners of real-world space, provides functionality to map real-world
 * coordinates to an arbitrary coordinate system and vice-versa. Uses a Plate Carree projection.
 * 
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 * 
 */
public abstract class Projection extends Debug
{
	/**
	 * The normalized northwest-most physical world point for this mapping; supplied coordinates will automatically be
	 * transformed to match this specification; that is, if the points are essentially NE and SW, then this point will be
	 * computed using the north-most and west-most components of those two coordinates.
	 */
	protected GPSDatum					physicalWorldPointNE				= null;

	/**
	 * The normalized southest-most physical world point for this mapping; supplied coordinates will automatically be
	 * transformed to match this specification; that is, if the points are essentially NE and SW, then this point will be
	 * computed using the south-most and east-most components of those two coordinates.
	 */
	protected GPSDatum					physicalWorldPointSW				= null;

	/**
	 * The upper-left-most point in the virtual world, which will be, more or less, mapped to physicalWorldPointNE based
	 * on the MappingMode for this object.
	 */
	protected Point2D.Double			virtualWorldPointUpperRight	= null;

	/**
	 * The lower-right-most point in the virtual world, which will be, more or less, mapped to physicalWorldPointSW based
	 * on the MappingMode for this object.
	 */
	protected Point2D.Double			virtualWorldPointLowerLeft		= null;

	protected RotationConstraintMode	rotConstMode;

	protected double						virtualWorldWidth, virtualWorldHeight;

	protected double						realWorldWidth, realWorldHeight;

	protected double						aspectRatio;

	/**
	 * The matrix that performs the transformation from real-world coordinates to virtual world coordinates.
	 */
	protected AffineTransform			transformMatrix					= new AffineTransform();

	public enum RotationConstraintMode
	{
		/**
		 * Indicates that the virtual world rectangle is to be overlaid parallel to the cardinal directions, so that the
		 * upper-left coordinate is the northwestern-most coordinate and the lower-right is the southeastern-most. This
		 * mode places the physical world coordinates along the edges of the projection, most likely NOT lining up the
		 * corners (unless the physical world coordinates, when used to define a rectangle, do so with the same aspect
		 * ratio as the virtual world.
		 */
		CARDINAL_DIRECTIONS,

		/**
		 * Indicates that the virtual world should be rotated so that it's defined verticies match the real world ones,
		 * scaling the virtual world so that it will fit between the points.
		 */
		ANCHOR_POINTS;
	}

	public Projection(GPSDatum physicalWorldPoint1, GPSDatum physicalWorldPoint2,
			Point2D.Double virtualWorldPointUpperLeft, Point2D.Double virtualWorldPointLowerRight,
			RotationConstraintMode rotConstMode) throws SameCoordinatesException
	{
		setVirtualWorldPointsOnly(virtualWorldPointUpperLeft, virtualWorldPointLowerRight);

		setRotationConstraintMode(rotConstMode);

		setPhysicalWorldCoordinatesOnly(physicalWorldPoint1, physicalWorldPoint2);

		reconfigure();
		
		debug("corners set: ");
		debug("NE: " + this.physicalWorldPointNE.toString());
		debug("SW: " + this.physicalWorldPointSW.toString());
	}

	/**
	 * @param rotConstMode
	 */
	private void setRotationConstraintMode(RotationConstraintMode rotConstMode)
	{
		this.rotConstMode = rotConstMode;
	}

	/**
	 * @param virtualWorldPointUpperLeft
	 * @param virtualWorldPointLowerRight
	 * @throws SameCoordinatesException
	 */
	public void setVirtualWorldPoints(Point2D.Double virtualWorldPointUpperLeft,
			Point2D.Double virtualWorldPointLowerRight) throws SameCoordinatesException
	{
		setVirtualWorldPointsOnly(virtualWorldPointUpperLeft, virtualWorldPointLowerRight);
		
		reconfigure();
	}

	/**
	 * @param virtualWorldPointUpperLeft
	 * @param virtualWorldPointLowerRight
	 * @throws SameCoordinatesException
	 */
	protected void setVirtualWorldPointsOnly(Point2D.Double virtualWorldPointUpperLeft,
			Point2D.Double virtualWorldPointLowerRight) throws SameCoordinatesException
	{
		this.virtualWorldPointUpperRight = virtualWorldPointUpperLeft;
		this.virtualWorldPointLowerLeft = virtualWorldPointLowerRight;

		if (this.virtualWorldPointLowerLeft.x == this.virtualWorldPointUpperRight.x
				|| this.virtualWorldPointLowerLeft.y == this.virtualWorldPointUpperRight.y)
		{
			throw new SameCoordinatesException("Virtual world space must have an area.");
		}
	}

	/**
	 * @param physicalWorldPoint1
	 * @param physicalWorldPoint2
	 * @throws SameCoordinatesException
	 */
	public void setPhysicalWorldCoordinates(GPSDatum physicalWorldPoint1, GPSDatum physicalWorldPoint2)
			throws SameCoordinatesException
	{
		setPhysicalWorldCoordinatesOnly(physicalWorldPoint1, physicalWorldPoint2);

		reconfigure();
	}

	/**
	 * 
	 */
	protected void reconfigure()
	{
		switch (this.rotConstMode)
		{
		case CARDINAL_DIRECTIONS:
			// we need to redfine NE and SW so that they correspond to a rectangle with the same aspect ratio as the two
			// virtual world coordinates

			redefineRealWorldCoordinatesForAspectRatio();

			break;
		case ANCHOR_POINTS:
			// TODO
			break;
		}

		this.configureTransformMatrix();
	}

	/**
	 * @param physicalWorldPoint1
	 * @param physicalWorldPoint2
	 * @throws SameCoordinatesException
	 */
	protected void setPhysicalWorldCoordinatesOnly(GPSDatum physicalWorldPoint1, GPSDatum physicalWorldPoint2)
			throws SameCoordinatesException
	{
		this.physicalWorldPointNE = new GPSDatum();
		this.physicalWorldPointSW = new GPSDatum();

		// now figure out which lat/lon to use for the NW and SE points, based on physicalWorldPoint1 and 2
		if (physicalWorldPoint1.compareNS(physicalWorldPoint2) > 0)
		{ // point 1 is north of point 2
			this.physicalWorldPointNE.setLat(physicalWorldPoint1.getLat());

			this.physicalWorldPointSW.setLat(physicalWorldPoint2.getLat());
		}
		else if (physicalWorldPoint1.compareNS(physicalWorldPoint2) < 0)
		{ // point 1 is south of point 2
			this.physicalWorldPointNE.setLat(physicalWorldPoint2.getLat());

			this.physicalWorldPointSW.setLat(physicalWorldPoint1.getLat());
		}
		else
		{ // points overlap
			throw new SameCoordinatesException("Real world space must have an area.");
		}

		if (physicalWorldPoint1.compareEW(physicalWorldPoint2) > 0)
		{ // point 1 is east of point 2
			this.physicalWorldPointNE.setLon(physicalWorldPoint1.getLon());

			this.physicalWorldPointSW.setLon(physicalWorldPoint2.getLon());
		}
		else if (physicalWorldPoint1.compareEW(physicalWorldPoint2) < 0)
		{ // point 1 is west of point 2
			this.physicalWorldPointNE.setLon(physicalWorldPoint2.getLon());

			this.physicalWorldPointSW.setLon(physicalWorldPoint1.getLon());
		}
		else
		{ // points overlap
			throw new SameCoordinatesException("Real world space must have an area.");
		}
		
		this.virtualWorldWidth = Point2D.Double.distance(this.virtualWorldPointUpperRight.getX(), 0,
				this.virtualWorldPointLowerLeft.getX(), 0);
		this.virtualWorldHeight = Point2D.Double.distance(0, this.virtualWorldPointUpperRight.getY(), 0,
				this.virtualWorldPointLowerLeft.getY());

		this.aspectRatio = this.virtualWorldWidth / this.virtualWorldHeight;
	}
	
	/**
	 * 
	 */
	protected abstract void redefineRealWorldCoordinatesForAspectRatio();

	/**
	 * This method is called automatically by the constructor, and by any method that will change the set of real world
	 * points.
	 * 
	 * This method must configure the internal AffineTransform object (transformMatrix) so that calls to project can use
	 * it to properly map real world coordinates to virtual world coordinates, and vice versa.
	 */
	protected abstract void configureTransformMatrix();

	/**
	 * Projects the given GPSDatum's coordinates into the virtual space, using some type of projection.
	 * 
	 * Subclasses may override this method, if they are not making affine transformations.
	 * 
	 * @param origPoint
	 * @return a new Point2D.Double containing the virtual space point for origPoint.
	 */
	public Point2D project(GPSDatum origPoint)
	{
		return this.project(origPoint, null);
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
	public Point2D project(GPSDatum origPoint, Point2D.Double destPoint)
	{
		return this.transformMatrix.transform(origPoint.getPointRepresentation(), destPoint);
	}
}
