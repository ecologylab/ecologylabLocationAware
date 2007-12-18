/**
 * 
 */
package ecologylab.sensor.gps.data;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

import ecologylab.sensor.gps.data.dataset.GGA;
import ecologylab.sensor.gps.data.dataset.GLL;
import ecologylab.sensor.gps.data.dataset.GPSDataFieldBase;
import ecologylab.sensor.gps.data.dataset.GSA;
import ecologylab.sensor.gps.data.dataset.RMC;
import ecologylab.sensor.gps.listener.GPSDataUpdatedListener;
import ecologylab.xml.ElementState;
import ecologylab.xml.XMLTranslationException;

/**
 * Represents an instant of GPS data computed from a series of NMEA strings. Each component of the datum is as
 * up-to-date as possible as of the time stamp. Whenever no new data is provided, the old data is retained.
 * 
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 * 
 */
public class GPSDatum extends ElementState
{
	@xml_attribute float				utcPosTime;

	@xml_attribute double			lat;

	@xml_attribute double			lon;

	/** Quality of GPS data; values will be either GPS_QUAL_NO, GPS_QUAL_GPS, GPS_QUAL_DGPS. */
	@xml_attribute int				gpsQual;

	/** Indicates no GPS. */
	public static final int			GPS_QUAL_NO					= 0;

	/** Indicates GPS satellite fix only. */
	public static final int			GPS_QUAL_GPS				= 1;

	/** Indicates GPS satellite fix + differential signal. */
	public static final int			GPS_QUAL_DGPS				= 2;

	@xml_attribute int				numSats;

	/**
	 * Horizontal Dilution of Precision - approximation of the size of the area in which the actual location of the GPS
	 * is horizontally based on the spread of the fixed satellites; higher numbers are worse, smaller mean better
	 * precision; this value may range between 1-50.
	 * 
	 * According to http://www.codepedia.com/1/Geometric+Dilution+of+Precision+(DOP):
	 * 
	 * <table>
	 * <tr>
	 * <td>DOP</td>
	 * <td>Rating</td>
	 * <td>Description</td>
	 * <tr>
	 * <td>1</td>
	 * <td>Ideal</td>
	 * <td>This is the highest possible confidence level to be used for applications demanding the highest possible
	 * precision at all times.</td>
	 * </tr>
	 * <tr>
	 * <td>2-3</td>
	 * <td>Excellent</td>
	 * <td>At this confidence level, positional measurements are considered accurate enough to meet all but the most
	 * sensitive applications.</td>
	 * </tr>
	 * <tr>
	 * <td>4-6</td>
	 * <td>Good</td>
	 * <td>Represents a level that marks the minimum appropriate for making business decisions. Positional measurements
	 * could be used to make reliable in-route navigation suggestions to the user.</td>
	 * </tr>
	 * <tr>
	 * <td>7-8</td>
	 * <td>Moderate</td>
	 * <td>Positional measurements could be used for calculations, but the fix quality could still be improved. A more
	 * open view of the sky is recommended.</td>
	 * </tr>
	 * <tr>
	 * <td>9-20</td>
	 * <td>Fair</td>
	 * <td>Represents a low confidence level. Positional measurements should be discarded or used only to indicate a
	 * very rough estimate of the current location.</td>
	 * </tr>
	 * <tr>
	 * <td>21-50</td>
	 * <td>Poor At this level, measurements are inaccurate by half a football field or more and should be discarded.</td>
	 * </tr>
	 * </table>
	 */
	@xml_attribute float				hdop;

	/** The altitude of the antenna of the GPS (location where the signals are recieved). In meters. */
	@xml_attribute float				geoidHeight;

	/** The differential between the elipsoid and the geoid. In meters. */
	@xml_attribute float				heightDiff;

	@xml_attribute float				dgpsAge;

	@xml_attribute int				dgpsRefStation;

	char[]								tempDataStore;

	List<GPSDataUpdatedListener>	gpsDataUpdatedListeners	= new LinkedList<GPSDataUpdatedListener>();

	/**
	 * A Point2D representation of this's latitude and longitude, instantiated and filled through lazy evaluation, when
	 * needed.
	 */
	private Point2D.Double			pointRepresentation		= null;

	/** Indicates that pointRepresentation is out of synch with the state of this object. */
	private boolean					pointDirty					= true;

	public GPSDatum()
	{
	}

	public GPSDatum(int latDeg, double latMin, int lonDeg, double lonMin)
	{
		this.lat = WorldCoord.fromDegMinSec(latDeg, latMin, 0);
		this.lon = WorldCoord.fromDegMinSec(lonDeg, lonMin, 0);
	}

