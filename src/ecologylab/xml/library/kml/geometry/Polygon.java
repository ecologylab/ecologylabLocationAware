package ecologylab.xml.library.kml.geometry;


import ecologylab.xml.xml_inherit;
import ecologylab.xml.ElementState;
import ecologylab.xml.ElementState.xml_leaf;
import ecologylab.xml.ElementState.xml_nested;
import ecologylab.xml.ElementState.xml_tag;

/**
 * From
 * http://code.google.com/apis/kml/documentation/kml_tags_beta1.html#linestring:
 * 
 * A Polygon is defined by an outer boundary and 0 or more inner boundaries. The 
 * boundaries, in turn, are defined by LinearRings. When a Polygon is extruded, its 
 * boundaries are connected to the ground to form additional polygons, which gives 
 * the appearance of a building or a box. Extruded Polygons use <PolyStyle> for 
 * their color, color mode, and fill.
 * 
 * @author Zach
 * 
 */
@xml_inherit @xml_tag("Polygon") public class Polygon extends ElementState
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
	
	@xml_nested @xml_tag("outerBoundaryIs") OuterBoundaryIs 		obi			= null;
	@xml_nested @xml_tag("innerBoundaryIs") InnerBoundaryIs 		ibi			= null;

	/**
	 * 
	 */
	public Polygon()
	{
		super();
		// do i need to do anything here? oh well.
	}

	public boolean isExtrude()
	{
		return extrude == 1 ? true : false;
	}

	public void setExtrude(boolean extrude)
	{
		this.extrude = extrude ? 1 : 0;
	}

	public boolean isTessellate()
	{
		return tessellate == 1 ? true : false;
	}

	public void setTessellate(boolean tessellate)
	{
		this.tessellate = tessellate ? 1 : 0;
	}

	public String getAltitudeMode()
	{
		return altitudeMode;
	}

	public void setAltitudeMode(String altitudeMode)
	{
		this.altitudeMode = altitudeMode;
	}
	
	public Coordinates getCoordinates() 
	{
		return obi.linearRing.coordinates;
	}
}