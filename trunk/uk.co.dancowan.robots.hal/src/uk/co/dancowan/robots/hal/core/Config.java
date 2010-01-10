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
package uk.co.dancowan.robots.hal.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import uk.co.dancowan.robots.hal.logger.LoggingService;

/**
 * Configuration class handles persistent non UI configuration properties. 
 */
public class Config
{
	public static Map<String, String> sProperties = null;
	public static boolean sIsReady = false;

	private static final Logger INFO_LOGGER = Logger.getLogger(LoggingService.INFO_LOGGER);
	private static final Logger ERROR_LOGGER = Logger.getLogger(LoggingService.ERROR_LOGGER);
	
	private static final String CONFIG_FILE = "etc/.config";
	private static final String SEP = "=";
	private static final String COMMENT = "#";

	/**
	 * Read the standard properties file <code>.config</code>
	 * <p>Properties are expected in the form:
	 * <pre>    name=value</pre>
	 * in a simple text file. Property names and values should
	 * avoid onerous characters.</p>
	 * 
	 * <p>May throw an <code>IOException</code> while reading from file.</p>
	 * @throws IOException
	 */
	public static void loadConfig()
	{
		sProperties = new HashMap<String, String>();
		try
		{
			InputStream is = Config.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			INFO_LOGGER.fine("Config loading parameters from file:");
			String line = br.readLine ();
			while (line != null)
			{
				if (!line.startsWith (COMMENT))
				{
					String[]tokens = line.split (SEP);
					if (tokens.length == 2 && tokens[0] != null && tokens[1] != null)
					{
						INFO_LOGGER.finer("    " + tokens[0].trim() + SEP + tokens[1].trim());
						sProperties.put (tokens[0].trim (), tokens[1].trim ());
					}
				}
				line = br.readLine ();
			}
			is.close();
			br.close ();
		}
		catch (IOException e)
		{
			ERROR_LOGGER.finest("Config.loadConfig() : " + e.getMessage());
		}
		sIsReady = true;
	}

	/**
	 * Write the properties to file.
	 * 
	 * <p>Properties are stored in the form:
	 * <pre>    name=value</pre>
	 * in a simple text file. Property names and values should avoid onerous
	 * characters.</p>
	 * 
	 * @throws IOException
	 */
	/*public static void saveConfig()
	{
		BufferedWriter writer;
		try
		{
			File file = new File(Config.class.getClassLoader().getResource(CONFIG_FILE).toURI());
			writer = new BufferedWriter (new FileWriter(file));

			INFO_LOGGER.fine("Config writing to file:");

			for(String property : sProperties.keySet())
			{
				String value = sProperties.get(property);
				INFO_LOGGER.finer("    " + property + SEP + value);
				writer.write(property + SEP + value);
				writer.newLine();
			}
	
			writer.close();
		}
		catch (IOException e)
		{
			ERROR_LOGGER.finest("Config.saveConfig() : " + e.getMessage());
		}
		catch (URISyntaxException e)
		{
			ERROR_LOGGER.finest("Config.saveConfig() : " + e.getMessage());
		}
	}*/
}
