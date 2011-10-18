/**
 * 
 */
package ecologylab.projection;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Scanner;

import ecologylab.sensor.location.gps.data.GeoCoordinate;
import ecologylab.serialization.SIMPLTranslationException;
import ecologylab.serialization.SimplTypesScope;
import ecologylab.serialization.formatenums.StringFormat;

/**
 * A plate carree projection directly maps X coordinates to longitude and Y coordinates to latitude.
 * This projection is fast, but will only generally be effective for small spaces and/or near the
 * equator. Otherwise, the distortion created by this mapping may be problematic.
 * 
 * Essentially, any north-south movement will take place directly along a great circle, any
 * east-west movement will run along a parallel. Distortion will come into play in that as a person
 * moves further north, EW movement in the virtual world will be accelerated: one step east or west
 * will cover more "ground" at high and low latitudes.
 * 
 * Note that, although it is somewhat confusing, this projection will map north to negative Y
 * (because in Java, negative Y is UP) and east to positive X.
 * 
 * PlateCarreeProjection sets scale in terms of virtual world points : degrees latitude/longitude.
 * 
 * @author Zachary O. Toups (zach@ecologylab.net)
 * 
 */
public class PlateCarreeProjection extends Projection
{
	/**
	 * The matrix that performs the transformation from real-world coordinates to virtual world
	 * coordinates.
	 */
	protected AffineTransform	transformMatrix;

	protected AffineTransform	inverseTransformMatrix;

	private double						xScaleFactor;
	
	private double 					yScaleFactor;

	private double						rotation;

	double										translateToOriginX;

	double										translateToOriginY;

	public PlateCarreeProjection(GeoCoordinate physicalWorldPoint1, GeoCoordinate physicalWorldPoint2,
			double scaleFactor, RotationConstraintMode rotConstMode) throws SameCoordinatesException
	{
		super(physicalWorldPoint1, physicalWorldPoint2, scaleFactor, rotConstMode);

		debug("Using plate carree projection.");
	}

	/**
	 * @param physicalWorldPoint1
	 *          the northmost and eastmost corner of the physical world that will be mapped to the
	 *          virtual space.
	 * @param physicalWorldPoint2
	 *          the southmost and westmost corner of the physical world that will be mapped to the
	 *          virtual space.
	 * @param virtualWorldWidth
	 *          the width of the virtual space, which will map between the west coordinate of
	 *          physicalWorldPoint1 and the east coordinate of physicalWorldPoint2.
	 * @param virtualWorldHeight
	 *          the height of the virtual space, which will map between the north coordinate of
	 *          physicalWorldPoint1 and the south coordinate of physicalWorldPoint2.
	 * @param rotConstMode
	 * @throws SameCoordinatesException
	 *           if physicalWorldPoint1 and physicalWorldPoint2 are the same.
	 */
	public PlateCarreeProjection(GeoCoordinate physicalWorldPoint1, GeoCoordinate physicalWorldPoint2,
			double virtualWorldWidth, double virtualWorldHeight, RotationConstraintMode rotConstMode)
			throws SameCoordinatesException
	{
		super(physicalWorldPoint1, physicalWorldPoint2, virtualWorldWidth, virtualWorldHeight,
				rotConstMode);

		debug("Using plate carree projection.");
	}

