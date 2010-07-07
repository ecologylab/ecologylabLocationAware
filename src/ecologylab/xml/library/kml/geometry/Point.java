/**
 * 
 */
package ecologylab.xml.library.kml.geometry;

import ecologylab.xml.Hint;
import ecologylab.xml.simpl_inherit;
import ecologylab.xml.ElementState.xml_tag;

/**
 * @author Zach
 * 
 */
@simpl_inherit
@xml_tag("Point") 
public class Point extends Geometry
{
	@simpl_scalar @simpl_hints(Hint.XML_LEAF) int extrude; // actually a boolean; can be either 0 or 1
	@simpl_scalar @simpl_hints(Hint.XML_LEAF) @xml_tag("altitudeMode") String altitudeMode; // enum values: "clampToGround", "relativeToGround", or "absolute"
	
	/**
	 * No-argument constructor for automatic translation to/from KML.
	 */
	public Point()
	{
	}
	
	public Point(Coordinates coordinates)
	{
		super(coordinates);
	}
}
