package ecologylab.xml.library.kml;

import ecologylab.xml.TranslationSpace;
import ecologylab.xml.types.element.ElementTypeTranslations;

/**
 * Provide XML translation mappings for use with KML.
 * 
 * @author Zachary O. Toups (toupsz@gmail.com)
 */
public class KMLTranslations
{
	public static final String	PACKAGE_NAME	= "ecologylab.xml.library.kml";

	public static final Class	TRANSLATIONS[]	=
															{ Container.class,
			Coordinates.class, Document.class, Geometry.class, Kml.class,
			KmlFeature.class, KmlObject.class, Placemark.class, Point.class };

	/**
	 * This accessor will work from anywhere, in any order, and stay efficient.
	 * 
	 * @return TranslationSpace for basic ecologylab.services
	 */
	public static TranslationSpace get()
	{
		return TranslationSpace.get(PACKAGE_NAME, TRANSLATIONS,
				ElementTypeTranslations.get());
	}
}
