package uk.co.dancowan.robots.hal.core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import uk.co.dancowan.robots.hal.logger.LoggingService;

/**
 * Basic implementation of the XModem protocol.
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class XModem
{
	private static final Logger LOGGER = Logger.getLogger(LoggingService.INFO_LOGGER);

	private static final byte CPMEOF = 26;
	private static final int MAXERRORS = 10;
	private static final int SECSIZE = 128;
	private static final int SENTIMOUT = 30;
	private static final int SLEEP = 30;

	/* Protocol characters used */
	private static final byte SOH = 1;		/* Start Of Header */
	private static final byte EOT = 4;		/* End Of Transmission */
	private static final byte ACK = 6;		/* ACKnowlege */
	private static final byte NAK = 0x15;	/* Negative AcKnowlege */

	private final Object mMutex = new Object();

	private boolean mGotChar;

	/**
	 * Send a file given the passed filename to the remote.
	 */
	public boolean send(String file) throws IOException, InterruptedException
	{
		DataInputStream dataStream = new DataInputStream(new FileInputStream(file));
		LOGGER.finest( "File " + file + " open, ready to send");

		gotChar(false);
		new IOTimer(SENTIMOUT, "NAK to start send").start();

		char errorCount = 0;
		char blockNumber = 1;
		byte character = getchar();
		while (character != NAK && errorCount < MAXERRORS);
		{
			gotChar(true);
			if (character != NAK && errorCount < MAXERRORS)
			{
				errorCount ++;
			}
			character = getchar();
		}

		if (errorCount == MAXERRORS)
		{
			LOGGER.finer("XModem: failed to establish communication with remote");
			return false; //throw an exception perhaps?
		}

		LOGGER.finest( "XModem: transmission beginning");
		char checkSum;
		byte[] sector = new byte[SECSIZE];
		int nBytes = dataStream.read(sector);
		while (nBytes != 0)
		{
			if (nBytes < SECSIZE)
				sector[nBytes] = CPMEOF;
			errorCount = 0;

			while (errorCount < MAXERRORS)
			{
				LOGGER.finest( "{" + blockNumber + "} ");
				putchar(SOH);			// header
				putchar(blockNumber);	// block number
				putchar(~blockNumber);	// block number complement
				checkSum = 0;
				for (char index = 0; index < SECSIZE; index++)
				{
					putchar(sector[index]);
					checkSum += sector[index];
				}
				putchar(checkSum);		// write checksum

				if (getchar() != ACK)
					++errorCount;
				else
					break;
			}
			if (errorCount == MAXERRORS)
			{
				LOGGER.finer("XModem: too many errors caught, abandoning transfer");
				return false; //throw an exception perhaps?
			}

			blockNumber ++;
			nBytes = dataStream.read(sector);
		}

		boolean isAck = false;
		while (!isAck)
		{
			putchar(EOT);
			isAck = getchar() == ACK;
		}
		LOGGER.finest( "Transmission complete.");
		return true;
	}

	/**
	 * Receive a file from the remote
	 */
	public boolean receive(String file) throws IOException, InterruptedException
	{
		LOGGER.finest("Remote has " + SLEEP + " seconds...");

		// wait for remote
		gotChar(false);
		new IOTimer(SLEEP, "receive from remote").start(); 

		LOGGER.finest("Starting receive...");
		putchar(NAK);

		char errorCount = 0;
		char blockNumber = 1;
		char checkSum = 0;
		byte[] sector = new byte[SECSIZE];
		byte character;
		DataOutputStream dataOutput = new DataOutputStream(new FileOutputStream(file));

		rxLoop:
			do
			{ 
				character = getchar();
				gotChar(true);
				if (character != EOT)
				{
					try
					{
						byte not_ch;
						if (character != SOH)
						{
							LOGGER.finest( "Not SOH");
							if (++errorCount < MAXERRORS)
								continue rxLoop;
							else
							{
								LOGGER.finer("XModem: too many read errors, aborting transfer");
								return false;
							}
						}

						character = getchar();
						not_ch = (byte)(~getchar());
						LOGGER.finest( "[" +  character + "] ");
						if (character != not_ch)
						{
							LOGGER.finest( "Blockcounts not ~");
							++errorCount;
							continue rxLoop;
						}
						if (character != blockNumber)
						{
							LOGGER.finest( "Wrong blocknumber");
							++errorCount;
							continue rxLoop;
						}
						checkSum = 0;
						for (char index = 0; index < SECSIZE; index++)
						{
							sector[index] = getchar();
							checkSum += sector[index];
						}
						if (checkSum != getchar())
						{
							LOGGER.finest( "Bad checksum");
							errorCount++;
							continue rxLoop;
						}
						putchar(ACK);
						blockNumber++;
						try
						{
							dataOutput.write(sector);
						}
						catch (IOException e)
						{
							LOGGER.finer("XModem write failed, blocknumber " + blockNumber);
						}
					}
					finally
					{
						if (errorCount != 0)
							putchar(NAK);
					}
				}
			}
			while (character != EOT);

		dataOutput.close();

		putchar(ACK);   // broadcast accepting EOT
		putchar(ACK);
		putchar(ACK);

		LOGGER.finest("Receive Completed.");
		return true;
	}

	/*
	 * Read one character from the Connection's input stream
	 */
	private byte getchar() throws IOException
	{
		// Make it check the connection type properly, don't assume serial
		Connection connection = HALRegistry.getInsatnce().getCommandQ().getConnection();
		return (byte) connection.read();
	}

	/*
	 * Write one character to the Connection's output stream
	 */
	private void putchar(int c) throws IOException
	{
		Connection connection = HALRegistry.getInsatnce().getCommandQ().getConnection();
		connection.write(c);
	}

	/*
	 * Used to set flag for timeout thread, synchronised
	 */
	private void gotChar(boolean state)
	{
		synchronized (mMutex)
		{
			mGotChar = state;
		}
	}

	/*
	 * Used by timeout thread, synchronised
	 */
	private boolean hasChar()
	{
		synchronized (mMutex)
		{
			return mGotChar;
		}
	}

	/*
	 *  Inner class to provide a read timeout for alarms
	 */
	private class IOTimer extends Thread
	{
		private String mMessage;
		private long mMillis;

		private IOTimer(long sec, String msg)
		{
			mMillis = 1000 * sec;
			mMessage = msg;

			setName("XModem timeout thread");
		}

		public void run()
		{
			try
			{
				Thread.sleep(mMillis);
			}
			catch (InterruptedException e)
			{
				// TODO handle properly
			}

			// Log the error
			if (! hasChar())
				LOGGER.finer("XModem: Timed out waiting for " + mMessage);
		}
	}
}
