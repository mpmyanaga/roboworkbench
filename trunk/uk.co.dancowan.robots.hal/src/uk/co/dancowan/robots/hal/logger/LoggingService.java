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
package uk.co.dancowan.robots.hal.logger;

/**
 * Constants and helper methods to enable command and exception logging within a single
 * framework built on top of the java logging framework.
 * 
 * <p>INFO_LOGGER should be used to log services and commands. Its levels should be:</p>
 * <ul><li>fine - used for service notifications</li>
 * <li>finer - used for command i/o at the highest level</li>
 * <li>finest - used for command or service debugging information</li></ul>
 * <p>ERROR_LOGGER should be used to log exceptions and additional error information.</p>
 * <ul><li>fine - used for exception messages</li>
 * <li>finer - used for additional error information not held in the exception</li>
 * <li>finest - used for stack traces</li></ul>
 * <p>ROOT_LOGGER is the root logger for both the information and exception loggers.</p>
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class LoggingService
{
	public static final String INFO_LOGGER = "uk.co.dancowan.robots.rootLogger.commandLogger";
	public static final String ERROR_LOGGER = "uk.co.dancowan.robots.rootLogger.exceptionLogger";
	public static final String ROOT_LOGGER = "uk.co.dancowan.robots.rootLogger";
}
