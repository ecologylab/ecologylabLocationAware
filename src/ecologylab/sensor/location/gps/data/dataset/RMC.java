/**
 * 
 */
package ecologylab.sensor.location.gps.data.dataset;

import ecologylab.sensor.location.gps.data.GPSDatum;

/**
 * According to GPS Basics: The RMC data set (Recommended Minimum Specific GNSS) contains
 * information on time, latitude, longitude and height, system status, speed, course and date. This
 * data set is relayed by all GPS receivers.
 * 
 * @author Z O. Toups (zach@ecologylab.net)
 */
public enum RMC implements GPSDataFieldBase
{
	UTC_POS_TIME, GPS_DATA_VALID, LAT, NSLAT, LON, EWLON, SPEED, COURSE, DATE, ADJ_DECLIN, WEST_DECLIN, CHECKSUM;

	/**
	 * Updates dst's internal data by parsing src according to the interpretation of the current mode.
	 * 
	 * @param data
	 * @param decLoc
	 * @param dst
	 */
	@Override
	public void update(String src, GPSDatum dst)
	{
		switch (this)
		{
			case UTC_POS_TIME:
				dst.updateUtcPosTime(src);
				break;
			case GPS_DATA_VALID:
				dst.updateDataValid(src);
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
			// TODO rest of the data
			case DATE:
				dst.updateDate(src);
				break;
		// TODO rest of the data
		}
	}
};
