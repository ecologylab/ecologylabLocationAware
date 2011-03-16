package ecologylab.serialization.library.kml.geometry;

import ecologylab.serialization.ElementState.xml_tag;
import ecologylab.serialization.Hint;
import ecologylab.serialization.simpl_inherit;

@simpl_inherit @xml_tag("LinearRing") public class LinearRing extends Geometry
{
	@simpl_scalar @simpl_hints(Hint.XML_LEAF) int extrude; // actually a boolean; can be either 0 or 1
	@simpl_scalar @simpl_hints(Hint.XML_LEAF) int tessellate; // actually a boolean; can be either 0 or 1
	@simpl_scalar @simpl_hints(Hint.XML_LEAF) @xml_tag("altitudeMode") String altitudeMode; // enum values: "clampToGround", "relativeToGround", or "absolute"
	
	/**
	 * No-argument constructor for automatic translation to/from KML.
	 */
	public LinearRing()
	{
		super();
	}
}
