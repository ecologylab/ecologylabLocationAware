/**
 * 
 */
package ecologylab.sensor.gps.data;

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
	@xml_attribute float							utcPosTime;

	@xml_attribute private int					latDeg;

	@xml_attribute float							latMin;

	@xml_attribute int							lonDeg;

	@xml_attribute float							lonMin;

	/** Quality of GPS data; values will be either GPS_QUAL_NO, GPS_QUAL_GPS, GPS_QUAL_DGPS. */
	@xml_attribute int							gpsQual;

	/** Indicates no GPS. */
	public static final int						GPS_QUAL_NO					= 0;

	/** Indicates GPS satellite fix only. */
	public static final int						GPS_QUAL_GPS				= 1;

	/** Indicates GPS satellite fix + differential signal. */
	public static final int						GPS_QUAL_DGPS				= 2;

	@xml_attribute int							numSats;

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
	@xml_attribute float							hdop;

	/** The altitude of the antenna of the GPS (location where the signals are recieved). In meters. */
	@xml_attribute float							geoidHeight;

	/** The differential between the elipsoid and the geoid. In meters. */
	@xml_attribute float							heightDiff;

	@xml_attribute float							dgpsAge;

	@xml_attribute int							dgpsRefStation;

	private char[]									tempDataStore;

	private List<GPSDataUpdatedListener>	gpsDataUpdatedListeners	= new LinkedList<GPSDataUpdatedListener>();

	public GPSDatum()
	{
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
	 * Splits and stores data from an NMEA GPS data set.
	 * 
	 * @param gpsData
	 *           a GPS data set, minus $ header and <CR><LF> trailer.
	 */
	public void integrateGPSData(String gpsData)
	{
		char[] tempData = tempDataStore();

		int dataLength = gpsData.length();
		int i = 6; // start looking after "GPGGA," -- the header of the message

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
		try
		{
			return this.translateToXML().toString();
		}
		catch (XMLTranslationException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * determine longitude sign
	 * 
	 * @param src
	 */
	public void updateLonHemisphere(String src)
	{
		if (src.charAt(0) == 'W') // southern hemisphere == negative latitude
			this.lonDeg *= -1;
	}

	/**
	 * determine longitude degress and minutes
	 * 
	 * @param src
	 */
	public void updateLon(String src)
	{
		this.lonDeg = Integer.parseInt(src.substring(0, 3));
		this.lonMin = Float.parseFloat(src.substring(3));
	}

	/**
	 * determine latitude sign
	 * 
	 * @param src
	 */
	public void updateLatHemisphere(String src)
	{
		if (src.charAt(0) == 'S') // southern hemisphere == negative latitude
			this.latDeg *= -1;
	}

	/**
	 * determine latitude degrees and minutes
	 * 
	 * @param src
	 */
	public void updateLat(String src)
	{
		this.latDeg = Integer.parseInt(src.substring(0, 2));
		this.latMin = Float.parseFloat(src.substring(2));
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

}
