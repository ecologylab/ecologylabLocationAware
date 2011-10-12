package ecologylab.serialization.library.kml;

import ecologylab.serialization.SimplTypesScope;
import ecologylab.serialization.library.kml.feature.KmlFeature;
import ecologylab.serialization.library.kml.feature.Placemark;
import ecologylab.serialization.library.kml.feature.container.Container;
import ecologylab.serialization.library.kml.feature.container.Document;
import ecologylab.serialization.library.kml.feature.container.Folder;
import ecologylab.serialization.library.kml.geometry.Geometry;
import ecologylab.serialization.library.kml.geometry.InnerBoundaryIs;
import ecologylab.serialization.library.kml.geometry.LineString;
import ecologylab.serialization.library.kml.geometry.LinearRing;
import ecologylab.serialization.library.kml.geometry.OuterBoundaryIs;
import ecologylab.serialization.library.kml.geometry.Point;
import ecologylab.serialization.library.kml.geometry.Polygon;
import ecologylab.serialization.library.kml.view.AbstractView;
import ecologylab.serialization.library.kml.view.Camera;
import ecologylab.serialization.library.kml.view.LookAt;
import ecologylab.serialization.types.element.ElementTypeTranslationsProvider;

/**
 * Provide XML translation mappings for use with KML.
 * 
 * @author Zachary O. Toups (toupsz@gmail.com)
 */
public class KMLTranslations
{
	public static final String	PACKAGE_NAME	= "ecologylab.serialization.library.kml";

	public static final Class	TRANSLATIONS[]	=
															{ Container.class,
			//Coordinates.class, 
			Document.class, Geometry.class, Kml.class,
			KmlFeature.class, KmlObject.class, Placemark.class, Point.class,
			LineString.class, Folder.class, AbstractView.class, Camera.class,
			LookAt.class, InnerBoundaryIs.class, OuterBoundaryIs.class,
			LinearRing.class, Polygon.class};

	/**
	 * This accessor will work from anywhere, in any order, and stay efficient.
	 * 
	 * @return TranslationSpace for basic ecologylab.oodss
	 */
	public static SimplTypesScope get()
	{
		return SimplTypesScope.get(PACKAGE_NAME, ElementTypeTranslationsProvider.get(),
				TRANSLATIONS);
	}
}
