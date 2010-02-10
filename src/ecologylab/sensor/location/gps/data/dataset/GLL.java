/**
 * 
 */
package ecologylab.sensor.location.gps.data.dataset;

import ecologylab.sensor.location.gps.data.GPSDatum;

/**
 * GLL data set -- Geographic position - Latitude and Longitude
 * 
 * @author Zachary O. Toups (zach@ecologylab.net)
 * 
 */
public enum GLL implements GPSDataFieldBase
{
	LAT, NSLAT, LON, EWLON, UTC_POS_TIME, GPS_DATA_VALID, CHECKSUM;

	/**
	 * Updates dst's internal data by parsing src according to the interpretation of the current mode.
	 * 
	 * @param data
	 * @param decLoc
	 * @param dst
	 */
	public void update(String src, GPSDatum dst)
	{
		switch (this)
		{
		case UTC_POS_TIME:
			dst.updateUtcPosTime(src);
			break;
		case LAT:
			dst.updateLat(src);
			break;
		case NSLAT:
			dst.updateLatHemisphere(src);
			break;
		case LON:
			dst.updateLon(src);
			break;
		case EWLON:
			dst.updateLonHemisphere(src);
			break;
		case GPS_DATA_VALID:
			dst.updateDataValid(src);
			break;
		}
	}
}
