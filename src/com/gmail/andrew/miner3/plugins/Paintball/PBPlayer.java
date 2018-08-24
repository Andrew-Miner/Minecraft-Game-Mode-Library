package com.gmail.andrew.miner3.plugins.Paintball;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.gmail.andrew.miner3.plugins.Paintball.Assist.SnowBowMethods;
import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.Lobby;
import com.gmail.andrew.miner3.plugins.Paintball.MapClasses.Bound;

public class PBPlayer 
{
	private int kills;
	private int deaths;
	private int currentScore;
	private int totalScore;
	private int wins;
	private int losses;
	private long lastShot;
	private long fireRate;
	private long lastReload;
	private boolean isReloading;
	private int shotsFired;
	
	private boolean isDead;
	private boolean spectating;
	private boolean pregame;
	private boolean ingame;
	private UUID id;
	
	private Location storedLoc;
	
	private Bound selection;
	
	public PBPlayer(UUID id)
	{
		this.id = id;
		this.kills = 0;
		this.deaths = 0;
		this.currentScore = 0;
		this.totalScore = 0;
		
		this.lastShot = 0;
		this.fireRate = SnowBowMethods.firingDelay0;
		this.lastReload = lastShot;
		this.isReloading = false;
		shotsFired = 0;
		
		
		pregame = true;
		ingame = false;
		spectating = false;
		isDead = false;
		
		storedLoc = null;	
		
		selection = new Bound();
	}
	
	public PBPlayer(UUID id, int kills, int deaths, int totalScore, int currentScore)
	{
		this.id = id;
		this.kills = kills;
		this.deaths = deaths;
		this.currentScore = currentScore;
		this.totalScore = totalScore;
		
		this.lastShot = 0;
		this.fireRate = SnowBowMethods.firingDelay0;
		this.lastReload = lastShot;
		this.isReloading = false;
		shotsFired = 0;
		
		
		pregame = true;
		ingame = false;
		spectating = false;
		isDead = false;
		
		storedLoc = null;	
		
		selection = new Bound();
	}
	
	@Override
	public int hashCode()
	{
		return id.hashCode();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof PBPlayer)
		{
			PBPlayer other = (PBPlayer) obj;
			
			if(other.id.equals(id))
				return true;
		}
		else if(obj instanceof UUID)
		{
			if(id.equals(obj))
				return true;
		}
		else
			return super.equals(obj);
		
		return false;
	}
	
	public UUID getID()
	{
		return id;
	}
	
	public void addKill()
	{
		kills++;
	}
	
	public void addDeath()
	{
		deaths++;
	}
	
	public int getKills()
	{
		return kills;
	}
	
	public int getDeaths()
	{
		return deaths;
	}
	
	public int getCurrentScore()
	{
		return currentScore;
	}
	
	public void setCurrentScore(int score)
	{
		this.currentScore = score;
	}
	
	public void addToScore(int val)
	{
		currentScore += val;
		totalScore += val;
	}
	
	public int getTotalScore()
	{
		return totalScore;
	}
	
	public void setTotalScore(int score)
	{
		totalScore = score;
	}
	
	public boolean isSpectating()
	{
		return spectating;
	}
	
	public boolean isInPregame()
	{
		return pregame;
	}
	
	public boolean isInGame()
	{
		return ingame;
	}
	
	public void setPregame()
	{
		ingame = false;
		spectating = false;
		pregame = true;
		isDead = false;
	}
	
	public void setInGame()
	{
		pregame = false;
		spectating = false;
		ingame = true;
		isDead = false;
	}
	
	public void setSpectating()
	{
		pregame = false;
		ingame = false;
		spectating = true;
		isDead = true;
	}
	
	public void storeLocation()
	{
		storedLoc = Bukkit.getPlayer(id).getLocation();
	}
	
	public Location getStoredLoc()
	{
		return storedLoc;
	}
	
	public void spawn(Location loc, SpawnKit kit)
	{
		if(pregame)
			spawnPregame(loc, kit);
		else if(spectating)
			spawnSpectating(loc, kit);
		else if(ingame)
			spawnInGame(loc, kit);
	}
	
	public void spawnPregame(Location loc, SpawnKit kit)
	{
		Player p = Bukkit.getServer().getPlayer(id);
		p.teleport(loc);
		p.getInventory().clear();
		p.setGameMode(GameMode.CREATIVE);
		
		if(kit != null)
			kit.giveKit(p);
	}
	
	public void spawnSpectating(Location loc, SpawnKit kit)
	{
		Player p = Bukkit.getServer().getPlayer(id);
		p.teleport(loc);
		p.getInventory().clear();
		p.setGameMode(GameMode.SPECTATOR);
		
		if(kit != null)
			kit.giveKit(p);
	}
	
	public void spawnInGame(Location loc, SpawnKit kit)
	{
		Player p = Bukkit.getServer().getPlayer(id);
		p.teleport(loc);
		p.getInventory().clear();
		p.setGameMode(GameMode.ADVENTURE);
		
		if(kit != null)
			kit.giveKit(p);
	}
	
	public boolean isDead()
	{
		return isDead;
	}
	
	public void setDead(boolean dead)
	{
		isDead = dead;
	}

	public int getWins() 
	{
		return wins;
	}
	
	public void setWins(int wins)
	{
		this.wins = wins;
	}

	public void addWin() 
	{
		this.wins++;
	}

	public int getLosses() 
	{
		return losses;
	}
	
	public void setLosses(int losses)
	{
		this.losses = losses;
	}

	public void addLoss() 
	{
		this.losses++;
	}
	
	public void setSelection(Bound selection)
	{
		this.selection = selection;
	}
	
	public Bound getSelection()
	{
		return selection;
	}
	
	public void storeLastShotTime()
	{
		lastShot = System.currentTimeMillis();
	}
	
	public long getLastShotTime() 
	{
		return lastShot;
	}
	
	public void setFireRate(long rate)
	{
		this.fireRate = rate;
	}
	
	public long getFireRate()
	{
		return fireRate;
	}
	
	public void setReloading(boolean rld)
	{
		if(rld)
		{
			storeReloadTime();
			isReloading = true;
		}
		else
			isReloading = false;
	}
	
	public boolean isReloading()
	{
		return isReloading;
	}
	
	public void storeReloadTime()
	{
		lastReload = System.currentTimeMillis();
	}
	
	public long getLastReload()
	{
		return lastReload;
	}
	
	public void setShotsFired(int shots)
	{
		shotsFired = shots;
	}
	
	public int getShotsFired()
	{
		return shotsFired;
	}

	public void resetBow() 
	{
		setShotsFired(0);
		setReloading(false);
		lastShot = 0;
		lastReload = 0;
	}
}
