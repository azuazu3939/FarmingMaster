package mclove32.farmingmaster;

import com.jeff_media.customblockdata.CustomBlockData;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class PlantListener implements Listener {

    private final FarmingMaster farming;
    public PlantListener(FarmingMaster farming) {
        this.farming = farming;
    }

    @EventHandler
    public void onPlant(@NotNull PlayerInteractEvent e) {

        if (e.getAction().isLeftClick()) return;
        Block b = e.getClickedBlock();
        if (b == null) return;
        if (!e.getAction().isRightClick()) return;

        Player p = e.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();
        if (!item.hasItemMeta()) return;
        if (!(FarmingUtil.isFarmingSeeds(item))) return;

        new Plant(farming).runPlant(FarmingUtil.getFarmland(item), b, item);
    }

    @EventHandler
    public void onBlockBreak(@NotNull BlockBreakEvent e) {

        Block b = e.getBlock();
        PersistentDataContainer pc = new CustomBlockData(b, farming);
        if (!pc.has(Plant.key)) return;

        String s = pc.get(Plant.key, PersistentDataType.STRING);
        if (s == null) return;

        int i = Integer.parseInt(s);
        if (i != 7) {

            b.setType(Material.AIR);
            pc.remove(Plant.idKey);
            pc.remove(Plant.chanceKey);
            pc.remove(Plant.dropKey);
            pc.remove(Plant.key);
            return;
        }

        String mmid = pc.get(Plant.idKey, PersistentDataType.STRING);
        if (mmid != null)  {
            try (MythicBukkit bukkit = MythicBukkit.inst()) {

                int a = FarmingMaster.ran.nextInt(3) + 1;
                ItemStack stack = bukkit.getItemManager().getItemStack(mmid, a);
                if (stack != null) DropUtil.dropItem(e.getPlayer(), stack);
            }
        }

        new Plant(farming).harvestPlant(e.getPlayer(), b);
        b.setType(Material.AIR);
        pc.remove(Plant.idKey);
        pc.remove(Plant.chanceKey);
        pc.remove(Plant.dropKey);
        pc.remove(Plant.key);
    }

    @EventHandler
    public void onAffect(@NotNull BlockPhysicsEvent e) {

        Block b = e.getBlock();
        PersistentDataContainer pc = new CustomBlockData(b, farming);
        if (pc.has(Plant.dropKey)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onFade(@NotNull BlockFadeEvent e) {

        if (e.getBlock().getType() == Material.FARMLAND) e.setCancelled(true);
    }

    @EventHandler
    public void onInteract(@NotNull PlayerInteractEvent e) {

        if (e.getAction().isLeftClick()) return;
        Block b = e.getClickedBlock();
        if (b == null) return;

        PersistentDataContainer pc = new CustomBlockData(b, farming);
        if (!pc.has(Plant.dropKey)) return;
        Player p = e.getPlayer();

        try {
            Ageable age = (Ageable) b.getBlockData();
            if (age.getAge() < 7) return;

            p.playSound(p, Sound.BLOCK_CROP_BREAK, 1, 1);
            new Plant(farming).harvestPlant(p, b);
            age.setAge(0);
            b.setBlockData(age);

            NamespacedKey speedKey = new NamespacedKey("farming", "speed");
            String speedS = pc.get(speedKey, PersistentDataType.STRING);
            if (speedS == null) return;

            new Plant(farming).plantTimer(Integer.parseInt(speedS), b, b.getType());

        } catch (Exception ignored) {
        }
    }
}
