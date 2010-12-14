/**
 * 
 */
package ecologylab.serialization.library.kml.feature.container;

import ecologylab.serialization.ElementState.xml_tag;
import ecologylab.serialization.simpl_inherit;
import ecologylab.serialization.library.kml.style.StyleSelector;

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
