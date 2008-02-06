/**
 * 
 */
package ecologylab.xml.library.kml;

import ecologylab.xml.xml_inherit;
import ecologylab.xml.ElementState.xml_tag;

/**
 * @author Zach
 * 
 */
@xml_inherit
@xml_tag("Point") 
public class Point extends Geometry
{
	@xml_leaf int extrude; // actually a boolean; can be either 0 or 1
	@xml_leaf private Coordinates	coordinates = new Coordinates();
	@xml_leaf @xml_tag("altitudeMode") String altitudeMode; // enum values: "clampToGround", "relativeToGround", or "absolute"
	
	/**
	 * No-argument constructor for automatic translation to/from KML.
	 */
	public Point()
	{
	}

	public void setCoordinates(Coordinates coordinates)
	{
		this.coordinates = coordinates;
	}

	public Coordinates getCoordinates()
	{
		return coordinates;
	}
}
