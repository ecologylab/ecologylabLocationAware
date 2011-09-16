/**
 * 
 */
package ecologylab.serialization.library.kml.style;

import java.awt.Color;

import ecologylab.serialization.annotations.Hint;
import ecologylab.serialization.annotations.simpl_hints;
import ecologylab.serialization.annotations.simpl_inherit;
import ecologylab.serialization.annotations.simpl_scalar;
import ecologylab.serialization.annotations.simpl_tag;

/**
 * @author Zach
 * 
 */
@simpl_inherit @simpl_tag("LineStyle") public class LineStyle extends ColorStyle
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
