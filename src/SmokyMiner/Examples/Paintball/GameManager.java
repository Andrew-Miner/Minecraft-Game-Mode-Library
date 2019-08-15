package SmokyMiner.Examples.Paintball;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.projectiles.ProjectileSource;

import SmokyMiner.MiniGames.Lobby.MGLobby;
import SmokyMiner.MiniGames.Lobby.MGMatchMaker;
import SmokyMiner.MiniGames.Lobby.MGPregameLobby;
import SmokyMiner.MiniGames.Lobby.Events.MGLobbyJoinEvent;
import SmokyMiner.MiniGames.Lobby.Events.MGLobbyLeaveEvent;
import SmokyMiner.MiniGames.Lobby.Events.MGLobbyListener;
import SmokyMiner.MiniGames.Lobby.Events.MGLobbyStageEndEvent;
import SmokyMiner.MiniGames.Lobby.Events.MGLobbyStageStartEvent;
import SmokyMiner.MiniGames.Lobby.Stages.MGGameStage;
import SmokyMiner.MiniGames.Maps.MGMapMetadata;
import SmokyMiner.MiniGames.Player.MGPlayer;
import SmokyMiner.Minigame.Main.MGManager;
import To.Be.Purged.SnowBowMethods;

public class GameManager implements MGLobbyListener, Listener
{
	public final MGManager manager;
	public final MGMatchMaker maker;
	protected HashMap<UUID, HashSet<Location>> blocksChanged;

	private MGPregameLobby lobby;

	public GameManager(MGManager manager, MGMapMetadata pregameMap)
	{
		this.manager = manager;
		this.maker = manager.getMatchMaker();
		this.lobby = new MGPregameLobby(this.manager, pregameMap);
		blocksChanged = new HashMap<UUID, HashSet<Location>>();

		maker.registerListener(this);
		maker.getBrowser().getBlock().setCanDrop(false);
	}

	public MGPlayer joinPregameLobby(UUID player)
	{
		MGPlayer info = manager.getPlayerManager().getMGPlayer(player);

		if (info == null)
			throw new IllegalStateException("loading player data");

		lobby.addPlayer(info);

		Player p = Bukkit.getPlayer(player);
		maker.getBrowser().giveBrowser(p);
		manager.getItemShop().giveBlock(p);
		manager.getInventoryBlock().giveBlock(p);

		return info;
	}

	@Override
	public void stageStartEvent(MGLobbyStageStartEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void stageEndEvent(MGLobbyStageEndEvent e)
	{
		if (e.getStage() instanceof MGGameStage)
		{
			if (blocksChanged.containsKey(e.getLobby().getLobbyId()))
			{
				Iterator<Location> locIt = blocksChanged.get(e.getLobby().getLobbyId()).iterator();
				while (locIt.hasNext())
				{
					Location loc = locIt.next();
					loc.getBlock().getState().update(true);
				}

				blocksChanged.get(e.getLobby().getLobbyId()).clear();
			}
		}
	}

	@Override
	public void playerJoinedEvent(MGLobbyJoinEvent e)
	{
		lobby.removePlayer(e.getMGPlayer());

		Player p = Bukkit.getPlayer(e.getMGPlayer().getID());
		maker.getBrowser().removeBrowser(p);
	}

	@Override
	public void playerLeaveEvent(MGLobbyLeaveEvent e)
	{
		try
		{
			joinPregameLobby(e.getMGPlayer().getID());
		} catch (IllegalStateException er)
		{
		}
	}

	@EventHandler
	public void playerJoinEvent(PlayerJoinEvent e)
	{
		try
		{
			joinPregameLobby(e.getPlayer().getUniqueId());
		} catch (IllegalStateException ex)
		{
			e.getPlayer().sendMessage(ChatColor.GOLD + MGManager.logPrefix + ChatColor.YELLOW + " Error: "
					+ ChatColor.RED + ex.getMessage());
		}
	}

	@EventHandler
	public void projectileHitEvent(ProjectileHitEvent e)
	{
		Entity ent = e.getEntity();

		if (ent instanceof Snowball)
		{
			ProjectileSource source = ((Snowball) ent).getShooter();
			if (source instanceof Player)
			{
				Player p = (Player) source;
				Block b = e.getHitBlock();

				if (b != null)
				{
					MGLobby lobby = manager.getMatchMaker().getLobbyByPlayer(p.getUniqueId());
					if (!(lobby == null || lobby.getCurrentStage() instanceof MGGameStage))
					{
						String color = ((MGGameStage) lobby.getCurrentStage()).getTeamManager().getTeam(p.getUniqueId())
								.getTeamColor();
						SnowBowMethods.sendBlockPaint(lobby, b.getLocation(), color);

						HashSet<Location> blocks;
						if (!blocksChanged.containsKey(lobby.getLobbyId()))
							blocksChanged.put(lobby.getLobbyId(), blocks = new HashSet<Location>());
						else
							blocks = blocksChanged.get(lobby.getLobbyId());
						blocks.add(b.getLocation());
					}
				}
			}
		}
	}

	// Bow Shooting Handler
	@EventHandler
	public void playerInteractEvent(PlayerInteractEvent e)
	{
		Player p = e.getPlayer();
		MGLobby lobby = manager.getMatchMaker().getLobbyByPlayer(p.getUniqueId());

		if (lobby == null || !(lobby.getCurrentStage() instanceof MGGameStage))
			return;

		if (((MGGameStage) lobby.getCurrentStage()).gameActive())
			SnowBowMethods.snowBowEvent(e, lobby);
		e.setCancelled(true);
	}

	// Player Killed Handler
	@EventHandler
	public void entityDamagEntityEvent(EntityDamageByEntityEvent e)
	{
		Entity ent = e.getEntity();
		if (!(ent instanceof Player))
			return;

		MGPlayer player = manager.getPlayerManager().getMGPlayer(ent.getUniqueId());
		if (player == null)
			return;

		MGLobby lobby = manager.getMatchMaker().getLobbyByPlayer(player.getID());
		if (lobby == null)
			return;

		Entity dmgr = e.getDamager();
		MGPlayer enemy = null;

		if (dmgr instanceof Snowball)
		{
			Snowball sb = (Snowball) dmgr;
			ProjectileSource shooter = sb.getShooter();

			if (shooter instanceof Player)
				enemy = lobby.findPlayer(((Player) shooter).getUniqueId());
		} 
		else if (dmgr instanceof Player)
		{
			Player pDmgr = (Player) dmgr;

			if (pDmgr.getInventory().getItemInMainHand().getType().equals(Material.WOODEN_SWORD))
				enemy = lobby.findPlayer(pDmgr.getUniqueId());
		}

		if (enemy != null)
		{
			if (lobby.getCurrentStage() instanceof MGGameStage)
				((MGGameStage) lobby.getCurrentStage()).playerKilled(player, 0, enemy, 10, 10);
		}

		e.setCancelled(true);
	}

	@EventHandler
	public void foodLevelChangeEvent(FoodLevelChangeEvent e)
	{
		if (manager.getMatchMaker().getPlayer(e.getEntity().getUniqueId()) != null)
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) 
	{
        if(event.toWeatherState())
            event.setCancelled(true);
    }

}
