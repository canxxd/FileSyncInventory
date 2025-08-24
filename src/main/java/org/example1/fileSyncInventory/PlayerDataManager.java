package org.example1.fileSyncInventory;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayerDataManager {

    private final File dataFolder;
    private final Logger logger;

    public PlayerDataManager(File dataFolder, Logger logger) {
        this.dataFolder = dataFolder;
        this.logger = logger;
    }

    public YamlConfiguration loadData(UUID uuid) {
        File playerFile = new File(dataFolder, uuid.toString() + ".yml");
        if (!playerFile.exists()) {
            return null;
        }
        return YamlConfiguration.loadConfiguration(playerFile);
    }

    public void saveData(UUID uuid, YamlConfiguration data) {
        File playerFile = new File(dataFolder, uuid.toString() + ".yml");
        try {
            data.save(playerFile);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Oyuncuya ait FileSynç verisi kaydedilirken hata oluşmakta hatayı kontrol et Kontrolü sağla " + uuid, e);
        }
    }

    public String inventoryToBase64(ItemStack[] items) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeInt(items.length);

            for (ItemStack item : items) {
                dataOutput.writeObject(item);
            }

            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "envanter base64 dönüştürülemedi", e);
            return null;
        }
    }

    public ItemStack[] inventoryFromBase64(String data) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];

            for (int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

            dataInput.close();
            return items;
        } catch (IOException | ClassNotFoundException e) {
            logger.log(Level.SEVERE, "envanteriniz Base64 ile okunurken hata oluştu ):", e);
            return null;
        }
    }
}
