package ecologylab.sensor.network.wireless2;

public interface WifiListener extends WifiConstants 
{
	public void onDisconnect();
	
	public void onConnect();
	
	public void onUpdate();
}
