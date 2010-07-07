/**
 * 
 */
package ecologylab.xml.library.kml.feature.container;

import java.util.ArrayList;

import ecologylab.serialization.simpl_inherit;
import ecologylab.xml.library.kml.feature.KmlFeature;
import ecologylab.xml.library.kml.feature.Placemark;
import ecologylab.xml.library.kml.style.StyleSelector;

/**
 * @author Zach
 * 
 */
@simpl_inherit public abstract class Container extends KmlFeature
{
	@simpl_collection("Placemark") ArrayList<Placemark>	placemarks;

	@simpl_collection("Document") ArrayList<Document>		documents;

	@simpl_collection("Folder") ArrayList<Folder>			folder;

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
		this.placemarks().add(newPlacemark);
	}
	
	private ArrayList<Placemark> placemarks()
	{
		if (placemarks == null)
		{
			synchronized (this)
			{
				if (placemarks == null)
					placemarks = new ArrayList<Placemark>();
			}
		}
		
		return placemarks;
	}
	
	/**
	 * Don't modify the placemarks you get from this function. Or do.
	 * Whatever.
	 */
	public ArrayList<Placemark> getPlacemarks() 
	{
		return placemarks();
	}
	public ArrayList<Document> getDocuments() 
	{
		return documents;
	}
	public ArrayList<Folder> getFolders() 
	{
		return folder;
	}
}
