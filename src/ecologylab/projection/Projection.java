/**
 * 
 */
package ecologylab.projection;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import ecologylab.generic.Debug;
import ecologylab.sensor.location.gps.data.GPSConstants;
import ecologylab.sensor.location.gps.data.GeoCoordinate;

/**
 * Using two GeoCoordinate objects, demarking two corners of real-world space, provides functionality to map real-world
 * coordinates to another (virtual) set of coordinates within a rectangle specified by its size.
 * 
 * The center of the virtual world is considered to be at the center of the two specified points. The angle at which it
 * is laid out depends on the projection mode.
 * 
 * CARDINAL_DIRECTIONS mode
 * 
 * CARDINAL_DIRECTIONS mode aligns the virtual world with the cardinal directions of the compass. It's height dimension
 * specifies its N/S length and its width dimension specifies its E/W length. In this mode, the two real world
 * coordinates are used to create a bounding box which contains the virtual world and scales it depending upon its
 * aspect ratio, so that it is always contained by the bounding box.
 * 
 * ANCHOR_POINTS mode
 * 
 * ANCHOR_POINTS mode rotates the virtual world, so that the midpoints of its top and bottom verticies are anchored to
 * the two specified GPS coordinates.
 * 
 * A projection's virtual world scale may be specified in one of two ways:
 * 
 * CONSTANT_SCALE_FACTOR mode
 * 
 * In this mode, a constant factor is used to scale the virtual world to the real world, specified as the ratio of
 * points (virtual space) : some real world measure (real space). In this mode, the size of the virtual world changes
 * depending on the specified real world space. The real-world measure must be specified by subclasses. It may be
 * degrees of latitude/longitude, real world distance, etc.
 * 
 * CONSTANT_SIZE mode
 * 
 * In this mode, the virtual world is held at a constant, specified size (width/height) and the scale between it and the
 * real world changes, depending on the other specifications.
 * 
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 * 
 */
public abstract class Projection extends Debug
{
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

	public enum ScaleConstraintMode
	{
		CONSTANT_SCALE_FACTOR, CONSTANT_SIZE
	}

	/**
	 * The normalized northwest-most physical world point for this mapping; supplied coordinates will automatically be
	 * transformed to match this specification; that is, if the points are essentially NE and SW, then this point will be
	 * computed using the north-most and west-most components of those two coordinates.
	 */
	protected GeoCoordinate					physicalWorldPointNE	= null;

	/**
	 * The normalized southest-most physical world point for this mapping; supplied coordinates will automatically be
	 * transformed to match this specification; that is, if the points are essentially NE and SW, then this point will be
	 * computed using the south-most and east-most components of those two coordinates.
	 */
	protected GeoCoordinate					physicalWorldPointSW	= null;

	protected GeoCoordinate					specedPWP1				= null;

	protected GeoCoordinate					specedPWP2				= null;

	protected RotationConstraintMode	rotConstMode;

	protected ScaleConstraintMode		scaleConstMode;

	protected double						virtualWorldWidth, virtualWorldHeight;

	protected double						realWorldWidth, realWorldHeight;

	protected double						aspectRatio;

	/**
	 * Constructs a new Projection using the specified rotation constraint mode and using constant size scale mode.
	 * 
	 * @param physicalWorldPoint1
	 * @param physicalWorldPoint2
	 * @param virtualWorldWidth
	 * @param virtualWorldHeight
	 * @param rotConstMode
	 * @throws SameCoordinatesException
	 */
	public Projection(GeoCoordinate physicalWorldPoint1, GeoCoordinate physicalWorldPoint2, double virtualWorldWidth,
			double virtualWorldHeight, RotationConstraintMode rotConstMode) throws SameCoordinatesException
	{
		this(physicalWorldPoint1, physicalWorldPoint2, virtualWorldWidth, virtualWorldHeight, 0.0, rotConstMode,
				ScaleConstraintMode.CONSTANT_SIZE);
	}

	public Projection(GeoCoordinate physicalWorldPoint1, GeoCoordinate physicalWorldPoint2, double scaleFactor,
			RotationConstraintMode rotConstMode) throws SameCoordinatesException
	{
		this(physicalWorldPoint1, physicalWorldPoint2, 0.0, 0.0, scaleFactor, rotConstMode,
				ScaleConstraintMode.CONSTANT_SCALE_FACTOR);
	}

