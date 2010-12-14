/**
 * 
 */
package ecologylab.serialization.library.kml.style;

import ecologylab.serialization.ElementState.xml_tag;
import ecologylab.serialization.Hint;
import ecologylab.serialization.simpl_inherit;

import java.awt.Color;

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
