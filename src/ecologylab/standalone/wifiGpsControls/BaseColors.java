package ecologylab.standalone.wifiGpsControls;

import java.awt.Color;

public interface BaseColors
{
    // HSV information for seeker avatar colors
    static final float   BASE_VALUE                       = 0.93f;

    static final float   BASE_SATURATION                  = 0.85f;

    static final float   OUT_VALUE_OFFSET                 = -0.4f;               // -0.39f;

    static final float   OUT_SATURATION_OFFSET            = -0.4f;

    static final float   DISCONNECTED_VALUE_OFFSET        = -0.4f;               // -0.63f;

    static final float   DISCONNECTED_SATURATION_OFFSET   = -0.4f;

    static final float   INVUL_VALUE_OFFSET               = 0.11f;               // -0.63f;

    static final float   INVUL_SATURATION_OFFSET          = -1.0f;

    static final float   GREEN_HUE                        = 0.33f;

    static final float   BLUE_HUE                         = 0.63f;

    static final float   ORANGE_HUE                       = 0.061f;

    static final float   PURPLE_HUE                       = 0.78f;

    static final float   CYAN_HUE                         = 0.47f;

    static final float   PINK_HUE                         = 0.9278f;

    static final float[] AVATAR_HUE_ARRAY                 =
                                                          { GREEN_HUE,
            BLUE_HUE, ORANGE_HUE, PURPLE_HUE, CYAN_HUE, PINK_HUE };

    // entity representation colors
    static final Color   THREAT_COLOR                     = new Color(213, 0, 0);

    static final Color   AVATAR_INVULNERABLE_COLOR        = new Color(255, 255,
                                                                  255);

    static final Color   AVATAR_OFFLINE_COLOR             = new Color(255, 248,
                                                                  242, 120);

    static final Color   GPS_UNCERTAINTY_FIELD_COLOR      = new Color(239, 239,
                                                                  239, 50);

    static final Color   SHIELD_OFF_COLOR                 = new Color(0, 0, 0,
                                                                  0);

    static final Color   SHIELD_ON_COLOR                  = new Color(255, 255,
                                                                  255, 255);

    static final int     GPS_UNCERTAINTY_FIELD_ALPHA      = 1677721600;

    static final float   GPS_UNCERTAINTY_FIELD_SATURATION = .3f;

    static final float   GPS_UNCERTAINTY_FIELD_VALUE      = .4f;

    static final Color   GOAL_ACTIVE_COLOR                = new Color(213, 208,
                                                                  43, 50);

    static final Color   GOAL_INACTIVE_COLOR              = new Color(69, 71,
                                                                  0, 50);

    // map colors
    static final Color   BACKGROUND_DARK_COLOR            = new Color(0, 0, 0);

    static final Color   BACKGROUND_LIGHT_COLOR           = new Color(16, 16,
                                                                  16);

    public static Color  SEEKER_BACKGROUND_COLOR          = new Color(64, 64,
                                                                  64);

    static final Color   BASE_COLOR                       = new Color(150, 104,
                                                                  14);

    static final Color   ACTIVATION_ZONE_PHYS_AREA        = new Color(150, 104,
                                                                  14, 30);

    static final Color   WALLS_COLOR                      = new Color(50, 70,
                                                                  70);

    // other colors
    static final Color   THREAT_SENSOR_COLOR              = new Color(255, 0,
                                                                  0, 5);

    static final Color   SEEKER_SENSOR_COLOR              = new Color(255, 255,
                                                                  255, 50);

    static final Color   GOAL_GRAVITY                     = new Color(254, 255,
                                                                  4, 20);

    static final Color   SELECTED_MOVER_EDGE_COLOR        = new Color(255, 255,
                                                                  255, 200);

    static final Color   MOUSE_OVER_MOVER_EDGE_COLOR      = new Color(200, 200,
                                                                  200, 200);

    static final Color   NORMAL_MOVER_EDGE_COLOR          = new Color(100, 100,
                                                                  100, 200);

    static final Color   PTT_EDGE_COLOR                   = new Color(15, 205,
                                                                  9);

    static final Color   METERS_COLOR                     = new Color(71, 89,
                                                                  255);

    static final Color   RADAR_FRAME_COLOR                = new Color(240, 240,
                                                                  240, 180);

    static final Color   EDGE_HASHER_MARKS_COLOR          = new Color(150, 150,
                                                                  150);

    static final Color   TIME_OUT_COLOR                   = new Color(247, 10,
                                                                  10);

    static final Color   TIME_FULL_COLOR                  = new Color(2, 83, 20);

    static final Color   TEXT_UNDERLAY_BACKGROUND         = new Color(255, 255,
                                                                  255, 150);

}
