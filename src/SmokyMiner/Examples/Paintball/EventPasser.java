package SmokyMiner.Examples.Paintball;

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

import SmokyMiner.MiniGames.Lobby.MGLobbyManager;
import SmokyMiner.MiniGames.Maps.MGBound;
import SmokyMiner.MiniGames.Maps.MGMapManager;
import SmokyMiner.MiniGames.Player.MGPlayer;
import SmokyMiner.Minigame.Main.MGManager;

public class EventPasser implements Listener
{
	private final MGManager plugin;
	private MGMapManager maps;
	
	public EventPasser()
	{
		plugin = null;
	}
	
	public EventPasser(MGManager plugin)
	{
		this.plugin = plugin;
	}
	
	public MGLobbyManager getLobbyManager()
	{
		return plugin.getLobbyManager();
	}
	
	@EventHandler
	public void playerMoveEvent(PlayerMoveEvent e)
	{
		plugin.getLobbyManager().playerMoveEvent(e);
	}
	
	@EventHandler
	public void playerJoinEvent(PlayerJoinEvent e)
	{
		try
		{
			plugin.getLobbyManager().PlayerJoined(e.getPlayer().getUniqueId());
		}
		catch(IllegalStateException ex)
		{
			e.getPlayer().sendMessage(ChatColor.GOLD + plugin.logPrefix + ChatColor.YELLOW + " " + ex.getMessage().substring(0, ex.getMessage().indexOf(':') + 1) + ChatColor.RED + ex.getMessage().substring(ex.getMessage().indexOf(':') + 1, ex.getMessage().length()));
		}
	}
	
	@EventHandler
	public void playerLeaveEvent(PlayerQuitEvent e)
	{
		plugin.getLobbyManager().PlayerLeft(e.getPlayer().getUniqueId());
	}
	
	@EventHandler
	public void playerKickEvent(PlayerKickEvent e)
	{
		plugin.getLobbyManager().PlayerLeft(e.getPlayer().getUniqueId());
	}
	
	@EventHandler
	public void playerChatEvent(AsyncPlayerChatEvent e)
	{
		plugin.getLobbyManager().playerChatEvent(e);
	}
	
	@EventHandler
	public void entityDamageEntityEvent(EntityDamageByEntityEvent e)
	{
		plugin.getLobbyManager().entityDamageEntityEvent(e);
	}
	
	@EventHandler
	public void playerInteractEvent(PlayerInteractEvent e)
	{
		plugin.getLobbyManager().playerInteractEvent(e);
		

		Player p = e.getPlayer();
		
		if(p.getInventory().getItemInMainHand().getType().equals(Material.BONE))
		{
			DecimalFormat df = new DecimalFormat("#.#");
			Action action = e.getAction();
			
			MGPlayer player = plugin.getPlayerManager().getMGPlayer(p.getUniqueId());
			
			if(player == null)
				return;
			
			Location loc = null;
			MGBound bound = player.getSelection();
			
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
				p.sendMessage(ChatColor.GOLD + MGManager.logPrefix + ChatColor.GRAY + ChatColor.ITALIC + " Selection 1 Set:  {" + df.format(bound.loc1.getX()) + " " + df.format(bound.loc1.getY()) + " " + df.format(bound.loc1.getZ()) + "}");
				break;
			case RIGHT_CLICK_BLOCK:
				loc = e.getClickedBlock().getLocation();
				
				if(bound.loc2 != null && bound.loc2.equals(loc))
				{
					e.setCancelled(true);
					return;
				}
				
				bound.loc2 = loc;
				p.sendMessage(ChatColor.GOLD + MGManager.logPrefix + ChatColor.GRAY + ChatColor.ITALIC + " Selection 2 Set:  {" + df.format(bound.loc2.getX()) + " " + df.format(bound.loc2.getY()) + " " + df.format(bound.loc2.getZ()) + "}");
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
		plugin.getLobbyManager().foodLevelChangeEvent(e);
	}
	
	@EventHandler
	public void entityDamageEvent(EntityDamageEvent e)
	{
		plugin.getLobbyManager().entityDamageEvent(e);
	}
	
	@EventHandler
	public void projectileHitEvent(ProjectileHitEvent e)
	{
		plugin.getLobbyManager().projectileHitEvent(e);
	}
}