package ecologylab.standalone.remoteGPSTracker;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

import ecologylab.collections.Scope;
import ecologylab.sensor.location.gps.listener.GPSDataLogger;
import ecologylab.services.distributed.server.NIODatagramServer;
import ecologylab.services.messages.DefaultServicesTranslations;
import ecologylab.xml.TranslationScope;

public class GPSRemoteServer extends NIODatagramServer
{

	GPSDataLogger logger;
	
	public GPSRemoteServer(int portNumber, TranslationScope translationScope,
			Scope objectRegistry)
	{
		super(portNumber, translationScope, objectRegistry);
		
		try
		{
			logger = new GPSDataLogger(new File("RemoteGpsDataLog.log"));
		}
		catch (IOException e)
		{
			debug("Failed to open gps logger!");
			e.printStackTrace();
		}
		
		objectRegistry.put(NMEAUpdateRequest.GPS_DATA_SINK, logger);
	}

	public static void main(String[] args)
	{
		Class[] GpsTrackerClasses = { NMEAUpdateRequest.class,
					NMEAUpdateResponse.class };

		TranslationScope nmeaUpdateTranslations = TranslationScope.get("nmeaUpdateTrans",
																	GpsTrackerClasses,
																	DefaultServicesTranslations.get());
		
		Scope serverScope = new Scope();
		
		GPSRemoteServer client = new GPSRemoteServer(2107, nmeaUpdateTranslations, serverScope);
		
		
		System.out.println("GPSLogServer Running!");
	}
	
}