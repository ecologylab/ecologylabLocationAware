/**
 * 
 */
package ecologylab.xml.library.kml;

import java.util.ArrayList;

import ecologylab.xml.ElementState;
import ecologylab.xml.XMLTranslationException;
import ecologylab.xml.library.kml.feature.KmlFeature;
import ecologylab.xml.library.kml.feature.Placemark;
import ecologylab.xml.library.kml.feature.container.Document;

/**
 * @author Zach
 * 
 */
public class Kml extends ElementState
{
	@xml_collection @xml_classes(
	{ Placemark.class, Document.class }) ArrayList<KmlFeature>	features	= new ArrayList<KmlFeature>();

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