	public GPSDatum(int latDeg, int lonDeg)
	{
		this(latDeg, 0f, lonDeg, 0f);
	}

	/**
	 * Ensure that tempDataStore is instantiated and clean, then return it.
	 * 
	 * @return
	 */
	private synchronized char[] tempDataStore()
	{
		if (tempDataStore == null)
		{
			tempDataStore = new char[79];
		}

		return tempDataStore;
	}

	/**
	 * @param that
	 * @return positive if this is farther north than that, negative if that is more north; 0 if they lie on exactly the
	 *         same parallel.
	 */
	public double compareNS(GPSDatum that)
	{
		return this.getLat() - that.getLat();
	}

	/**
	 * @param that
	 * @return compares two GPSDatum's based on the acute angle between their longitudes. Returns 1 if this is farther
	 *         east than that, -1 if this is farther west, 0 if the two points lie on the same arc, 180/-180 if they are
	 *         opposite.
	 */
	public double compareEW(GPSDatum that)
	{
		double diff = this.lon - that.getLon();

		if (diff > 180)
		{
			return diff - 360;
		}
		else if (diff < -180)
		{
			return diff + 360;
		}
		else
		{
			return diff;
		}
	}

	/**
	 * Splits and stores data from an NMEA GPS data set.
	 * 
	 * @param gpsData
	 *           a GPS data set, minus $ header and <CR><LF> trailer.
	 */
	public synchronized void integrateGPSData(String gpsData)
	{
		char[] tempData = tempDataStore();

		int dataLength = gpsData.length();
		int i = 6; // start looking after "GPXXX," -- the header of the message

		int dataStart = i;
		boolean finishedField = false;

		GPSDataFieldBase[] fieldBase = null;

		switch (gpsData.charAt(2))
		{
		case ('G'):
			// either GGA, GLL, GSA, or GSV
			switch (gpsData.charAt(3))
			{
			case ('G'):
				// assume GGA
				fieldBase = GGA.values();
				break;
			case ('L'):
				// assume GLL
				fieldBase = GLL.values();
				break;
			case ('S'):
				switch (gpsData.charAt(4))
				{
				case ('A'): // GSA
					fieldBase = GSA.values();
					break;
				case ('V'): // GSV
					// TODO
					System.out.print("GSV - ");
					break;
				}
				break;
			}
			break;
		case ('R'): // assume RMC
			fieldBase = RMC.values();
			break;

		case ('V'):
			// assume VTG
			System.out.print("VTG - ");
			// TODO
			break;

		case ('Z'):
			// assume ZDA
			System.out.print("ZDA - ");
			// TODO
			break;
		}

		if (fieldBase != null)
		{
			synchronized (tempDataStore)
			{
				for (GPSDataFieldBase field : fieldBase)
				{
					finishedField = false;
					dataStart = i;

					// now we will read each data field in, one at at time, since we know the order of the GGA data set.
					// we will break out of each for loop when finished.
					while (i < dataLength && !finishedField)
					{
						tempData[i] = gpsData.charAt(i);

						if (tempData[i] == ',')
						{
							if (i - dataStart > 0)
							{
								field.update(new String(tempData, dataStart, (i - dataStart)), this);
							}
							finishedField = true;
						}

						i++;
					}
				}
			}
		}
		else
		{
			System.out.println("data type unidentified");
		}

		this.fireGPSDataUpdatedEvent();
	}

	private void fireGPSDataUpdatedEvent()
	{
		for (GPSDataUpdatedListener l : this.gpsDataUpdatedListeners)
		{
			l.gpsDatumUpdated(this);
		}
	}

	@Override public String toString()
	{
		return new String("GPSDatum: " + this.lat + ", " + this.lon);
	}

	/**
	 * determine longitude sign
	 * 
	 * @param src
	 */
	public void updateLonHemisphere(String src)
	{
		if (src.charAt(0) == 'W') // southern hemisphere == negative latitude
			this.lon *= -1;

		this.pointDirty = true;
	}

	/**
	 * determine longitude degress and minutes
	 * 
	 * @param src
	 */
	public void updateLon(String src)
	{
		this.lon = WorldCoord.fromDegMinSec(Integer.parseInt(src.substring(0, 3)), Double.parseDouble(src.substring(3)),
				0);

		this.pointDirty = true;
	}

	/**
	 * determine latitude sign
	 * 
	 * @param src
	 */
	public void updateLatHemisphere(String src)
	{
		if (src.charAt(0) == 'S') // southern hemisphere == negative latitude
			this.lat *= -1;

		this.pointDirty = true;
	}

