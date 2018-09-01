package SmokyMiner.Examples.Paintball;

import org.bukkit.plugin.java.JavaPlugin;

import SmokyMiner.Minigame.Main.MGManager;

public class PaintballPlugin extends JavaPlugin
{
	private MGManager manager;
	private EventPasser passer;
	
	@Override
	public void onEnable()
	{
		manager = new MGManager(this, "wt");
		passer = new EventPasser(manager);
		getServer().getPluginManager().registerEvents(passer, this);
	}

	@Override
	public void onDisable()
	{
		manager.close();
	}
}
