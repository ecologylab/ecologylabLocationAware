/**
 * 
 */
package ecologylab.standalone;

import ecologylab.serialization.ElementState;
import ecologylab.serialization.SIMPLTranslationException;
import ecologylab.serialization.TranslationScope;
import ecologylab.xml.library.kml.KMLTranslations;

/**
 * @author Zach
 * 
 */
public class TestKML
{
	public static String someKml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
			"<kml xmlns=\"http://earth.google.com/kml/2.2\">\r\n" + 
			"<Document>\r\n" + 
			"  <name>BalloonStyle.kml</name>\r\n" + 
			"  <open>1</open>\r\n" + 
			"  <Placemark>\r\n" + 
			"    <name>BalloonStyle</name>\r\n" + 
			"    <description>An example of BalloonStyle</description>\r\n" + 
			"    <Point>\r\n" + 
			"      <coordinates>-122.370533,37.823842,0</coordinates>\r\n" + 
			"    </Point>\r\n" + 
			"  </Placemark>\r\n" + 
			"</Document>\r\n" + 
			"</kml>";

	/**
	 * @param args
	 * @throws SIMPLTranslationException 
	 */
	public static void main(String[] args) throws SIMPLTranslationException
	{
		ElementState k =  KMLTranslations.get().deserializeCharSequence(someKml);
		
		k.serialize(System.out);
	}

}
