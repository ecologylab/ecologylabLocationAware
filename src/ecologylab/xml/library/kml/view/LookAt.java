/**
 * 
 */
package ecologylab.xml.library.kml.view;

import ecologylab.serialization.Hint;
import ecologylab.serialization.simpl_inherit;
import ecologylab.serialization.ElementState.xml_tag;

/**
 * @author Zach
 *
 */
@simpl_inherit
@xml_tag("LookAt")
public class LookAt extends AbstractView
{
	@simpl_scalar @simpl_hints(Hint.XML_LEAF) double									longitude;

	@simpl_scalar @simpl_hints(Hint.XML_LEAF) double									latitude;

	@simpl_scalar @simpl_hints(Hint.XML_LEAF) double									altitude;

	@simpl_scalar @simpl_hints(Hint.XML_LEAF) double									heading;

	@simpl_scalar @simpl_hints(Hint.XML_LEAF) double									tilt;

	@simpl_scalar @simpl_hints(Hint.XML_LEAF) double									roll;

	@simpl_scalar @simpl_hints(Hint.XML_LEAF) @xml_tag("altitudeMode") String	altitudeMode;	// enum values:
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
