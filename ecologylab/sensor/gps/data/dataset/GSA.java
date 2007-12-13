/**
 * 
 */
package ecologylab.sensor.gps.data.dataset;

import ecologylab.sensor.gps.data.GPSDatum;

/**
 * GSA Data Set - GNSS DOP and Active Satellites
 * 
 * According to GPS Basics:
 * 
 * TheGSAdataset(GNSSDOPandActiveSatellites)containsinformationonthemeasuringmode(2Dor3D),the
 * numberof satellitesused to determine the positionand theaccuracy of themeasurements (DOP:Dilution of
 * Precision).
 * 
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 * 
 */
public enum GSA implements GPSDataFieldBase
{
	CALC_MODE_DECISION, CALC_MODE, SAT00, SAT01, SAT02, SAT03, SAT04, SAT05, SAT06, SAT07, SAT08, SAT09, SAT10, SAT11, PDOP, HDOP, VDOP, CHECKSUM;

	/**
	 * Updates dst's internal data by parsing src according to the interpretation of the current mode.
	 * 
	 * @param data
	 * @param decLoc
	 * @param dst
	 */
	public void update(String src, GPSDatum dst)
	{
		int i = 0;

		switch (this)
		{
		case CALC_MODE_DECISION:
			dst.updateCalcModeDecision(src);
			break;
		case CALC_MODE:
			dst.updateCalcMode(src);
			break;
		case SAT11:
			i++;
		case SAT10:
			i++;
		case SAT09:
			i++;
		case SAT08:
			i++;
		case SAT07:
			i++;
		case SAT06:
			i++;
		case SAT05:
			i++;
		case SAT04:
			i++;
		case SAT03:
			i++;
		case SAT02:
			i++;
		case SAT01:
			i++;
		case SAT00:
			dst.updateSat(src, i);
			break;
		case PDOP:
			dst.updatePDOP(src);
			break;
		case HDOP:
			dst.updateHDOP(src);
			break;
		case VDOP:
			dst.updateVDOP(src);
			break;
		}
	}
};
