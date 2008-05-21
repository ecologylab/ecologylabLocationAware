package ecologylab.xml.library.kml.geometry;

import ecologylab.xml.xml_inherit;
import ecologylab.xml.ElementState;
import ecologylab.xml.ElementState.xml_nested;
import ecologylab.xml.ElementState.xml_tag;

@xml_inherit @xml_tag("innerBoundaryIs") public class InnerBoundaryIs extends ElementState{
	
	@xml_nested @xml_tag("LinearRing") LinearRing 	linearRing	= null;
	
	public InnerBoundaryIs()
	{
		super();
		// do i need to do anything here? oh well.
	}

}
