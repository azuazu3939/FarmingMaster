package mclove32.farmingmaster;

import com.jeff_media.customblockdata.CustomBlockData;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;



public class FarmingUtil {

    public static boolean isFarmingSeeds(@NotNull ItemStack item) {

        NamespacedKey key = new NamespacedKey("farming", "farmland");
        PersistentDataContainer pc = item.getItemMeta().getPersistentDataContainer();
        return pc.has(key, PersistentDataType.STRING);
    }

    public static @NotNull List<Material> getFarmland(@NotNull ItemStack item) {

        NamespacedKey key = new NamespacedKey("farming", "farmland");
        PersistentDataContainer pc = item.getItemMeta().getPersistentDataContainer();
        String s = pc.get(key, PersistentDataType.STRING);

        List<Material> list = new ArrayList<>();
        if (s == null) return list;

        while (s.contains(" ")) {

            String get = s.substring(0, s.indexOf(" "));
            list.add(Material.valueOf(get.toUpperCase()));

            s = s.substring(s.indexOf(" ") + 1);
        }
        list.add(Material.valueOf(s.toUpperCase()));
        return list;
    }

    public static int getGrowSpeed(@NotNull ItemStack item) {

        NamespacedKey key = new NamespacedKey("farming", "grow_speed");
        PersistentDataContainer pc = item.getItemMeta().getPersistentDataContainer();
        String s = pc.get(key, PersistentDataType.STRING);

        if (s == null) return 0;

        return Integer.parseInt(s);
    }

    public static String getFarmType(@NotNull ItemStack item) {

        NamespacedKey key = new NamespacedKey("farming", "type");
        PersistentDataContainer pc = item.getItemMeta().getPersistentDataContainer();
        return pc.get(key, PersistentDataType.STRING);
    }


    public static String getFarmDropString(@NotNull ItemStack item) {

        NamespacedKey key = new NamespacedKey("farming", "drop");
        PersistentDataContainer pc = item.getItemMeta().getPersistentDataContainer();
        return pc.get(key, PersistentDataType.STRING);
    }

    public static @NotNull List<String> getFarmDrops(String s) {

        List<String> list = new ArrayList<>();
        if (s == null) return list;

        while (s.contains(" ")) {

            String get = s.substring(0, s.indexOf(" "));
            list.add(get);

            s = s.substring(s.indexOf(" ") + 1);
        }
        list.add(s);

        return list;
    }

    public static @Nullable ItemStack getFarmDrop(@NotNull String s, Block b) {

        try (MythicBukkit bukkit = MythicBukkit.inst()) {

            if (s.contains(":")) {

                int chance = getChance(b);
                int get = Integer.parseInt(s.substring(s.indexOf(":") + 1));
                if (FarmingMaster.ran.nextInt(chance) < get) {

                    return bukkit.getItemManager().getItemStack(s.substring(0, s.indexOf(":")), 1);
                }
            }
        }
        return null;
    }

    public static int getChance(@NotNull Block b) {

        PersistentDataContainer pc = new CustomBlockData(b, FarmingMaster.inst());
        NamespacedKey key = new NamespacedKey("farming", "chance");
        String s = pc.get(key, PersistentDataType.STRING);
        if (s == null) return 1;

        return Integer.parseInt(s);
    }
}
