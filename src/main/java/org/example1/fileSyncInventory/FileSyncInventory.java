package org.example1.fileSyncInventory;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

public class FileSyncInventory extends JavaPlugin {

    private File dataFolder;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        String dataPath = getConfig().getString("data_path", "player_data");
        this.dataFolder = new File(dataPath);

        if (!dataFolder.exists()) {
            if (dataFolder.mkdirs()) {
                getLogger().info("verileri için klasör oluşturuldu: " + dataFolder.getAbsolutePath());
            } else {
                getLogger().warning("verileri klasörü oluşturulamadı: " + dataFolder.getAbsolutePath());
            }
        }

        List<String> disabledWorlds = getConfig().getStringList("disabled_worlds");
        if (disabledWorlds.isEmpty()) {
            getLogger().info("dünya devre dışı bırakmadın");
        } else {
            getLogger().info("devre dışı bırakılan dünyaların: " + disabledWorlds);
        }

        PlayerDataListener listener = new PlayerDataListener(this, dataFolder, disabledWorlds);
        getServer().getPluginManager().registerEvents(listener, this);

        boolean autoSaveEnabled = getConfig().getBoolean("auto_save_interval.enabled", false);

        double autoSaveSeconds = getConfig().getDouble("auto_save_interval.seconds", 30.0);

        if (autoSaveEnabled) {
            long ticks = (long) (autoSaveSeconds * 20L);

            if (ticks < 1) {
                ticks = 1;
                getLogger().warning("Belirtilen kaydetme aralığı 1 tick ten 50ms daha kısa Minimum 1 tick olarak ayarlandı");
            }

            getLogger().info("otomatik kaydetme her " + autoSaveSeconds + " saniyede bir (" + ticks + " tick) aktif");

            getServer().getGlobalRegionScheduler().runAtFixedRate(this, task -> {
                for (org.bukkit.entity.Player player : getServer().getOnlinePlayers()) {
                    if (!disabledWorlds.contains(player.getWorld().getName().toLowerCase())) {
                        listener.savePlayerData(player);
                    }
                }
            }, ticks, ticks);
        } else {
            getLogger().info("oto kaydetme devre dışı");
        }

        getLogger().info("FileSync folia/paper destekli eklenti sunucuyla başarıyla çalıştı şuan aktif");
    }

    @Override
    public void onDisable() {
        getLogger().info("FileSync devre dışı şuan çalışmıyor birşeyde hata mevcut");
    }
}