package Database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.andrew.miner3.plugins.Paintball.PluginManager;
import com.gmail.andrew.miner3.plugins.Paintball.Assist.ConfigMethods;

public class SQLiteDatabase extends Database
{
	private final static String DB_EXTENSION = ".db";
	private File dataFile;
	
	public SQLiteDatabase(JavaPlugin plugin, String database) 
	{
		super(plugin, "localhost", 0, database, "root", null);
	}
	
	public SQLiteDatabase(PluginManager plugin, String database, DatabaseCallback callback) 
	{
		super(plugin, "localhost", 0, database, "root", null, callback);
	}

	@Override
	protected void initialize(DatabaseCallback callback) 
	{
		dataFile = ConfigMethods.createFile(plugin, database + DB_EXTENSION);
		
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
	
	@Override
	protected Connection getSQLConnection() throws SQLException, ClassNotFoundException 
	{
		if(dataFile == null)
			return null;
		
		synchronized (this)
		{
	        Class.forName("org.sqlite.JDBC");
	        return DriverManager.getConnection("jdbc:sqlite:" + dataFile);
		}
	}

}
