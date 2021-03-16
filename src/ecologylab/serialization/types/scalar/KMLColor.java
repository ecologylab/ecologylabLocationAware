/**
 * 
 */
package ecologylab.serialization.types.scalar;

import java.awt.Color;
import java.awt.color.ColorSpace;

import ecologylab.serialization.types.ScalarType;

/**
 * @author Zach
 * 
 */
public class KMLColor extends Color
{
	private static final long	serialVersionUID	= 1L;
	
	//TODO -- put this and CoordinateReferenceSystemType into a class called LocationAwareTypes, and ensure its early initialization
	public static final ScalarType<KMLColor> KML_COLOR_TYPE	= new KMLColorType();

	public KMLColor(Color orig)
	{
		this(orig.getRed(), orig.getGreen(), orig.getBlue(), orig.getAlpha());
	}
	
	/**
	 * @param rgb
	 */
	public KMLColor(int rgb)
	{
		super(rgb);
	}

	/**
	 * @param rgba
	 * @param hasalpha
	 */
	public KMLColor(int rgba, boolean hasalpha)
	{
		super(rgba, hasalpha);
	}

	/**
	 * @param r
	 * @param g
	 * @param b
	 */
	public KMLColor(int r, int g, int b)
	{
		super(r, g, b);
	}

	/**
	 * @param r
	 * @param g
	 * @param b
	 */
	public KMLColor(float r, float g, float b)
	{
		super(r, g, b);
	}

	/**
	 * @param cspace
	 * @param components
	 * @param alpha
	 */
	public KMLColor(ColorSpace cspace, float[] components, float alpha)
	{
		super(cspace, components, alpha);
	}

	/**
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public KMLColor(int r, int g, int b, int a)
	{
		super(r, g, b, a);
	}

	/**
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public KMLColor(float r, float g, float b, float a)
	{
		super(r, g, b, a);
	}
}