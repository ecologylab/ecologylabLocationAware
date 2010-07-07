/**
 * 
 */
package ecologylab.xml.library.kml.style;

import java.awt.Color;

import ecologylab.serialization.Hint;

/**
 * @author Zach
 * 
 */
public class PolyStyle extends ColorStyle
{
	@simpl_scalar @simpl_hints(Hint.XML_LEAF) int	fill;	// boolean: 0 or 1

	@simpl_scalar @simpl_hints(Hint.XML_LEAF) int	outline; // boolean: 0 or 1

	/**
	 * 
	 */
	public PolyStyle()
	{
	}

	public PolyStyle(Color color, String colorMode, boolean fill, boolean outline)
	{
		super(color, colorMode);

		this.fill = (fill ? 1 : 0);
		this.outline = (outline ? 1 : 0);
	}

	public boolean isFill()
	{
		return (fill == 1 ? true : false);
	}

	public boolean isOutline()
	{
		return (outline == 1 ? true : false);
	}
}