package org.example1.fileSyncInventory;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerDataListener implements Listener {

    private final JavaPlugin plugin;
    private final PlayerDataManager dataManager;
    private final List<String> disabledWorlds;

    public PlayerDataListener(JavaPlugin plugin, File dataFolder, List<String> disabledWorlds) {
        this.plugin = plugin;
        this.dataManager = new PlayerDataManager(dataFolder, plugin.getLogger());
        this.disabledWorlds = disabledWorlds;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (disabledWorlds.contains(player.getWorld().getName().toLowerCase())) {
            plugin.getLogger().info(player.getName() + "bir dünayı configdeki ayarını devre dışı bırakılan bir dünya için veri senkronizasyon atlandı");
            return;
        }

        UUID uuid = player.getUniqueId();

        CompletableFuture.supplyAsync(() -> dataManager.loadData(uuid))
                .thenAcceptAsync(loadedData -> {
                    if (loadedData != null) {
                        plugin.getServer().getGlobalRegionScheduler().run(plugin, task -> {
                            try {
                                if (player.isOnline()) {
                                    if (loadedData.contains("inventory")) {
                                        ItemStack[] invContents = dataManager.inventoryFromBase64(loadedData.getString("inventory"));
                                        if (invContents != null) player.getInventory().setContents(invContents);
                                    }
                                    if (loadedData.contains("enderchest")) {
                                        ItemStack[] enderContents = dataManager.inventoryFromBase64(loadedData.getString("enderchest"));
                                        if (enderContents != null) player.getEnderChest().setContents(enderContents);
                                    }
                                    if (loadedData.contains("health")) player.setHealth(loadedData.getDouble("health"));
                                    if (loadedData.contains("food_level")) player.setFoodLevel(loadedData.getInt("food_level"));
                                    if (loadedData.contains("experience")) {
                                        player.setLevel(loadedData.getInt("experience.level"));
                                        player.setExp((float) loadedData.getDouble("experience.exp"));
                                    }
                                }
                            } catch (Exception e) {
                                plugin.getLogger().severe("Oyuncumuz " + player.getName() + " için veri yüklenirkene hata medyana geldi");
                                e.printStackTrace();
                            }
                        });
                    } else {
                        savePlayerData(player);
                    }
                });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (disabledWorlds.contains(player.getWorld().getName().toLowerCase())) {
            plugin.getLogger().info(player.getName() + " bir dünayı configdeki ayarını devre dışı bırakılan bir dünya için veri senkronizasyon atlandı");
            return;
        }

        savePlayerData(player);
    }

    public void savePlayerData(Player player) {
        YamlConfiguration data = new YamlConfiguration();
        data.set("inventory", dataManager.inventoryToBase64(player.getInventory().getContents()));
        data.set("enderchest", dataManager.inventoryToBase64(player.getEnderChest().getContents()));
        data.set("health", player.getHealth());
        data.set("food_level", player.getFoodLevel());
        data.set("experience.level", player.getLevel());
        data.set("experience.exp", player.getExp());

        CompletableFuture.runAsync(() -> dataManager.saveData(player.getUniqueId(), data));
    }
}
