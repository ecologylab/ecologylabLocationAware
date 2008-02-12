/**
 * 
 */
package ecologylab.services.messages;

import ecologylab.appframework.Scope;
import ecologylab.xml.library.kml.Kml;

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
	 * @see ecologylab.services.messages.RequestMessage#performService(ecologylab.appframework.Scope, java.lang.String)
	 */
	@Override public ResponseMessage performService(
			Scope objectRegistry, String sessionId)
	{
		KmlResponse resp = new KmlResponse((Kml)objectRegistry.lookup(KML_DATA));
		
		return resp;
	}
}
