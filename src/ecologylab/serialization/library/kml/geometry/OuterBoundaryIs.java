package ecologylab.serialization.library.kml.geometry;

import ecologylab.serialization.ElementState;
import ecologylab.serialization.annotations.simpl_composite;
import ecologylab.serialization.annotations.simpl_inherit;
import ecologylab.serialization.annotations.simpl_tag;

@simpl_inherit
@simpl_tag("outerBoundaryIs")
public class OuterBoundaryIs extends ElementState
{
	@simpl_composite
	@simpl_tag("LinearRing")
	LinearRing	linearRing	= null;

	public OuterBoundaryIs()
	{
		super();
		// do i need to do anything here? oh well.
	}

}
