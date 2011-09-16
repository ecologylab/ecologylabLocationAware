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
import ecologylab.serialization.library.kml.KmlObject;
import ecologylab.serialization.types.scalar.KMLColor;

/**
 * @author Zach
 * 
 */
@simpl_inherit public class ColorStyle extends KmlObject
{
	@simpl_scalar @simpl_hints(Hint.XML_LEAF) KMLColor								color;

	@simpl_scalar @simpl_hints(Hint.XML_LEAF) @simpl_tag("colorMode") String	colorMode;	// either normal or random

	/**
	 * 
	 */
	public ColorStyle()
	{
	}

	public ColorStyle(String id, Color color, String colorMode)
	{
		super(id);

		this.setColor(color);
		this.colorMode = colorMode;
	}
	
	public ColorStyle(Color color, String colorMode)
	{
		this(null, color, colorMode);
	}
	
	public Color getColor()
	{
		return color;
	}

	public void setColor(Color color)
	{
		this.color = new KMLColor(color);
	}
}
