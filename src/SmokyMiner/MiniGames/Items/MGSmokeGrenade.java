package SmokyMiner.MiniGames.Items;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import SmokyMiner.MiniGames.Lobby.MGLobby;
import SmokyMiner.MiniGames.Player.MGPlayer;
import SmokyMiner.Minigame.Main.MGManager;
import SmokyMiner.MiniGames.Effects.MGEffectUtils;

public class MGSmokeGrenade extends MGGrenade
{
	public static final String SMOKE_GRENADE = "Smoke Grenade";
	private int smokeTicks;
	private double radius;
	
	public MGSmokeGrenade(MGManager manager, int smokeTicks, double smokeRadius)
	{
		super(manager, SMOKE_GRENADE, Material.PAPER, 1, 40, 10);
		this.smokeTicks = smokeTicks;
		radius = smokeRadius;
		
	}
	
	public MGSmokeGrenade(MGManager manager, int smokeTicks, double smokeRadius, int cost)
	{
		super(manager, SMOKE_GRENADE, Material.PAPER, 1, 40, cost);
		this.smokeTicks = smokeTicks;
		radius = smokeRadius;
	}
	
	@Override
	protected ArrayList<String> buildLore()
	{
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Produces a large cloud");
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "of smoke when thrown.");
		lore.add("");
		lore.add(ChatColor.GOLD + "Cost: " + cost);
		return lore;
	}
	
	@Override
	protected void explode(Item projectile, Player thrower)
	{
		MGLobby lobby = manager.getMatchMaker().getLobbyByPlayer(thrower.getUniqueId());
		
		if(lobby != null)
			lobby.playSound(projectile.getLocation(), Sound.ENTITY_ITEM_BREAK, 7.0f, 1.0f);
		else
			thrower.getWorld().playSound(projectile.getLocation(), Sound.ENTITY_ITEM_BREAK, 7.0f, 1.0f);

		MGEffectUtils.drawSmokeSphere(manager.plugin(), projectile.getLocation(), smokeTicks, 7, radius, projectile.getVelocity());
		projectile.remove();
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
		p.sendMessage(ChatColor.GREEN + "Successfully Purchased " + ChatColor.RESET + SMOKE_GRENADE + ChatColor.GOLD + "!");
		return Sound.BLOCK_LAVA_POP;
		
	}
}
