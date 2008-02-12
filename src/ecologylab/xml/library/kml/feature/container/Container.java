/**
 * 
 */
package ecologylab.xml.library.kml.feature.container;

import java.util.ArrayList;

import ecologylab.xml.xml_inherit;
import ecologylab.xml.library.kml.feature.KmlFeature;
import ecologylab.xml.library.kml.feature.Placemark;
import ecologylab.xml.library.kml.style.StyleSelector;

/**
 * @author Zach
 * 
 */
@xml_inherit public abstract class Container extends KmlFeature
{
	@xml_collection("Placemark") ArrayList<Placemark>	placemarks	= new ArrayList<Placemark>();

	@xml_collection("Document") ArrayList<Document>		documents	= new ArrayList<Document>();

	@xml_collection("Folder") ArrayList<Folder>			folder		= new ArrayList<Folder>();

	// TODO Folder, NetworkLink, PhotoOverlay, ScreenOverlay, GroundOverlay
	/**
	 * 
	 */
	public Container()
	{
	}

	public Container(String name, String description, StyleSelector selector)
	{
		super(name, description, selector);
	}
	
	public void addPlacemark(Placemark newPlacemark)
	{
		this.placemarks.add(newPlacemark);
	}
}