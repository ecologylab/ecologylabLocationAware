package ecologylab.services.messages;

import java.util.Calendar;
import java.util.Iterator;
import java.util.concurrent.BlockingDeque;

import ecologylab.collections.Scope;
import ecologylab.oodss.messages.RequestMessage;
import ecologylab.oodss.messages.ResponseMessage;
import ecologylab.sensor.location.compass.CompassDatum;
import ecologylab.sensor.location.gps.data.GPSDatum;
import ecologylab.serialization.annotations.simpl_scalar;
import ecologylab.services.distributed.server.varieties.GeoServer.GeoServer;

public class LocationDataRequest extends RequestMessage
{
	@simpl_scalar
	protected Calendar	time	= null;

	public LocationDataRequest()
	{

	}

	public LocationDataRequest(Calendar time)
	{
		this.time = time;
	}

	@Override
	public ResponseMessage performService(Scope clientSessionScope)
	{
		BlockingDeque<CompassDatum> cData = (BlockingDeque<CompassDatum>) clientSessionScope
				.get(GeoServer.COMPASS_DATA);
		BlockingDeque<GPSDatum> gData = (BlockingDeque<GPSDatum>) clientSessionScope
				.get(GeoServer.GPS_DATA);
		
		if (time == null)
		{
			return new LocationDataResponse(cData.getLast(), gData.getLast());
		}
		else
		{ // search for data prior to the current time
			Iterator<CompassDatum> cDataIter = cData.descendingIterator();
			Iterator<GPSDatum> gDataIter = gData.descendingIterator();

			CompassDatum cResult = null;
			GPSDatum gResult = null;

			while (cResult == null && cDataIter.hasNext())
			{
				CompassDatum cDatum = cDataIter.next();

				if (!time.before(cDatum.getTime()))
					cResult = cDatum;
			}

			if (cResult == null)
				cResult = cData.getLast();

			while (gResult == null && gDataIter.hasNext())
			{
				GPSDatum gDatum = gDataIter.next();

				if (!time.before(gDatum.getTime()))
					gResult = gDatum;
			}

			if (gResult == null)
				gResult = gData.getLast();

			return new LocationDataResponse(cResult, gResult);
		}
	}
}
