/**
 * 
 */
package ecologylab.xml.library.kml;

import java.util.ArrayList;

import ecologylab.xml.xml_inherit;

/**
 * @author Zach
 *
 */
@xml_inherit
public abstract class Container extends KmlFeature
{
	@xml_collection("Placemark") ArrayList<Placemark> placemarks = new ArrayList<Placemark>();
	@xml_collection("Document") ArrayList<Document> documents = new ArrayList<Document>();

	// TODO Folder, NetworkLink, PhotoOverlay, ScreenOverlay, GroundOverlay
	
	/**
	 * 
	 */
	public Container()
	{
	}
}
