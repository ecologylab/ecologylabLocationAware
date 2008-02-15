/**
 * 
 */
package ecologylab.sensor.location.gps.data;

import java.awt.geom.Point2D;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ecologylab.sensor.location.LocationStatus;
import ecologylab.sensor.location.gps.data.dataset.GGA;
import ecologylab.sensor.location.gps.data.dataset.GLL;
import ecologylab.sensor.location.gps.data.dataset.GPSDataFieldBase;
import ecologylab.sensor.location.gps.data.dataset.GSA;
import ecologylab.sensor.location.gps.data.dataset.RMC;
import ecologylab.sensor.location.gps.listener.GPSDataUpdatedListener;
import ecologylab.sensor.location.gps.listener.GPSDataUpdatedListener.GPSUpdateInterest;
import ecologylab.xml.xml_inherit;
import ecologylab.xml.ElementState.xml_attribute;
import ecologylab.xml.ElementState.xml_nested;
import ecologylab.xml.types.element.ArrayListState;

/**
 * Represents an instant of GPS data computed from a series of NMEA strings.
 * Each component of the datum is as up-to-date as possible as of the time
 * stamp. Whenever no new data is provided, the old data is retained.
 * 
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 * 
 */
@xml_inherit public class GPSDatum extends LocationStatus<GeoCoordinate>
{
	/** Indicates no GPS. */
	public static final int								GPS_QUAL_NO								= 0;

	/** Indicates GPS satellite fix only. */
	public static final int								GPS_QUAL_GPS							= 1;

	/** Indicates GPS satellite fix + differential signal. */
	public static final int								GPS_QUAL_DGPS							= 2;

	/** Indicates that there is no calcuating mode set. */
	public static final int								CALC_MODE_NONE							= 1;

	/** Indicates that the calculating mode is 2D. */
	public static final int								CALC_MODE_2D							= 2;

	/** Indicates that the calculating mode is 3D. */
	public static final int								CALC_MODE_3D							= 3;

	/**
	 * Quality of GPS data; values will be either GPS_QUAL_NO, GPS_QUAL_GPS,
	 * GPS_QUAL_DGPS.
	 */
	@xml_attribute protected int						gpsQual;

	@xml_attribute protected int						numSats;

	/**
	 * Horizontal Dilution of Precision - approximation of the size of the area
	 * in which the actual location of the GPS is horizontally based on the
	 * spread of the fixed satellites; higher numbers are worse, smaller mean
	 * better precision; this value may range between 1-50.
	 * 
	 * According to
	 * http://www.codepedia.com/1/Geometric+Dilution+of+Precision+(DOP):
	 * 
	 * <table>
	 * <tr>
	 * <td>DOP</td>
	 * <td>Rating</td>
	 * <td>Description</td>
	 * <tr>
	 * <td>1</td>
	 * <td>Ideal</td>
	 * <td>This is the highest possible confidence level to be used for
	 * applications demanding the highest possible precision at all times.</td>
	 * </tr>
	 * <tr>
	 * <td>2-3</td>
	 * <td>Excellent</td>
	 * <td>At this confidence level, positional measurements are considered
	 * accurate enough to meet all but the most sensitive applications.</td>
	 * </tr>
	 * <tr>
	 * <td>4-6</td>
	 * <td>Good</td>
	 * <td>Represents a level that marks the minimum appropriate for making
	 * business decisions. Positional measurements could be used to make reliable
	 * in-route navigation suggestions to the user.</td>
	 * </tr>
	 * <tr>
	 * <td>7-8</td>
	 * <td>Moderate</td>
	 * <td>Positional measurements could be used for calculations, but the fix
	 * quality could still be improved. A more open view of the sky is
	 * recommended.</td>
	 * </tr>
	 * <tr>
	 * <td>9-20</td>
	 * <td>Fair</td>
	 * <td>Represents a low confidence level. Positional measurements should be
	 * discarded or used only to indicate a very rough estimate of the current
	 * location.</td>
	 * </tr>
	 * <tr>
	 * <td>21-50</td>
	 * <td>Poor At this level, measurements are inaccurate by half a football
	 * field or more and should be discarded.</td>
	 * </tr>
	 * </table>
	 */
	@xml_attribute protected float					hdop;

	/** Position Dillution of Precision */
	@xml_attribute protected float					pdop;

