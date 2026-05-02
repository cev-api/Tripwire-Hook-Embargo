package me.mimi.tripwire;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class TripwirePlugin extends JavaPlugin implements TabCompleter {
    private static final String ENABLED_PATH = "enabled";

    private boolean enabled;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        enabled = getConfig().getBoolean(ENABLED_PATH, true);

        TradeListener tradeListener = new TradeListener(this);
        Bukkit.getPluginManager().registerEvents(tradeListener, this);

        if (getCommand("hookembargo") != null) {
            getCommand("hookembargo").setExecutor(this);
            getCommand("hookembargo").setTabCompleter(this);
        }

        sanitizeLoadedFletchers();
        getLogger().info("Tripwire hook blocking is " + (enabled ? "enabled" : "disabled") + ".");
    }

    public boolean isBlockingEnabled() {
        return enabled;
    }

    public void setBlockingEnabled(boolean enabled) {
        this.enabled = enabled;

        FileConfiguration config = getConfig();
        config.set(ENABLED_PATH, enabled);
        saveConfig();
    }

    public boolean sanitizeVillager(Villager villager) {
        if (villager.getProfession() != Villager.Profession.FLETCHER) {
            return false;
        }

        List<MerchantRecipe> filteredRecipes = new ArrayList<>();
        boolean changed = false;

        for (MerchantRecipe recipe : villager.getRecipes()) {
            if (isTripwireHookBuyTrade(recipe)) {
                changed = true;
                continue;
            }

            filteredRecipes.add(recipe);
        }

        if (changed) {
            villager.setRecipes(filteredRecipes);
        }

        return changed;
    }

    public int sanitizeLoadedFletchers() {
        if (!enabled) {
            return 0;
        }

        int updated = 0;
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Villager villager && sanitizeVillager(villager)) {
                    updated++;
                }
            }
        }

        return updated;
    }

    private boolean isTripwireHookBuyTrade(MerchantRecipe recipe) {
        ItemStack result = recipe.getResult();
        return result.getType().isAir() == false
            && result.getType() == org.bukkit.Material.EMERALD
            && recipe.getIngredients().stream().anyMatch(ingredient -> ingredient.getType() == org.bukkit.Material.TRIPWIRE_HOOK);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Tripwire hook embargo is currently " + (enabled ? "enabled" : "disabled") + ".");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage("Usage: /" + label + " [true|false]");
            return true;
        }

        String value = args[0].toLowerCase(Locale.ROOT);
        if (!value.equals("true") && !value.equals("false")) {
            sender.sendMessage("Usage: /" + label + " [true|false]");
            return true;
        }

        boolean requestedState = Boolean.parseBoolean(value);
        if (requestedState == enabled) {
            sender.sendMessage("Tripwire hook embargo is already " + (enabled ? "enabled" : "disabled") + ".");
            return true;
        }

        setBlockingEnabled(requestedState);

        if (requestedState) {
            int updated = sanitizeLoadedFletchers();
            sender.sendMessage("Tripwire hook embargo enabled. Updated " + updated + " loaded fletcher villager(s).");
        } else {
            sender.sendMessage("Tripwire hook embargo disabled.");
        }

        return true;
    }

    @Override
    public @NotNull List<String> onTabComplete(
        @NotNull CommandSender sender,
        @NotNull Command command,
        @NotNull String alias,
        String[] args
    ) {
        if (args.length != 1) {
            return Collections.emptyList();
        }

        String input = args[0].toLowerCase(Locale.ROOT);
        List<String> suggestions = new ArrayList<>();
        if ("true".startsWith(input)) {
            suggestions.add("true");
        }
        if ("false".startsWith(input)) {
            suggestions.add("false");
        }
        return suggestions;
    }
}
