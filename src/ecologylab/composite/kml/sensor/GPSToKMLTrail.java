/**
 * 
 */
package ecologylab.composite.kml.sensor;

import ecologylab.sensor.location.gps.data.GPSDatum;
import ecologylab.sensor.location.gps.data.GeoCoordinate;
import ecologylab.sensor.location.gps.listener.GPSDataUpdatedListener;
import ecologylab.sensor.location.gps.listener.GPSDataUpdater;
import ecologylab.serialization.library.kml.geometry.Geometry;

import java.util.ArrayList;
import java.util.EnumSet;

/**
 * Repeats the coordinates from a GPS to a KML Geometry's object. This object can then be placed
 * into a Kml object's child to send to Google Earth.
 * 
 * @author Zach
 */
public class GPSToKMLTrail extends GPSDataUpdater implements GPSDataUpdatedListener
{
	Geometry														kmlGeographyTarget;

	int																	trailLength;

	int																	sampleRate;

	int																	currentSample;

	/**
	 * The list of coordinates inside the kmlGeometryTarget. Automatically set when the
	 * kmlGeographyTarget is set. kmlGeographyTarget is automatically update with this object whenever
	 * it is changed by new GPS data.
	 */
	protected ArrayList<GeoCoordinate>	coordinateList;

	/**
	 * 
	 * @param kmlGeographyTarget
	 * @param trailLength
	 * @param sampleRate
	 *          - the number of times the GPS data has to be updated before it appears in the KML list
	 */
	public GPSToKMLTrail(Geometry kmlGeographyTarget, int trailLength, int sampleRate)
	{
		this.kmlGeographyTarget = kmlGeographyTarget;
		this.trailLength = trailLength;

		this.sampleRate = sampleRate;

		this.coordinateList = kmlGeographyTarget.getCoordinateList();

		this.addDataUpdatedListener(this);
	}

	public void gpsDatumUpdated(GPSDatum datum)
	{
		currentSample++;
		if (currentSample == sampleRate)
		{
			// go to add the updated data
			if (coordinateList.size() == trailLength)
			{
				coordinateList.remove(0);
			}

			coordinateList.add(new GeoCoordinate(this.datum.getLat(), this.datum.getLon(), 0));

			currentSample = 0;
		}
	}

	/**
	 * Indicates which GPS update operations this is interested in; in this case, all of them.
	 */
	private static EnumSet<GPSUpdateInterest>	interestSet	= EnumSet.of(GPSUpdateInterest.LAT_LON);

	public EnumSet<GPSUpdateInterest> getInterestSet()
	{
		return interestSet;
	}
}
