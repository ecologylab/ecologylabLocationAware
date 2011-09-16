/**
 * 
 */
package ecologylab.serialization.library.kml.geometry;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ecologylab.sensor.location.gps.data.GeoCoordinate;
import ecologylab.serialization.TranslationContext;
import ecologylab.serialization.annotations.Hint;
import ecologylab.serialization.annotations.simpl_hints;
import ecologylab.serialization.annotations.simpl_inherit;
import ecologylab.serialization.annotations.simpl_scalar;
import ecologylab.serialization.library.kml.KmlObject;

/**
 * @author Zach
 * 
 */
@simpl_inherit
public abstract class Geometry extends KmlObject
{
	private static final String		COORDINATE_PATTERN_STRING	= "([+-]?\\d+(?:\\.\\d+)?)[\\\\z,]?";

	private static final Pattern	PATTERN										= Pattern
																															.compile(COORDINATE_PATTERN_STRING);

	@simpl_scalar
	@simpl_hints(Hint.XML_LEAF)
	protected String							coordinates;

	ArrayList<GeoCoordinate>			coordinateList						= new ArrayList<GeoCoordinate>();

	/**
	 * 
	 */
	public Geometry()
	{
	}

	public Geometry(GeoCoordinate coordinates)
	{
		this.coordinateList.add(coordinates);
	}

	public String getCoordinates()
	{
		return this.coordinates;
	}

	/**
	 * Parses a String of coordinates, expressed as comma-delimited triples (longitude, latitude,
	 * altitude). If the last triple in the list is incomplete, it is not appended unless it is a
	 * special case of having exactly two, in which case the altitude is assumed to be 0.
	 * 
	 * @param coords
	 */
	protected void appendStringRepresentation(String coords)
	{
		int i = 0;
		int timesThrough = 0;

		double readValue;

		double lon = 0;
		double lat = 0;
		double alt = 0;

		GeoCoordinate g;

		if (coords != null)
		{
			Matcher m = PATTERN.matcher(coords);

			while (m.find())
			{
				timesThrough++;
				readValue = Double.parseDouble(m.group(1));

				switch (i)
				{ // we're reading one coordinate at a time, and they go lon, lat, alt
				case (0):
					lon = readValue;
					i++;
					break;
				case (1):
					lat = readValue;
					i++;
					break;
				case (2):
					alt = readValue;
					i = 0;

					g = new GeoCoordinate(lat, lon, alt);
					this.coordinateList.add(g);

					break;
				}
			}

			if (timesThrough == 2)
			{ // special case for a Point, where we can JUST specify lon and lat
				// in this case, since we have not yet had 3 values come out, we have
				// added NO coordinates to the coordinate list. We add them now.
				g = new GeoCoordinate(lat, lon, 0);
				this.coordinateList.add(g);
			}
		}
	}

	public ArrayList<GeoCoordinate> getCoordinateList()
	{
		return coordinateList;
	}

	private void loadCoordsFromCoordinateList()
	{
		// make a stringbuilder for the intermediate values; estimate needed size
		StringBuilder newCoord = new StringBuilder(this.coordinateList.size() * 15);

		for (int i = 0; i < this.coordinateList.size(); i++)
		{
			newCoord.append(this.coordinateList.get(i).getKMLCommaDelimitedString());

			if (i < (this.coordinateList.size() - 1))
			{ // every time but the last time, we need a comma
				newCoord.append(',');
			}
		}

		this.coordinates = newCoord.toString();
	}

	/**
	 * Parses and adds the geographic coordinates in coords to the coordinateList; called
	 * automatically immediately after translating FROM KML.
	 */
	@Override
	public void deserializationPostHook(TranslationContext translationContext, Object object )
	{
		super.deserializationPostHook( translationContext, object);

		this.appendStringRepresentation(this.coordinates);
	}

	/**
	 * Loads coords with the current values from coordinateList; called automatically immediately
	 * before translating this TO KML.
	 */
	@Override
	public void serializationPreHook(TranslationContext translationContext)
	{
		super.serializationPreHook( translationContext);

		this.loadCoordsFromCoordinateList();
	}

	/**
	 * For Geometry's with only a single point; clears the coordinate list and adds the parameter
	 * value.
	 * 
	 * @param coordinates2
	 */
	public void setCoordinate(GeoCoordinate coordinates2)
	{
		this.coordinateList.clear();
		this.coordinateList.add(coordinates2);
	}
}
