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
package uk.co.dancowan.robots.ui.views.commandConsole;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;

import uk.co.dancowan.robots.hal.core.Command;
import uk.co.dancowan.robots.hal.core.Connection;
import uk.co.dancowan.robots.hal.core.ConnectionListener;
import uk.co.dancowan.robots.hal.core.HALRegistry;
import uk.co.dancowan.robots.hal.logger.LoggingService;
import uk.co.dancowan.robots.ui.Activator;
import uk.co.dancowan.robots.ui.editors.actions.CommandAction;
import uk.co.dancowan.robots.ui.preferences.PreferenceConstants;
import uk.co.dancowan.robots.ui.utils.ColourManager;
import uk.co.dancowan.robots.ui.utils.TextUtils;
import uk.co.dancowan.robots.ui.views.ScrolledView;
import uk.co.dancowan.robots.ui.views.actions.ClearAction;
import uk.co.dancowan.robots.ui.views.actions.Clearable;
import uk.co.dancowan.robots.ui.views.actions.EscCmd;
import uk.co.dancowan.robots.ui.views.actions.Lockable;
import uk.co.dancowan.robots.ui.views.actions.ScrollLockAction;

/**
 * Eclipse <code>View</code> class attaches to root <code>Logger</code> instance
 * exposing methods to change log level along with standard edit utilities.
 * 
 * <p>Also embeds a command line accessed by hitting the enter key. Command style
 * can be set in the preferences and allows control over return expectations
 * and hexidecimal/decimal encodings</p>
 * 
 * @author Dan Cowan
 * @since version 1.0.0
 */
public class CommandConsole extends ScrolledView implements IPropertyChangeListener, Lockable, Clearable, ConnectionListener
{
	public static final String ID = "uk.co.dancowan.robots.ui.commandConsole";

	private static final Point MIN_SIZE = new Point(300, 140);
	private static final int FINE = 0;
	private static final int FINER = 1;
	private static final int FINEST = 2;
	private static final int UNLIMITED = -1;

	private StyledText mText;
	private Text mCommandText;
	private RGB mOutputColour;
	private RGB mErrorColour;
	private Logger mConfiguredLogger;

	private LogHandler mHandler;
	private long mMaxBufferSize;
	private MenuItem[] mLevels;
	private boolean mInitialized;
	private boolean mPin;
	private boolean mShowExceptions;
	private boolean mWrap;

	private InputRenderer mInputRenderer;
	private final CommandLine mCommandLine;

	/**
	 * C'tor.
	 */
	public CommandConsole()
	{
		mCommandLine = new CommandLine();
		HALRegistry.getInsatnce().getCommandQ().getConnection().addConnectionListener(this);
	}

	/**
	 * Overrides default implementation in <code>View</code> to enable defaults from
	 * <code>IMemento</code> instance or preferences.
	 */
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException
	{
		super.init(site, memento);

		mLevels = new MenuItem[3];

		Activator.getDefault().getPreferenceStore().addPropertyChangeListener(this);
		initializeFromPreferences();

		mPin = false;
		mInitialized = false;
	}

