package ecologylab.standalone;

import java.io.IOException;
import java.util.Calendar;

import ecologylab.collections.Scope;
import ecologylab.oodss.distributed.client.NIOClient;
import ecologylab.oodss.distributed.exception.MessageTooLargeException;
import ecologylab.oodss.messages.ResponseMessage;
import ecologylab.services.distributed.server.varieties.GeoServer.LocationTranslations;
import ecologylab.services.messages.LocationDataRequest;
import ecologylab.services.messages.LocationDataResponse;

/**
 * An NIOClient that provides access to GPS and compass data. This client is a little unorthodox
 * because it can also provide the same data via it's updateLocation methods.
 * 
 * @author William A. Hamilton (bill@ecologylab.net)
 * @author Zachary O. Toups (ztoups@nmsu.edu)
 */
public class GeoClient
{
	private NIOClient													client;

	// private static final LocationDataRequest request = new LocationDataRequest();

	public synchronized LocationDataResponse updateLocation()
	{
		return updateLocation(null);
	}

	public synchronized LocationDataResponse updateLocation(Calendar time)
	{
		try
		{
			ResponseMessage resp = client.sendMessage(new LocationDataRequest(time));

			if (resp instanceof LocationDataResponse)
			{
				return (LocationDataResponse) resp;
			}
		}
		catch (MessageTooLargeException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public GeoClient()
	{
		Scope s = new Scope();
		try
		{
			client = new NIOClient("localhost", GeoService.SERVICE_PORT, LocationTranslations.get(), s);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public boolean connect()
	{
		return client.connect();
	}

	public void disconnect()
	{
		client.disconnect();
	}

	public boolean connected()
	{
		return client.connected();
	}
}
