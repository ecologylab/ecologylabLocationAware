package ecologylab.xml.library.kml.geometry;

import ecologylab.xml.xml_inherit;
import ecologylab.xml.ElementState.xml_tag;

@xml_inherit @xml_tag("LinearRing") public class LinearRing extends Geometry
{
	@xml_leaf int extrude; // actually a boolean; can be either 0 or 1
	@xml_leaf int tessellate; // actually a boolean; can be either 0 or 1
	@xml_leaf @xml_tag("altitudeMode") String altitudeMode; // enum values: "clampToGround", "relativeToGround", or "absolute"
	
	/**
	 * No-argument constructor for automatic translation to/from KML.
	 */
	public LinearRing()
	{
		super();
	}
	
	public LinearRing(Coordinates coordinates)
	{
		super(coordinates);
	}

}