	/**
	 * Catch-all constructor that is called by all the other constructors.
	 * 
	 * @param physicalWorldPoint1
	 * @param physicalWorldPoint2
	 * @param virtualWorldWidth
	 * @param virtualWorldHeight
	 * @param scaleFactor
	 * @param rotConstMode
	 * @param scaleConstMode
	 * @throws SameCoordinatesException
	 */
	protected Projection(GeoCoordinate physicalWorldPoint1, GeoCoordinate physicalWorldPoint2, double virtualWorldWidth,
			double virtualWorldHeight, double scaleFactor, RotationConstraintMode rotConstMode,
			ScaleConstraintMode scaleConstMode) throws SameCoordinatesException
	{
		setVirtualWorldSizeOnly(virtualWorldWidth, virtualWorldHeight);

		setRotConstModeOnly(rotConstMode);

		setScaleConstModeOnly(scaleConstMode);

		setPhysicalWorldCoordinatesOnly(physicalWorldPoint1, physicalWorldPoint2);

		configure();

		debug("corners set: ");
		debug("NE: " + this.physicalWorldPointNE.toString());
		debug("SW: " + this.physicalWorldPointSW.toString());

		debug("virtual world size: ");
		debug("width: " + this.virtualWorldWidth);
		debug("height: " + this.virtualWorldHeight);

		debug("real world size: ");
		debug("width: " + this.realWorldWidth);
		debug("height: " + this.realWorldHeight);
	}

	/**
	 * @return the physicalWorldPointNE
	 */
	public GeoCoordinate getPhysicalWorldPointNE()
	{
		return physicalWorldPointNE;
	}

	/**
	 * @return the physicalWorldPointSW
	 */
	public GeoCoordinate getPhysicalWorldPointSW()
	{
		return physicalWorldPointSW;
	}

	/**
	 * Compute (if necessary) the scale factor between virtual world and real world units. Should be virtual points :
	 * real world measure. The real world measure is dependent on the subclass.
	 * 
	 * @return the scale factor between the virtual world and the real world.
	 */
	public abstract double getScale();

	/**
	 * @return the virtualWorldHeight
	 */
	public double getVirtualWorldHeight()
	{
		return virtualWorldHeight;
	}

	/**
	 * @return the virtualWorldWidth
	 */
	public double getVirtualWorldWidth()
	{
		return virtualWorldWidth;
	}

	public final GeoCoordinate projectIntoReal(Point2D.Double origPoint)
	{
		return this.projectIntoReal(origPoint, null);
	}

	public final GeoCoordinate projectIntoReal(Point2D.Double origPoint, GeoCoordinate destDatum)
	{
		if (destDatum == null)
		{
			destDatum = new GeoCoordinate();
		}

		return this.projectIntoRealImpl(origPoint, destDatum);
	}

	/**
	 * Projects the given GeoCoordinate's coordinates into the virtual space, using some type of projection.
	 * 
	 * Subclasses may override this method, if they are not making affine transformations.
	 * 
	 * @param origDatum
	 * @return a new Point2D.Double containing the virtual space point for origPoint.
	 */
	public final Point2D.Double projectIntoVirtual(GeoCoordinate origDatum)
	{
		return this.projectIntoVirtual(origDatum, null);
	}

	/**
	 * Projects the given GeoCoordinate's coordinates into the virtual space, using some type of projection.
	 * 
	 * This version takes an instantiated Point2D, so as not to expend resources instantating a new one.
	 * 
	 * Subclasses may override this method, if they are not making affine transformations.
	 * 
	 * @param origDatum
	 * @param destPoint
	 * @return destPoint containing the virtual space point for origPoint.
	 */
	public final Point2D.Double projectIntoVirtual(GeoCoordinate origDatum, Point2D.Double destPoint)
	{
		if (destPoint == null)
		{
			destPoint = new Point2D.Double();
		}

		return this.projectIntoVirtualImpl(origDatum, destPoint);
	}

