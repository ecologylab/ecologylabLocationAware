/**
 * 
 */
package ecologylab.sensor.location.gps.data.dataset;

import ecologylab.sensor.location.gps.data.GPSDatum;

/**
 * @author bilhamil.local
 *
 */
public enum VTG implements GPSDataFieldBase
{
	TRUE_TRACK_MADE_GOOD
	{
		public void update(String src, GPSDatum dst)
		{
		}
	},
	T
	{
		public void update(String src, GPSDatum dst)
		{
			dst.updateLat(src);
		}
	},
	MAGNETIC_TRACK_MADE_GOOD
	{
		public void update(String src, GPSDatum dst)
		{
		}
	},
	M
	{
		public void update(String src, GPSDatum dst)
		{
		}
	},
	GROUND_SPEED_KNOTS
	{
		public void update(String src, GPSDatum dst)
		{
		}
	},
	N
	{
		public void update(String src, GPSDatum dst)
		{
		}
	},
	GROUND_SPEED_KPH
	{
		public void update(String src, GPSDatum dst)
		{
			dst.updateGroundSpeed(src);
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
}
