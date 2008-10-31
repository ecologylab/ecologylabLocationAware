/*
 * Created on Dec 31, 2004 at the Interface Ecology Lab.
 */
package ecologylab.xml.types.scalar;

import ecologylab.xml.ScalarUnmarshallingContext;
import ecologylab.xml.library.kml.geometry.Coordinates;

/**
 * Type system entry for {@link ecologylab.xml.library.kml.geometry.Coordinates Coordinates}. A very
 * simple case.
 * 
 * @author Zachary O. Toups (touspz@gmail.com)
 */
public class CoordinatesType extends ReferenceType<Coordinates>
{
	/**
	 * This constructor should only be called once per session, through a static initializer,
	 * typically in TypeRegistry.
	 * <p>
	 * To get the instance of this type object for use in translations, call
	 * <code>TypeRegistry.get("java.lang.String")</code>.
	 * 
	 */
	public CoordinatesType()
	{
		super(Coordinates.class);
	}

	@Override
	public Coordinates getInstance(String value)
	{
		return new Coordinates(value);
	}

	@Override
	public Coordinates getInstance(String value, String[] formatStrings,
			ScalarUnmarshallingContext scalarUnmarshallingContext)
	{
		return this.getInstance(value);
	}
}
