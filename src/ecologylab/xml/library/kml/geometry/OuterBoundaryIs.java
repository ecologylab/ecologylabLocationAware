package ecologylab.xml.library.kml.geometry;

import ecologylab.serialization.ElementState;
import ecologylab.serialization.simpl_inherit;
import ecologylab.serialization.ElementState.xml_tag;

@simpl_inherit @xml_tag("outerBoundaryIs") public class OuterBoundaryIs extends ElementState{
	
	@simpl_composite @xml_tag("LinearRing") LinearRing 	linearRing	= null;
	
	public OuterBoundaryIs()
	{
		super();
		// do i need to do anything here? oh well.
	}

}
