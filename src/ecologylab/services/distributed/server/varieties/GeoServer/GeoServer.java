package ecologylab.services.distributed.server.varieties.GeoServer;

import java.io.IOException;
import java.net.BindException;
import java.util.EnumSet;

import ecologylab.collections.Scope;
import ecologylab.oodss.distributed.server.DoubleThreadedNIOServer;
import ecologylab.oodss.distributed.server.NIODatagramServer;
import ecologylab.oodss.messages.DefaultServicesTranslations;
import ecologylab.sensor.location.LocationUpdatedListener;
import ecologylab.sensor.location.compass.CompassDatum;
import ecologylab.sensor.location.gps.data.GPSDatum;
import ecologylab.sensor.location.gps.listener.GPSDataUpdatedListener.GPSUpdateInterest;
import ecologylab.serialization.TranslationScope;
import ecologylab.services.messages.LocationDataRequest;
import ecologylab.services.messages.LocationDataResponse;

public class GeoServer extends DoubleThreadedNIOServer implements LocationUpdatedListener
{
	
	public static final String COMPASS_DATUM = "COMPASS_DATUM";
	private CompassDatum compassData = new CompassDatum();
	
	public static final String GPS_DATUM = "GPS_DATUM";
	private GPSDatum GPSData = new GPSDatum();
	
	private static EnumSet<GPSUpdateInterest> interestSet;
	
	public static Class[] LOCATION_MESSAGE_CLASSES = {LocationDataRequest.class, LocationDataResponse.class};
	
	public GeoServer(int portNumber, Scope objectRegistry,
			boolean useCompression, GPSDatum gData, CompassDatum cData) throws BindException, IOException
	{
		
		super(portNumber, LocationTranslations.get(),
				objectRegistry);

		objectRegistry.put(GPS_DATUM, gData);

		objectRegistry.put(COMPASS_DATUM, cData);

		this.GPSData = gData;

		this.compassData = cData;
	}

	@Override
	public EnumSet<GPSUpdateInterest> getInterestSet()
	{
		if(interestSet == null)
		{
			interestSet = EnumSet.of(GPSUpdateInterest.ALT, GPSUpdateInterest.SPEED,
					GPSUpdateInterest.LAT_LON, GPSUpdateInterest.OTHERS);
		}
		
		return interestSet;
	}

	@Override
	public void gpsDatumUpdated(GPSDatum datum)
	{
		/* Should be using the same gps data */
	}

	@Override
	public void compassDataUpdated(CompassDatum data)
	{
		this.compassData.conformTo(data);
	}

}
