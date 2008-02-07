/**
 * 
 */
package ecologylab.xml.library.kml;

import ecologylab.xml.ElementState;

/**
 * From:
 * http://code.google.com/apis/kml/documentation/kml_tags_beta1.html#object
 * 
 * This is an abstract base class and cannot be used directly in a KML file. It
 * provides the id attribute, which allows unique identification of a KML
 * element, and the targetId attribute, which is used to reference objects that
 * have already been loaded into Google Earth. The id attribute must be assigned
 * if the <Update> mechanism is to be used.
 * 
 * @author Zach
 * 
 */
public abstract class KmlObject extends ElementState
{
	@xml_attribute String	id;

	/**
	 * 
	 */
	public KmlObject()
	{
	}
}
