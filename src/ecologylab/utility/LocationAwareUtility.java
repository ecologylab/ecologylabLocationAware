/**
 * 
 */
package ecologylab.utility;

import ecologylab.sensor.location.EarthData;
import ecologylab.sensor.location.gps.data.GPSDatum;

/**
 * @author Z O. Toups (zach@ecologylab.net)
 */
public class LocationAwareUtility
{
	/**
	 * Calculates the bearing from the first coordinate (lat1, lon1) to the second coordinate (lat2,
	 * lon2) as an angle opening east from north. Formula comes from <a
	 * href="http://mathforum.org/library/drmath/view/55417.html">Dr. Math Forum</a>.
	 * 
	 * @param lat1
	 *          The latitude for the first coordinate, in degrees.
	 * @param lon1
	 *          The longitude for the first coordinate, in degrees.
	 * @param lat2
	 *          The latitude for the second coordinate, in degrees.
	 * @param lon2
	 *          The longitude for the second coordinate, in degrees.
	 * @return The bearing from coordinate 1 to coordinate 2, in degrees.
	 */
	public static double bearing(double lat1, double lon1, double lat2, double lon2)
	{
		lat1 = Math.toRadians(lat1);
		lon1 = Math.toRadians(lon1);

		lat2 = Math.toRadians(lat2);
		lon2 = Math.toRadians(lon2);

		return normalizeHeading(Math.toDegrees(Math.atan2(
				Math.sin(lon2 - lon1) * Math.cos(lat2),
				(Math.cos(lat1) * Math.sin(lat2))
						- (Math.sin(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1)))
				% (2.0 * Math.PI)));
	}

	/**
	 * Returns the approximate overland distanceToUser between the two locations, in meters.
	 * 
	 * @param wayLatCoord
	 * @param wayLonCoord
	 * @param dat
	 * @return Distance between the two pairs of coordinates, in meters.
	 */
	public static double distance(double photoLatCoord, double photoLonCoord, GPSDatum dat)
	{// Orthodromic Distance
		return distance(photoLatCoord, photoLonCoord, dat.getLat(), dat.getLon());
	}

	/**
	 * Returns the approximate overland distanceToUser between the two locations, in meters.
	 * 
	 * @param wayLatCoord
	 * @param wayLonCoord
	 * @param otherLatCoord
	 * @param otherLonCoord
	 * @return Distance between the two pairs of coordinates, in meters.
	 */
	public static double distance(double wayLatCoord, double wayLonCoord, double otherLatCoord,
			double otherLonCoord)
	{// Orthodromic Distance
		double distance = 0;
		distance = 2 * Math
				.asin(Math.sqrt(Math.pow(
						Math.sin((Math.toRadians(wayLatCoord) - Math.toRadians(otherLatCoord)) / 2), 2)
						+ Math.cos(Math.toRadians(wayLatCoord))
						* Math.cos(Math.toRadians(otherLatCoord))
						* Math.pow(Math.sin((Math.toRadians(wayLonCoord) - Math.toRadians(otherLonCoord)) / 2),
								2)));
		distance *= EarthData.RADIUS_EARTH_METERS;
		return distance;
	}

	/**
	 * Normalize a heading to be between 0 and 359.99999 degrees.
	 * 
	 * @param heading
	 *          in degrees with north at 0, increasing clockwise.
	 */
	public static double normalizeHeading(double heading)
	{
		int multVal = (Math.abs((int) (heading / 360)) + (heading < 0 ? 1 : 0));

		return heading + (multVal * 360.0 * (heading < 0 ? 1 : -1));
	}

	/**
	 * Given two normalized angles in degrees, computes the number of degrees through which you must
	 * move from angleFrom to angleTo. A negative value means the difference moves counter-clockwise;
	 * a positive value means the angle moves clockwise.
	 * 
	 * Formula from
	 * http://blog.open-design.be/2009/06/02/find-the-shortest-rotation-angle-between-two-angles/.
	 * 
	 * @param angleFrom
	 * @param angleTo
	 * @return
	 */
	public static double angleDifference(double angleFrom, double angleTo)
	{
		double radians = Math.toRadians(angleFrom - angleTo);
		return Math.toDegrees(Math.atan2(Math.sin(radians), Math.cos(radians)));
	}
}
