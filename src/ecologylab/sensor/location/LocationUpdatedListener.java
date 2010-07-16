package ecologylab.sensor.location;

import ecologylab.sensor.location.compass.CompassDataListener;
import ecologylab.sensor.location.gps.listener.GPSDataUpdatedListener;

public interface LocationUpdatedListener extends GPSDataUpdatedListener, CompassDataListener
{
	public static final String LOCATION_UPDATE_LISTENER = "LOCATION_UPDATE_LISTENER";
}
