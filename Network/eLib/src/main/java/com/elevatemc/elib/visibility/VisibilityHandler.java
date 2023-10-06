package com.elevatemc.elib.visibility;

import com.elevatemc.elib.eLib;
import com.elevatemc.elib.visibility.action.OverrideAction;
import com.elevatemc.elib.visibility.action.VisibilityAction;
import com.elevatemc.elib.visibility.listener.VisibilityListener;
import com.elevatemc.elib.visibility.provider.OverrideProvider;
import com.elevatemc.elib.visibility.provider.VisibilityProvider;
import lombok.Getter;

import org.bukkit.entity.Player;

import java.util.*;

public class VisibilityHandler {

    @Getter private final Map<String, VisibilityProvider> handlers = new LinkedHashMap<>();
    @Getter private final Map<String, OverrideProvider> overrideHandlers = new LinkedHashMap<>();

    public VisibilityHandler() {
        eLib.getInstance().getServer().getPluginManager().registerEvents(new VisibilityListener(), eLib.getInstance());
    }

    public void registerHandler(String identifier, VisibilityProvider handler) {
        this.handlers.put(identifier, handler);
    }

    public void registerOverride(String identifier, OverrideProvider handler) {
        this.overrideHandlers.put(identifier, handler);
    }

    public void update(Player player) {

        if (!this.handlers.isEmpty() || !this.overrideHandlers.isEmpty()) {
            this.updateAllTo(player);
            this.updateToAll(player);
        }

    }

    /** @deprecated */
    @Deprecated
    public void updateAllTo(Player viewer) {
        
        for (Player target : eLib.getInstance().getServer().getOnlinePlayers()) {

            if (!this.shouldSee(target, viewer)) {
                viewer.hidePlayer(target);
            } else {
                viewer.showPlayer(target);
            }
            
        }
        
    }

    /** @deprecated */
    @Deprecated
    public void updateToAll(Player target) {
        
        for (Player viewer : eLib.getInstance().getServer().getOnlinePlayers()) {

            if (!this.shouldSee(target, viewer)) {
                viewer.hidePlayer(target);
            } else {
                viewer.showPlayer(target);
            }
            
        }

    }

    public boolean treatAsOnline(Player target, Player viewer) {
        return viewer.canSee(target) || !target.hasMetadata("invisible");
    }

    private boolean shouldSee(Player target,Player viewer) {

        for (VisibilityProvider visibilityProvider : this.handlers.values()) {

            for (OverrideProvider overrideProvider : this.overrideHandlers.values()) {
                return overrideProvider.getAction(target,viewer) == OverrideAction.SHOW;
            }

            return visibilityProvider.getAction(target,viewer) == VisibilityAction.NEUTRAL;
        }

        return true;
    }
}
