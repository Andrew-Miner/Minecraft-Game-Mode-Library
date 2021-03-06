package SmokyMiner.MiniGames.Player;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import SmokyMiner.Minigame.Main.MGManager;
import SmokyMiner.MiniGames.Database.*;
import SmokyMiner.MiniGames.Utils.SerializationUtils;

public class MGPlayerManager implements Listener
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
	public final String INVENTORY = "inventory";
	public final String TABLE_NAME = "MGPlayerData";
	
	private MGManager main;
	private MGDatabase sqlDB;
	
	private HashMap<UUID, MGPlayer> playerList;
	
	public MGPlayerManager(MGManager main)
	{
		this.main = main;
		this.sqlDB = null;
		this.playerList = new HashMap<UUID, MGPlayer>();
		
		loadDatabase();
		
	}

	private void loadDatabase() 
	{
		FileConfiguration config = main.getConfig();
		boolean useSQLite = config.getBoolean(SQL_INFO + "." + SQLITE + "." + USE_SQLITE);
		
		MGDBCallback initializeTable = new MGDBCallback()
		{
			@Override
			public void onInitializeDone()
			{
				loadTable();
			}
		};
		
		
		if(useSQLite)
		{
			sqlDB = new SQLiteDatabase(main.plugin(), config.getString(SQL_INFO + "." + SQLITE + "." + DATABASE), initializeTable);
		}
		else
		{
			main.plugin().getLogger().info("FALSE");
			sqlDB = new MySQLDatabase(main.plugin(),
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
		
		String createInvTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "_inv (" +
				"'" + PLAYER_ID + "' VARCHAR(36) NOT NULL, " +
				"'" + INVENTORY + "' VARCHAR(65535), " +
				"PRIMARY KEY ('" + PLAYER_ID + "')" +
				");";
		sqlDB.executeUpdateAsync(createInvTable, null);
	}

	@EventHandler
	public void preLoginEvent(AsyncPlayerPreLoginEvent e)
	{
		loadMGPlayerSync(e.getUniqueId());
	}
	
	@EventHandler
	public void loginEvent(PlayerLoginEvent e)
	{
		if(!main.plugin().getServer().getOnlineMode())
			loadMGPlayerSync(e.getPlayer().getUniqueId());
	}
	
	@EventHandler
	public void leaveEvent(PlayerQuitEvent e)
	{
		main.plugin().getLogger().info("QUIT EVENT");
		removePlayer(e.getPlayer().getUniqueId());
	}
	
	@EventHandler
	public void kickEvent(PlayerKickEvent e)
	{
		main.plugin().getLogger().info("KICK EVENT");
		removePlayer(e.getPlayer().getUniqueId());
	}
	
	private void removePlayer(UUID id) 
	{
		updateMGPlayer(id);
		playerList.remove(id);
	}
	
	public MGPlayer getMGPlayer(UUID id)
	{
		return playerList.get(id);
	}
	
	private void loadMGPlayerAsync(UUID id) 
	{
		main.plugin().getLogger().info("LOGIN1 EVENT");
		sqlDB.executeQueryAsync("SELECT * FROM " + TABLE_NAME + " WHERE " + PLAYER_ID + " = '" + id.toString() + "';", new MGDBCallback()
		{
			@Override
			public void onQueryDone(ResultSet set)
			{

				main.plugin().getLogger().info("LOGIN2 EVENT");
				try 
				{
					if(set == null || !set.next())
					{
						playerList.put(id, new MGPlayer(id));
						main.plugin().getLogger().info("SET EMPTY ADDED PLAYER");
					}
					else
					{
						main.plugin().getLogger().info("SET FULL LOADED PLAYER");
						MGPlayer player = new MGPlayer(id, set.getInt(KILLS), set.getInt(DEATHS), set.getInt(SCORE), 0);
						player.setWins(set.getInt(WINS));
						player.setLosses(set.getInt(LOSSES));
						player.setCurrency(set.getInt(CREDITS));
						playerList.put(id, player);
					}
					
					sqlDB.executeQueryAsync("SELECT " + INVENTORY + " FROM " + TABLE_NAME + "_inv WHERE " + PLAYER_ID + " = '" + id.toString() + "';", new MGDBCallback()
					{
						@Override
						public void onQueryDone(ResultSet set)
						{
							try 
							{
								if(!(set == null || !set.next()))
									playerList.get(id).setInventory(SerializationUtils.invFromBase64(set.getString(INVENTORY)));
							} 
							catch (SQLException e) { main.plugin().getLogger().warning(e.getMessage()); } 
							catch (IOException e){ main.plugin().getLogger().warning(e.getMessage()); }
						}
					});
				} 
				catch (SQLException e) 
				{
					main.plugin().getLogger().warning(e.getMessage());
					playerList.put(id, new MGPlayer(id));
				}
			}
		});
	}

	private void loadMGPlayerSync(UUID id)
	{
		ResultSet set = sqlDB.executeQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + PLAYER_ID + " = '" + id.toString() + "';");
		
		try 
		{
			if(set == null || !set.next())
				playerList.put(id, new MGPlayer(id));
			else
			{
				MGPlayer player = new MGPlayer(id, set.getInt(KILLS), set.getInt(DEATHS), set.getInt(SCORE), 0);
				player.setWins(set.getInt(WINS));
				player.setLosses(set.getInt(LOSSES));
				player.setCurrency(set.getInt(CREDITS));
				playerList.put(id, player);
			}
			
			sqlDB.executeQueryAsync("SELECT " + INVENTORY + " FROM " + TABLE_NAME + "_inv WHERE " + PLAYER_ID + " = '" + id.toString() + "';", new MGDBCallback()
			{
				@Override
				public void onQueryDone(ResultSet set)
				{
					try 
					{
						if(!(set == null || !set.next()))
							playerList.get(id).setInventory(SerializationUtils.invFromBase64(set.getString(INVENTORY)));
					} 
					catch (SQLException e) { main.plugin().getLogger().warning(e.getMessage()); } 
					catch (IOException e){ main.plugin().getLogger().warning(e.getMessage()); }
				}
			});
		} 
		catch (SQLException e) 
		{
			main.plugin().getLogger().warning(e.getMessage());
			playerList.put(id, new MGPlayer(id));
		}
	}

	private void loadMGPlayerSync(final UUID id, final boolean invSync)
	{
		ResultSet set = sqlDB.executeQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + PLAYER_ID + " = '" + id.toString() + "';");
		
		try 
		{
			if(set == null || !set.next())
				playerList.put(id, new MGPlayer(id));
			else
			{
				MGPlayer player = new MGPlayer(id, set.getInt(KILLS), set.getInt(DEATHS), set.getInt(SCORE), 0);
				player.setWins(set.getInt(WINS));
				player.setLosses(set.getInt(LOSSES));
				player.setCurrency(set.getInt(CREDITS));
				playerList.put(id, player);
			}
			
			MGDBCallback callback = new MGDBCallback()
			{
				@Override
				public void onQueryDone(ResultSet set)
				{
					try 
					{
						if(!(set == null || !set.next()))
							playerList.get(id).setInventory(SerializationUtils.invFromBase64(set.getString(INVENTORY)));
					} 
					catch (SQLException e) { main.plugin().getLogger().warning(e.getMessage()); } 
					catch (IOException e){ main.plugin().getLogger().warning(e.getMessage()); }
				}
			};
			
			if(invSync)
				callback.onQueryDone(sqlDB.executeQuery("SELECT " + INVENTORY + " FROM " + TABLE_NAME + "_inv WHERE " + PLAYER_ID + " = '" + id.toString() + "';"));
			else
				sqlDB.executeQueryAsync("SELECT " + INVENTORY + " FROM " + TABLE_NAME + "_inv WHERE " + PLAYER_ID + " = '" + id.toString() + "';", callback);
		} 
		catch (SQLException e) 
		{
			main.plugin().getLogger().warning(e.getMessage());
			playerList.put(id, new MGPlayer(id));
		}
	}
	
	public void updateMGPlayer(UUID id)
	{
		MGPlayer player = playerList.get(id);
		
		if(player == null)
			return;
		
		String update = "REPLACE INTO " + TABLE_NAME + " (" + PLAYER_ID + ", " + SCORE + ", " + CREDITS + ", " + KILLS + ", " + DEATHS + ", " + WINS + ", " + LOSSES + ") VALUES (" +
						"'" + player.getID().toString() + "', " + player.getTotalScore() + ", " + player.getCurrency() + ", " + player.getKills() + ", " + player.getDeaths() + ", " + player.getWins() + ", " + player.getLosses() + ");";

		sqlDB.executeUpdateAsync(update, null);
		
		Bukkit.getScheduler().runTaskAsynchronously(main.plugin(), new Runnable()
		{
			@Override
			public void run()
			{
				String bytes = SerializationUtils.invToBase64(player.getInventory());
				String update = "REPLACE INTO " + TABLE_NAME + "_inv (" + PLAYER_ID + ", " + INVENTORY + 
								") VALUES (" +"'" + player.getID().toString() + "', '" + bytes + "');";
				
				sqlDB.executeUpdateAsync(update, null);
			}
			
		});
	}

	public void updatePlayersAsync() 
	{
		playerList.forEach((id, player) -> updateMGPlayer(id));
	}
	
	public void updatePlayersSync() 
	{
		playerList.forEach((id, player) -> updateMGPlayerSync(id));
	}
	
	public void updatePlayersSync(boolean invSync) 
	{
		playerList.forEach((id, player) -> updateMGPlayerSync(id, invSync));
	}
	
	private void updateMGPlayerSync(UUID id)
	{
		MGPlayer player = playerList.get(id);
		
		if(player == null)
			return;
		
		String update = "REPLACE INTO " + TABLE_NAME + " (" + PLAYER_ID + ", " + SCORE + ", " + CREDITS + ", " + KILLS + ", " + DEATHS + ", " + WINS + ", " + LOSSES + ") VALUES (" +
						"'" + player.getID().toString() + "', " + player.getTotalScore() + ", " + player.getCurrency() + ", " + player.getKills() + ", " + player.getDeaths() + ", " + player.getWins() + ", " + player.getLosses() + ");";
		
		sqlDB.executeUpdate(update);
		
		Bukkit.getScheduler().runTaskAsynchronously(main.plugin(), new Runnable()
		{
			@Override
			public void run()
			{
				String bytes = SerializationUtils.invToBase64(player.getInventory());
				String update = "REPLACE INTO " + TABLE_NAME + "_inv (" + PLAYER_ID + ", " + INVENTORY + 
									") VALUES (" +"'" + player.getID().toString() + "', " + bytes + ");";
					
				sqlDB.executeUpdateAsync(update, null);
			}
		});
	}
	
	private void updateMGPlayerSync(UUID id, boolean invSync)
	{
		MGPlayer player = playerList.get(id);
		
		if(player == null)
			return;
		
		String update = "REPLACE INTO " + TABLE_NAME + " (" + PLAYER_ID + ", " + SCORE + ", " + CREDITS + ", " + KILLS + ", " + DEATHS + ", " + WINS + ", " + LOSSES + ") VALUES (" +
						"'" + player.getID().toString() + "', " + player.getTotalScore() + ", " + player.getCurrency() + ", " + player.getKills() + ", " + player.getDeaths() + ", " + player.getWins() + ", " + player.getLosses() + ");";
		
		sqlDB.executeUpdate(update);
		
		Runnable task = new Runnable()
		{
			@Override
			public void run()
			{
				String bytes = SerializationUtils.invToBase64(player.getInventory());
				String update = "REPLACE INTO " + TABLE_NAME + "_inv (" + PLAYER_ID + ", " + INVENTORY + 
								") VALUES (" +"'" + player.getID().toString() + "', '" + bytes + "');";
					
				if(invSync)
					sqlDB.executeUpdate(update);
				else
					sqlDB.executeUpdateAsync(update, null);
			}
		};
		
		if(invSync)
			task.run();
		else
			Bukkit.getScheduler().runTaskAsynchronously(main.plugin(), task);
	}

	public void close() 
	{
		sqlDB.close();
	}
}