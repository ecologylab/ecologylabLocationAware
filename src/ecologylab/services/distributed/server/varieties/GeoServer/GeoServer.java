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
import ecologylab.serialization.SimplTypesScope;
import ecologylab.services.messages.LocationDataRequest;
import ecologylab.services.messages.LocationDataResponse;

public class GeoServer extends DoubleThreadedNIOServer implements LocationUpdatedListener
{
	
	public static final String COMPASS_DATUM = "COMPASS_DATUM";
	private CompassDatum compassData = new CompassDatum();
	
	public static final String GPS_DATUM = "GPS_DATUM";
	private GPSDatum gpsData = new GPSDatum();
	
	private static EnumSet<GPSUpdateInterest> interestSet;
	
	public static Class[] LOCATION_MESSAGE_CLASSES = {LocationDataRequest.class, LocationDataResponse.class};
	
	public GeoServer(int portNumber, Scope objectRegistry,
			boolean useCompression) throws BindException, IOException
	{
		
		super(portNumber, LocationTranslations.get(),
				objectRegistry);

		this.gpsData = new GPSDatum();

		this.compassData = new CompassDatum(0,0,0,0);
		
		objectRegistry.put(GPS_DATUM, this.gpsData);

		objectRegistry.put(COMPASS_DATUM, this.compassData);
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
		if(this.gpsData != datum)
		{
			this.gpsData = datum;
			this.applicationObjectScope.put(GPS_DATUM, this.gpsData);
		}
	}

	@Override
	public void compassDataUpdated(CompassDatum data)
	{
		if(this.compassData != data)
		{
			this.compassData = data;
			this.applicationObjectScope.put(COMPASS_DATUM, this.compassData);
		}
	}

}
