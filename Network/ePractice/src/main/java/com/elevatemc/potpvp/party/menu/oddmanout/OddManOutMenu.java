package com.elevatemc.potpvp.party.menu.oddmanout;

import com.google.common.base.Preconditions;
import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.menu.Menu;
import com.elevatemc.elib.util.Callback;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public final class OddManOutMenu extends Menu {

    private final Callback<Boolean> callback;

    public OddManOutMenu(Callback<Boolean> callback) {
        this.callback = Preconditions.checkNotNull(callback, "callback");
        setPlaceholder(true);
    }

    @Override
    public String getTitle(Player player) {
        return "Continue with unbalanced teams?";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(getSlot(2, 1), new OddManOutButton(true, callback));
        buttons.put(getSlot(6, 1), new OddManOutButton(false, callback));

        return buttons;
    }

    @Override
    public int size(Player player) {
        return 9 * 3;
    }
}