/**
 * 
 */
package ecologylab.sensor.location.gps.data.dataset;

import ecologylab.sensor.location.gps.data.GPSDatum;

/**
 * GSV Data Set - GNSS Satellites in View According to GPS Basics: The  GSV
 *  data  set  (GNSS  Satellites  in  View)  contains  information  on  the
 *  number  of  satellites  in  view,  their  identification,  their  elevation 
 * and  azimuth,  and  the  signal-to-noise  ratio .
 * 
 * @author Z O. Toups (zach@ecologylab.net)
 */
public enum GSV implements GPSDataFieldBase
{
	TOTAL_GSVS, CURRENT_GSV, NUM_SATS,

	SAT_A_ID, SAT_A_ELEV, SAT_A_AZI, SAT_A_SNR,

	SAT_B_ID, SAT_B_ELEV, SAT_B_AZI, SAT_B_SNR,

	SAT_C_ID, SAT_C_ELEV, SAT_C_AZI, SAT_C_SNR,

	SAT_D_ID, SAT_D_ELEV, SAT_D_AZI, SAT_D_SNR,

	CHECKSUM;

	/**
	 * Updates dst's internal data by parsing src according to the interpretation
	 * of the current mode.
	 * 
	 * @param data
	 * @param decLoc
	 * @param dst
	 */
	public void update(String src, GPSDatum dst)
	{
		switch (this)
		{
		case NUM_SATS:
			dst.updateNumSats(src);
			break;
		case SAT_A_ID:
			dst.setCurrentSV(src);
			break;
		case SAT_A_ELEV:
			dst.setCurrentSVElev(src);
			break;
		case SAT_A_AZI:
			dst.setCurrentSVAzi(src);
			break;
		case SAT_A_SNR:
			dst.setCurrentSVSNR(src);
			dst.unsetCurrentSV();
			break;
		case SAT_B_ID:
			dst.setCurrentSV(src);
			break;
		case SAT_B_ELEV:
			dst.setCurrentSVElev(src);
			break;
		case SAT_B_AZI:
			dst.setCurrentSVAzi(src);
			break;
		case SAT_B_SNR:
			dst.setCurrentSVSNR(src);
			dst.unsetCurrentSV();
			break;
		case SAT_C_ID:
			dst.setCurrentSV(src);
			break;
		case SAT_C_ELEV:
			dst.setCurrentSVElev(src);
			break;
		case SAT_C_AZI:
			dst.setCurrentSVAzi(src);
			break;
		case SAT_C_SNR:
			dst.setCurrentSVSNR(src);
			dst.unsetCurrentSV();
			break;
		case SAT_D_ID:
			dst.setCurrentSV(src);
			break;
		case SAT_D_ELEV:
			dst.setCurrentSVElev(src);
			break;
		case SAT_D_AZI:
			dst.setCurrentSVAzi(src);
			break;
		case SAT_D_SNR:
			dst.setCurrentSVSNR(src);
			dst.unsetCurrentSV();
			break;
		}
	}
};
