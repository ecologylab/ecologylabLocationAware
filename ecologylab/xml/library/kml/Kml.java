/**
 * 
 */
package ecologylab.xml.library.kml;

import java.util.ArrayList;

import ecologylab.xml.ElementState;
import ecologylab.xml.XMLTranslationException;
import ecologylab.xml.ElementState.xml_collection;

/**
 * @author Zach
 * 
 */
public class Kml extends ElementState
{
	@xml_collection("Placemark") ArrayList<Placemark> placemarks = new ArrayList<Placemark>();
	@xml_collection("Document") ArrayList<Document> documents = new ArrayList<Document>();

	// TODO Folder, NetworkLink, PhotoOverlay, ScreenOverlay, GroundOverlay
	
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
