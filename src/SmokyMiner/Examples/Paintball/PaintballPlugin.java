package SmokyMiner.Examples.Paintball;

import org.bukkit.plugin.java.JavaPlugin;

import SmokyMiner.MiniGames.Items.MGExplosiveGrenade;
import SmokyMiner.MiniGames.Items.MGSmokeGrenade;
import SmokyMiner.MiniGames.Maps.MGMapMethods;
import SmokyMiner.Minigame.Main.MGManager;

public class PaintballPlugin extends JavaPlugin
{
	private MGManager manager;
	private GameManager gm;
	
	@Override
	public void onEnable()
	{
		manager = new MGManager(this, "wt");
		gm = new GameManager(manager, MGMapMethods.loadSpawnMap(manager));
		getServer().getPluginManager().registerEvents(gm, this);
		
		manager.getItemShop().addItem(new MGSmokeGrenade(manager, 15*20, 3));
		manager.getItemShop().addItem(new MGExplosiveGrenade(manager, 3, 5));
	}

	@Override
	public void onDisable()
	{
		manager.close();
	}
}
