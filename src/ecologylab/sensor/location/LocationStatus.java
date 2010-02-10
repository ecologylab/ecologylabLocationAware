/**
 * 
 */
package ecologylab.sensor.location;

import ecologylab.sensor.location.gps.data.GeoCoordinate;
import ecologylab.xml.ElementState;

/**
 * Represents the location data from a location sensor of some sort.
 * 
 * @author Zachary O. Toups (zach@ecologylab.net)
 */
public class LocationStatus extends GeoCoordinate
{
	/** TODO make this an actual date. */
	@xml_attribute protected String				utcTime;

	@xml_attribute protected String				utcDate;

	/**
	 * 
	 */
	public LocationStatus()
	{
	}

	public GeoCoordinate getCurrentLocation()
	{
		return this;
	}

	public String getUtcTime()
	{
		return utcTime;
	}

	public String getUtcDate()
	{
		return utcDate;
	}
}
