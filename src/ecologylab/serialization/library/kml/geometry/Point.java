/**
 * 
 */
package ecologylab.serialization.library.kml.geometry;

import ecologylab.sensor.location.gps.data.GeoCoordinate;
import ecologylab.serialization.ElementState.xml_tag;
import ecologylab.serialization.Hint;
import ecologylab.serialization.simpl_inherit;

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
	
	public Point(GeoCoordinate coordinates)
	{
		super(coordinates);
	}
	
	public void setCoordinate(GeoCoordinate coordinates)
	{
		super.setCoordinate(coordinates);
	}
}
