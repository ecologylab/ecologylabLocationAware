/**
 * 
 */
package ecologylab.sensor.location.gps.data;

import java.awt.geom.Point2D;

import ecologylab.sensor.location.Location;
import ecologylab.serialization.simpl_inherit;

/**
 * An object for representing a set of 3d coordinates on the earth's surface: latitude, longitude,
 * and altitude.
 * 
 * @author Zachary O. Toups (zach@ecologylab.net)
 */
@simpl_inherit
public class GeoCoordinate extends Location
{
	/** The latitude, expressed in degrees in double-precision degrees. */
	@simpl_scalar
	double										lat;

	/** The longitude, expressed in degrees in double-precision degrees. */
	@simpl_scalar
	double										lon;

	/** The altitude, expressed in meters. */
	@simpl_scalar
	double										alt;

	/**
	 * A Point2D.Double representation of this's latitude and longitude, instantiated and filled
	 * through lazy evaluation, when needed.
	 */
	protected Point2D.Double	pointRepresentation	= null;

	/**
	 * 
	 */
	public GeoCoordinate()
	{
	}

	public GeoCoordinate(double lat, double lon, double alt)
	{
		this.lat = lat;
		this.lon = lon;
		this.alt = alt;
	}

	public double getLat()
	{
		return lat;
	}

	public void setLat(double lat)
	{
		this.lat = lat;

		kMLCommaDelimited = null;
	}

	public double getLon()
	{
		return lon;
	}

	public void setLon(double lon)
	{
		this.lon = lon;

		kMLCommaDelimited = null;
	}

	public double getAlt()
	{
		return alt;
	}

	public void setAlt(double alt)
	{
		this.alt = alt;

		kMLCommaDelimited = null;
	}

	/**
	 * A String reprsenting the current data for KML; cached for re-use and computed only when needed.
	 */
	String	kMLCommaDelimited	= null;

	/**
	 * Get the set of coordinates, serialized for use in KML / Google Earth.
	 * 
	 * @return
	 */
	public String getKMLCommaDelimitedString()
	{
		if (kMLCommaDelimited == null)
		{
			kMLCommaDelimited = this.lon + "," + this.lat + "," + this.alt;
		}

		return kMLCommaDelimited;
	}

	public Point2D.Double getPointRepresentation()
	{
		if (pointRepresentation == null)
			pointRepresentation = new Point2D.Double(lon, lat);
		else
			pointRepresentation.setLocation(lon, lat);
		return pointRepresentation;
	}

	/**
	 * @param that
	 * @return positive if this is farther north than that, negative if that is more north; 0 if they
	 *         lie on exactly the same parallel.
	 */
	public double compareNS(GeoCoordinate that)
	{
		return this.getLat() - that.getLat();
	}

	/**
	 * @param that
	 * @return compares two GPSDatum's based on the acute angle between their longitudes. Returns 1 if
	 *         this is farther east than that, -1 if this is farther west, 0 if the two points lie on
	 *         the same arc, 180/-180 if they are opposite.
	 */
	public double compareEW(GeoCoordinate that)
	{
		double diff = getLon() - that.getLon();

		if (diff > 180)
		{
			return diff - 360;
		}
		else if (diff < -180)
		{
			return diff + 360;
		}
		else
		{
			return diff;
		}
	}
}