	/**
	 * determine latitude degrees and minutes
	 * 
	 * @param src
	 */
	public void updateLat(String src)
	{
		this.lat = WorldCoord.fromDegMinSec(Integer.parseInt(src.substring(0, 3)), Double.parseDouble(src.substring(3)),
				0);

		this.pointDirty = true;
	}

	/**
	 * @param src
	 */
	public void updateUtcPosTime(String src)
	{
		this.utcPosTime = Float.parseFloat(src);
	}

	/**
	 * @param src
	 */
	public void updateHDOP(String src)
	{
		this.hdop = Float.parseFloat(src);
	}

	/**
	 * @param src
	 */
	public void updateNumSats(String src)
	{
		this.numSats = Integer.parseInt(src);
	}

	/**
	 * @param src
	 */
	public void updateGPSQual(String src)
	{
		this.gpsQual = Integer.parseInt(src);
	}

	/**
	 * @param src
	 */
	public void updateDGPSRef(String src)
	{
		this.dgpsRefStation = Integer.parseInt(src);
	}

	/**
	 * @param src
	 */
	public void updateDiffHeightUnit(String src)
	{
		if (src.charAt(0) != 'M')
		{
			warning("GPS is not using meters: " + src);
		}
	}

	/**
	 * @param src
	 */
	public void updateDGPSAge(String src)
	{
		this.dgpsAge = Float.parseFloat(src);
	}

	/**
	 * @param src
	 */
	public void updateHeightUnit(String src)
	{
		if (src.charAt(0) != 'M')
		{
			warning("GPS is not using meters: " + src);
		}
	}

	/**
	 * @param src
	 */
	public void updateGeoidHeight(String src)
	{
		this.geoidHeight = Float.parseFloat(src);
	}

	/**
	 * @param src
	 */
	public void updateCalcModeDecision(String src)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @param src
	 */
	public void updateCalcMode(String src)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @param src
	 * @param i
	 */
	public void updateSat(String src, int i)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @param src
	 */
	public void updatePDOP(String src)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @param src
	 */
	public void updateVDOP(String src)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @param src
	 */
	public void updateHeightDiff(String src)
	{
		// TODO Auto-generated method stub

	}

	public static void main(String[] args)
	{
		GPSDatum a = new GPSDatum(0, 0);
		GPSDatum b = new GPSDatum(10, 10);
		GPSDatum c = new GPSDatum(120, 120);
		GPSDatum d = new GPSDatum(-35, -35);
		GPSDatum e = new GPSDatum(-150, -150);
		GPSDatum f = new GPSDatum(-180, -180);

		GPSDatum[] all =
		{ a, b, c, d, e, f };

		for (int i = 0; i < all.length; i++)
		{
			for (int j = 0; j < all.length; j++)
			{
				System.out.println("-------------------");
				System.out.println("pt lat: " + all[i].getLat() + ", lon: " + all[i].getLon() + " is "
						+ nsString(all[i].compareNS(all[j])) + " lat: " + all[j].getLat() + ", lon: " + all[j].getLon());
				System.out.println("pt lat: " + all[i].getLat() + ", lon: " + all[i].getLon() + " is "
						+ ewString(all[i].compareEW(all[j])) + " lat: " + all[j].getLat() + ", lon: " + all[j].getLon());
			}
		}
	}

	static String nsString(double dir)
	{
		if (dir > 0)
		{
			return "north of";
		}
		else if (dir < 0)
		{
			return "south of";
		}
		else
		{
			return "the same latitude as";
		}
	}

	static String ewString(double dir)
	{
		if (dir > 0)
		{
			return "west of";
		}
		else if (dir < 0)
		{
			return "east of";
		}
		else if (dir == -180 || dir == 180)
		{
			return "opposite the earth from";
		}
		else
		{
			return "the same longitude as";
		}
	}

	/**
	 * @return the lat
	 */
	public double getLat()
	{
		return lat;
	}

	/**
	 * @return the lon
	 */
	public double getLon()
	{
		return lon;
	}

	/**
	 * @param lat
	 *           the lat to set
	 */
	public void setLat(double lat)
	{
		this.lat = lat;

		this.pointDirty = true;
	}

	/**
	 * @param lon
	 *           the lon to set
	 */
	public void setLon(double lon)
	{
		this.lon = lon;

		this.pointDirty = true;
	}

	/**
	 * Gets the latitude and longitude of this datum as a Point2D, where x = longitude and y = latitude.
	 * 
	 * @return the pointRepresentation
	 */
	public Point2D.Double getPointRepresentation()
	{
		if (pointRepresentation == null)
		{
			pointRepresentation = new Point2D.Double();
		}

		if (this.pointDirty)
		{
			this.pointRepresentation.setLocation(this.lon, this.lat);
			this.pointDirty = false;
		}

		return pointRepresentation;
	}
}
