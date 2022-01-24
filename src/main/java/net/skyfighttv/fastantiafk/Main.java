package net.skyfighttv.fastantiafk;

import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    private static Main instance;

    {
        instance = this;
    }

    @Override
    public void onEnable() {
        new AntiAFK().runTaskTimerAsynchronously(this, 40, 40);
    }

    public static Main getInstance() {
        return instance;
    }
}
