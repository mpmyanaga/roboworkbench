/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (C) 2009 Dan Cowan
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
package uk.co.dancowan.robots.srv.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import uk.co.dancowan.robots.hal.logger.LoggingService;
import uk.co.dancowan.robots.srv.SRVConfig;

/**
 * This thread serves as the URL notification worker.
 * 
 * <p>Every ASCII character read from the SRV-1 is sent to this worker.
 * When the internal buffer ('collector') has reached SEARCH_INTERVAL characters,
 * NotifyService searches the string for tokens (defined in srv_notify.config).  
 * When a token is encountered, the associated URL is added to the visit queue.</p>
 * 
 * <p>Derived from code written by the Surveyor Corporation (c) 2005 2009.</p>
 * 
 * @Author Dan Cowan
 * @Since version 1.0.0
 */
public class NotifyService extends Thread
{
	private static final Logger INFO_LOGGER = Logger.getLogger(LoggingService.INFO_LOGGER);
	private static final Logger ERROR_LOGGER = Logger.getLogger(LoggingService.ERROR_LOGGER);

	// run a search every 64 characters by default
	private static final int SEARCH_INTERVAL = 64;

    private Map<String, String> mNotifyTokens;
	private List<String> mURLs;  // queue that hold URLs to be visited
	private StringBuffer mBuffer;

	/**
	 * C'tor.
	 */
	public NotifyService()
	{
		mNotifyTokens = SRVConfig.loadNotifyTokens();
		mURLs = Collections.synchronizedList(new ArrayList<String>());
		mBuffer = new StringBuffer();
		setName("Notify Service thread");
	}

	/**
	 * Adds a character to the buffer.
	 * 
	 * @param c the character to add.
	 */
	public void newChar(char c) 
	{
		mBuffer.append(c);
	}

	/**
	 * Adds token-url mappings read from srv_notify.config.
	 * 
	 * <p>Token map should contains:
	 * <pre>    token=url</pre></p>
	 * 
	 * @param tokens Map&lt;String, String&gt;
	 */
	public void addProperties(Map<String, String> tokens)
	{
		mNotifyTokens.putAll(tokens);
	}

	/**
	 * NotifyService thread loop.
	 * 
	 * <p>Thread will scan the buffer for tokens and visit indicated urls.</p>
	 */
	@Override
	public void run() 
	{
		INFO_LOGGER.fine("Notify service started.");
		while(true)
		{
			try
			{
				Thread.sleep(250);
			}
			catch (InterruptedException ie)
			{
				// NOP
			}

			if (mBuffer.length() >= SEARCH_INTERVAL)
			{
				int lastIndex = findNotificationTokens(mBuffer.toString());
				mBuffer = new StringBuffer(mBuffer.substring(lastIndex));
			}

			for (String urlStr : mURLs)
			{
				try
				{
					if(urlStr != null)
					{
						URL url = new URL(urlStr);
						InputStream urlis = url.openStream();
						urlis.close();
					}
					else
					{
						ERROR_LOGGER.finer("Notify Service found 'null' URL.");
					}
				}
				catch(MalformedURLException e)
				{
					ERROR_LOGGER.finest("NotifyService.run(): " + e.getMessage());
				}
				catch(IOException e)
				{
					ERROR_LOGGER.finest("NotifyService.run(): " + e.getMessage());
				}
			}
		}
	}

	/*
	 * Scan the buffer for tokens.
	 */
	private int findNotificationTokens(String str)
	{
		int lastIndex = 0;

		for(String tok : mNotifyTokens.keySet())
		{
			int index = str.indexOf(tok);
			if (index != -1)
			{
				if (index > lastIndex)
				{
					lastIndex = index + tok.length();
				}

				String url = mNotifyTokens.get(tok);
				mURLs.add(url);
				INFO_LOGGER.finer("Notify Service found token '" + tok + "' for URL '" + url + "'.");
			}
		}

		if(lastIndex == 0)
		{
			lastIndex  = str.length() - (SEARCH_INTERVAL/2);
		}
		return lastIndex;
	}
}