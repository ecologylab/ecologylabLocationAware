package ecologylab.sensor.network.wireless2;

import java.io.IOException;

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

	public void onConnect()
	{
		System.out.println("<------------connected callback!---------------->");
	}

	public void onDisconnect()
	{
		System.out.println("<-----------disconnected callback!-------------->");
	}

	public void onUpdate()
	{
		System.out.println(WifiUtils.getStatusString());
	}
}
