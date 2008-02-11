/**
 * 
 */
package ecologylab.xml.library.kml.style;

import java.awt.Color;

/**
 * @author Zach
 * 
 */
public class PolyStyle extends ColorStyle
{
	@xml_leaf int	fill;	// boolean: 0 or 1

	@xml_leaf int	outline; // boolean: 0 or 1

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