package uk.co.dancowan.robots.srv.ui.views.camera;

import java.awt.image.BufferedImage;

/**
 * Interface for classes which intend to draw on top of an SRV camera image.
 * 
 * <p>AWT paint methods should be used as the camera image is a <code>
 * java.awt.Image</code> instance. the method <code>shouldRun()</code>
 * will be called and should return <code>true</code> iff an overlay is
 * to be painted. Returning <code>false</code> effectively turns this overlay
 * off.</p>
 */
public interface OverlayContributor
{
	/**
	 * Return <code>true</code> if this overlay should be painted
	 * @return boolean
	 */
	public boolean shouldRun();
	
	/**
	 * Paint this overlay to the <code>BufferedImage</code> instance.
	 */
	public void paintOverlay(BufferedImage image);
	
	/**
	 * Set this contributors run state.
	 */
	public void setShouldRun(boolean shouldRun);

	/**
	 * return the overlay's unique ID
	 */
	public String getID();
}
