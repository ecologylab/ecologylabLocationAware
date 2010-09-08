package ecologylab.standalone;

import java.io.IOException;
import java.net.BindException;
import java.util.TooManyListenersException;

import ecologylab.appframework.ApplicationEnvironment;
import ecologylab.appframework.types.AppFrameworkTranslations;
import ecologylab.appframework.types.prefs.Pref;
import ecologylab.collections.Scope;
import ecologylab.oodss.messages.DefaultServicesTranslations;
import ecologylab.sensor.location.NMEAReader;
import ecologylab.sensor.location.compass.CompassDataUpdater;
import ecologylab.sensor.location.gps.listener.GPSDataUpdater;
import ecologylab.serialization.SIMPLTranslationException;
import ecologylab.serialization.TranslationScope;
import ecologylab.services.distributed.server.varieties.GeoServer.GeoServer;
import ecologylab.services.distributed.server.varieties.GeoServer.LocationTranslations;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

public class GeoService extends ApplicationEnvironment
{

	/* Preferences */
	public static final String GPS_PORT = "GPS_PORT";
	public static final String GPS_BAUD = "GPS_BAUD";
	public static final String COMPASS_PORT = "COMPASS_PORT";
	public static final String COMPASS_BAUD = "COMPASS_BAUD";
	public static final int SERVICE_PORT = 14449;
	
	/* State Fields */
	private GeoServer server;
	private NMEAReader compass;
	private NMEAReader gps;
	
	private boolean connectCompass()
	{
		try
		{
			compass = new NMEAReader(Pref.lookupString(COMPASS_PORT), Pref.lookupInt(COMPASS_BAUD));
		}
		catch (NoSuchPortException e)
		{
			e.printStackTrace();
			compass = null;
			return false;

		}
		catch (IOException e)
		{
			e.printStackTrace();
			compass = null;
			return false;
		}
		
		CompassDataUpdater compassUpdater = new CompassDataUpdater();
		
		compass.addGPSDataListener(compassUpdater);
		
		compassUpdater.addCompassDataListener(server);
		
		try
		{
			compass.connect();
		}
		catch (PortInUseException e)
		{
			e.printStackTrace();
			compass = null;
			return false;
		}
		catch (UnsupportedCommOperationException e)
		{
			e.printStackTrace();
			compass = null;
			return false;
		}
		catch (TooManyListenersException e)
		{
			e.printStackTrace();
			compass = null;
			return false;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			compass = null;
			return false;
		}
		return true;
	}
	
	private boolean connectGPS()
	{
		try
		{
			gps = new NMEAReader(Pref.lookupString(GPS_PORT), Pref.lookupInt(GPS_BAUD));
		}
		catch (NoSuchPortException e)
		{
			e.printStackTrace();
			gps = null;
			return false;

		}
		catch (IOException e)
		{
			e.printStackTrace();
			gps = null;
			return false;
		}
		
		GPSDataUpdater gpsUpdater = new GPSDataUpdater();
		
		gps.addGPSDataListener(gpsUpdater);
		
		gpsUpdater.addDataUpdatedListener(server);
		
		try
		{
			gps.connect();
		}
		catch (PortInUseException e)
		{
			e.printStackTrace();
			gps = null;
			return false;
		}
		catch (UnsupportedCommOperationException e)
		{
			e.printStackTrace();
			gps = null;
			return false;
		}
		catch (TooManyListenersException e)
		{
			e.printStackTrace();
			gps = null;
			return false;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			gps = null;
			return false;
		}
		return true;
	}
	
	public GeoService(String[] args) throws SIMPLTranslationException, BindException, ClassCastException, IOException
	{
		super("Location Service", AppFrameworkTranslations.get(), (TranslationScope)null, args, 0.0f);
		
		Scope scope = new Scope();
		
		server = new GeoServer(SERVICE_PORT, scope, true);
		
		connectCompass();
		connectGPS();
	}
	
	public static void main(String[] args) throws SIMPLTranslationException, BindException, ClassCastException, IOException
	{
		new GeoService(args).startProgram();
	}

	public void startProgram()
	{
		this.server.start();
	}
}