	/**
	 * @see uk.co.dancowan.robots.ui.views.ScrolledView#getPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control getPartControl(Composite parent)
	{
		final Composite part = new Composite(parent, SWT.BORDER);
		part.setLayout(new FormLayout());

		mText = new StyledText(part, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION | SWT.READ_ONLY);
		mText.setWordWrap(mWrap);
		mInputRenderer = new InputRenderer(this);
		mText.addExtendedModifyListener(mInputRenderer);
		mText.setMenu(getContextMenu());

		setInitialText();

		mCommandText = new Text(part, SWT.SINGLE | SWT.BORDER);
		mCommandText.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyReleased(KeyEvent e)
			{
				if (e.keyCode == 27) // send escape
				{
					Command cmd = new EscCmd(false);
					HALRegistry.getInsatnce().getCommandQ().addCommand(cmd);
				}
				else if (e.keyCode == 13) // enter - execute command
				{
					mCommandLine.setText(mCommandText.getText());
					mCommandLine.execute();
					mText.append(mCommandText.getText() + TextUtils.CR);
					mCommandText.setText("");
				}
				else if (e.keyCode == 16777217) // up cursor key - do history
				{
					String str = mCommandLine.last();
					if (str != null)
						mCommandText.setText(str);
				}
				else if (e.keyCode == 16777218) // up cursor key - do history
				{
					String str = mCommandLine.next();
					if (str != null)
						mCommandText.setText(str);
				}
			}
		});

		FormData data = new FormData();
		data.top = new FormAttachment(0, 5);
		data.left = new FormAttachment(0, 5);
		data.right = new FormAttachment(100, -5);
		data.bottom = new FormAttachment(mCommandText, -2, SWT.TOP);
		mText.setLayoutData(data);

		data = new FormData();
		data.top = new FormAttachment(100, -21);
		data.left = new FormAttachment(0, 5);
		data.right = new FormAttachment(100, -5);
		data.bottom = new FormAttachment(100, -5);
		mCommandText.setLayoutData(data);

		if (mShowExceptions)
			attachToLogger(LoggingService.ROOT_LOGGER);
		else
			attachToLogger(LoggingService.INFO_LOGGER);

		createToolbar();

		part.pack();
		return part;
	}

	/**
	 * Returns this view's unique identifier.
	 * 
	 * @return String
	 */
	public String getID()
	{
		return ID;
	}

	/**
	 * Returns the contents of the widget as a String.
	 * 
	 * @return String
	 */
	public String getText()
	{
		return mText.getText();
	}

	/**
	 * @see uk.co.dancowan.robots.ui.views.ScrolledView#getMinSize()
	 */
	@Override
	public Point getMinSize()
	{
		return MIN_SIZE;
	}

	/**
	 * Sets the passed <code>StyleRange</code> on the classes' <code>org.eclipse.jface.StyledText</code>
	 * instance.
	 * 
	 * @param range
	 */
	public void setStyleRange(StyleRange range)
	{
		mText.setStyleRange(range);
	}

	/**
	 * Implementation clears the class's text widget.
	 * 
	 * @see uk.co.dancowan.robots.ui.views.actions.Clearable#clear()
	 */
	@Override
	public void clear()
	{
		mText.setText("");
	}

	/**
	 * @see uk.co.dancowan.robots.ui.views.actions.Lockable#pin()
	 */
	@Override
	public void pin()
	{
		mPin = true;
	}

	/**
	 * @see uk.co.dancowan.robots.ui.views.actions.Lockable#release()
	 */
	@Override
	public void release()
	{
		mPin = false;
	}

	public StyledText getWidget()
	{
		return mText;
	}

	/**
	 * Sets widget state according to connection events.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.ConnectionListener#connected()
	 */
	@Override
	public void connected()
	{
		Connection connection = HALRegistry.getInsatnce().getCommandQ().getConnection();
		final StringBuilder sb = new StringBuilder("Connected to ");
		if (connection.isNetworkPort())
		{
			sb.append(connection.getHost());
			sb.append(":");
			sb.append(connection.getNetworkPort());
		}
		else
		{
			sb.append("com port: ");
			sb.append(connection.getComPort());
		}
		insertMessage(sb.toString(), false);
		newLine();
	}

	/**
	 * Sets widget state according to connection events.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.ConnectionListener#disconnected()
	 */
	@Override
	public void disconnected()
	{
		String message = "Disconnected";
		insertMessage(message, false);
		newLine();
	}

	/**
	 * Sets widget state according to connection events.
	 * 
	 * @see uk.co.dancowan.robots.hal.core.ConnectionListener#error(java.lang.String)
	 */
	@Override
	public void error(final String message)
	{
		if (message != null)
		{
			insertMessage(message, true);
			newLine();
		}
	}

	/**
	 * No-op
	 * 
	 * @see uk.co.dancowan.robots.hal.core.ConnectionListener#rx(java.lang.String)
	 */
	public void rx(final String message)
	{
		// NOP
	}

	/**
	 * No-op
	 * 
	 * @see uk.co.dancowan.robots.hal.core.ConnectionListener#tx(java.lang.String)
	 */
	public void tx(String message)
	{
		// NOP
	}

