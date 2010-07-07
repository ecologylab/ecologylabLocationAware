/**
 * 
 */
package ecologylab.sensor.location;

import ecologylab.sensor.location.gps.data.GeoCoordinate;

/**
 * Represents the location data from a location sensor of some sort.
 * 
 * @author Zachary O. Toups (zach@ecologylab.net)
 */
public class LocationStatus extends GeoCoordinate
{
	/** TODO make this an actual date. */
	@simpl_scalar protected String				utcTime;

	@simpl_scalar protected String				utcDate;

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
