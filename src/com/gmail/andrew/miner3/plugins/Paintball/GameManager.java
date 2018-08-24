package com.gmail.andrew.miner3.plugins.Paintball;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.LobbyManager;
import com.gmail.andrew.miner3.plugins.Paintball.MapClasses.Bound;
import com.gmail.andrew.miner3.plugins.Paintball.MapClasses.MapManager;


public class GameManager implements Listener
{
	private final PluginManager plugin;
	private LobbyManager matchmaking;
	private MapManager maps;
	
	public GameManager()
	{
		matchmaking = new LobbyManager(null);
		plugin = null;
	}
	
	public GameManager(PluginManager plugin)
	{
		this.plugin = plugin;
		matchmaking = new LobbyManager(plugin);
	}
	
	public LobbyManager getLobbyManager()
	{
		return matchmaking;
	}
	
	@EventHandler
	public void playerMoveEvent(PlayerMoveEvent e)
	{
		matchmaking.playerMoveEvent(e);
	}
	
	@EventHandler
	public void playerJoinEvent(PlayerJoinEvent e)
	{
		try
		{
			matchmaking.PlayerJoined(e.getPlayer().getUniqueId());
		}
		catch(IllegalStateException ex)
		{
			e.getPlayer().sendMessage(ChatColor.GOLD + plugin.logPrefix + ChatColor.YELLOW + " " + ex.getMessage().substring(0, ex.getMessage().indexOf(':') + 1) + ChatColor.RED + ex.getMessage().substring(ex.getMessage().indexOf(':') + 1, ex.getMessage().length()));
		}
	}
	
	@EventHandler
	public void playerLeaveEvent(PlayerQuitEvent e)
	{
		matchmaking.PlayerLeft(e.getPlayer().getUniqueId());
	}
	
	@EventHandler
	public void playerKickEvent(PlayerKickEvent e)
	{
		matchmaking.PlayerLeft(e.getPlayer().getUniqueId());
	}
	
	@EventHandler
	public void playerChatEvent(AsyncPlayerChatEvent e)
	{
		matchmaking.playerChatEvent(e);
	}
	
	@EventHandler
	public void entityDamageEntityEvent(EntityDamageByEntityEvent e)
	{
		matchmaking.entityDamageEntityEvent(e);
	}
	
	@EventHandler
	public void playerInteractEvent(PlayerInteractEvent e)
	{
		matchmaking.playerInteractEvent(e);
		

		Player p = e.getPlayer();
		
		if(p.getInventory().getItemInMainHand().getType().equals(Material.BONE))
		{
			DecimalFormat df = new DecimalFormat("#.#");
			Action action = e.getAction();
			
			PBPlayer player = plugin.getPlayerManager().getPBPlayer(p.getUniqueId());
			
			if(player == null)
				return;
			
			Location loc = null;
			Bound bound = player.getSelection();
			
			switch(action)
			{
			case LEFT_CLICK_BLOCK:
				loc = e.getClickedBlock().getLocation();
				
				if(bound.loc1 != null && bound.loc1.equals(loc))
				{
					e.setCancelled(true);
					return;
				}
				
				bound.loc1 = loc;
				p.sendMessage(ChatColor.GOLD + PluginManager.logPrefix + ChatColor.GRAY + ChatColor.ITALIC + " Selection 1 Set:  {" + df.format(bound.loc1.getX()) + " " + df.format(bound.loc1.getY()) + " " + df.format(bound.loc1.getZ()) + "}");
				break;
			case RIGHT_CLICK_BLOCK:
				loc = e.getClickedBlock().getLocation();
				
				if(bound.loc2 != null && bound.loc2.equals(loc))
				{
					e.setCancelled(true);
					return;
				}
				
				bound.loc2 = loc;
				p.sendMessage(ChatColor.GOLD + PluginManager.logPrefix + ChatColor.GRAY + ChatColor.ITALIC + " Selection 2 Set:  {" + df.format(bound.loc2.getX()) + " " + df.format(bound.loc2.getY()) + " " + df.format(bound.loc2.getZ()) + "}");
				break;
			default:
				return;
			}
			
			player.setSelection(bound);
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void foodLevelChangeEvent(FoodLevelChangeEvent e)
	{
		matchmaking.foodLevelChangeEvent(e);
	}
	
	@EventHandler
	public void entityDamageEvent(EntityDamageEvent e)
	{
		matchmaking.entityDamageEvent(e);
	}
	
	@EventHandler
	public void projectileHitEvent(ProjectileHitEvent e)
	{
		matchmaking.projectileHitEvent(e);
	}
}
