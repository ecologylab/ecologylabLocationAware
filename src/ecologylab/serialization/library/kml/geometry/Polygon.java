package ecologylab.serialization.library.kml.geometry;

import java.util.List;

import ecologylab.sensor.location.gps.data.GeoCoordinate;
import ecologylab.serialization.ElementState;
import ecologylab.serialization.annotations.Hint;
import ecologylab.serialization.annotations.simpl_composite;
import ecologylab.serialization.annotations.simpl_hints;
import ecologylab.serialization.annotations.simpl_inherit;
import ecologylab.serialization.annotations.simpl_scalar;
import ecologylab.serialization.annotations.simpl_tag;

/**
 * From http://code.google.com/apis/kml/documentation/kml_tags_beta1.html#linestring:
 * 
 * A Polygon is defined by an outer boundary and 0 or more inner boundaries. The boundaries, in
 * turn, are defined by LinearRings. When a Polygon is extruded, its boundaries are connected to the
 * ground to form additional polygons, which gives the appearance of a building or a box. Extruded
 * Polygons use <PolyStyle> for their color, color mode, and fill.
 * 
 * @author Zach
 * 
 */
@simpl_inherit
@simpl_tag("Polygon")
public class Polygon extends ElementState
{
	/**
	 * Boolean value (0 or 1). Specifies whether to connect the LineString to the ground. To extrude a
	 * LineString, the value for <altitudeMode> must be either relativeToGround or absolute, and the
	 * altitude component within the <coordinates> element must be greater than 0 (that is, in the
	 * air). The vertices in the LineString are extruded toward the center of the Earth's sphere.
	 */
	@simpl_scalar
	@simpl_hints(Hint.XML_LEAF)
	int							extrude;

	/**
	 * Boolean value (0 or 1). Specifies whether to allow the LineString to follow the terrain. To
	 * enable tessellation, the value for <altitudeMode> must be clampToGround. Very large LineStrings
	 * should enable tessellation so that they follow the curvature of the earth (otherwise, they may
	 * go underground and be hidden).
	 */
	@simpl_scalar
	@simpl_hints(Hint.XML_LEAF)
	int							tessellate;

	@simpl_scalar
	@simpl_hints(Hint.XML_LEAF)
	@simpl_tag("altitudeMode")
	String					altitudeMode; // enum values:

	// "clampToGround",
	// "relativeToGround",
	// or "absolute"

	@simpl_composite
	@simpl_tag("outerBoundaryIs")
	OuterBoundaryIs	obi	= null;

	@simpl_composite
	@simpl_tag("innerBoundaryIs")
	InnerBoundaryIs	ibi	= null;

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

	public List<GeoCoordinate> getCoordinates()
	{
		return obi.linearRing.coordinateList;
	}
}