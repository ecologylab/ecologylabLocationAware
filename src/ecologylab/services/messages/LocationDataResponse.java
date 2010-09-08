package ecologylab.services.messages;

import ecologylab.collections.Scope;
import ecologylab.oodss.messages.ResponseMessage;
import ecologylab.sensor.location.LocationUpdatedListener;
import ecologylab.sensor.location.compass.CompassDatum;
import ecologylab.sensor.location.gps.data.GPSDatum;
import ecologylab.serialization.ElementState.simpl_composite;

public class LocationDataResponse extends ResponseMessage
{
	
	@simpl_composite
	public CompassDatum compassData;
	
	@simpl_composite
	public GPSDatum gpsData;
	
	public LocationDataResponse()
	{
		
	}
	
	public LocationDataResponse(CompassDatum cData, GPSDatum gpsData)
	{
		this.compassData = cData;
		this.gpsData = gpsData;
	}
	
	@Override
	public void processResponse(Scope s)
	{
		LocationUpdatedListener listener = (LocationUpdatedListener) s.get(LocationUpdatedListener.LOCATION_UPDATE_LISTENER);
		if(listener != null)
		{
			if (compassData != null)
				listener.compassDataUpdated(compassData);
			if(gpsData != null)
				listener.gpsDatumUpdated(gpsData);
		}
	}

	@Override
	public boolean isOK()
	{
		return true;
	}

}
