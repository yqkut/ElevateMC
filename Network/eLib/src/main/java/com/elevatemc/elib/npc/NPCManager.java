package com.elevatemc.elib.npc;

import com.elevatemc.elib.eLib;
import com.elevatemc.elib.npc.command.NPCCommand;
import com.elevatemc.elib.npc.command.NPCContextResolver;
import com.elevatemc.elib.npc.entry.NPCEntry;
import com.elevatemc.elib.npc.entry.NPCEntryMap;
import com.elevatemc.elib.util.TaskUtil;
import com.elevatemc.elib.util.json.GsonProvider;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NPCManager {

    @Getter
    private static NPCManager instance;

    public NPCManager() {
        instance = this;
        TaskUtil.scheduleAtFixedRateOnPool(new NPCUpdateArrowTask(), 45, 45, TimeUnit.SECONDS);
        registerAllNPCs();
        eLib.getInstance().getCommandHandler().registerParameterType(NPC.class, new NPCContextResolver());
        eLib.getInstance().getCommandHandler().registerClass(NPCCommand.class);

    }

    public void registerAllNPCs() {
        eLib.getInstance().getLogger().info("Registering all NPCs...");
        try {
            String json = new String(Files.readAllBytes(this.getNPCFilePath()));
            NPCEntryMap npcEntryMap = GsonProvider.fromJson(json, NPCEntryMap.class);
            npcEntryMap.getAllEntries().forEach(this::registerNPC);
            eLib.getInstance().getLogger().info(String.format("Registered %d NPCs", npcEntryMap.getAllEntries().size()));
        } catch (NoSuchFileException e) {
            eLib.getInstance().getLogger().warning("Couldn't find npcs.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveAllNPCs() {
        eLib.getInstance().getLogger().info("Saving all NPCs...");
        try {
            List<NPCEntry> npcList = new ArrayList<>();
            eLib.getInstance().getFakeEntityHandler().getEntities().iterator().forEachRemaining(entity -> {
                if (entity instanceof NPC) {
                    npcList.add(((NPC) entity).toEntry());
                }
            });

            NPCEntryMap entryMap = new NPCEntryMap();
            entryMap.putAllEntries(npcList);
            Files.write(this.getNPCFilePath(), GsonProvider.toJsonPretty(entryMap).getBytes());
            eLib.getInstance().getLogger().info(String.format("Saved %d NPCs.", entryMap.getAllEntries().size()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registerNPC(NPCEntry npcEntry) {
        eLib.getInstance().getLogger().info(String.format("Registering NPC %d @%s", npcEntry.getId(), npcEntry.getLocation()));
        NPC npc = new NPC(npcEntry);
        npc.setCommand(npcEntry.getCommand());
        eLib.getInstance().getFakeEntityHandler().registerFakeEntity(npc);
        npc.showToAll();
        for (Player target : Bukkit.getOnlinePlayers()) {
            npc.addToTeam(target);
        }
    }

    private Path getNPCFilePath() {
        return new File(eLib.getInstance().getDataFolder(), "npcs.json").toPath();
    }
}