	public static void main(String[] args) throws SameCoordinatesException
	{
		PlateCarreeProjection p = new PlateCarreeProjection(new GeoCoordinate( 30.62241857929544, -96.33219192254931, 0),
				new GeoCoordinate( 30.60736755997001, -96.34768568921732, 0), 1483.91, 1667.10, Projection.RotationConstraintMode.CARDINAL_DIRECTIONS);

		Scanner scan = new Scanner(System.in);
		
		ArrayList<GeoCoordinate> coords = new ArrayList<GeoCoordinate>();
		
		while(scan.hasNextDouble())
		{
			double longitude = scan.nextDouble();
			double latitude = scan.nextDouble();
			
			GeoCoordinate temp = new GeoCoordinate(latitude, longitude, 0);
			coords.add(temp);
		}

		for (GeoCoordinate d : coords)
		{
			Point2D.Double transformedPoint = p.projectIntoVirtual(d);

			try
			{
				System.out.println(SimplTypesScope.serialize(d, StringFormat.XML));
			}
			catch (SIMPLTranslationException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.print(d.getLon() + ", " + d.getLat());
			System.out.print(" > ");
			System.out.println(transformedPoint.x + ", " + transformedPoint.y);
		}

	}

	/**
	 * Adjusts internal variables to match the modes, based on the current parameters. This method
	 * should be called whenever any states are changed.
	 */
	@Override
	protected void configure()
	{
		double westMostLon = this.physicalWorldPointSW.getLon();
		double northMostLat = this.physicalWorldPointNE.getLat();

		translateToOriginX = Point2D.distance(westMostLon, 0, 0, 0) * (westMostLon > 0 ? -1.0 : 1.0);
		translateToOriginY = Point2D.distance(0, northMostLat, 0, 0) * (northMostLat > 0 ? -1.0 : 1.0);

		switch (this.rotConstMode)
		{
		case CARDINAL_DIRECTIONS:
			// we need to redfine NE and SW so that they correspond to a rectangle with the same aspect
			// ratio as the two
			// virtual world coordinates

			// we need to maintain a constant size and aspect ratio for the virtual world

			// first determine the scaling factor
			// need the longest dimension of the virtual world
						
			this.yScaleFactor = this.virtualWorldHeight / this.realWorldHeight;
		
			this.xScaleFactor = this.virtualWorldWidth / this.realWorldWidth;
		
			break;
		case ANCHOR_POINTS:
			double virtualCenterLineSq = (this.virtualWorldHeight * this.virtualWorldHeight)
					+ (this.virtualWorldWidth * this.virtualWorldWidth);
			double realPointsDistance = this.specedPWP1.getPointRepresentation().distance(
					this.specedPWP2.getPointRepresentation());

			// figure out the angle of rotation for the virtual world
			rotation = Math.asin(this.virtualWorldWidth / Math.sqrt(virtualCenterLineSq));

			// we need to maintain a constant size and aspect ratio for the virtual world

			// determine the scaling factor
			this.xScaleFactor = this.yScaleFactor = Math.sqrt(virtualCenterLineSq) / realPointsDistance;

			break;
		}

		if (this.transformMatrix == null)
		{
			this.transformMatrix = new AffineTransform();
		}

		
		this.transformMatrix.setToIdentity();

		
		this.transformMatrix.scale(xScaleFactor, -1.0 * yScaleFactor);
		this.transformMatrix.rotate(rotation, 0, 0);
		this.transformMatrix.translate(translateToOriginX, translateToOriginY);

		// add inverse matrix
		try
		{
			this.inverseTransformMatrix = this.transformMatrix.createInverse();
		}
		catch (NoninvertibleTransformException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Projects the given GPSDatum's coordinates into the virtual space, using some type of
	 * projection.
	 * 
	 * This version takes an instantiated Point2D, so as not to expend resources instantating a new
	 * one.
	 * 
	 * Subclasses may override this method, if they are not making affine transformations.
	 * 
	 * @param origPoint
	 * @param destPoint
	 * @return destPoint containing the virtual space point for origPoint.
	 */
	@Override
	protected Point2D.Double projectIntoVirtualImpl(GeoCoordinate origPoint, Point2D.Double destPoint)
	{
		return (Double) this.transformMatrix.transform(origPoint.getPointRepresentation(), destPoint);
	}

	@Override
	protected GeoCoordinate projectIntoRealImpl(Point2D.Double origPoint, GeoCoordinate destDatum)
	{
		Point2D.Double inversePoint = (Double) this.inverseTransformMatrix.transform(origPoint, null);
		destDatum.setLon(inversePoint.getX());
		destDatum.setLat(inversePoint.getY());

		return destDatum;
	}

	/**
	 * @return the transformMatrix
	 */
	public AffineTransform getTransformMatrix()
	{
		return transformMatrix;
	}

	/**
	 * @return the inverseTransformMatrix
	 */
	public AffineTransform getInverseTransformMatrix()
	{
		return inverseTransformMatrix;
	}

	@Override
	public double getScale()
	{
		return (this.xScaleFactor + this.yScaleFactor) / 2;
	}
}
