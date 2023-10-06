package com.elevatemc.elib.visibility.provider;

import com.elevatemc.elib.visibility.action.OverrideAction;
import org.bukkit.entity.Player;

public interface OverrideProvider {

    OverrideAction getAction(Player target, Player viewer);

}
