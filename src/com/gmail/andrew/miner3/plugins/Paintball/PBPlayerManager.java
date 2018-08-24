package com.gmail.andrew.miner3.plugins.Paintball;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import Database.Database;
import Database.DatabaseCallback;
import Database.MySQLDatabase;
import Database.SQLiteDatabase;

public class PBPlayerManager implements Listener
{
	public final String SQL_INFO = "SQL Info";
	public final String SQLITE = "SQLite";
	public final String MySQL = "MySQL";
	public final String USE_SQLITE = "Use SQLite";
	public final String DATABASE = "Database";
	public final String HOST = "Host";
	public final String PORT = "Port";
	public final String USERNAME = "Username";
	public final String PASSWORD = "Password";
	
	public final String PLAYER_ID = "playerID";
	public final String KILLS = "kills";
	public final String DEATHS = "deaths";
	public final String SCORE = "score";
	public final String WINS = "wins";
	public final String LOSSES = "losses";
	public final String CREDITS = "credits";
	public final String TABLE_NAME = "pbplayerData";
	
	private PluginManager plugin;
	private Database sqlDB;
	
	private HashMap<UUID, PBPlayer> playerList;
	
	public PBPlayerManager(PluginManager plugin)
	{
		this.plugin = plugin;
		this.sqlDB = null;
		this.playerList = new HashMap<UUID, PBPlayer>();
		
		loadDatabase();
		
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	private void loadDatabase() 
	{
		FileConfiguration config = plugin.getConfig();
		boolean useSQLite = config.getBoolean(SQL_INFO + "." + SQLITE + "." + USE_SQLITE);
		
		DatabaseCallback initializeTable = new DatabaseCallback()
		{
			@Override
			public void onInitializeDone()
			{
				loadTable();
			}
		};
		
		
		if(useSQLite)
		{
			sqlDB = new SQLiteDatabase(plugin, config.getString(SQL_INFO + "." + SQLITE + "." + DATABASE), initializeTable);
		}
		else
		{
			plugin.getLogger().info("FALSE");
			sqlDB = new MySQLDatabase(plugin,
					config.getString(SQL_INFO + "." + MySQL + "." + HOST),
					config.getInt(SQL_INFO + "." + MySQL + "." + PORT),
					config.getString(SQL_INFO + "." + MySQL + "." + DATABASE),
					config.getString(SQL_INFO + "." + MySQL + "." + USERNAME),
					config.getString(SQL_INFO + "." + MySQL + "." + PASSWORD).toCharArray(),
					initializeTable);
		}
	}
	
	private void loadTable() 
	{
		String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
				"'" + PLAYER_ID + "' VARCHAR(36) NOT NULL, " +
				"'" + SCORE + "' INT NOT NULL, " +
				"'" + CREDITS + "' INT NOT NULL, " +
				"'" + KILLS + "' INT NOT NULL, " +
				"'" + DEATHS + "' INT NOT NULL, " +
				"'" + WINS + "' INT NOT NULL, " +
				"'" + LOSSES + "' INT NOT NULL, " +
				"PRIMARY KEY ('" + PLAYER_ID + "')" +
				");";
		
		sqlDB.executeUpdateAsync(createTable, null);
	}

	@EventHandler
	public void preLoginEvent(AsyncPlayerPreLoginEvent e)
	{
		loadPBPlayerSync(e.getUniqueId());
	}
	
	@EventHandler
	public void loginEvent(PlayerLoginEvent e)
	{
		if(!plugin.getServer().getOnlineMode())
			loadPBPlayer(e.getPlayer().getUniqueId());
	}
	
	@EventHandler
	public void leaveEvent(PlayerQuitEvent e)
	{
		plugin.getLogger().info("QUIT EVENT");
		removePlayer(e.getPlayer().getUniqueId());
	}
	
	@EventHandler
	public void kickEvent(PlayerKickEvent e)
	{
		plugin.getLogger().info("KICK EVENT");
		removePlayer(e.getPlayer().getUniqueId());
	}
	
	private void removePlayer(UUID id) 
	{
		updatePBPlayer(id);
		playerList.remove(id);
	}

	public PBPlayer getPBPlayer(UUID id)
	{
		return playerList.get(id);
	}
	
	private void loadPBPlayer(UUID id) 
	{
		plugin.getLogger().info("LOGIN1 EVENT");
		sqlDB.executeQueryAsync("SELECT * FROM " + TABLE_NAME + " WHERE " + PLAYER_ID + " = '" + id.toString() + "';", new DatabaseCallback()
		{
			@Override
			public void onQueryDone(ResultSet set)
			{

				plugin.getLogger().info("LOGIN2 EVENT");
				try 
				{
					if(set == null || !set.next())
					{
						playerList.put(id, new PBPlayer(id));
						plugin.getLogger().info("SET EMPTY ADDED PLAYER");
					}
					else
					{
						plugin.getLogger().info("SET FULL LOADED PLAYER");
						PBPlayer player = new PBPlayer(id, set.getInt(KILLS), set.getInt(DEATHS), set.getInt(SCORE), 0);
						player.setWins(set.getInt(WINS));
						player.setLosses(set.getInt(LOSSES));
						playerList.put(id, player);
					}
				} 
				catch (SQLException e) 
				{
					plugin.getLogger().warning(e.getMessage());
					playerList.put(id, new PBPlayer(id));
				}
			}
		});
	}

	private void loadPBPlayerSync(UUID id)
	{
		ResultSet set = sqlDB.executeQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + PLAYER_ID + " = '" + id.toString() + "';");
		
		try 
		{
			if(set == null || !set.next())
				playerList.put(id, new PBPlayer(id));
			else
			{
				PBPlayer player = new PBPlayer(id, set.getInt(KILLS), set.getInt(DEATHS), set.getInt(SCORE), 0);
				player.setWins(set.getInt(WINS));
				player.setLosses(set.getInt(LOSSES));
				playerList.put(id, player);
			}
		} 
		catch (SQLException e) 
		{
			plugin.getLogger().warning(e.getMessage());
			playerList.put(id, new PBPlayer(id));
		}
	}
	
