/**
 * 
 */
package ecologylab.xml.library.kml.feature.container;

import java.util.ArrayList;

import ecologylab.xml.xml_inherit;
import ecologylab.xml.library.kml.feature.KmlFeature;
import ecologylab.xml.library.kml.feature.Placemark;

/**
 * @author Zach
 * 
 */
@xml_inherit public abstract class Container extends KmlFeature
{
	@xml_collection @xml_classes(
	{ Placemark.class, Document.class, Container.class, Folder.class }) ArrayList<KmlFeature>	features	= new ArrayList<KmlFeature>();

	/*
	 * @xml_collection("Placemark") ArrayList<Placemark> placemarks = new
	 * ArrayList<Placemark>();
	 * 
	 * @xml_collection("Document") ArrayList<Document> documents = new ArrayList<Document>();
	 * 
	 * @xml_collection("Container") ArrayList<Container> containers = new
	 * ArrayList<Container>();
	 */
	// TODO Folder, NetworkLink, PhotoOverlay, ScreenOverlay, GroundOverlay
	/**
	 * 
	 */
	public Container()
	{
	}
}
