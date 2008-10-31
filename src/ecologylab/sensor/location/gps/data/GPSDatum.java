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
import ecologylab.sensor.location.gps.data.dataset.GSV;
import ecologylab.sensor.location.gps.data.dataset.RMC;
import ecologylab.sensor.location.gps.listener.GPSDataUpdatedListener;
import ecologylab.sensor.location.gps.listener.GPSDataUpdatedListener.GPSUpdateInterest;
import ecologylab.xml.xml_inherit;

/**
 * Represents an instant of GPS data computed from a series of NMEA strings. Each component of the
 * datum is as up-to-date as possible as of the time stamp. Whenever no new data is provided, the
 * old data is retained.
 * 
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 * 
 */
@xml_inherit
public class GPSDatum extends LocationStatus implements GPSConstants
{
	public enum DopType
	{
		PDOP, VDOP, HDOP, NOT_AVAILABLE
	}

	/**
	 * Quality of GPS data; values will be either GPS_QUAL_NO, GPS_QUAL_GPS, GPS_QUAL_DGPS.
	 */
	@xml_attribute
	public int													gpsQual;

	/**
	 * The number of satellites the GPS receiver is using to compute the solution.
	 */
	@xml_attribute
	public int													numSats;

	/**
	 * Horizontal Dilution of Precision - approximation of the size of the area in which the actual
	 * location of the GPS is horizontally based on the spread of the fixed satellites; higher numbers
	 * are worse, smaller mean better precision; this value may range between 1-50.
	 * 
	 * See http://www.codepedia.com/1/Geometric+Dilution+of+Precision+(DOP) for more information.
	 */
	@xml_attribute
	public float												hdop;

	/** Position Dillution of Precision */
	@xml_attribute
	public float												pdop;

	/** Vertical Dillution of Precision */
	@xml_attribute
	public float												vdop;

	/**
	 * The altitude of the antenna of the GPS (location where the signals are recieved). In meters.
	 */
	@xml_attribute
	protected float											geoidHeight;

	/** The differential between the elipsoid and the geoid. In meters. */
	@xml_attribute
	protected float											heightDiff;

	@xml_attribute
	protected float											dgpsAge;

	@xml_attribute
	protected int												dgpsRefStation;

	/** Indicates whether or not the current GPS data is valid. */
	@xml_attribute
	protected boolean										dataValid;

	/**
	 * Indicates whether or not the calculation mode (2D/3D) is automatically selected.
	 */
	@xml_attribute
	protected boolean										autoCalcMode;

	/**
	 * Calculating mode (2D/3D); valid values are CALC_MODE_NONE, CALC_MODE_2D, or CALC_MODE_3D.
	 */
	@xml_attribute
	protected int												calcMode;

	/**
	 * Used during the processing of a GSV (GNSS Satellites in View) sentence. Specifies which SV's
	 * data is being updated. Because specific SV data comes over several pieces of a message, and
	 * these pieces are processed independently using enums, the GSPDatum object must track the
	 * current SV, to ensure that it is updated correctly.
	 */
	private SVData											currentSV;

	/**
	 * References to data about the currently-tracked satellites (space vehicles, SVs).
	 */
	@xml_nested
	private SVData[]										trackedSVs;

	/**
	 * All up-to-date data on SVs that have been reported on by the GPS hardware.
	 */
	protected HashMap<Integer, SVData>	allSVs;

	/** Used for moving data around when processing NMEA sentences. */
	private char[]											tempDataStore;

	public GPSDatum()
	{
		this.currentLocation = new GeoCoordinate();
	}

	public GPSDatum(double latDeg, double latMin, double lonDeg, double lonMin)
	{
		this();

		this.currentLocation.setLat(AngularCoord.fromDegMinSec(latDeg, latMin, 0));
		this.currentLocation.setLon(AngularCoord.fromDegMinSec(lonDeg, lonMin, 0));
	}

	public GPSDatum(double latDeg, double lonDeg)
	{
		this(latDeg, 0f, lonDeg, 0f);
	}

	/**
	 * Ensure that tempDataStore is instantiated and clean, then return it. Used for lazy
	 * instantiation; no need to instantiate it if this is a serialized set of data from somewhere
	 * else (for example).
	 * 
	 * @return
	 */
	private synchronized char[] tempDataStore()
	{
		if (tempDataStore == null)
		{
			tempDataStore = new char[80];
		}

		return tempDataStore;
	}

	/**
	 * @param that
	 * @return positive if this is farther north than that, negative if that is more north; 0 if they
	 *         lie on exactly the same parallel.
	 */
	public double compareNS(GPSDatum that)
	{
		return this.getLat() - that.getLat();
	}

