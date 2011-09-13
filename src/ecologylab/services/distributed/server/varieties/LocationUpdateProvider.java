package ecologylab.services.distributed.server.varieties;

import ecologylab.sensor.location.LocationUpdatedListener;

public interface LocationUpdateProvider
{

	/**
	 * A List<LocationUpdatedListener> of program objects that need to be updated when the simulated
	 * data arrive.
	 */
	public static final String	LOCATION_UPDATE_LISTENER_LIST	= "LOCATION_UPDATE_LISTENER_LIST";

	public abstract void addLocationUpdateListener(LocationUpdatedListener l);

}