/**
 * 
 */
package ecologylab.serialization.library.kml.overlay;

import ecologylab.serialization.annotations.Hint;
import ecologylab.serialization.annotations.simpl_composite;
import ecologylab.serialization.annotations.simpl_hints;
import ecologylab.serialization.annotations.simpl_inherit;
import ecologylab.serialization.annotations.simpl_scalar;
import ecologylab.serialization.annotations.simpl_tag;
import ecologylab.serialization.library.kml.feature.KmlFeature;
import ecologylab.serialization.library.kml.style.Icon;
import ecologylab.serialization.library.kml.style.StyleSelector;
import ecologylab.serialization.types.scalar.KMLColor;

/**
 * According to
 * http://code.google.com/apis/kml/documentation/kml_tags_beta1.html#overlay
 * 
 * This is an abstract element and cannot be used directly in a KML file.
 * <Overlay> is the base type for image overlays drawn on the planet surface or
 * on the screen. <Icon> specifies the image to use and can be configured to
 * reload images based on a timer or by camera changes. This element also
 * includes specifications for stacking order of multiple overlays and for
 * adding color and transparency values to the base image.
 * 
 * @author Z O. Toups (zach@ecologylab.net) (Java classes only)
 */
@simpl_inherit public abstract class Overlay extends KmlFeature
{
	@simpl_scalar @simpl_hints(Hint.XML_LEAF) protected KMLColor							color;

	@simpl_scalar @simpl_hints(Hint.XML_LEAF) @simpl_tag("drawOrder") protected int	drawOrder;
	
	@simpl_composite @simpl_tag("Icon") protected Icon			icon;

	/**
	 * 
	 */
	public Overlay()
	{
		super();
	}

	/**
	 * @param name
	 * @param description
	 * @param selector
	 */
	public Overlay(String name, String description, StyleSelector selector)
	{
		super(name, description, selector);
	}

	/**
	 * @param name
	 * @param description
	 * @param styleUrl
	 */
	public Overlay(String name, String description, String styleUrl)
	{
		super(name, description, styleUrl);
	}

	/**
	 * @param name
	 * @param description
	 */
	public Overlay(String name, String description)
	{
		super(name, description);
	}
}
