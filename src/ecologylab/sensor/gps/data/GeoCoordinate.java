/**
 * 
 */
package ecologylab.sensor.gps.data;

import ecologylab.xml.ElementState;

/**
 * @author Zach
 * 
 */
public class GeoCoordinate extends ElementState
{
	/** The latitude, expressed in degrees in double-precision degrees. */
	@xml_attribute AngularCoord	lat;

	/** The longitude, expressed in degrees in double-precision degrees. */
	@xml_attribute AngularCoord	lon;

	/** The altitude, expressed in meters. */
	@xml_attribute double			alt;

	String								kMLCommaDelimited	= null;

	/**
	 * 
	 */
	public GeoCoordinate()
	{
	}

	public GeoCoordinate(double lat, double lon, double alt)
	{
		this.lat = new AngularCoord(lat);
		this.lon = new AngularCoord(lon);
		this.alt = alt;
	}

	public AngularCoord getLat()
	{
		return lat;
	}

	public void setLat(AngularCoord lat)
	{
		this.lat = lat;

		kMLCommaDelimited = null;
	}

	public AngularCoord getLon()
	{
		return lon;
	}

	public void setLon(AngularCoord lon)
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

	public String getKMLCommaDelimitedString()
	{
		if (kMLCommaDelimited == null)
		{
			kMLCommaDelimited = this.lon.getCoord()+","+this.lat.getCoord()+","+this.alt;
		}
		
		return kMLCommaDelimited;
	}
}
