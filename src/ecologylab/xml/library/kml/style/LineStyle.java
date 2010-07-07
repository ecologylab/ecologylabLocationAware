/**
 * 
 */
package ecologylab.xml.library.kml.style;

import java.awt.Color;

import ecologylab.serialization.Hint;
import ecologylab.serialization.simpl_inherit;
import ecologylab.serialization.ElementState.xml_tag;

/**
 * @author Zach
 * 
 */
@simpl_inherit @xml_tag("LineStyle") public class LineStyle extends ColorStyle
{
	@simpl_scalar @simpl_hints(Hint.XML_LEAF) int	width;

	/**
	 * 
	 */
	public LineStyle()
	{
	}

	public LineStyle(Color color, String colorMode, int width)
	{
		super(color, colorMode);

		this.width = width;
	}
}
