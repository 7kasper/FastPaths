package nl.kaspermuller.fastpaths;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//import org.bukkit.Bukkit;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.bergerkiller.bukkit.common.events.EntityMoveEvent;

public class FastPaths extends JavaPlugin implements Listener{

	List<Material> pathTypes = new ArrayList<Material>();
	List<Material> noSnowTypes = new ArrayList<Material>();
	int checkInterval = 200;
	int amplification = 1;
	int duration = 20;

	Location to = null;
	
	@Override
	public void onEnable() {
		saveDefaultConfig();
		getServer().getPluginManager().registerEvents(this, this);
		to = new Location(getServer().getWorlds().get(0), 0, 0, 0);
		try {
			pathTypes = getConfig().getStringList("path-blocks").stream().map((str) -> Material.valueOf(str)).collect(Collectors.toList());
			noSnowTypes = getConfig().getStringList("no-snow-blocks").stream().map((str) -> Material.valueOf(str)).collect(Collectors.toList());
			checkInterval = getConfig().getInt("interval");
			amplification = getConfig().getInt("amplification");
			duration = getConfig().getInt("duration");
		} catch (IllegalArgumentException e) {
			getServer().getConsoleSender().sendMessage("Error in §aFastPaths§r blocktypes.");
			e.printStackTrace();
		}
		getServer().getConsoleSender().sendMessage("Enabled §aFastPaths§r plugin :D");
//		pathTypes.forEach((mat) -> {
//			getServer().getConsoleSender().sendMessage("Mat: " + mat);
//		});
	}
	
	
    //=====================================================\\
    //				        Listeners  	     	  		   \\
    //=====================================================\\

	/**
	 * Track entity movement and apply speed if the entity is on a path.
	 * @param e
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityMove(EntityMoveEvent e) {
		if (e.getEntityType().isAlive()) {
			checkApply((LivingEntity) e.getEntity(), e.getTo(to));
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerMove(PlayerMoveEvent e) {
		if (e.getPlayer().hasPermission("fastpaths.use")) {
			checkApply(e.getPlayer(), e.getTo());
		}
	}
	
	public void checkApply(LivingEntity le, Location to) {
		// We want quite a low frequency to check.
		if (le.getMetadata("smollInt").isEmpty()) {
			le.setMetadata("smollInt", new FixedMetadataValue(this, System.currentTimeMillis()));
		}
		if (System.currentTimeMillis() - le.getMetadata("smollInt").get(0).asLong() >= checkInterval) {
//			Bukkit.broadcastMessage("1: LE check");
			Block pathBlock = (to.getY() % 1) > 0.4 ? to.getBlock() : to.getBlock().getRelative(BlockFace.DOWN);
//			Bukkit.broadcastMessage("2: PathCheck: " + pathBlock.getType());
			if (isPathBlock(pathBlock)) {
//				Bukkit.broadcastMessage("3: Applying potion");
				if (le.hasPotionEffect(PotionEffectType.SPEED)) {
					PotionEffect sp = le.getPotionEffect(PotionEffectType.SPEED);
					if (sp.getDuration() > duration) return; 
				}
				le.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, amplification));
			}
			le.setMetadata("smollInt", new FixedMetadataValue(this, System.currentTimeMillis()));
		}
	}

	/**
	 * Prevent snow forming on path blocks.
	 * @param e
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockForm(BlockFormEvent e) {
//		Bukkit.broadcastMessage("Forming stuff");
		if (e.getNewState().getType() == Material.SNOW) {
//			Bukkit.broadcastMessage("its snow: " + e.getBlock().getRelative(BlockFace.DOWN).getType());
			
			if (isNoSnowBlock(e.getBlock().getRelative(BlockFace.DOWN))) {
//				Bukkit.broadcastMessage("shouldnt be.");
				e.setCancelled(true);
			}
		}
	}
	
	
    //=====================================================\\
    //				       	   API  	     	  		   \\
    //=====================================================\\

	/**
	 * Checks if the specified block is a path block.
	 * Path Blocks ill have enityspeed on them increased.
	 * @param b - The block to check.
	 * @return true, if this is a path block.
	 */
	public boolean isPathBlock(Block b) {
		return isPathBlock(b.getType());
	}

	/**
	 * Checks if the specified material is a path block.
	 * Path Blocks will have entityspeed on them increased.
	 * @param m - The material to check.
	 * @return true, if this is a path block.
	 */
	public boolean isPathBlock(Material m) {
		return pathTypes.contains(m);
	}

	/**
	 * Checks if the specified block is a no-snow block.
	 * No-Snow blocks will have natural snow forming on them prevented.
	 * @param b - The block to check.
	 * @return true, if this is a no-snow block.
	 */
	public boolean isNoSnowBlock(Block b) {
		return noSnowTypes.contains(b.getType());
	}

	/**
	 * Checks if the specified material is a no-show block.
	 * No-Snow blocks will have natural snow forming on them prevented.
	 * @param m - The material of the block to check.
	 * @return true, if this is a no-snow block.
	 */
	public boolean isNoSnowBlock(Material m) {
		return noSnowTypes.contains(m);
	}
	
}
