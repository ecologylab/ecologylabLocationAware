/**
 * 
 */
package ecologylab.xml.library.kml.geometry;

import ecologylab.xml.xml_inherit;
import ecologylab.xml.library.kml.KmlObject;

/**
 * @author Zach
 *
 */
@xml_inherit
public abstract class Geometry extends KmlObject
{
	@xml_leaf protected Coordinates	coordinates	= new Coordinates();

	/**
	 * 
	 */
	public Geometry()
	{
	}
	
	public Geometry(Coordinates coordinates)
	{
		this.setCoordinates(coordinates);
	}

	@Override protected void postTranslationProcessingHook()
	{
		super.postTranslationProcessingHook();
		
		this.coordinates.postTranslationProcessingHook();
	}

	@Override protected void preTranslationProcessingHook()
	{
		super.preTranslationProcessingHook();
		
		this.coordinates.preTranslationProcessingHook();
	}
	
	public void setCoordinates(Coordinates coordinates)
	{
		this.coordinates = coordinates;
	}

	public Coordinates getCoordinates()
	{
		return this.coordinates;
	}
}
