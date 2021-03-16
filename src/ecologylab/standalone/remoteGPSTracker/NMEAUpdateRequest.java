package ecologylab.standalone.remoteGPSTracker;

import ecologylab.collections.Scope;
import ecologylab.oodss.messages.RequestMessage;
import ecologylab.oodss.messages.ResponseMessage;
import ecologylab.sensor.location.NMEAStringListener;
import ecologylab.serialization.annotations.simpl_scalar;

public class NMEAUpdateRequest extends RequestMessage
{

	public static final String GPS_DATA_SINK = "GPS_DATA_SINK";
	
	public static final NMEAUpdateResponse response = new NMEAUpdateResponse();
	
	private @simpl_scalar String nmea = "";
	
	@Override
	public ResponseMessage performService(Scope clientSessionScope)
	{
		NMEAStringListener listener = (NMEAStringListener) clientSessionScope.get(GPS_DATA_SINK);
		listener.processIncomingNMEAString(this.nmea);
		
		System.err.println(nmea);
		
		return response;
	}

	public void setNMEAString(String s)
	{
		nmea = s;
	}
	
	public boolean isDisposable()
	{
		return true;
	}
	
	public int compareTo(Object arg0)
	{
		// TODO Auto-generated method stub
		return 0;
	}

}
