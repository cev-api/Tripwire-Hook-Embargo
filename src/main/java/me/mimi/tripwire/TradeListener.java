package me.mimi.tripwire;

import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.scheduler.BukkitScheduler;

public final class TradeListener implements Listener {
    private final TripwirePlugin plugin;

    public TradeListener(TripwirePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onVillagerAcquireTrade(VillagerAcquireTradeEvent event) {
        if (!plugin.isBlockingEnabled()) {
            return;
        }

        AbstractVillager entity = event.getEntity();
        if (!(entity instanceof Villager villager)) {
            return;
        }

        BukkitScheduler scheduler = plugin.getServer().getScheduler();
        scheduler.runTask(plugin, () -> plugin.sanitizeVillager(villager));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntitiesLoad(EntitiesLoadEvent event) {
        if (!plugin.isBlockingEnabled()) {
            return;
        }

        BukkitScheduler scheduler = plugin.getServer().getScheduler();
        scheduler.runTask(plugin, () -> {
            event.getEntities().stream()
                .filter(Villager.class::isInstance)
                .map(Villager.class::cast)
                .forEach(plugin::sanitizeVillager);
        });
    }
}
