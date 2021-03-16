package ecologylab.services.messages;

import java.util.Iterator;
import java.util.concurrent.BlockingDeque;

import ecologylab.collections.Scope;
import ecologylab.oodss.messages.RequestMessage;
import ecologylab.sensor.location.compass.CompassDatum;
import ecologylab.sensor.location.gps.data.GPSDatum;
import ecologylab.serialization.annotations.simpl_scalar;
import ecologylab.services.distributed.server.varieties.GeoServer.GeoServer;

public class LocationDataRequest extends RequestMessage
{
	@simpl_scalar
	protected long	timeInMillis	= -1;

	public LocationDataRequest()
	{

	}

	public LocationDataRequest(long timeInMillis)
	{
		this.timeInMillis = timeInMillis;
	}

	@Override
	public LocationDataResponse performService(Scope clientSessionScope)
	{
		BlockingDeque<CompassDatum> cData = (BlockingDeque<CompassDatum>) clientSessionScope
				.get(GeoServer.COMPASS_DATA);
		BlockingDeque<GPSDatum> gData = (BlockingDeque<GPSDatum>) clientSessionScope
				.get(GeoServer.GPS_DATA);
		
		if (timeInMillis == -1)
			return new LocationDataResponse(
					(cData != null && cData.size() > 0 ? cData.getLast() : null),
					(gData != null && gData.size() > 0 ? gData.getLast() : null));
		else
		{ // search for data prior to the current time
			Iterator<CompassDatum> cDataIter = cData.descendingIterator();
			Iterator<GPSDatum> gDataIter = gData.descendingIterator();

			CompassDatum cResult = null;
			GPSDatum gResult = null;

			while (cResult == null && cDataIter.hasNext())
			{
				CompassDatum cDatum = cDataIter.next();

				if (cDatum.getTimeInMillis() != -1 && timeInMillis >= cDatum.getTimeInMillis())
					cResult = cDatum;
			}

			if (cResult == null && cData.size() > 0)
				cResult = cData.getLast();

			long lastTime = -1;
			while (gResult == null && gDataIter.hasNext())
			{
				GPSDatum gDatum = gDataIter.next();

				if (gDatum.getTimeInMillis() != -1 && timeInMillis >= gDatum.getTimeInMillis())
				{
					gResult = gDatum;
					System.out.println("Found GPS datum: requested: " + timeInMillis + "; found: "
							+ gDatum.getTimeInMillis() + "; data: " + gDatum.toString() + "; (last: " + lastTime
							+ ")");
				}
				lastTime = gDatum.getTimeInMillis();
			}

			if (gResult == null && gData.size() > 0)
				gResult = gData.getLast();

			return new LocationDataResponse(cResult, gResult);
		}
	}
}
