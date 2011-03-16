package ecologylab.services.messages;

import ecologylab.collections.Scope;
import ecologylab.oodss.messages.RequestMessage;
import ecologylab.oodss.messages.ResponseMessage;
import ecologylab.sensor.location.compass.CompassDatum;
import ecologylab.sensor.location.gps.data.GPSDatum;
import ecologylab.services.distributed.server.varieties.GeoServer.GeoServer;

public class LocationDataRequest extends RequestMessage
{
	
	@Override
	public ResponseMessage performService(Scope clientSessionScope)
	{
		CompassDatum cData = (CompassDatum) clientSessionScope.get(GeoServer.COMPASS_DATUM);
		GPSDatum gData = (GPSDatum) clientSessionScope.get(GeoServer.GPS_DATUM);
		
		LocationDataResponse resp = new LocationDataResponse(cData, gData);
		return resp;
	}

}
