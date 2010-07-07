/**
 * 
 */
package ecologylab.services.messages;

import ecologylab.oodss.messages.ResponseMessage;
import ecologylab.xml.library.kml.Kml;

/**
 * @author Zach
 *
 */
public class KmlResponse extends ResponseMessage
{
	Kml kml;

	/**
	 * 
	 */
	public KmlResponse()
	{
	}
	
	public KmlResponse(Kml kml)
	{
		this.kml = kml;
	}

	/**
	 * @see ecologylab.oodss.messages.ResponseMessage#isOK()
	 */
	@Override public boolean isOK()
	{
		return true;
	}

	public Kml getKml()
	{
		return kml;
	}
}