	/**
	 * @param that
	 * @return compares two GPSDatum's based on the acute angle between their longitudes. Returns 1 if
	 *         this is farther east than that, -1 if this is farther west, 0 if the two points lie on
	 *         the same arc, 180/-180 if they are opposite.
	 */
	public double compareEW(GPSDatum that)
	{
		double diff = this.currentLocation.getLon() - that.getLon();

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

		d.integrateGPSData("GPRMC,223832.804,V,3037.3725,N,09620.2286,W,0.00,0.00,120208,,,N*6C");
	}

	/**
	 * Splits and stores data from an NMEA GPS data set.
	 * 
	 * @param gpsData
	 *          a GPS data set, minus $ header and <CR><LF> trailer.
	 */
	public synchronized void integrateGPSData(String gpsData)
	{
		char[] tempData = tempDataStore();

		// check the checksum before doing any processing
		int checkSumSplit = gpsData.length() - 3;

		String messageStringToCheck = gpsData.substring(0, checkSumSplit);

		int checkSum = 0;

		for (int i = 0; i < messageStringToCheck.length(); i++)
		{
			checkSum = (checkSum ^ messageStringToCheck.charAt(i));
		}

		String computedCheckSum = Integer.toHexString((checkSum & 0xF0) >>> 4)
				+ Integer.toHexString(checkSum & 0x0F);

		if (computedCheckSum.toUpperCase().equals(gpsData.substring(checkSumSplit + 1)))
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
					// check GGA
					if (gpsData.charAt(4) == 'A')
					{
						fieldBase = GGA.values();
					}
					break;
				case ('L'):
					// check GLL
					if (gpsData.charAt(4) == 'L')
					{
						fieldBase = GLL.values();
					}
					break;
				case ('S'):
					switch (gpsData.charAt(4))
					{
					case ('A'): // GSA
						fieldBase = GSA.values();
						break;
					case ('V'): // GSV
						fieldBase = GSV.values();
						break;
					}
					break;
				}
				break;
			case ('R'): // check RMC
				if (gpsData.charAt(3) == 'M' && gpsData.charAt(4) == 'C')
				{
					fieldBase = RMC.values();
				}
				break;
			case ('V'):
				// TODO check VTG
				break;
			case ('Z'):
				// TODO check ZDA
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

