package com.elevatemc.elib.combatlogger;

import com.elevatemc.elib.eLib;
import com.elevatemc.elib.combatlogger.listener.CombatLoggerListener;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatLoggerHandler {

    @Getter private final Map<UUID,CombatLogger> combatLoggerMap = new HashMap<>();

    public CombatLoggerHandler() {
        eLib.getInstance().getServer().getPluginManager().registerEvents(new CombatLoggerListener(), eLib.getInstance().getInstance());
    }

}
