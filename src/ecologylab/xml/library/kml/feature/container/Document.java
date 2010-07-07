/**
 * 
 */
package ecologylab.xml.library.kml.feature.container;

import ecologylab.xml.simpl_inherit;
import ecologylab.xml.ElementState.xml_tag;
import ecologylab.xml.library.kml.style.StyleSelector;

/**
 * @author Zach
 * 
 */
@simpl_inherit @xml_tag("Document") public class Document extends Container // TODO
																									// -
																									// unclear,
																									// does
																									// this
																									// subclass
																									// container,
																									// or
																									// feature??
{
	// TODO Schema

	/**
	 * 
	 */
	public Document()
	{
	}

	public Document(String name, String description, StyleSelector selector)
	{
		super(name, description, selector);
	}
}
