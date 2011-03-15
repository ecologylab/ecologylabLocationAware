package ecologylab.standalone;

import java.io.IOException;

import ecologylab.collections.Scope;
import ecologylab.oodss.distributed.client.NIOClient;
import ecologylab.oodss.distributed.exception.MessageTooLargeException;
import ecologylab.oodss.messages.ResponseMessage;
import ecologylab.services.distributed.server.varieties.GeoServer.LocationTranslations;
import ecologylab.services.messages.LocationDataRequest;
import ecologylab.services.messages.LocationDataResponse;

public class GeoClient 
{
	private NIOClient client;
	private static final LocationDataRequest request = new LocationDataRequest();
	
	public synchronized LocationDataResponse updateLocation()
	{
		try
		{
			ResponseMessage resp = client.sendMessage(request);
			
			if (resp instanceof LocationDataResponse)
			{
				return (LocationDataResponse) resp;
			}
		}
		catch (MessageTooLargeException e)
		{
			// TODO Auto-generated catch block
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
