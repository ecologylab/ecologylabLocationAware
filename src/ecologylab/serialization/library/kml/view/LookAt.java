/**
 * 
 */
package ecologylab.serialization.library.kml.view;

import ecologylab.serialization.annotations.Hint;
import ecologylab.serialization.annotations.simpl_hints;
import ecologylab.serialization.annotations.simpl_inherit;
import ecologylab.serialization.annotations.simpl_scalar;
import ecologylab.serialization.annotations.simpl_tag;

/**
 * @author Zach
 *
 */
@simpl_inherit
@simpl_tag("LookAt")
public class LookAt extends AbstractView
{
	@simpl_scalar @simpl_hints(Hint.XML_LEAF) double									longitude;

	@simpl_scalar @simpl_hints(Hint.XML_LEAF) double									latitude;

	@simpl_scalar @simpl_hints(Hint.XML_LEAF) double									altitude;

	@simpl_scalar @simpl_hints(Hint.XML_LEAF) double									heading;

	@simpl_scalar @simpl_hints(Hint.XML_LEAF) double									tilt;

	@simpl_scalar @simpl_hints(Hint.XML_LEAF) double									roll;

	@simpl_scalar @simpl_hints(Hint.XML_LEAF) @simpl_tag("altitudeMode") String	altitudeMode;	// enum values:
																				// "clampToGround",
																				// "relativeToGround",
																				// or "absolute"
	/**
	 * 
	 */
	public LookAt()
	{
		// TODO Auto-generated constructor stub
	}

}
