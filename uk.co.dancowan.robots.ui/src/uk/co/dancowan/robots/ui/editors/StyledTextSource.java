package uk.co.dancowan.robots.ui.editors;

import org.eclipse.swt.custom.StyledText;

/**
 * Implementations are able to provide access to a <code>StyledText</code> widget.
 * 
 * @author Dan Cowan
 * @since 1.0.0
 *
 */
public interface StyledTextSource
{
	/**
	 * Returns the <code>StyledText</code> widget provided by the source.
	 * 
	 * @return StyledText
	 */
	public StyledText getText();
}
