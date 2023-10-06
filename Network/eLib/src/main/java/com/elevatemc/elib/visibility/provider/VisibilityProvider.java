package com.elevatemc.elib.visibility.provider;

import com.elevatemc.elib.visibility.action.VisibilityAction;
import org.bukkit.entity.Player;

public interface VisibilityProvider {

    VisibilityAction getAction(Player player,Player target);

}
