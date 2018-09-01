package SmokyMiner.MiniGames.Database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.plugin.java.JavaPlugin;

import SmokyMiner.MiniGames.Database.MGDBCallback;

public abstract class MGDatabase 
{
	protected static final int PASSWORD_LENGTH = 32;

	protected JavaPlugin plugin;
	
	protected Connection connection;
	protected String host, database, username;
	protected int port;
	protected char[] password;
	
	protected Statement statement;
	
	public MGDatabase(JavaPlugin plugin, String host, int port, String database, String username, char[] password)
	{
		if(password != null && password.length > PASSWORD_LENGTH)
			throw new IllegalArgumentException("Password exceeds " + PASSWORD_LENGTH + " character limit");
	
		this.plugin = plugin;
		this.host = host;
		this.port = port;
		this.database = database;
		this.username = username;
		this.password = password;
		
		connection = null;
		statement = null;
		
		initialize();
	}

	public MGDatabase(JavaPlugin plugin, String host, int port, String database, String username, char[] password, MGDBCallback callback) 
	{
		if(password != null && password.length > PASSWORD_LENGTH)
			throw new IllegalArgumentException("Password exceeds " + PASSWORD_LENGTH + " character limit");
	
		this.plugin = plugin;
		this.host = host;
		this.port = port;
		this.database = database;
		this.username = username;
		this.password = password;
		
		connection = null;
		statement = null;
		
		initialize(callback);
	}

	// Abstract Functions
	protected abstract Connection getSQLConnection() throws SQLException, ClassNotFoundException;
	
	protected void initialize()
	{
		initialize(null);
	}
	
	protected void initialize(MGDBCallback callback) 
	{
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable()
		{
			@Override
			public void run() 
			{
				if(connection != null)
					close();
				
				try 
				{
					connection = getSQLConnection();
					
					if(connection != null)
					{
						statement = connection.createStatement();
						
						if(callback != null)
						{
							plugin.getServer().getScheduler().runTask(plugin, new Runnable()
							{
								@Override
								public void run() 
								{
									callback.onInitializeDone();
								}
							
							});
						}
					}
				} 
				catch (ClassNotFoundException | SQLException e) 
				{
					//plugin.getLogger().warning(e.getMessage());
					e.printStackTrace();
					close();
				}
			}
			
		});
	}
	
	public void close()
	{
		if(password != null)
			for(int i = 0; i < PASSWORD_LENGTH; i++)
				password[i] = 'X';
		
		try 
		{
			if(statement != null && !statement.isClosed())
				statement.close();
			if(connection != null && !connection.isClosed())
				connection.close();
		} 
		catch (SQLException e) 
		{
			//plugin.getLogger().warning(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public String getName()
	{
		return database;
	}
	
	public void executeQueryAsync(String query, MGDBCallback callBack)
	{
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable()
		{
			@Override
			public void run() 
			{
				ResultSet set = executeQuery(query);
				
				if(callBack != null)
				{
					plugin.getServer().getScheduler().runTask(plugin, new Runnable()
					{
						@Override
						public void run() 
						{
							callBack.onQueryDone(set);
						}
						
					});
				}
			}
		});
	}
	
	public void executeUpdateAsync(String query, MGDBCallback callBack)
	{
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable()
		{
			@Override
			public void run() 
			{
				executeUpdate(query);
				
				if(callBack != null)
				{
					plugin.getServer().getScheduler().runTask(plugin, new Runnable()
					{
						@Override
						public void run() 
						{
							callBack.onUpdateDone();
						}
						
					});
				}
			}
		});
	}
	
	public ResultSet executeQuery(String query)
	{
		if(statement == null)
			throw new IllegalStateException("SQL Statement null");
		
		try {
			return statement.executeQuery(query);
		} catch (SQLException e) {
			//plugin.getLogger().warning(e.getMessage());
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void executeUpdate(String update)
	{
		if(statement == null)
			throw new IllegalStateException("SQL Statement null");
		
		try {
			statement.executeUpdate(update);
		} catch (SQLException e) {
			//plugin.getLogger().warning(e.getMessage());
			e.printStackTrace();
		}
	}
}