	/** Vertical Dillution of Precision */
	@xml_attribute protected float					vdop;

	/**
	 * The altitude of the antenna of the GPS (location where the signals are
	 * recieved). In meters.
	 */
	@xml_attribute protected float					geoidHeight;

	/** The differential between the elipsoid and the geoid. In meters. */
	@xml_attribute protected float					heightDiff;

	@xml_attribute protected float					dgpsAge;

	@xml_attribute protected int						dgpsRefStation;

	/** Indicates whether or not the current GPS data is valid. */
	@xml_attribute protected boolean					dataValid;

	/**
	 * Indicates whether or not the calculation mode (2D/3D) is automatically
	 * selected.
	 */
	@xml_attribute protected boolean					autoCalcMode;

	/**
	 * Calculating mode (2D/3D); valid values are CALC_MODE_NONE, CALC_MODE_2D,
	 * or CALC_MODE_3D.
	 */
	@xml_attribute protected int						calcMode;

	/**
	 * References to data about the currently-tracked satellites (space vehicles,
	 * SVs).
	 */
	@xml_nested private ArrayListState<SVData>	trackedSVs;

	/**
	 * All up-to-date data on SVs that have been reported by the GPS hardware.
	 */
	protected HashMap<Integer, SVData>				allSVs;

	/** Used for moving data around when processing NMEA sentences. */
	private char[]											tempDataStore;

	/**
	 * List of listeners who want to be notified of latitude or longitude
	 * updates.
	 */
	private List<GPSDataUpdatedListener>			latLonUpdatedListeners;

	/** List of listeners who want to be notified of altitude updates. */
	private List<GPSDataUpdatedListener>			altUpdatedListeners;

	/**
	 * List of listeners who want to be notified of any updates not covered
	 * above.
	 */
	private List<GPSDataUpdatedListener>			otherUpdatedListeners;

	/** Semaphore for instantiating the above lists lazilly. */
	private Object											listenerLock							= new Object();

	/** Set of listeners to notify for this update, as determined by interest. */
	private Set<GPSDataUpdatedListener>				gpsDataUpdatedListenersToUpdate	= new HashSet<GPSDataUpdatedListener>();

	/**
	 * A Point2D.Double representation of this's latitude and longitude,
	 * instantiated and filled through lazy evaluation, when needed.
	 */
	private Point2D.Double								pointRepresentation					= null;

	/**
	 * Indicates that pointRepresentation is out of synch with the state of this
	 * object.
	 */
	private boolean										pointDirty								= true;

	public GPSDatum()
	{
	}

	public GPSDatum(double latDeg, double latMin, double lonDeg, double lonMin)
	{
		this.currentLocation.setLat(new AngularCoord(latDeg, latMin, 0));
		this.currentLocation.setLon(new AngularCoord(lonDeg, lonMin, 0));
	}

	public GPSDatum(double latDeg, double lonDeg)
	{
		this(latDeg, 0f, lonDeg, 0f);
	}

	private List<GPSDataUpdatedListener> latLonUpdatedListeners()
	{
		if (this.latLonUpdatedListeners == null)
		{
			synchronized (this.listenerLock)
			{
				if (this.latLonUpdatedListeners == null)
				{
					this.latLonUpdatedListeners = new LinkedList<GPSDataUpdatedListener>();
				}
			}
		}

		return this.latLonUpdatedListeners;
	}

	private List<GPSDataUpdatedListener> altUpdatedListeners()
	{
		if (this.altUpdatedListeners == null)
		{
			synchronized (this.listenerLock)
			{
				if (this.altUpdatedListeners == null)
				{
					this.altUpdatedListeners = new LinkedList<GPSDataUpdatedListener>();
				}
			}
		}

		return this.altUpdatedListeners;
	}

