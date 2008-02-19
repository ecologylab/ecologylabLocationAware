/*
 * Created on Apr 24, 2006
 */
package ecologylab.standalone.wifiGpsControls;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;

import rogue.common.BaseColors;
import rogue.common.CommonRenderers;
import rogue.common.CommonShapes;
import rogue.entity.SeekerAvatar;
import rogue.gamedata.GameDataInterface;
import rogue.prefs.ClientPrefs;

public class WiFiMeter extends JPanel implements BaseColors, CommonRenderers,
        ClientPrefs
{
    private static final long serialVersionUID = -5401407313156785164L;

    private SeekerAvatar      seeker;

    private AffineTransform   saveXForm;

    private Color             color            = BaseColors.EDGE_HASHER_MARKS_COLOR;

    public WiFiMeter(GameDataInterface sGameData, String id)
    {
        this((SeekerAvatar) sGameData.retrieve(id));
    }

    public WiFiMeter(SeekerAvatar avatar)
    {
        this.seeker = avatar;

        // if (Pref.lookupBoolean(OLD_INTERFACE_MODE, false))
        // {
        // color = OLD_SA_REND.generateColor(seeker);
        // }
        // else
        // {
        // color = SA_REND.generateColor(seeker);
        // }
    }

    /**
     * 
     */
    public void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        saveXForm = g2.getTransform();

        int scaleFactor;

        int height = this.getHeight();
        int width = this.getWidth();

        // determine limiting factor
        if (height > (width / 4))
        {
            scaleFactor = width / 40;
        }
        else
        {
            scaleFactor = height / 10;
        }

        g2.setColor(color);

        g2.translate(scaleFactor * 5, height / 2);

        g2.scale(scaleFactor, scaleFactor);

        int numIcons = seeker.getWiFiStr();
        if (numIcons > 4)
            numIcons = 4;

        for (int i = 0; i < numIcons; i++)
        {
            g2.fill(CommonShapes.getWifiSymbolShape());

            g2.translate(10, 0);
        }

        g2.setTransform(saveXForm);
    }
}
