package SmokyMiner.MiniGames.Effects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import SmokyMiner.MiniGames.Lobby.MGLobby;
import SmokyMiner.MiniGames.Lobby.Events.MGLobbyListener;
import SmokyMiner.MiniGames.Player.MGPlayer;

public class MGEffectUtils
{
	public static HashSet<BukkitTask> effectTasks = new HashSet<BukkitTask>();
	static final double DROP_RATE = -3;

	public static void scheduleTaskCancel(JavaPlugin plugin, BukkitTask thisTask, int ticks)
	{
		if (thisTask == null)
			return;

		effectTasks.add(thisTask);
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
		{
			@Override
			public void run()
			{
				if (!thisTask.isCancelled())
					thisTask.cancel();
				effectTasks.remove(thisTask);
			}

		}, ticks);
	}

	public static HashSet<Location> buildSphere(final Location center, final double radius, final boolean fill, final double posNoise, final double negNoise)
	{
		double totalNoise = posNoise + negNoise;
		HashSet<Location> locations = new HashSet<Location>();
		int layers = (fill) ? (int) ((radius - 1 > 0 ? radius - 1 : 0) / .8) : 0; // Magic Formula created by testing visibility in
																		// game, honed for radius 3

		for (int i = 0; i <= layers; i++)
		{
			double offset = Math.random() * 45;
			for (double theta = -180; theta < 180; theta += 10 + (i * .8) * 10)
			{
				for (double phi = 0 + offset; phi < 180 + offset; phi += 12 + (i * .8) * 10)
				{
					double rPhi = Math.toRadians(phi);
					double rTheta = Math.toRadians(theta);
					Location nLoc = new Location(center.getWorld(),
							center.getX() + (radius - i * .8 + (Math.random() * totalNoise - negNoise))
									* Math.sin(rTheta) * Math.cos(rPhi),
							center.getY()
									+ (radius - i * .8 + (Math.random() * totalNoise - negNoise)) * Math.cos(rTheta),
							center.getZ() + (radius - i * .8 + (Math.random() * totalNoise - negNoise))
									* Math.sin(rTheta) * Math.sin(rPhi));

					if (nLoc.getBlock().getType().equals(Material.AIR)
							|| nLoc.getBlock().getType().name().contains("CARPET")
							|| nLoc.getBlock().getType().isInteractable())
						locations.add(nLoc);
				}
			}
		}

		return locations;
	}

	public static void drawAsyncRepeating(final JavaPlugin plugin, final Location groundCheck, final HashSet<Location> locations, final Particle particle, final int data, final int tickTime, final int refreshRate, final double radius, final Vector velocity)
	{
		BukkitTask thisTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new Runnable()
		{
			boolean onGround = onGround(groundCheck);

			@Override
			public void run()
			{
				Iterator<Location> it = locations.iterator();
				if (!onGround)
				{
					while (it.hasNext())
					{
						Location loc = it.next();
						loc.getWorld().spawnParticle(particle, loc, data);
						if (loc.getBlock().getType().equals(Material.AIR)
								|| loc.getBlock().getType().name().contains("CARPET")
								|| loc.getBlock().getType().isInteractable())
							loc.add(velocity);
						else
							it.remove(); 
					}
				}
				else while(it.hasNext())
				{
					Location loc = it.next();
					loc.getWorld().spawnParticle(particle, loc, data);
				}
			}
		}, 0, refreshRate);
		
		scheduleTaskCancel(plugin, thisTask, tickTime);
	}
	
	public static void drawExplosion(final JavaPlugin plugin, final Location center, final double radius)
	{
		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable()
		{
			@Override
			public void run()
			{
				HashSet<Location> explosion = buildSphere(center, radius, false, 1, .5);
				
				for(Location loc : explosion)
					loc.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, loc, 0);
			}
			
		});
	}

	// Default Refresh Rate 8
	public static void drawSmokeSphere(final JavaPlugin plugin, final Location center, final int tickTime, final int refreshRate, final double radius, Vector velocity)
	{
		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable()
		{
			@Override
			public void run()
			{
				HashSet<Location> smoke = buildSphere(center, radius, true, .5, 1);
				Bukkit.getLogger().info("Smoke Count: " + smoke.size());
				Vector vel = new Vector(velocity.getX() * refreshRate / 2, DROP_RATE, velocity.getZ() * refreshRate / 2);
				drawAsyncRepeating(plugin, center, smoke, Particle.CLOUD, 0, tickTime, refreshRate, radius, vel);
			}
		});
	}
	
	public static void drawSmokeSphere(final JavaPlugin plugin, final Location center, final int tickTime, final int refreshRate, final double radius)
	{
		drawSmokeSphere(plugin, center, tickTime, refreshRate, radius, new Vector(0, DROP_RATE, 0));
	}

	public static boolean onGround(Location loc)
	{
		if (loc.getBlock().getType().equals(Material.AIR))
		{
			Location tmp = loc.clone();
			tmp.setY(tmp.getY() - 1);

			if (tmp.getBlock().getType().equals(Material.AIR))
				return false;
		}
		return true;
	}

	public static ArrayList<MGPlayer> getNearbyPlayers(MGLobby lobby, Location location, double maxRadius,
			double minRadius)
	{
		ArrayList<MGPlayer> players = lobby.getPlayers(), nearby = new ArrayList<MGPlayer>();

		for (MGPlayer player : players)
		{
			double distance = Bukkit.getPlayer(player.getID()).getLocation().distance(location);
			if (distance <= maxRadius && distance > minRadius)
				nearby.add(player);
		}
		return nearby;
	}
}
