package com.gmail.andrew.miner3.plugins.Paintball.Assist;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.gmail.andrew.miner3.plugins.Paintball.PBPlayer;
import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.Lobby;

public class SnowBowMethods 
{
	public static final long firingDelay0 = 150L;
	public static final long firingDelay1 = 900L;
	public static final long reloadTime = 1500L;
	public static final int clipLength = 16;
	public static final long second = 1000L;
	
	public static enum BowState
	{
		NOBOW,
		HOLDING,
		FIRING,
		WAITING,
		SWITCHING,
		RELOADING,
	}
	
	public static boolean snowBowEvent(PlayerInteractEvent e, Lobby lobby)
	{
		Player p = e.getPlayer();
		PBPlayer pbp = lobby.findPlayer(p.getUniqueId());
		
		if(pbp == null)
			return false;
		
		BowState state = isUsingSnowBow(p, e.getAction(), pbp);
		
		switch(state)
		{
		case FIRING:
			return fireBow(p, pbp, lobby);
		case SWITCHING:
			switchMode(p, pbp, lobby);
			return true;
		case RELOADING:
			reload(p, pbp, lobby);
			return true;
		default:
			return false;
		}
	}
	
	public static BowState isUsingSnowBow(Player p, Action action, PBPlayer pbp)
	{
		PlayerInventory inv = p.getInventory();
		
		if(isHoldingSnowBow(inv))
		{
			if(action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK)
				return BowState.SWITCHING;
			if(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
			{
				if(pbp.isReloading())
					return BowState.RELOADING;
				
				if(System.currentTimeMillis() - pbp.getLastShotTime() >= pbp.getFireRate())
					return BowState.FIRING;
				
				return BowState.WAITING;
			}
			
			return BowState.HOLDING;
		}
		return BowState.NOBOW;
	}
	
	public static boolean removeAmmo(PlayerInventory inv, int amount)
	{
		if(inv.contains(Material.SNOW_BALL, amount))
		{
			inv.removeItem(new ItemStack(Material.SNOW_BALL, amount));
			return true;
		}
		else
			return false;
	}
	
	public static boolean isHoldingSnowBow(PlayerInventory inv)
	{
		if(inv.getItemInMainHand().getType() == Material.BOW)
			return true;
		return false;
		
	}
	
	public static boolean fireBow(Player p, PBPlayer pbp, Lobby lobby) 
	{
		final int perShot = 1;
		
		PlayerInventory inv = p.getInventory();
		int shotsFired = pbp.getShotsFired();
		
		if(inv.contains(Material.SNOW_BALL))
		{
			if(shotsFired < clipLength && removeAmmo(inv, perShot))
			{
				pbp.setShotsFired(shotsFired + perShot);
				
				for(int i = 0; i < perShot; i++)
				{
					pbp.storeLastShotTime();
					Snowball snowball = p.launchProjectile(Snowball.class);
					snowball.setGlowing(true);
					//lobby.getMatchState().getScoreboard().getTeamObject(lobby.getTeams().getTeam(pbp).getId()).addEntry(""+snowball.getEntityId());
					lobby.playSound(p.getLocation(), Sound.ENTITY_SNOWMAN_SHOOT, 2f, 1f);
				}
				
				return true;
			}
			else if(shotsFired == clipLength)
			{
				p.sendTitle("", ChatColor.RED + "RELOADING", 8, 4, 8);				
				lobby.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, .2f, 1f);
				
				pbp.storeLastShotTime();
				pbp.setReloading(true);
			}
		}
		else
		{
			lobby.playSound(p.getLocation(), Sound.BLOCK_WOOD_PRESSUREPLATE_CLICK_ON, .2f, 1f);
			
			if(System.currentTimeMillis() - pbp.getLastShotTime() >= second)
			{
				pbp.storeLastShotTime();
				p.sendTitle("", ChatColor.DARK_RED + "OUT OF AMMO", 8, 4, 8);
			}
		}
		
		
		return false;
	}

	public static void switchMode(Player p, PBPlayer pbp, Lobby lobby) 
	{
		if(pbp.getFireRate() == firingDelay0)
		{
			pbp.setFireRate(firingDelay1);
			p.sendTitle("", ChatColor.YELLOW + "Semi-Auto", 4, 8, 4);
		}
		else
		{
			pbp.setFireRate(firingDelay0);
			p.sendTitle("", ChatColor.YELLOW + "Automatic", 4, 8, 4);
		}
		
		p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, .5f, 1f);
	}

	public static void reload(Player p, PBPlayer pbp, Lobby lobby) 
	{
		long time = System.currentTimeMillis();
		
		if(time - pbp.getLastReload() >= reloadTime)
		{
			pbp.setReloading(false);
			pbp.setShotsFired(0);
		}
		else 
		{
			lobby.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, .2f, 1f);
			
			if(time - pbp.getLastShotTime() >= second)
			{
				p.sendTitle("", ChatColor.RED + "RELOADING", 8, 4, 8);
				pbp.storeLastShotTime();
				
			}
		}
	}
	
	public static void sendBlockPaint(Lobby lobby, Location loc, ChatColor color)
	{
		sendBlockPaint(lobby, loc, "" + color);
	}
	
	@SuppressWarnings("deprecation")
	public static void sendBlockPaint(Lobby lobby, Location loc, String chatColor)
	{
		if(!isPaintable(loc.getBlock()))
			return;
		
		String color = chatColor.replace("" + ChatColor.COLOR_CHAR, "");
		ArrayList<PBPlayer> players = lobby.getPlayers();
		
		for(PBPlayer player : players)
		{
			Bukkit.getPlayer(player.getID()).sendBlockChange(loc, Material.CONCRETE, chatColorToConcreteDV(color.charAt(0)));
		}
	}
	
	private static boolean isPaintable(Block block) 
	{
		// TODO Move Finals To Config
		Material type = block.getType();
		switch(type)
		{
		case WOOL:
		case SNOW_BLOCK:
		case OBSIDIAN:
		case STONE_SLAB2:
		case GLOWSTONE:
		case IRON_BLOCK:
		case GLASS:
		case GRASS:
		case DIRT:
			return true;
		default:
			return false;
		}
	}

	public static byte chatColorToConcreteDV(char color)
	{
		switch(color)
		{
		case '0':
			return 15;
		case '1':
			return 11;
		case '2':
			return 13;
		case '3':
			return 9;
		case '4':
			return 14;
		case '5':
			return 2;
		case '6':
			return 1;
		case '7':
			return 8;
		case '8':
			return 7;
		case '9':
			return 3;
		case 'a':
			return 5;
		case 'b':
			return 9;
		case 'c':
			return 14;
		case 'd':
			return 10;
		case 'e':
			return 4;
		case 'f':
			return 0;
		default:
			return 15;
		}
	}
}