	private List<GPSDataUpdatedListener> otherUpdatedListeners()
	{
		if (this.otherUpdatedListeners == null)
		{
			synchronized (this.listenerLock)
			{
				if (this.otherUpdatedListeners == null)
				{
					this.otherUpdatedListeners = new LinkedList<GPSDataUpdatedListener>();
				}
			}
		}

		return this.otherUpdatedListeners;
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
	 * @return positive if this is farther north than that, negative if that is
	 *         more north; 0 if they lie on exactly the same parallel.
	 */
	public double compareNS(GPSDatum that)
	{
		return this.getLat() - that.getLat();
	}

	/**
	 * @param that
	 * @return compares two GPSDatum's based on the acute angle between their
	 *         longitudes. Returns 1 if this is farther east than that, -1 if
	 *         this is farther west, 0 if the two points lie on the same arc,
	 *         180/-180 if they are opposite.
	 */
	public double compareEW(GPSDatum that)
	{
		double diff = this.currentLocation.getLon().coord - that.getLon();

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

	public static void main(String[] args)
	{
		GPSDatum d = new GPSDatum();

		d
				.integrateGPSData("GPRMC,223832.804,V,3037.3725,N,09620.2286,W,0.00,0.00,120208,,,N*6C");
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

		// check the checksum before doing any processing
		int checkSumSplit = gpsData.lastIndexOf('*'); // TODO maybe more
		// efficient just to
		// assume last 2 are
		// checksum

		String messageStringToCheck = gpsData.substring(0, checkSumSplit);

		int checkSum = 0;

		for (int i = 0; i < messageStringToCheck.length(); i++)
		{
			checkSum = (checkSum ^ messageStringToCheck.charAt(i));
		}

		String computedCheckSum = Integer.toHexString((checkSum & 0xF0) >>> 4)
				+ Integer.toHexString(checkSum & 0x0F);

		if (computedCheckSum.toUpperCase().equals(
				gpsData.substring(checkSumSplit + 1)))
		{

			int dataLength = gpsData.length();
			int i = 6; // start looking after "GPXXX," -- the header of the
			// message

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

			// TODO don't assume anything!!!

			if (fieldBase != null)
			{
				synchronized (tempDataStore)
				{
					for (GPSDataFieldBase field : fieldBase)
					{
						finishedField = false;
						dataStart = i;

						// now we will read each data field in, one at at time, since
						// we
						// know the order of the GGA data set.
						// we will break out of each for loop when finished.
						while (i < dataLength && !finishedField)
						{
							tempData[i] = gpsData.charAt(i);

							if (tempData[i] == ',')
							{
								if (i - dataStart > 0)
								{
									field.update(new String(tempData, dataStart,
											(i - dataStart)), this);
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

			// TODO GET OTHER GPS INTERESTS!!!

			this.fireGPSDataUpdatedEvent();
		}
		else
		{
			debug("NMEA sentence checksum bad: " + gpsData);
		}
	}

	private void fireGPSDataUpdatedEvent()
	{
		for (GPSDataUpdatedListener l : this.gpsDataUpdatedListenersToUpdate)
		{
			l.gpsDatumUpdated(this);
		}
	}

	@Override public String toString()
	{
		return new String("GPSDatum: " + this.currentLocation.getLat().coord
				+ ", " + this.currentLocation.getLon().coord);
	}

	/**
	 * determine longitude sign
	 * 
	 * @param src
	 */
	public void updateLonHemisphere(String src)
	{
		this.currentLocation.lon.setHemisphere(src.charAt(0));

		this.pointDirty = true;
	}

	/**
	 * determine longitude degress and minutes
	 * 
	 * @param src
	 */
	public void updateLon(String src)
	{
		double oldLon = this.currentLocation.getLon().coord;

		this.currentLocation.setLon(new AngularCoord(Integer.parseInt(src
				.substring(0, 3)), Double.parseDouble(src.substring(3)), 0));

		this.pointDirty = true;

		if (oldLon != this.currentLocation.getLon().coord
				&& this.latLonUpdatedListeners != null)
		{
			this.gpsDataUpdatedListenersToUpdate
					.addAll(this.latLonUpdatedListeners);
		}
	}

	/**
	 * determine latitude sign
	 * 
	 * @param src
	 */
	public void updateLatHemisphere(String src)
	{
		this.currentLocation.getLat().setHemisphere(src.charAt(0));

		this.pointDirty = true;
	}

	/**
	 * determine latitude degrees and minutes
	 * 
	 * @param src
	 */
	public void updateLat(String src)
	{
		double oldLat = this.currentLocation.getLat().coord;

		this.currentLocation.setLat(new AngularCoord(Integer.parseInt(src
				.substring(0, 2)), Double.parseDouble(src.substring(2)), 0));

		this.pointDirty = true;

		if (oldLat != this.currentLocation.getLat().coord
				&& this.latLonUpdatedListeners != null)
		{
			this.gpsDataUpdatedListenersToUpdate
					.addAll(this.latLonUpdatedListeners);
		}
	}

	/**
	 * Stores the UTC time according to the way it is represented in the NMEA
	 * sentence: HHMMSS.S
	 * 
	 * @param src
	 */
	public void updateUtcPosTime(String src)
	{
		this.utcTime = src;
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

	public void updateDataValid(String src)
	{
		switch (src.charAt(0))
		{
		case 'A':
		case 'a':
			this.dataValid = true;
			break;
		case 'V':
		case 'v':
			this.dataValid = false;
			break;
		}
	}

	/**
	 * @param src
	 */
	public void updateAutoCalcMode(String src)
	{
		switch (src.charAt(0))
		{
		case 'A':
		case 'a':
			this.autoCalcMode = true;
			break;
		case 'M':
		case 'm':
			this.autoCalcMode = false;
			break;
		}
	}

	/**
	 * @param src
	 */
	public void updateCalcMode(String src)
	{
		// values will be either 0, 1, or 2
		this.calcMode = Integer.parseInt(src);
	}

	/**
	 * Specifies the SV number to be added to the list of tracked SVs. Because
	 * NMEA sentences report data on up to 12 tracked SVs, each SV has an index
	 * (in addition to its ID).
	 * 
	 * @param src
	 *           the data String containing the ID of the tracked SV.
	 * @param i
	 *           the index of the tracked SV (will overwrite whichever was stored
	 *           previously).
	 */
	public void updateSV(String src, int i)
	{
		Integer index = new Integer(i);
		int sVID = Integer.parseInt(src);

		HashMap<Integer, SVData> allSVsLocal = this.allSVs();

		SVData currentData = allSVsLocal.get(sVID);

		if (currentData == null)
		{
			currentData = new SVData(sVID);

			allSVsLocal.put(index, currentData);
		}

		// TODO !!! this.trackedSVs().set(i, currentData);
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
	public void updatePDOP(String src)
	{
		this.pdop = Float.parseFloat(src);
	}

	/**
	 * @param src
	 */
	public void updateVDOP(String src)
	{
		this.vdop = Float.parseFloat(src);
	}

	/**
	 * @param src
	 */
	public void updateHeightDiff(String src)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @return the lat
	 */
	public double getLat()
	{
		return this.currentLocation.getLat().coord;
	}

	/**
	 * @return the lon
	 */
	public double getLon()
	{
		return this.currentLocation.getLon().coord;
	}

	/**
	 * @param lat
	 *           the lat to set
	 */
	public void setLat(double lat)
	{
		this.currentLocation.setLat(lat);

		this.pointDirty = true;
	}

	/**
	 * @param lon
	 *           the lon to set
	 */
	public void setLon(double lon)
	{
		this.currentLocation.setLon(lon);

		this.pointDirty = true;
	}

	/**
	 * Gets the latitude and longitude of this datum as a Point2D, where x =
	 * longitude and y = latitude.
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
			this.pointRepresentation.setLocation(
					this.currentLocation.getLon().coord, this.currentLocation
							.getLat().coord);
			this.pointDirty = false;
		}

		return pointRepresentation;
	}

	public void addGPSDataUpdatedListener(GPSDataUpdatedListener l)
	{
		EnumSet<GPSUpdateInterest> interestSet = l.getInterestSet();

		if (interestSet.contains(GPSUpdateInterest.LAT_LON))
		{
			this.latLonUpdatedListeners().add(l);
		}
		if (interestSet.contains(GPSUpdateInterest.ALT))
		{
			this.altUpdatedListeners().add(l);
		}
		if (interestSet.contains(GPSUpdateInterest.OTHERS))
		{
			this.otherUpdatedListeners().add(l);
		}
	}

	private HashMap<Integer, SVData> allSVs()
	{
		if (this.allSVs == null)
		{
			this.allSVs = new HashMap<Integer, SVData>();
		}

		return this.allSVs;
	}

	protected ArrayListState<SVData> trackedSVs()
	{
		if (this.trackedSVs == null)
		{
			this.trackedSVs = new ArrayListState<SVData>();
		}

		return this.trackedSVs;
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
}
