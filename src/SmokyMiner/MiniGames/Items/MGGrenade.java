package SmokyMiner.MiniGames.Items;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import SmokyMiner.MiniGames.InventoryMenu.MGItemClickEvent;
import SmokyMiner.MiniGames.Player.MGPlayer;
import SmokyMiner.Minigame.Main.MGManager;

public class MGGrenade extends MGItem
{
	public static final String GRENADE = "Grenade";
	
	protected int tickDelay;
	protected double throwForce;
	
	public MGGrenade(MGManager manager, Material material, double throwForce, int tickDelay)
	{
		super(manager, GRENADE, material);
		
		this.tickDelay = tickDelay;
		this.throwForce = throwForce;
	}
	
	protected MGGrenade(MGManager manager, String name, Material material, double throwForce, int tickDelay)
	{
		super(manager, name, material);
		
		this.tickDelay = tickDelay;
		this.throwForce = throwForce;
	}
	
	public MGGrenade(MGManager manager, Material material, double throwForce, int tickDelay, int cost)
	{
		super(manager, GRENADE, material, cost);
		
		this.tickDelay = tickDelay;
		this.throwForce = throwForce;
	}
	
	protected MGGrenade(MGManager manager, String name, Material material, double throwForce, int tickDelay, int cost)
	{
		super(manager, name, material, cost);
		
		this.tickDelay = tickDelay;
		this.throwForce = throwForce;
	}
	
	public static void launchProjectile(Item projectile, Vector direction, double speed)
	{
		projectile.setVelocity(direction.multiply(speed));
	}
	
	@Override
	public void onItemClick(MGItemClickEvent e)
	{
		UUID id = e.getWhoClicked().getUniqueId();
		MGPlayer p = manager.getPlayerManager().getMGPlayer(id);
		
		if(p.getCurrency() >= cost)
		{
			p.addCurrency(cost*-1);
			e.setClickSound(purchaseSuccessful(p));
		}
		else
			e.setClickSound(purchaseFailed(p));
	}

	@Override
	public void onItemInteraction(PlayerInteractEvent e)
	{
		Player p = e.getPlayer();
		if(removeBlock(p, 1))
		{
			Item projectile = p.getWorld().dropItem(p.getEyeLocation(), new ItemStack(this.getMaterial()));
			MGGrenade.launchProjectile(projectile, p.getEyeLocation().getDirection(), throwForce);
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(manager.plugin(), new Runnable() 
			{
				Player thrower = p;
				Item grenade = projectile;
				
	            @Override
	            public void run() 
	            {
	            	explode(grenade, thrower);
	            }
	            
	        }, tickDelay);
		}
	}
	
	protected void explode(Item projectile, Player thrower)
	{
		
	}
	
	protected Sound purchaseFailed(MGPlayer player)
	{
		return Sound.UI_BUTTON_CLICK;
	}
	
	protected Sound purchaseSuccessful(MGPlayer player)
	{
		player.getInventory().addItem(this.getItemStack());
		return Sound.UI_BUTTON_CLICK;
	}
}
