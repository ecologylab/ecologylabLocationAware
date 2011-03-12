/**
 * 
 */
package ecologylab.serialization.library.kml.overlay;

import ecologylab.serialization.ElementState;
import ecologylab.serialization.Hint;

/**
 * According to
 * http://code.google.com/apis/kml/documentation/kml_tags_beta1.html#photooverlay
 * 
 * Defines how much of the current scene is visible. Specifying the field of
 * view is analogous to specifying the lens opening in a physical camera. A
 * small field of view, like a telephoto lens, focuses on a small part of the
 * scene. A large field of view, like a wide-angle lens, focuses on a large part
 * of the scene.
 * 
 * @author Zachary O. Toups (zach@ecologylab.net) (Java classes only)
 */
public class ImagePyramid extends ElementState
{
	/**
	 * Angle, in degrees, between the camera's viewing direction and the left
	 * side of the view volume.
	 */
	@simpl_scalar @simpl_hints(Hint.XML_LEAF) @xml_tag("leftFov") public int		leftFov;

	/**
	 * Angle, in degrees, between the camera's viewing direction and the right
	 * side of the view volume.
	 */
	@simpl_scalar @simpl_hints(Hint.XML_LEAF) @xml_tag("rightFov") public int		rightFov;

	/**
	 * Angle, in degrees, between the camera's viewing direction and the bottom
	 * side of the view volume.
	 */
	@simpl_scalar @simpl_hints(Hint.XML_LEAF) @xml_tag("bottomFov") public int	bottomFov;

	/**
	 * Angle, in degrees, between the camera's viewing direction and the top side
	 * of the view volume.
	 */
	@simpl_scalar @simpl_hints(Hint.XML_LEAF) @xml_tag("topFov") public int		topFov;

	/**
	 * Measurement in meters along the viewing direction from the camera
	 * viewpoint to the PhotoOverlay shape.
	 */
	@simpl_scalar @simpl_hints(Hint.XML_LEAF) @xml_tag("near") public int			near;

	/**
	 * 
	 */
	public ImagePyramid()
	{
	}

}