	/**
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event)
	{
		if (PreferenceConstants.COMMAND_BUFFER_SIZE.equals(event.getProperty()))
		{
			int buffer = (Integer) event.getNewValue();
			setBufferSizeK(buffer); // preference is in k so multiply
		}
		else if (PreferenceConstants.COMMAND_UNLIMITED_BUFFER.equals(event.getProperty()))
		{
			boolean unlimited = (Boolean) event.getNewValue();
			if (unlimited)
			{
				setBufferSizeK(UNLIMITED);
			}
			else
			{
				IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
				setBufferSizeK(prefs.getInt(PreferenceConstants.COMMAND_BUFFER_SIZE));
			}
		}
		else if (PreferenceConstants.COMMAND_EXCEPTIONS.equals(event.getProperty()))
		{
			boolean errors = (Boolean) event.getNewValue();
			if (errors)
				attachToLogger(LoggingService.ROOT_LOGGER);
			else
				attachToLogger(LoggingService.INFO_LOGGER);
		}
		else if (PreferenceConstants.COMMAND_WRAP.equals(event.getProperty()))
		{
			mWrap = (Boolean) event.getNewValue();
			if (!mText.isDisposed())
				mText.setWordWrap(mWrap);
		}

		mCommandLine.initFromPreferences();
		mInputRenderer.configureFromPreferences();
	}

	/*
	 * Recursive call calculates the position of the passed control relative
	 * to the display.
	 */
	/*package*/ Point getDisplayRelativeLocation(Control control)
	{
		if (control.getParent() == null) //assuming this is a shell
			return control.getLocation();
		Point pTree = getDisplayRelativeLocation(control.getParent());
		Point pThis = control.getLocation();
		return new Point(pTree.x + pThis.x, pTree.y + pThis.y);
	}

	/*
	 * Set the initial text in the widget
	 */
	private void setInitialText()
	{
		mText.setText("RoboWorkbench Command Console");
		mText.append(TextUtils.CR);
		mText.append("======================");
		mText.append(TextUtils.CR);
	}

	/*
	 * Attach to a java.util.logging.Logger of the given name.
	 */
	private void attachToLogger(String name)
	{
		if (mConfiguredLogger != null && mHandler != null)
		{
			mConfiguredLogger.fine("Command Console detached from logger: " + mConfiguredLogger.getName());
			mConfiguredLogger.removeHandler(mHandler);
		}

		if (!mInitialized)
		{
			insertMessage(Activator.getDefault().getLogHistory(), false);
			mInitialized = true;
			mHandler = new LogHandler();
		}
		mConfiguredLogger = Logger.getLogger(name);
		mConfiguredLogger.addHandler(mHandler);
		mConfiguredLogger.fine("Command Console attached to logger: " + name);
		setLevelWidgets();
	}

	/*
	 * Sets the internal buffer size of this log viewer in kb.
	 *
	 * <p>Buffer size should be > 0. A value of -1 will set no limit on the buffer size.</p>
	 * 
	 * @throws IllegalArgumentException if the buffer size < 1k and != -1
	 */
	private void setBufferSizeK(long maxLength)
	{
		if (maxLength != UNLIMITED)
		{
			if (maxLength < 1)
				throw new IllegalArgumentException("Buffer length out of bounds (must be > -1) :" + maxLength);
			maxLength *= 1024;
		}
		mMaxBufferSize = maxLength;
	}

	/*
	 * Create toolbar.
	 */
    private void createToolbar()
    {
    	IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
    	mgr.add(new CommandAction(new EscCmd(false), "ESC", "Send escape character.", Activator.getImageDescriptor("icons/cmd_escape.gif")));
    	mgr.add(new Separator());
    	mgr.add(new ScrollLockAction(this));
    	mgr.add(new ClearAction(this));
	}

