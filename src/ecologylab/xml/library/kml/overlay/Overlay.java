/**
 * 
 */
package ecologylab.xml.library.kml.overlay;

import ecologylab.xml.xml_inherit;
import ecologylab.xml.library.kml.feature.KmlFeature;
import ecologylab.xml.library.kml.style.Icon;
import ecologylab.xml.library.kml.style.StyleSelector;
import ecologylab.xml.types.scalar.KMLColor;

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
 * @author Zachary O. Toups (toupsz@cs.tamu.edu) (Java classes only)
 */
@xml_inherit public abstract class Overlay extends KmlFeature
{
	@xml_leaf protected KMLColor							color;

	@xml_leaf @xml_tag("drawOrder") protected int	drawOrder;
	
	@xml_nested @xml_tag("Icon") protected Icon			icon;

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