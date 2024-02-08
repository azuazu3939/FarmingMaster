package mclove32.farmingmaster;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class DropUtil {

    public static void dropItem(@NotNull Player p, ItemStack item) {

        for (ItemStack stack : p.getInventory().addItem(item).values()) {
            if (stack == null) continue;
            p.getWorld().dropItem(p.getLocation(), stack);
        }
    }
}
