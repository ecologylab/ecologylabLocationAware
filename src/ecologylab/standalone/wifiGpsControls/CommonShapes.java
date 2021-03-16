/*
 * Created on Feb 28, 2006
 */
package ecologylab.standalone.wifiGpsControls;

import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;

/**
 * Whenever painting for Rogue, this class should be instantiated, and then the
 * entity to be drawn should be passed into the chooseEntityShape(Entity)
 * method. This method will return the appropriate shape, based upon the
 * Entity's type and status.
 * 
 * All shapes are 10 x 10 clicks in the graphics space. They are also all
 * centered at 0, 0.
 * 
 * @author Z O. Toups (zach@ecologylab.net)
 */
public abstract class CommonShapes
{
    private static Area    gpsSymbolShape     = null;

    private static boolean shapesInstantiated = false;

    private static Area    wifiSymbolShape    = null;

    private static Area    compassRoseShape   = null;

    /**
     * @return Returns the gpsSymbolShape.
     */
    public static Area getGpsSymbolShape()
    {
        if (!shapesInstantiated)
            instantiateShapes();

        return gpsSymbolShape;
    }

    /**
     * @return Returns the wifiSymbolShape.
     */
    public static Area getWifiSymbolShape()
    {
        if (!shapesInstantiated)
            instantiateShapes();

        return wifiSymbolShape;
    }

    /**
     * Initializes all of the painting objects, primarily the odd shapes, which
     * must be created with code.
     * 
     */
    private static void instantiateShapes()
    {
        makeWifiSymbolShape();
        makeGpsSymbolShape();

        makeCompassRoseShape();

        shapesInstantiated = true;
    }

    private static void makeCompassRoseShape()
    {
        // chevron shape for avatar
        float chevronPointsX[] =
        { 0, 5, 0, -5 };
        float chevronPointsY[] =
        { -5, 5, 3, 5 };

        compassRoseShape = makeShape(chevronPointsX, chevronPointsY);
    }

    public static Area getCompassRoseShape()
    {
        if (!shapesInstantiated)
            instantiateShapes();

        return compassRoseShape;
    }

    /**
     * Makes the satellite shape for the GPS symbol.
     * 
     */
    private static void makeGpsSymbolShape()
    {
        float gpsShapeX[] =
        { 2, 5.5f, 6, 5, 7, 10, 8, 7, 6.5f, 10, 8, 4.5f, 4, 5, 3, 0, 2, 3,
                3.5f, 0 };
        float gpsShapeY[] =
        { 0, 3.5f, 3, 2, 0, 3, 5, 4, 4.5f, 8, 10, 6.5f, 7, 8, 10, 7, 5, 6,
                5.5f, 2 };

        // we made this before deciding to center all icons at 0, 0
        // so we are just going to hack it a bit real quick

        for (int i = 0; i < gpsShapeX.length; i++)
        {
            gpsShapeX[i] -= 5;
            gpsShapeY[i] -= 5;
        }

        gpsSymbolShape = makeShape(gpsShapeX, gpsShapeY);
    }

    private static Area makeShape(float[] pointsX, float[] pointsY)
    {
        GeneralPath tempShape;

        tempShape = new GeneralPath(GeneralPath.WIND_EVEN_ODD, pointsX.length);
        tempShape.moveTo(pointsX[0], pointsY[0]);

        for (int i = 1; i < pointsX.length; i++)
        {
            tempShape.lineTo(pointsX[i], pointsY[i]);
        }

        tempShape.lineTo(pointsX[0], pointsY[0]);
        tempShape.closePath();

        Area tempArea = new Area(tempShape);

        AffineTransform transform = new AffineTransform();
        transform.setToRotation(Math.toRadians(90));

        return tempArea.createTransformedArea(transform);
    }

    private static void makeWifiSymbolShape()
    {
        wifiSymbolShape = new Area(new Ellipse2D.Double(-5, -5, 10, 10));
    }
}