	/*
	 * Initialise widgets from preferences.
	 */
	private void initializeFromPreferences()
	{
		IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();

		boolean unlimited = ! prefs.getBoolean(PreferenceConstants.COMMAND_UNLIMITED_BUFFER);
		if (unlimited)
		{
			setBufferSizeK(UNLIMITED);
		}
		else
		{
			setBufferSizeK(prefs.getInt(PreferenceConstants.COMMAND_BUFFER_SIZE));
		}

		mShowExceptions = prefs.getBoolean(PreferenceConstants.COMMAND_EXCEPTIONS);
		mWrap = prefs.getBoolean(PreferenceConstants.COMMAND_WRAP);
		mOutputColour = PreferenceConverter.getColor(prefs, PreferenceConstants.COMMAND_OUTPUT_COLOUR);
		mErrorColour = PreferenceConverter.getColor(prefs, PreferenceConstants.COMMAND_ERROR_COLOUR);

		if (mInputRenderer != null)
			mInputRenderer.configureFromPreferences();
		if (mCommandLine != null)
			mCommandLine.initFromPreferences();
	}

	/*
	 * Adjust the menu item selection state to match the new logger level
	 */
	private void setLevelWidgets()
	{
		Logger logger = mConfiguredLogger;
		if (logger.getLevel() == null)
		{
			logger = logger.getParent();
		}
		assert logger.getLevel() != null : "Unexpected 'null' LogLevel for logger " + mConfiguredLogger.getName() + " and its parent " + logger.getName();

		if (logger.getLevel().equals(Level.FINEST))
			setLevels(FINEST);
		else if (logger.getLevel().equals(Level.FINER))
			setLevels(FINER);
		else
			setLevels(FINE);
	}

	/*
	 * Set one logger state menu item as selected, other not selected
	 */
	private void setLevels(int index)
	{
		for (int i = 0; i < mLevels.length; i ++)
			mLevels[i].setSelection(i == index);
	}

	/*
	 * Gets the StyledText widget's context menu
	 */
	private Menu getContextMenu()
	{
		final Menu menu = new Menu(mText);

		final MenuItem level = new MenuItem(menu, SWT.CASCADE);
		level.setText("Log level");
		final Menu levels = new Menu(menu);
		level.setMenu(levels);

		final MenuItem fine = new MenuItem(levels, SWT.RADIO);
		fine.setText("Fine");
		fine.setSelection(false);
		fine.addSelectionListener(new SelectionAdapter()
		{	
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (mConfiguredLogger != null && fine.getSelection())
					mConfiguredLogger.setLevel(Level.FINE);
			}
		});
		mLevels[FINE] = fine;

