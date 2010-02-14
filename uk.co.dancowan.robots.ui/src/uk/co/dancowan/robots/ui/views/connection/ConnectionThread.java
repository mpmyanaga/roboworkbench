package uk.co.dancowan.robots.ui.views.connection;

import uk.co.dancowan.robots.hal.core.Connection;
import uk.co.dancowan.robots.hal.core.HALRegistry;

public class ConnectionThread extends Thread
{
	public ConnectionThread()
	{
		setName("Connection Thread");
	}

	public void run()
	{
		Connection connection = HALRegistry.getInsatnce().getCommandQ().getConnection();
		connection.openConnection();
	}
}
