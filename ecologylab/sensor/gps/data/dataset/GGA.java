/**
 * 
 */
package ecologylab.sensor.gps.data.dataset;

import ecologylab.sensor.gps.data.GPSDatum;

public enum GGA implements GPSDataFieldBase
{
	UTC_POS_TIME, LAT, NSLAT, LON, EWLON, GPS_QUAL, NUM_SATS, HDOP, GEOID_HEIGHT, HEIGHT_UNIT, HEIGHT_DIFF, HEIGHT_DIFF_UNIT, DGPS_AGE, DGPS_REF, CHECKSUM;

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
		case GPS_QUAL:
			dst.updateGPSQual(src);
			break;
		case NUM_SATS:
			dst.updateNumSats(src);
			break;
		case HDOP:
			dst.updateHDOP(src);
			break;
		case GEOID_HEIGHT:
			dst.updateGeoidHeight(src);
			break;
		case HEIGHT_UNIT:
			dst.updateHeightUnit(src);
			break;
		case HEIGHT_DIFF:
			dst.updateHeightDiff(src);
			break;
		case HEIGHT_DIFF_UNIT:
			dst.updateDiffHeightUnit(src);
			break;
		case DGPS_AGE:
			dst.updateDGPSAge(src);
			break;
		case DGPS_REF:
			dst.updateDGPSRef(src);
			break;
		}
	}
};
