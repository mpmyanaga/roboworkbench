/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (C) 2009  Dan Cowan
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details (www.gnu.org/licenses)
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package uk.co.dancowan.robots.srv.ui.views.camera.overlays;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import uk.co.dancowan.robots.srv.ui.views.camera.OverlayContributor;

/**
 * Class manages instances of OverlayContributor supplied programaticaly or by the
 * defined extension point.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class OverlayManager implements Iterable<OverlayContributor>
{
	private static final String OVERLAY_EXT = "uk.co.dancowan.robots.srv.overlay";
	private final Map<String, OverlayContributor> mOverlays;

	/**
	 * C'tor.
	 *
	 */
	public OverlayManager()
	{
		mOverlays = new HashMap<String, OverlayContributor>();

		readExtensionPoint();
	}

	/**
	 * Add a new overlay to those managed by this manager.
	 * 
	 * @param overlay the OverlayContributor to manage
	 */
	public void addOverlay(OverlayContributor overlay)
	{
		mOverlays.put(overlay.getID(), overlay);
	}

	/**
	 * Returns the identified <code>OverlayContributor</code> or <code>null</code
	 * if no such overlay is managed by this class.
	 * 
	 * @param id the identifier for the overlay requested
	 * @return OverlayContributor
	 */
	public OverlayContributor getOverlay(String id)
	{
		return mOverlays.get(id);
	}

	/**
	 * Returns an <code>Iterator</code> for the <code>OverlayContributors<code> managed
	 * by this manager.
	 * 
	 * @return Iterator
	 */
	public Iterator<OverlayContributor> iterator()
	{
		return new OverlayIterator();
	}

	/*
	 * Read the overlays from the extension point registry
	 */
	private void readExtensionPoint()
	{
		IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(OVERLAY_EXT);
		for (IConfigurationElement element : elements)
		{
			OverlayContributor overlay;
			try
			{
				overlay = (OverlayContributor) element.createExecutableExtension("class");
				addOverlay(overlay);
			}
			catch (CoreException e)
			{
				// NOP
			}
		}
	}

	private class OverlayIterator implements Iterator<OverlayContributor>
	{
		private final List<OverlayContributor> mOverlayList;
		private int mIndex;

		/*
		 * C'tor makes a defensive copy of the internal map as a list
		 */
		private OverlayIterator()
		{
			mIndex = 0;
			mOverlayList = new ArrayList<OverlayContributor>();
			for (String key : mOverlays.keySet())
			{
				mOverlayList.add(mOverlays.get(key));
			}
		}

		/**
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext()
		{
			return mIndex < mOverlayList.size();
		}

		/**
		 * @see java.util.Iterator#next()
		 */
		@Override
		public OverlayContributor next()
		{
			return mOverlayList.get(mIndex ++);
		}

		/**
		 * Throws an UnsupportedOperationException.
		 * 
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove()
		{
			throw new UnsupportedOperationException("OverlayIterator does not support removal.");
		}
	}
}
