package ecologylab.xml.library.kml.geometry;

import ecologylab.serialization.ElementState;
import ecologylab.serialization.simpl_inherit;
import ecologylab.serialization.ElementState.xml_tag;

@simpl_inherit @xml_tag("innerBoundaryIs") public class InnerBoundaryIs extends ElementState{
	
	@simpl_composite @xml_tag("LinearRing") LinearRing 	linearRing	= null;
	
	public InnerBoundaryIs()
	{
		super();
		// do i need to do anything here? oh well.
	}

}
