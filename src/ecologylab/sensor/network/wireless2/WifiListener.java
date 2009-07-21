package ecologylab.sensor.network.wireless2;

public interface WifiListener extends WifiConstants 
{
	public abstract void onDisconnect();
	
	public abstract void onConnect();
	
	public abstract void onUpdate();
}
