package SmokyMiner.MiniGames.Items;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import SmokyMiner.MiniGames.Effects.MGEffectUtils;
import SmokyMiner.MiniGames.Lobby.MGLobby;
import SmokyMiner.MiniGames.Lobby.Stages.MGGameStage;
import SmokyMiner.MiniGames.Lobby.Stages.MGLobbyStage;
import SmokyMiner.MiniGames.Player.MGPlayer;
import SmokyMiner.Minigame.Main.MGManager;

public class MGExplosiveGrenade extends MGGrenade
{

	public static final String EXPLOSIVE_GRENADE = "Explosive Grenade";
	private double radius;
	private double dizzyRadius;
	
	public MGExplosiveGrenade(MGManager manager, double explosionRadius, double concussionRadius)
	{
		super(manager, EXPLOSIVE_GRENADE, Material.EGG, 1, 40, 10);
		radius = explosionRadius;
		dizzyRadius = concussionRadius;
		
	}
	
	public MGExplosiveGrenade(MGManager manager,double explosionRadius, double concussionRadius, int cost)
	{
		super(manager, EXPLOSIVE_GRENADE, Material.EGG, 1, 40, cost);
		radius = explosionRadius;
		dizzyRadius = concussionRadius;
	}
	
	@Override
	protected ArrayList<String> buildLore()
	{
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "A small explosive");
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "grenade. Effective");
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "in close quarters.");
		lore.add("");
		lore.add(ChatColor.GOLD + "Cost: " + cost);
		return lore;
	}
	
	@Override
	protected void explode(Item projectile, Player thrower)
	{
		MGLobby lobby = manager.getMatchMaker().getLobbyByPlayer(thrower.getUniqueId());
		MGEffectUtils.drawExplosion(manager.plugin(), projectile.getLocation(), .5);
		projectile.remove();

		if(lobby != null)
		{
			thrower.getWorld().playSound(projectile.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, .8f, .50f);
			
			MGPlayer mgPlayer = lobby.findPlayer(thrower.getUniqueId());
			Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable()
			{
				@Override
				public void run() 
				{
					ArrayList<MGPlayer> dizzy = MGEffectUtils.getNearbyPlayers(lobby, projectile.getLocation(), dizzyRadius, radius);
					
					for(MGPlayer player : dizzy)
						Bukkit.getPlayer(player.getID()).addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 60, 10));
					
					ArrayList<MGPlayer> dead = MGEffectUtils.getNearbyPlayers(lobby, projectile.getLocation(), radius, -1.0);
					
					for(MGPlayer player : dead)
					{
						MGLobbyStage stage = lobby.getCurrentStage();
						
						if(stage instanceof MGGameStage)
						{
							MGGameStage gmStage = (MGGameStage) stage;
							gmStage.playerKilled(player, mgPlayer);
						}
						else
							break;
					}
				}
			});
		}
		else
			thrower.getWorld().playSound(projectile.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, .8f, .50f);
			
	}
	
	protected Sound purchaseFailed(MGPlayer player)
	{
		Player p = Bukkit.getPlayer(player.getID());
		p.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "You Don't Have Enough Points!");
		return Sound.ITEM_SHIELD_BREAK;
	}
	
	protected Sound purchaseSuccessful(MGPlayer player)
	{
		Player p = Bukkit.getPlayer(player.getID());
		player.getInventory().addItem(this.getItemStack());
		p.sendMessage(ChatColor.GREEN + "Successfully Purchased " + ChatColor.RESET + EXPLOSIVE_GRENADE + ChatColor.GOLD + "!");
		return Sound.BLOCK_LAVA_POP;
		
	}
}
