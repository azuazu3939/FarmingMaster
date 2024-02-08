package mclove32.farmingmaster;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public final class FarmingMaster extends JavaPlugin {

    private static FarmingMaster farming;
    public FarmingMaster() {farming = this;}
    public static FarmingMaster inst() {return farming;}
    public static final Random ran = new Random();

    @Override
    public void onEnable() {

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlantListener(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