	/**
	 * @param physicalWorldPoint1
	 * @param physicalWorldPoint2
	 * @throws SameCoordinatesException
	 */
	public final void setPhysicalWorldCoordinates(GeoCoordinate physicalWorldPoint1, GeoCoordinate physicalWorldPoint2)
			throws SameCoordinatesException
	{
		setPhysicalWorldCoordinatesOnly(physicalWorldPoint1, physicalWorldPoint2);

		configure();
	}

	/**
	 * @param rotConstMode
	 */
	public final void setRotConstMode(RotationConstraintMode rotConstMode)
	{
		setRotConstModeOnly(rotConstMode);

		configure();
	}

	/**
	 * @param scaleConstMode
	 *           the scaleConstMode to set
	 */
	public final void setScaleConstMode(ScaleConstraintMode scaleConstMode)
	{
		setScaleConstModeOnly(scaleConstMode);

		configure();
	}

	/**
	 * @param virtualWorldPointUpperLeft
	 * @param virtualWorldPointLowerRight
	 * @throws SameCoordinatesException
	 */
	public final void setVirtualWorldPoints(double virtualWorldWidth, double virtualWorldHeight)
			throws SameCoordinatesException
	{
		setVirtualWorldSizeOnly(virtualWorldWidth, virtualWorldHeight);

		configure();
	}

	/**
	 * Adjusts internal variables to match the modes, based on the current parameters. This method should be called
	 * whenever any parameters are changed.
	 * 
	 * This method is automatically called by the public setter methods.
	 */
	protected abstract void configure();

	/**
	 * This method does the real work of projectIntoReal; all calls to it are guaranteed to pass an instantiated GeoCoordinate
	 * object.
	 * 
	 * @param destPoint
	 * @param destDatum
	 * @return
	 */
	protected abstract GeoCoordinate projectIntoRealImpl(Point2D.Double destPoint, GeoCoordinate destDatum);

	/**
	 * This method does the real work of projectIntoVirtual; all calls to it are guaranteed to pass an instantiated
	 * Point2D.Double object.
	 * 
	 * @param origDatum
	 * @param destPoint
	 * @return
	 */
	protected abstract Point2D.Double projectIntoVirtualImpl(GeoCoordinate origDatum, Point2D.Double destPoint);

	/**
	 * @param physicalWorldPoint1
	 * @param physicalWorldPoint2
	 * @throws SameCoordinatesException
	 */
	protected void setPhysicalWorldCoordinatesOnly(GeoCoordinate physicalWorldPoint1, GeoCoordinate physicalWorldPoint2)
			throws SameCoordinatesException
	{
		this.specedPWP1 = physicalWorldPoint1;
		this.specedPWP2 = physicalWorldPoint2;

		this.physicalWorldPointNE = new GeoCoordinate();
		this.physicalWorldPointSW = new GeoCoordinate();

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

		this.realWorldWidth = Point2D.distance(this.physicalWorldPointNE.getLon(), 0, this.physicalWorldPointSW.getLon(),
				0);
		this.realWorldHeight = Point2D.distance(0, this.physicalWorldPointNE.getLat(), 0, this.physicalWorldPointSW
				.getLat());
	}

	/**
	 * @param rotConstMode
	 */
	protected void setRotConstModeOnly(RotationConstraintMode rotConstMode)
	{
		this.rotConstMode = rotConstMode;
	}

	/**
	 * @param scaleConstMode
	 */
	protected void setScaleConstModeOnly(ScaleConstraintMode scaleConstMode)
	{
		this.scaleConstMode = scaleConstMode;
	}

	/**
	 * @param virtualWorldPointUpperLeft
	 * @param virtualWorldPointLowerRight
	 * @throws SameCoordinatesException
	 */
	protected void setVirtualWorldSizeOnly(double virtualWorldWidth, double virtualWorldHeight)
			throws SameCoordinatesException
	{
		this.virtualWorldWidth = virtualWorldWidth;
		this.virtualWorldHeight = virtualWorldHeight;

		if (this.virtualWorldHeight == 0 || this.virtualWorldWidth == 0)
		{
			throw new SameCoordinatesException("Virtual world space must have an area.");
		}

		this.aspectRatio = this.virtualWorldWidth / this.virtualWorldHeight;
	}
}
