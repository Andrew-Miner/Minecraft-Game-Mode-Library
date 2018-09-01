package SmokyMiner.MiniGames.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.bukkit.plugin.java.JavaPlugin;

public class MySQLDatabase extends MGDatabase
{
	public MySQLDatabase(JavaPlugin plugin, String host, int port, String database, String username, char[] password) 
	{
		super(plugin, host, port, database, username, password);
	}

	public MySQLDatabase(JavaPlugin plugin, String host, int port, String database, String username, char[] password, MGDBCallback initializeTable)
	{
		super(plugin, host, port, database, username, password, initializeTable);
	}

	@Override
	protected Connection getSQLConnection() throws SQLException, ClassNotFoundException 
	{
	    synchronized (this)
	    {
	        Class.forName("com.mysql.jdbc.Driver");
	        return DriverManager.getConnection("jdbc:mysql://" + this.host+ ":" + this.port + "/" + this.database, this.username, String.valueOf(this.password, 0, PASSWORD_LENGTH));
	    }
	}
}