	public void updatePBPlayer(UUID id)
	{
		PBPlayer player = playerList.get(id);
		
		if(player == null)
			return;
		
		String update = "REPLACE INTO " + TABLE_NAME + " (" + PLAYER_ID + ", " + SCORE + ", " + CREDITS + ", " + KILLS + ", " + DEATHS + ", " + WINS + ", " + LOSSES + ") VALUES (" +
						"'" + player.getID().toString() + "', " + player.getTotalScore() + ", " + 0 + ", " + player.getKills() + ", " + player.getDeaths() + ", " + player.getWins() + ", " + player.getLosses() + ");";
		
		sqlDB.executeUpdateAsync(update, null);
	}

	public void updatePlayersAsync() 
	{
		playerList.forEach((id, player) -> updatePBPlayer(id));
	}
	
	public void updatePlayersSync() 
	{
		playerList.forEach((id, player) -> updatePBPlayerSync(id));
	}
	
	private void updatePBPlayerSync(UUID id)
	{
		PBPlayer player = playerList.get(id);
		
		if(player == null)
			return;
		
		String update = "REPLACE INTO " + TABLE_NAME + " (" + PLAYER_ID + ", " + SCORE + ", " + CREDITS + ", " + KILLS + ", " + DEATHS + ", " + WINS + ", " + LOSSES + ") VALUES (" +
						"'" + player.getID().toString() + "', " + player.getTotalScore() + ", " + 0 + ", " + player.getKills() + ", " + player.getDeaths() + ", " + player.getWins() + ", " + player.getLosses() + ");";
		
		sqlDB.executeUpdate(update);
	}

	public void close() 
	{
		sqlDB.close();
	}
}
