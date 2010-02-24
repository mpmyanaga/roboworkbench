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
package uk.co.dancowan.robots.srv.hal.commands.camera;

import java.io.IOException;

import uk.co.dancowan.robots.hal.core.CommandQ;
import uk.co.dancowan.robots.hal.core.Connection;
import uk.co.dancowan.robots.hal.core.commands.AbstractCommand;
import uk.co.dancowan.robots.hal.core.commands.CommandUtils;
import uk.co.dancowan.robots.srv.hal.camera.FrameDecoder;

/**
 * Grabs and decodes an <code>java.awt.Image</code> from the
 * SRV camera.
 * 
 * @author Dan Cowan
 * @since 1.0.0
 *
 */
public class GrabImageCmd extends AbstractCommand
{
	// low priority interruptible command
	private static final int PRIORITY = 1000;

	private static final byte[] FRAME_HEADER = { '#', '#', 'I', 'M', 'J' };
	private static final String COMMAND = "I";

	private int mFrameSize;
	private char mFrameDim;
	private byte[] mFrame;

	private FrameDecoder mFrameDecoder;

	/**
	 * C'tor.
	 * 
	 * @param decoder a FrameDecoder instance
	 */
	public GrabImageCmd(FrameDecoder decoder)
	{
		super(true);

		mFrameDecoder = decoder;
		mFrameDecoder.start();

		mFrameDim = '5';
		mFrameSize = 0;
	}

	/**
	 * Implementation returns "I".
	 * 
	 * @see uk.co.dancowan.robots.hal.core.Command#getName()
	 */
	@Override
	public String getName()
	{
		return COMMAND;
	}

	/**
	 * Low priority command.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.commands.AbstractCommand#getPriority()
	 */
	@Override
	public int getPriority()
	{
		return PRIORITY;
	}

	/**
	 * Returns the command string to execute
	 * 
	 * @see uk.co.dancowan.robots.hal.core.commands.AbstractCommand#getCommandString()
	 */
	@Override
	protected String getCommandString()
	{
		return CommandUtils.byteArrayToHex(COMMAND.getBytes());
	}

	/**
	 * Read the data from the connection into an Image
	 * 
	 * @see uk.co.dancowan.robots.hal.core.commands.AbstractCommand#read(CommandQ)
	 */
	@Override
	protected String read(CommandQ srv) throws IOException
	{
		int pos = 0;
		String header = "-";

		Connection connection = srv.getConnection();
		if (mFrameSize == 0)
		{
			header = consumeHeader(connection);

			if (FRAME_HEADER.length == header.length())
			{
				byte b = (byte) connection.read();
				mFrameDim = (char) b;

				for (int i = 0; i < 4; i++)
				{
					int in = connection.read();
					mFrameSize += in * Math.pow(256, i);
				}
			}
		} 

		if (mFrameSize > 0 && mFrameSize < (100 * 1024))
		{
			// limit frames to 100k
			if (mFrame == null)
			{
				mFrame = new byte[mFrameSize];
			}

			int timeout = 0;
			while (pos < mFrameSize && timeout < 600 && shouldRun())
			{
				// wait for some data to process
				if (connection.available() <= 0)
				{
					timeout++;
					try
					{
						Thread.sleep(50);
					}
					catch (InterruptedException ie)
					{
						//NOP
					}
					continue;
				}

				int thisRead = connection.read(mFrame, pos, mFrameSize - pos);

				pos += thisRead;
			}

			if (pos == mFrameSize)
			{
				mFrameDecoder.newRawFrame("IMJ" + mFrameDim, mFrame);
			}
		}
		else
		{
			header = "## failed reading frame of size " + mFrameSize;
		}
		mFrameSize = 0;
		mFrame = null;
		connection.readComplete();
		return header;
	}

	/*
	 * Returns the result header: ##IMJ
	 */
	private byte[] getHeader()
	{
		return FRAME_HEADER;
	}

	/*
	 * Reads the input stream to consume the command's header.
	 */
	private String consumeHeader(Connection connection) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		int mark = 0;
		int count = 0;
		byte[] header = getHeader();
		while(mark < header.length && shouldRun() && count < READ_LOOP_TIMEOUT)
		{
			count ++;
			while (connection.available() > 0 && mark < getHeader().length)
			{
				byte b = (byte) connection.read();
				if (getHeader()[mark] == (char) b)
				{
					mark++;
					sb.append((char) b);
				}
				else
				{
					mark = 0;
				}
			}
		}
		return sb.toString();
	}
}
