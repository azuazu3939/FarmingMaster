package mclove32.farmingmaster;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.utils.jnbt.CompoundTag;
import org.bukkit.inventory.ItemStack;

import static io.lumine.mythic.bukkit.MythicBukkit.inst;

public class MythicUtil {

    public static boolean isMythicItem(ItemStack item) {

        if (item == null || !item.hasItemMeta()) return false;
        try (MythicBukkit mythic = inst()) {

            CompoundTag tag = mythic.getVolatileCodeHandler().getItemHandler().getNBTData(item);
            return tag != null && tag.containsKey("MYTHIC_TYPE");
        }
    }

    public static ItemStack getMythicItem(String s) {

        try (MythicBukkit mythic = inst()) {
            return mythic.getItemManager().getItemStack(s);
        }
    }

    public static String getMythicID(ItemStack item) {

        if (item == null || !item.hasItemMeta()) return null;
        try (MythicBukkit mythic = inst()) {

            CompoundTag tag = mythic.getVolatileCodeHandler().getItemHandler().getNBTData(item);
            if (tag != null && tag.containsKey("MYTHIC_TYPE")) {

                return tag.getString("MYTHIC_TYPE");
            }
            return null;
        }
    }
}
