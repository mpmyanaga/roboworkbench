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
package uk.co.dancowan.robots.ui.views.keypad;

import java.net.URL;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

/**
 * Describes a Button for a Command.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class CommandButtonDescriptor implements Comparable<CommandButtonDescriptor>
{
	private final String mName;
	private final String mTooltip;
	private final String mIcon;
	private final String mType;
	private final String mRadioID;
	private final String mOnCmd;
	private final String mOffCmd;
	private final boolean mEncode;
	private final ImageDescriptor mDescriptor;

	private int mReturn;
	private int mIndex;

	/**
	 * C'tor.
	 *
	 * @param element The IConfigurationElement from the plugin registry
	 */
	public CommandButtonDescriptor(IConfigurationElement element)
	{
		mName = element.getAttribute("name");
		mTooltip = element.getAttribute("tooltip");
		mIcon = element.getAttribute("icon");
		mType = element.getAttribute("type");
		mRadioID = element.getAttribute("radioID");
		mOnCmd = element.getAttribute("onCmd");
		mOffCmd = element.getAttribute("offCmd");
		if ("true".equals(element.getAttribute("encode")))
			mEncode = true;
		else
			mEncode = false;

		try
		{
			mIndex = Integer.parseInt(element.getAttribute("index"));
		}
		catch (NumberFormatException e)
		{
			mIndex = -1; // not assigned
		}
		try
		{
			mReturn = Integer.parseInt(element.getAttribute("return"));
		}
		catch (NumberFormatException e)
		{
			mReturn = -1; // expect line ending char
		}
		if (mIcon != null)
		{
			Bundle bundle = Platform.getBundle(element.getContributor().getName());
			URL url = bundle.getEntry("/" + mIcon);
			mDescriptor = ImageDescriptor.createFromURL(url);
		}
		else
		{
			mDescriptor = null;
		}
	}

	/**
	 * @return the Button's name
	 */
	public String getName()
	{
		return mName;
	}

	public Image getImage()
	{
		return mDescriptor.createImage();
	}

	/**
	 * @return the Button's tool-tip
	 */
	public String getTooltip()
	{
		return mTooltip;
	}

	/**
	 * @return the Button's icon
	 */
	public String getIcon()
	{
		return mIcon;
	}

	/**
	 * @return the Button's type
	 */
	public String getType()
	{
		return mType;
	}

	/**
	 * @return the Button's radioID
	 */
	public String getRadioID()
	{
		return mRadioID;
	}

	/**
	 * @return the Button's on command in hex
	 */
	public String getOnHex()
	{
		return mOnCmd;
	}

	/**
	 * @return the Button's off command in hex
	 */
	public String getOffHex()
	{
		return mOffCmd;
	}

	/**
	 * @return expected return string length
	 */
	public int getReturn()
	{
		return mReturn;
	}

	/**
	 * @return index
	 */
	public int getIndex()
	{
		return mIndex;
	}

	/**
	 * @return boolean true if this command needs encoding in hexadecimal
	 */
	public boolean shouldEncode()
	{
		return mEncode;
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(CommandButtonDescriptor o)
	{
		if (mIndex < 0) // not assigned
			return 1; // to end of list
		return mIndex - o.getIndex();
	}
}
