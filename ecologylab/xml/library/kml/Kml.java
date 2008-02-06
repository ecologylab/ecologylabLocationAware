/**
 * 
 */
package ecologylab.xml.library.kml;

import java.util.ArrayList;

import ecologylab.xml.ElementState;
import ecologylab.xml.XMLTranslationException;

/**
 * @author Zach
 * 
 */
public class Kml extends ElementState
{
	@xml_collection ArrayList<KmlFeature> features = new ArrayList<KmlFeature>();
	
	/**
	 * No-argument constructor for automatic translation to/from KML.
	 */
	public Kml()
	{
	}
	
	public static void main(String[] args) throws XMLTranslationException
	{
		Kml k = new Kml();
		
		k.translateToXML(System.out);
	}
}
