package ecologylab.sensor.location.gps.data;

public interface GPSConstants
{

	/** Indicates no GPS. */
	public static final int	GPS_QUAL_NO		= 0;

	/** Indicates GPS satellite fix only. */
	public static final int	GPS_QUAL_GPS	= 1;

	/** Indicates GPS satellite fix + differential signal. */
	public static final int	GPS_QUAL_DGPS	= 2;

	/** Indicates that there is no calcuating mode set. */
	public static final int	CALC_MODE_NONE	= 1;

	/** Indicates that the calculating mode is 2D. */
	public static final int	CALC_MODE_2D	= 2;

	/** Indicates that the calculating mode is 3D. */
	public static final int	CALC_MODE_3D	= 3;

}