/**
 * 
 */
package ecologylab.serialization.library.kml.feature.container;

import ecologylab.serialization.simpl_inherit;
import ecologylab.serialization.ElementState.xml_tag;

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
@simpl_inherit @xml_tag("Folder") public class Folder extends Container 
{
	/**
	 * 
	 */
	public Folder()
	{
		super();
	}
}
