/**
 * 
 */
package ecologylab.sensor.gps.data.dataset;

import ecologylab.sensor.gps.data.GPSDatum;

public enum GGA implements GPSDataFieldBase
{
	UTC_POS_TIME
	{
		public void update(String src, GPSDatum dst)
		{
			dst.updateUtcPosTime(src);
		}
	},
	LAT
	{
		public void update(String src, GPSDatum dst)
		{
			dst.updateLat(src);
		}
	},
	NSLAT
	{
		public void update(String src, GPSDatum dst)
		{
			dst.updateLatHemisphere(src);
		}
	},
	LON
	{
		public void update(String src, GPSDatum dst)
		{
			dst.updateLon(src);
		}
	},
	EWLON
	{
		public void update(String src, GPSDatum dst)
		{
			dst.updateLonHemisphere(src);
		}
	},
	GPS_QUAL
	{
		public void update(String src, GPSDatum dst)
		{
			dst.updateGPSQual(src);
		}
	},
	NUM_SATS
	{
		public void update(String src, GPSDatum dst)
		{
			dst.updateNumSats(src);
		}
	},
	HDOP
	{
		public void update(String src, GPSDatum dst)
		{
			dst.updateHDOP(src);
		}
	},
	GEOID_HEIGHT
	{
		public void update(String src, GPSDatum dst)
		{
			dst.updateGeoidHeight(src);
		}
	},
	HEIGHT_UNIT
	{
		public void update(String src, GPSDatum dst)
		{
			dst.updateHeightUnit(src);
		}
	},
	HEIGHT_DIFF
	{
		public void update(String src, GPSDatum dst)
		{
			dst.updateHeightDiff(src);
		}
	},
	HEIGHT_DIFF_UNIT
	{
		public void update(String src, GPSDatum dst)
		{
			dst.updateDiffHeightUnit(src);
		}
	},
	DGPS_AGE
	{
		public void update(String src, GPSDatum dst)
		{
			dst.updateDGPSAge(src);
		}
	},
	DGPS_REF
	{
		public void update(String src, GPSDatum dst)
		{
			dst.updateDGPSRef(src);
		}
	},
	CHECKSUM
	{
		public void update(String src, GPSDatum dst)
		{

		}
	};

};
