package ecologylab.standalone;

import java.io.IOException;
import java.net.BindException;
import java.util.TooManyListenersException;

import ecologylab.appframework.SingletonApplicationEnvironment;
import ecologylab.appframework.types.AppFrameworkTranslations;
import ecologylab.appframework.types.prefs.Pref;
import ecologylab.collections.Scope;
import ecologylab.sensor.location.NMEAReader;
import ecologylab.sensor.location.compass.CompassDataUpdater;
import ecologylab.sensor.location.gps.listener.GPSDataUpdater;
import ecologylab.serialization.SIMPLTranslationException;
import ecologylab.serialization.SimplTypesScope;
import ecologylab.services.distributed.server.varieties.GeoServer.GeoServer;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

public class GeoService extends SingletonApplicationEnvironment
{

	/* Preferences */
	public static final String GPS_PORT = "GPS_PORT";
	public static final String GPS_BAUD = "GPS_BAUD";
	public static final String COMPASS_PORT = "COMPASS_PORT";
	public static final String COMPASS_BAUD = "COMPASS_BAUD";

	public static final String	HISTORY_LENGTH	= "HISTORY_LENGTH";

	public static final int SERVICE_PORT = 14449;
	
	/* State Fields */
	private final GeoServer server;
	private NMEAReader compass;
	private NMEAReader gps;
	private GPSDataUpdater	gpsUpdater;

	private CompassDataUpdater	compassUpdater;
	
	private boolean connectCompass()
	{
		try
		{
			compass = new NMEAReader(Pref.lookupString(COMPASS_PORT), Pref.lookupInt(COMPASS_BAUD));
		}
		catch (NoSuchPortException | IOException e)
		{
			e.printStackTrace();
			compass = null;
			return false;

		}
		
		compassUpdater = new CompassDataUpdater();
		
		compass.addGPSDataListener(compassUpdater);
		
		compassUpdater.addCompassDataListener(server);
		
		try
		{
			compass.connect();
		}
		catch (PortInUseException | UnsupportedCommOperationException | TooManyListenersException
				| IOException e)
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

			gpsUpdater = new GPSDataUpdater();
			
			gps.addGPSDataListener(gpsUpdater);
			
			gpsUpdater.addDataUpdatedListener(server);
		}
		catch (NoSuchPortException | IOException e)
		{
			e.printStackTrace();
			gps = null;
			return false;
		}
		
		try
		{
			gps.connect();
		}
		catch (PortInUseException | UnsupportedCommOperationException | TooManyListenersException
				| IOException e)
		{
			e.printStackTrace();
			gps = null;
			return false;
		}

		return true;
	}

	public GeoService(String[] args) throws SIMPLTranslationException, BindException, ClassCastException, IOException
	{
		super("Location Service", AppFrameworkTranslations.get(), (SimplTypesScope)null, args, 0.0f);
		
		Scope scope = new Scope();
		
		server = new GeoServer(SERVICE_PORT, scope, true, Pref.lookupInt(HISTORY_LENGTH, 1));
		
		connectGPS();

		if (connectCompass())
			this.compassUpdater.connectGPS(gpsUpdater);
	}
	
	public static void main(String[] args) throws SIMPLTranslationException, BindException, ClassCastException, IOException
	{
		GeoService service = new GeoService(args);
		service.startProgram();
	}

	public void startProgram()
	{
		this.server.start();
	}
}
