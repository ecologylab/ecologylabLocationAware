/**
 * 
 */
package ecologylab.xml.library.kml.geometry;

import ecologylab.xml.xml_inherit;
import ecologylab.xml.ElementState.xml_leaf;
import ecologylab.xml.ElementState.xml_tag;

/**
 * From
 * http://code.google.com/apis/kml/documentation/kml_tags_beta1.html#linestring:
 * 
 * Defines a connected set of line segments. Use <LineStyle> to specify the
 * color, color mode, and width of the line. When a LineString is extruded, the
 * line is extended to the ground, forming a polygon that looks somewhat like a
 * wall or fence. For extruded LineStrings, the line itself uses the current
 * LineStyle, and the extrusion uses the current PolyStyle. See the KML Tutorial
 * for examples of LineStrings (or paths).
 * 
 * @author Zach
 * 
 */
@xml_inherit @xml_tag("LineString") public class LineString extends Geometry
{
	/**
	 * Boolean value (0 or 1). Specifies whether to connect the LineString to the
	 * ground. To extrude a LineString, the value for <altitudeMode> must be
	 * either relativeToGround or absolute, and the altitude component within the
	 * <coordinates> element must be greater than 0 (that is, in the air). The
	 * vertices in the LineString are extruded toward the center of the Earth's
	 * sphere.
	 */
	@xml_leaf int										extrude;

	/**
	 * Boolean value (0 or 1). Specifies whether to allow the LineString to
	 * follow the terrain. To enable tessellation, the value for <altitudeMode>
	 * must be clampToGround. Very large LineStrings should enable tessellation
	 * so that they follow the curvature of the earth (otherwise, they may go
	 * underground and be hidden).
	 */
	@xml_leaf int										tessellate;

	@xml_leaf @xml_tag("altitudeMode") String	altitudeMode;	// enum values:
																				// "clampToGround",
																				// "relativeToGround",
																				// or "absolute"

	
	
	/**
	 * 
	 */
	public LineString()
	{
		super();
	}

}
