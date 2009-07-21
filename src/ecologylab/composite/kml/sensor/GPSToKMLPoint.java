/**
 * 
 */
package ecologylab.composite.kml.sensor;

import ecologylab.sensor.location.NMEAStringListener;
import ecologylab.sensor.location.gps.data.GPSDatum;
import ecologylab.sensor.location.gps.data.GeoCoordinate;
import ecologylab.sensor.location.gps.listener.GPSDataUpdater;
import ecologylab.xml.library.kml.geometry.Coordinates;
import ecologylab.xml.library.kml.geometry.Point;

/**
 * @author Administrator
 * 
 */
public class GPSToKMLPoint implements NMEAStringListener
{
	GPSDataUpdater	data	= new GPSDataUpdater(new GPSDatum());

	Point				controlledPoint;

	/**
	 * 
	 */
	public GPSToKMLPoint(Point pointToControl)
	{
		this.controlledPoint = pointToControl;
	}

	public void processIncomingNMEAString(String gpsDataString)
	{
		this.data.processIncomingNMEAString(gpsDataString);

		this.controlledPoint.setCoordinates(new Coordinates(new GeoCoordinate(
				data.getDatum().getLat(), data.getDatum().getLon(), data.getDatum()
						.getAlt())));
	}
}
