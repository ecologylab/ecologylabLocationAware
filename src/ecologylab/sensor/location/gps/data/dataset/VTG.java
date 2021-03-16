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
	K
	{
		public void update(String src, GPSDatum dst)
		{
		}
	},
	CHECKSUM
	{
		public void update(String src, GPSDatum dst)
		{

		}
	};
}