		final MenuItem finer = new MenuItem(levels, SWT.RADIO);
		finer.setText("Finer");
		finer.addSelectionListener(new SelectionAdapter()
		{	
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (mConfiguredLogger != null && finer.getSelection())
					mConfiguredLogger.setLevel(Level.FINER);
			}
		});
		mLevels[FINER] = finer;

		final MenuItem finest = new MenuItem(levels, SWT.RADIO);
		finest.setText("Finest");
		finest.setSelection(true);
		finest.addSelectionListener(new SelectionAdapter()
		{	
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (mConfiguredLogger != null && finest.getSelection())
					mConfiguredLogger.setLevel(Level.FINEST);
			}
		});
		mLevels[FINEST] = finest;

		new MenuItem(menu, SWT.SEPARATOR);

		final MenuItem copy = new MenuItem(menu, SWT.PUSH);
		copy.setText("Copy");
		copy.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				mText.copy();
			}
		});

		final MenuItem paste = new MenuItem(menu, SWT.PUSH);
		paste.setText("Paste");
		paste.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				mText.paste();
			}
		});

		final MenuItem clear = new MenuItem(menu, SWT.PUSH);
		clear.setText("Clear");
		clear.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				mText.setText("");
			}
		});

		finer.setSelection(false);
		return menu;
	}

	/*
	 * Insert a new line
	 */
	private void newLine()
	{
		insertMessage(TextUtils.CR, false);
	}

	/*
	 * Append an incoming message to the StyledText widget
	 */
	private void insertMessage(final String message, final boolean error)
	{
		Display.getDefault().asyncExec(new Runnable()
		{
			@Override
			public void run()
			{
				if ( ! mText.isDisposed())
				{
					mText.removeExtendedModifyListener(mInputRenderer);

					int length = message.length();

					Color foreground = error ? ColourManager.getInstance().getColour(mErrorColour) : ColourManager.getInstance().getColour(mOutputColour);
					// append message
					StyleRange range = new StyleRange(mText.getText().length(), length, foreground, ColourManager.getColour(SWT.COLOR_WHITE));
					mText.append(message);
					mText.setStyleRange(range);

					mText.addExtendedModifyListener(mInputRenderer);

					trimBuffer();
					mText.setCaretOffset(mText.getText().length());
					if (!mPin)
						mText.showSelection();
				}
			}
		});	
	}

	/*
	 * Trim the buffer to the max length
	 */
	/*package*/ void trimBuffer()
	{
		mText.removeExtendedModifyListener(mInputRenderer);

		// check buffer size and trim as necessary
		while (mMaxBufferSize != UNLIMITED && mText.getText().length() > mMaxBufferSize)
		{
			int start = (int) (mText.getText().length() - mMaxBufferSize);
			String newStr = mText.getText().substring(start);

			List<StyleRange> newRanges = replaceRanges(start, (int)mMaxBufferSize, mText.getStyleRanges());
			mText.setText(newStr);
			mText.replaceStyleRanges(0, (int) mMaxBufferSize, newRanges.toArray(new StyleRange[newRanges.size()]));
			
			mText.setCaretOffset(mText.getText().length());
			if (!mPin)
				mText.showSelection();
		}
		mText.addExtendedModifyListener(mInputRenderer);
	}

	/*
	 * Adjusts the styles to match the widget's content when the content is trimmed to
	 * a maximum length.
	 */
	/*package*/ List<StyleRange> replaceRanges(int start, int length, StyleRange[] ranges)
	{
		// inbound ranges are initially expected to cover 0 to > length
		// start and length mark the substring which will be shifted to 0 -> length
		// outbound ranges should cover 0 to length if possible, or r1.start to length

		List<StyleRange> newRanges = new ArrayList<StyleRange>();
		StyleRange first = ranges[0]; // the initial range: may need clipping at the front

		if (first.start < start)
		{
			// range starts before new text start, clip front
			int len = first.length - (start - first.start);
			// check the fit of the length
			if (len > length)
				len = length;
			if (len > 0)
			{
				StyleRange firstRep = new StyleRange(0, len, first.foreground, first.background);
				newRanges.add(firstRep);
			}
		}
		else
		{
			// range starts after new text start : new range start adjusted
			int st = first.start - start;
			if (st < 0)
				st = 0;
			int len = first.length;
			// check the fit of the length
			if (len > length)
				len = length;
			StyleRange firstRep = new StyleRange(st, len, first.foreground, first.background);
			newRanges.add(firstRep);
		}

		for (int i = 1; i < ranges.length; i ++)
		{
			StyleRange next = ranges[i];
			if (next.start > start + length) // escape if next rang is out of bounds (should never happen)
				return newRanges;

			// range starts after new text start : new range start adjusted
			int st = next.start - start;
			int len = next.length;
			if (st < 0)
			{
				len += st; // (len = len - abs(st)
				st = 0;
			}
			// check the fit of the length
			if (len > start + next.length)
				len = length;
			StyleRange nextRep = new StyleRange(st, len, next.foreground, next.background);
			newRanges.add(nextRep);
		}

		return newRanges;
	}

	/*
	 * Internal implementation of the Handler class
	 */
	private class LogHandler extends Handler
	{
		/**
		 * @see java.util.logging.Handler#close()
		 */
		@Override
		public void close() throws SecurityException
		{
			insertMessage("Logger disconnected.", false);
		}

		/**
		 * @see java.util.logging.Handler#flush()
		 */
		@Override
		public void flush()
		{
			// NOP, base StyledText widget does not require flushing
		}

		/**
		 * @see java.util.logging.Handler#publish(java.util.logging.LogRecord)
		 */
		@Override
		public void publish(LogRecord record)
		{
			boolean error = LoggingService.ERROR_LOGGER.equals(record.getLoggerName());
			insertMessage(record.getMessage(), error);
			newLine();
		}		
	}
}