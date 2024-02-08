package mclove32.farmingmaster;

import com.jeff_media.customblockdata.CustomBlockData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Plant {

    public static final NamespacedKey key = new NamespacedKey("farming", "grow");
    public static final NamespacedKey dropKey = new NamespacedKey("farming", "drop");
    public static final NamespacedKey chanceKey = new NamespacedKey("farming", "chance");
    public static final NamespacedKey idKey = new NamespacedKey("farming", "mmid");
    private final FarmingMaster master;
    public Plant(FarmingMaster master) {

        this.master = master;
    }

    public void runPlant(@NotNull List<Material> list, @NotNull Block b, ItemStack item) {

        Material get = b.getType();
        for (Material m: list) {

            if (m == get && canPlantLoc(b)) {
                setPlant(b.getRelative(BlockFace.UP), item);
                item.setAmount(item.getAmount() - 1);
                return;
            }
        }
    }

    public boolean canPlantLoc(@NotNull Block b) {

        Block get = b.getRelative(BlockFace.UP);
        return get.getType() == Material.AIR;
    }


    public void setPlant(@NotNull Block b, ItemStack item) {

        int speed = FarmingUtil.getGrowSpeed(item);
        String type = FarmingUtil.getFarmType(item);
        Material m = Material.valueOf(type.toUpperCase());

        b.setBlockData(m.createBlockData());
        PersistentDataContainer pc = new CustomBlockData(b, master);
        pc.set(dropKey, PersistentDataType.STRING, FarmingUtil.getFarmDropString(item));

        int i;
        String s = item.getItemMeta().getPersistentDataContainer().get(chanceKey, PersistentDataType.STRING);
        if (s == null) {i = 1;}
        else i = Integer.parseInt(s);
        pc.set(chanceKey, PersistentDataType.STRING, String.valueOf(i));
        pc.set(idKey, PersistentDataType.STRING, MythicUtil.getMythicID(item));

        plantTimer(speed, b, m);
    }

    public void plantTimer(int speed, Block b, Material m) {

        PersistentDataContainer pc = new CustomBlockData(b, master);

        if (b.getType() != m) {

            pc.remove(Plant.idKey);
            pc.remove(Plant.chanceKey);
            pc.remove(Plant.dropKey);
            pc.remove(Plant.key);
            return;
        }

        Ageable age = (Ageable) b.getBlockData();
        int i = age.getAge() + 1;

        if (pc.has(key)) if (i > 7) {

            NamespacedKey speedKey = new NamespacedKey("farming", "speed");
            pc.set(speedKey, PersistentDataType.STRING, String.valueOf(speed));
            return;
        }
        Bukkit.getScheduler().runTaskLater(master, () -> {

            if (b.getType() != m || !pc.has(dropKey)) {

                pc.remove(Plant.idKey);
                pc.remove(Plant.chanceKey);
                pc.remove(Plant.dropKey);
                pc.remove(Plant.key);
                return;
            }

            age.setAge(i);
            pc.set(key, PersistentDataType.STRING, String.valueOf(i));
            b.setBlockData(age);

            plantTimer(speed, b, m);
        }, speed);
    }

    public void harvestPlant(Player p, Block b) {

        PersistentDataContainer pc = new CustomBlockData(b, master);
        String s = pc.get(dropKey, PersistentDataType.STRING);
        if (s == null) return;

        List<String> list = FarmingUtil.getFarmDrops(s);
        for (String get : list) {
            if (get == null) continue;

            ItemStack item = FarmingUtil.getFarmDrop(get, b);
            if (item == null) continue;
            DropUtil.dropItem(p, item);
        }
    }
}
