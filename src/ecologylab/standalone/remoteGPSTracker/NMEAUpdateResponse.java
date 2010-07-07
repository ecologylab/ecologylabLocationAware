package ecologylab.standalone.remoteGPSTracker;

import ecologylab.collections.Scope;
import ecologylab.oodss.messages.ResponseMessage;

public class NMEAUpdateResponse<S extends Scope> extends ResponseMessage<S>
{

	@Override
	public boolean isOK()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public void processResponse(Scope objectRegistry)
   {
		System.err.println("Got response!");
   }

}
