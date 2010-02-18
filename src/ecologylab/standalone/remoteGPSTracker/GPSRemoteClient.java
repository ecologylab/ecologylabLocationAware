package ecologylab.standalone.remoteGPSTracker;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.TooManyListenersException;

import ecologylab.collections.Scope;
import ecologylab.sensor.location.NMEAReader;
import ecologylab.sensor.location.NMEAStringListener;
import ecologylab.services.distributed.client.NIODatagramClient;
import ecologylab.services.messages.DefaultServicesTranslations;
import ecologylab.tutorials.oodss.HistoryEchoRequest;
import ecologylab.tutorials.oodss.HistoryEchoResponse;
import ecologylab.xml.TranslationScope;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

public class GPSRemoteClient extends NIODatagramClient implements NMEAStringListener
{

	NMEAUpdateRequest update = new NMEAUpdateRequest();
	
	public GPSRemoteClient(InetSocketAddress serverAddress,
			TranslationScope translationScope, Scope objectRegistry, int timeout)
	{
		super(serverAddress, translationScope, objectRegistry, timeout);
	}

	public void processIncomingNMEAString(String gpsDataString)
	{		
		if(gpsDataString.startsWith("GPGGA"))
		{
			update.setNMEAString(gpsDataString);
			this.sendMessageAsync(update);
			System.err.println("Sent message: " + gpsDataString);
		}
	}

	public static void main(String[] args)
	{
		NMEAReader gps = null;
		
		try
		{
			gps = new NMEAReader("COM5", 115200);
		}
		catch (NoSuchPortException e)
		{
			e.printStackTrace();
			return;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return;
		}
		
		Class[] GpsTrackerClasses = { NMEAUpdateRequest.class,
				 								NMEAUpdateResponse.class };
		
		TranslationScope nmeaUpdateTranslations = TranslationScope.get("nmeaUpdateTrans",
																							DefaultServicesTranslations.get(),
																							GpsTrackerClasses);
		
		Scope clientScope = new Scope();
		
		GPSRemoteClient client = new GPSRemoteClient(new InetSocketAddress("shady.cs.tamu.edu", 2107),
																	nmeaUpdateTranslations, clientScope, 250);
	
		gps.addGPSDataListener(client);
		try
		{
			gps.connect();
		}
		catch (PortInUseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (UnsupportedCommOperationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (TooManyListenersException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
