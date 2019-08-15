package SmokyMiner.MiniGames.Lobby.Stages;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import SmokyMiner.MiniGames.Items.ItemShop.MGItemShop;
import SmokyMiner.MiniGames.Lobby.MGLobby;
import SmokyMiner.MiniGames.Lobby.Events.MGLobbyJoinEvent;
import SmokyMiner.MiniGames.Lobby.Events.MGLobbyLeaveEvent;
import SmokyMiner.MiniGames.Lobby.Scoreboards.MGPregameScoreboard;
import SmokyMiner.MiniGames.Maps.MGMapMetadata;
import SmokyMiner.MiniGames.Player.MGPlayer;
import SmokyMiner.MiniGames.Player.MGSpawnKit;
import SmokyMiner.Minigame.Main.MGManager;

// TODO: Make work with map == null

public class MGPregameStage extends MGLobbyStage
{
	protected MGPregameScoreboard board;
	protected ArrayList<BukkitTask> tasks;
	protected MGSpawnKit kit;
	protected MGItemShop shop;
	
	public MGPregameStage(MGManager manager)
	{
		super(manager);

		board = null;
		tasks = new ArrayList<BukkitTask>();

		kit = new MGSpawnKit(ChatColor.WHITE);
		
		shop = null;
	}

	public MGPregameStage(MGManager manager, MGLobby lobby)
	{
		super(manager, lobby);

		board = new MGPregameScoreboard(manager, lobby, 120);
		tasks = new ArrayList<BukkitTask>();

		kit = new MGSpawnKit(ChatColor.WHITE);
		kit.setItem(8, lobby.getExitItem().getItemStack());
		kit.addItem(manager.getInventoryBlock().getItemStack());
		
		shop = null;
	}

	public MGPregameStage(MGManager manager, MGLobby lobby, MGMapMetadata map)
	{
		super(manager, lobby, map);

		board = new MGPregameScoreboard(manager, lobby, 120);
		tasks = new ArrayList<BukkitTask>();

		kit = new MGSpawnKit(ChatColor.WHITE);
		kit.setItem(8, lobby.getExitItem().getItemStack());
		kit.addItem(manager.getInventoryBlock().getItemStack());
		
		shop = null;
	}
	
	public MGPregameStage(MGManager manager, MGLobby lobby, MGItemShop shop)
	{
		super(manager, lobby);

		board = new MGPregameScoreboard(manager, lobby, 120);
		tasks = new ArrayList<BukkitTask>();

		
		this.shop = shop;
		kit = new MGSpawnKit(ChatColor.WHITE);
		kit.setItem(8, lobby.getExitItem().getItemStack());
		kit.addItem(manager.getInventoryBlock().getItemStack());
		kit.addItem(shop.getBlock().getItemStack());
	}
	
	public MGPregameStage(MGManager manager, MGLobby lobby, MGMapMetadata map, MGItemShop shop)
	{
		super(manager, lobby, map);

		board = new MGPregameScoreboard(manager, lobby, 120);
		tasks = new ArrayList<BukkitTask>();
		
		this.shop = shop;

		kit = new MGSpawnKit(ChatColor.WHITE);
		kit.setItem(8, lobby.getExitItem().getItemStack());
		kit.addItem(manager.getInventoryBlock().getItemStack());
		kit.addItem(shop.getBlock().getItemStack());
	}

	@Override
	public void close()
	{
		super.close();

		if (board != null)
			board.close();

		cancelTasks();
	}

	@Override
	public void setLobby(MGLobby lobby)
	{
		super.setLobby(lobby);

		if (board != null)
			board.close();

		board = new MGPregameScoreboard(manager, lobby, 120);
		kit = new MGSpawnKit(ChatColor.WHITE);
		kit.setItem(8, lobby.getExitItem().getItemStack());
		kit.addItem(manager.getInventoryBlock().getItemStack());
	}

