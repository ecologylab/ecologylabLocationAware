package ecologylab.sensor.network.wireless2;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;

public class WifiTest implements WifiListener
{
	public static void main(String[] args) throws IOException
	{
		WifiTest util = new WifiTest();
		try
		{
			Thread.sleep(120000);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

	@Override
	public void onConnect()
	{
		System.out.println("<------------connected callback!---------------->");
	}

	@Override
	public void onDisconnect()
	{
		System.out.println("<-----------disconnected callback!-------------->");
	}

	@Override
	public void onUpdate()
	{
		System.out.println(WifiUtils.getStatusString());
	}
}
