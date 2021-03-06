/**
 * 
 */
package ecologylab.services.messages;

import ecologylab.collections.Scope;
import ecologylab.oodss.messages.RequestMessage;
import ecologylab.oodss.messages.ResponseMessage;
import ecologylab.serialization.library.kml.Kml;

/**
 * @author Zach
 *
 */
public class KmlRequest extends RequestMessage
{
	public static final KmlRequest	STATIC_INSTANCE	= new KmlRequest();
	
	public static final String KML_DATA = "KML_DATA";

	/**
	 * 
	 */
	public KmlRequest()
	{
	}

	/**
	 * @see ecologylab.oodss.messages.RequestMessage#performService(ecologylab.collections.Scope)
	 */
	@Override public ResponseMessage performService(
			Scope objectRegistry)
	{
		KmlResponse resp = new KmlResponse((Kml)objectRegistry.get(KML_DATA));
		
		return resp;
	}
}
