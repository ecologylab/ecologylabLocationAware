/**
 * 
 */
package ecologylab.xml.library.kml.feature.container;

import ecologylab.xml.xml_inherit;
import ecologylab.xml.ElementState.xml_tag;

/**
 * From http://code.google.com/apis/kml/documentation/kml_tags_beta1.html#folder
 * 
 * A Folder is used to arrange other Features hierarchically (Folders,
 * Placemarks, NetworkLinks, or Overlays). A Feature is visible only if it and
 * all its ancestors are visible.
 * 
 * @author Zach
 * 
 */
@xml_inherit @xml_tag("Folder") public class Folder extends Container 
{
	/**
	 * 
	 */
	public Folder()
	{
	}
}