						/*
						 * now we will read each data field in, one at at time, since we know the order of the
						 * data set. we will break out of each for loop when finished.
						 */
						while (i < dataLength && !finishedField)
						{
							try
							{ // XXX this should be removed once the bug is fixed
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
							catch (Exception e)
							{
								error("Exception occurred!");
								error("i                = " + i);
								error("gpsData.length() = " + gpsData.length());
								error("tempData.length  = " + tempData.length);

								e.printStackTrace();
							}
						}
					}
				}
			}
			else
			{
				debug(gpsData.substring(2, 5) + " not recognized.");
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

	@Override
	public String toString()
	{
		return new String("GPSDatum: " + this.currentLocation.getLat() + ", "
				+ this.currentLocation.getLon());
	}

	/**
	 * determine longitude sign
	 * 
	 * @param src
	 */
	public void updateLonHemisphere(String src)
	{
		this.currentLocation.setLon(AngularCoord.signForHemisphere(src.charAt(0), currentLocation
				.getLon()));

		this.pointDirty = true;
	}

	/**
	 * determine longitude degress and minutes
	 * 
	 * @param src
	 */
	public void updateLon(String src)
	{
		double oldLon = this.currentLocation.getLon();

		this.currentLocation.setLon(AngularCoord.fromDegMinSec(Integer.parseInt(src.substring(0, 3)),
				Double.parseDouble(src.substring(3)), 0));

		this.pointDirty = true;

		if (oldLon != this.currentLocation.getLon() && this.latLonUpdatedListeners != null)
		{
			this.gpsDataUpdatedListenersToUpdate.addAll(this.latLonUpdatedListeners);
		}
	}

	/**
	 * determine latitude sign
	 * 
	 * @param src
	 */
	public void updateLatHemisphere(String src)
	{
		this.currentLocation.setLat(AngularCoord.signForHemisphere(src.charAt(0), currentLocation
				.getLat()));

		this.pointDirty = true;
	}

	/**
	 * determine latitude degrees and minutes
	 * 
	 * @param src
	 */
	public void updateLat(String src)
	{
		double oldLat = this.currentLocation.getLat();

		this.currentLocation.setLat(AngularCoord.fromDegMinSec(Integer.parseInt(src.substring(0, 2)),
				Double.parseDouble(src.substring(2)), 0));

		this.pointDirty = true;

		if (oldLat != this.currentLocation.getLat() && this.latLonUpdatedListeners != null)
		{
			this.gpsDataUpdatedListenersToUpdate.addAll(this.latLonUpdatedListeners);
		}
	}

	/**
	 * Stores the UTC time according to the way it is represented in the NMEA sentence: HHMMSS.S
	 * 
	 * @param utcString
	 */
	public void updateUtcPosTime(String utcString)
	{
		this.utcTime = utcString;
	}

	/**
	 * Update the number of satellites the GPS receiver is using to produce a solution.
	 * 
	 * @param src
	 *          - part of the NMEA string carrying the relevant data.
	 */
	public void updateNumSats(String src)
	{
		this.numSats = Integer.parseInt(src);
	}

	/**
	 * Update the quality rating for the GPS receiver: none (0), GPS (1), or DGPS (2).
	 * 
	 * @param src
	 *          - part of the NMEA string carrying the relevant data.
	 */
	public void updateGPSQual(String src)
	{
		this.gpsQual = Integer.parseInt(src);
	}

	/**
	 * Update the differential GPS reference station.
	 * 
	 * @param src
	 *          - part of the NMEA string carrying the relevant data.
	 */
	public void updateDGPSRef(String src)
	{
		this.dgpsRefStation = Integer.parseInt(src);
	}

	/**
	 * Checks the unit mode for height; this should be meters, and thus should be "M".
	 * 
	 * TODO handle other units, instead of printing an error? I haven't seen any documentation
	 * describing the use of other units - Zach
	 * 
	 * @param src
	 *          - part of the NMEA string carrying the relevant data.
	 */
	public void updateDiffHeightUnit(String src)
	{
		if (src.charAt(0) != 'M')
		{
			warning("GPS is not using meters: " + src);
		}
	}

	/**
	 * Updates the age of the differential GPS reading, per the GPS.
	 * 
	 * @param src
	 *          - part of the NMEA string carrying the relevant data.
	 */
	public void updateDGPSAge(String src)
	{
		this.dgpsAge = Float.parseFloat(src);
	}

	/**
	 * @param src
	 *          - part of the NMEA string carrying the relevant data.
	 */
	public void updateHeightUnit(String src)
	{
		if (src.charAt(0) != 'M')
		{
			warning("GPS is not using meters: " + src);
		}
	}

	/**
	 * Updates the height of the geoid, indicated at this location.
	 * 
	 * @param src
	 *          - part of the NMEA string carrying the relevant data.
	 */
	public void updateGeoidHeight(String src)
	{
		this.geoidHeight = Float.parseFloat(src);
	}

	/**
	 * Updates the validity rating for the data. Data can be valid ("A") or invalid ("V").
	 * 
	 * @param src
	 *          - part of the NMEA string carrying the relevant data.
	 */
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
	 * Updates the auto calculation mode setting. Can be automatic ("A") or manual ("M").
	 * 
	 * @param src
	 *          - part of the NMEA string carrying the relevant data.
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
	 * Specifies the SV number to be added to the list of tracked SVs. Because NMEA sentences report
	 * data on up to 12 tracked SVs, each SV has an index (in addition to its ID).
	 * 
	 * @param sVIDString
	 *          the data String containing the ID of the tracked SV.
	 * @param i
	 *          the index of the tracked SV (will overwrite whichever was stored previously).
	 */
	public void addSVToTrackedList(String sVIDString, int i)
	{
		Integer index = new Integer(i);
		int sVID = Integer.parseInt(sVIDString);

		HashMap<Integer, SVData> allSVsLocal = this.allSVs();

		SVData currentData = allSVsLocal.get(sVID);

		if (currentData == null)
		{
			currentData = new SVData(sVID);

			allSVsLocal.put(index, currentData);
		}

		SVData[] trackedSVsLocal = this.trackedSVs();
		trackedSVsLocal[i] = currentData;
	}

	/**
	 * Specifies which SV is going to be updated by the following calls to setCurrentSVElev(...),
	 * setCurrentSVAzi(...), setCurrentSVSNR(...).
	 * 
	 * @param sVIDString
	 *          the ID of the SV to set.
	 */
	public void setCurrentSV(String sVIDString)
	{
		int sVID = Integer.parseInt(sVIDString);

		HashMap<Integer, SVData> allSVsLocal = this.allSVs();

		SVData tempCurrentSV = allSVsLocal.get(sVID);

		if (tempCurrentSV == null)
		{
			tempCurrentSV = new SVData(sVID);
		}

		this.currentSV = tempCurrentSV;
	}

	/**
	 * Sets the elevation on the current SV. THIS METHOD ASSUMES THAT setCurrentSV(...) has been
	 * called in advance; if it has not, your program will crash.
	 * 
	 * @param currentSVElevationString
	 */
	public void setCurrentSVElev(String currentSVElevationString)
	{
		int elevation = Integer.parseInt(currentSVElevationString);

		this.currentSV.setElevation(elevation);
	}

	/**
	 * Sets the azimuth on the current SV. THIS METHOD ASSUMES THAT setCurrentSV(...) has been called
	 * in advance; if it has not, your program will crash.
	 * 
	 * @param currentSVElevationString
	 */
	public void setCurrentSVAzi(String currentSVAzimuthString)
	{
		int azimuth = Integer.parseInt(currentSVAzimuthString);

		this.currentSV.setAzimuth(azimuth);
	}

	/**
	 * Sets the signal-to-noise ratio on the current SV. THIS METHOD ASSUMES THAT setCurrentSV(...)
	 * has been called in advance; if it has not, your program will crash.
	 * 
	 * @param currentSVElevationString
	 */
	public void setCurrentSVSNR(String currentSVSNRString)
	{
		int snr = Integer.parseInt(currentSVSNRString);

		this.currentSV.setSnr(snr);
	}

	public void unsetCurrentSV()
	{
		this.currentSV = null;
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
		return this.currentLocation.getLat();
	}

	public double getAlt()
	{
		return this.currentLocation.getAlt();
	}

	/**
	 * @return the lon
	 */
	public double getLon()
	{
		return this.currentLocation.getLon();
	}

	/**
	 * @param lat
	 *          the lat to set
	 */
	public void setLat(double lat)
	{
		this.currentLocation.setLat(lat);

		this.pointDirty = true;
	}

	/**
	 * @param lon
	 *          the lon to set
	 */
	public void setLon(double lon)
	{
		this.currentLocation.setLon(lon);

		this.pointDirty = true;
	}

	private HashMap<Integer, SVData> allSVs()
	{
		if (this.allSVs == null)
		{
			this.allSVs = new HashMap<Integer, SVData>();
		}

		return this.allSVs;
	}

	protected SVData[] trackedSVs()
	{
		if (this.trackedSVs == null)
		{
			this.trackedSVs = new SVData[12];
		}

		return this.trackedSVs;
	}

	/**
	 * List of listeners who want to be notified of latitude or longitude updates.
	 */
	private List<GPSDataUpdatedListener>	latLonUpdatedListeners;

	/** List of listeners who want to be notified of altitude updates. */
	private List<GPSDataUpdatedListener>	altUpdatedListeners;

	/**
	 * List of listeners who want to be notified of any updates not covered above.
	 */
	private List<GPSDataUpdatedListener>	otherUpdatedListeners;

	/** Semaphore for instantiating the above lists lazilly. */
	private Object												listenerLock										= new Object();

	/** Set of listeners to notify for this update, as determined by interest. */
	private Set<GPSDataUpdatedListener>		gpsDataUpdatedListenersToUpdate	= new HashSet<GPSDataUpdatedListener>();

	/**
	 * A Point2D.Double representation of this's latitude and longitude, instantiated and filled
	 * through lazy evaluation, when needed.
	 */
	private Point2D.Double								pointRepresentation							= null;

	/**
	 * Indicates that pointRepresentation is out of synch with the state of this object.
	 */
	private boolean												pointDirty											= true;

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
	 * Gets the latitude and longitude of this datum as a Point2D, where x = longitude and y =
	 * latitude.
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
			this.pointRepresentation.setLocation(this.currentLocation.getLon(), this.currentLocation
					.getLat());
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

	/**
	 * @return the trackedSVs
	 */
	public SVData[] getTrackedSVs()
	{
		return trackedSVs;
	}

	public int getNumSats()
	{
		return numSats;
	}

	public float getHdop()
	{
		return hdop;
	}

	/**
	 * @return the pdop
	 */
	public float getPdop()
	{
		return pdop;
	}

	/**
	 * @return the vdop
	 */
	public float getVdop()
	{
		return vdop;
	}

	/**
	 * Examines the current data on DOP, and indicates the best type of DOP available. Note that if
	 * there is stale data, this will use it.
	 * 
	 * Types are:
	 * 
	 * PDOP - position dillution of precision (3D)
	 * 
	 * HDOP - horizontal dillution of precision (2D, surface)
	 * 
	 * VDOP - vertical dillution of precision (1D, vertical only)
	 * 
	 * @return
	 */
	public DopType getDopType()
	{
		if (this.pdop > 0)
		{
			return DopType.PDOP;
		}
		else if (this.hdop > 0)
		{
			return DopType.HDOP;
		}
		else if (this.vdop > 0)
		{
			return DopType.VDOP;
		}

		return DopType.NOT_AVAILABLE;
	}

	/**
	 * @return the gpsQual
	 */
	public int getGpsQual()
	{
		return gpsQual;
	}
}
