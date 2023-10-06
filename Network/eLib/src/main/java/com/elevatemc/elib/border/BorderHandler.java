package com.elevatemc.elib.border;

import com.elevatemc.elib.eLib;
import com.elevatemc.elib.border.runnable.EnsureInsideRunnable;
import com.elevatemc.elib.border.listener.BorderListener;
import com.elevatemc.elib.border.listener.InternalBorderListener;
import lombok.Getter;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

public class BorderHandler {

    @Getter private final Map<World,Border> borderMap = new HashMap<>();

    public BorderHandler() {
        eLib.getInstance().getServer().getPluginManager().registerEvents(new BorderListener(), eLib.getInstance());
        eLib.getInstance().getServer().getPluginManager().registerEvents(new InternalBorderListener(), eLib.getInstance());

        new EnsureInsideRunnable().runTaskTimer(eLib.getInstance(), 5L, 5L);
    }

    public Border getBorderForWorld(World world) {
        return this.borderMap.get(world);
    }

    void addBorder(Border border) {
        this.borderMap.put(border.getOrigin().getWorld(), border);
    }


}