	@Override
	public void startStage()
	{
		if (lobby == null)
			throw new IllegalStateException("lobby instance null");

		super.startStage();

		board.resetTimer();
		board.resetScoreboard();

		if (!board.updatePlayerCount())
			board.startTimer();

		board.open();

		Bukkit.getServer().getLogger().info("LOBBY SIZE:" + lobby.getPlayers().size());
		Bukkit.getServer().getLogger().info("WORLD: " + map.getMapCenter().getWorld().toString());
		Bukkit.getServer().getLogger().info("X: " + map.getMapCenter().getBlockX());
		Bukkit.getServer().getLogger().info("Y: " + map.getMapCenter().getBlockY());
		Bukkit.getServer().getLogger().info("Z: " + map.getMapCenter().getBlockZ());

		for (MGPlayer p : lobby.getPlayers())
		{
			p.setPregame();
			p.spawn(map.getMapCenter(), kit);
		}

		scheduleTasks();
	}

	@Override
	public void endStage()
	{
		super.endStage();
		cancelTasks();
		board.resetTimer();
	}

	private void scheduleTasks()
	{
		tasks.add(Bukkit.getScheduler().runTaskTimer(manager.plugin(), new Runnable()
		{
			@Override
			public void run()
			{
				lobby.broadcastLines(3);
				lobby.broadcastMessage(ChatColor.WHITE + "           Welcome To Paintball!         ");
				lobby.broadcastMessage(ChatColor.GOLD + " ==============================");
				lobby.broadcastMessage(
						ChatColor.WHITE + "      Please Note:  " + ChatColor.YELLOW + "This plugin is   ");
				lobby.broadcastMessage(ChatColor.YELLOW + "    currently under developement.  ");
				lobby.broadcastMessage(ChatColor.YELLOW + "   We are in the early alpha stage.");
				lobby.broadcastMessage(ChatColor.WHITE + "           There Will Be Bugs!       ");
				lobby.broadcastLines(2);
			}
		}, 0L, 20L * 60L));

		tasks.add(Bukkit.getScheduler().runTaskTimer(manager.plugin(), new Runnable()
		{
			int taskId = tasks.size();

			@Override
			public void run()
			{
				if (board.updatePlayerCount())
				{
					lobby.broadcastMessage("");
					lobby.broadcastMessage(ChatColor.GOLD + "Waiting for more players: " + ChatColor.RED
							+ lobby.getPlayers().size() + "/" + lobby.getMinPlayers());
					lobby.broadcastMessage("");
				} else
				{
					BukkitTask temp = tasks.get(taskId);
					tasks.remove(taskId);
					temp.cancel();
				}
			}
		}, 20L * 5L, 20L * 20L));
	}

	private void cancelTasks()
	{
		for (BukkitTask task : tasks)
		{
			task.cancel();
		}

		tasks.clear();
	}

	@Override
	public void playerJoinedEvent(MGLobbyJoinEvent e)
	{
		Player p = Bukkit.getPlayer(e.getMGPlayer().getID());
		board.addPlayer(p);
		e.getMGPlayer().setPregame();

		if (map != null && !map.contains(p.getLocation(), false))
			e.getMGPlayer().spawn(map.getMapCenter(), kit);
		else
		{
			p.getInventory().clear();
			kit.giveKit(p);
		}
	}

	@Override
	public void playerLeaveEvent(MGLobbyLeaveEvent e)
	{
		Player p = Bukkit.getPlayer(e.getMGPlayer().getID());

		try
		{
			if (p != null)
				p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		} catch (IllegalStateException er)
		{
			manager.plugin().getLogger().warning(er.toString());
		}

		board.removePlayer(p);
	}

	public MGPregameScoreboard getPregameBoard()
	{
		return board;
	}

	public MGSpawnKit getKit()
	{
		return kit;
	}

	public void setKit(MGSpawnKit kit)
	{
		this.kit = kit;
	}

}
