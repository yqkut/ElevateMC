package com.elevatemc.elib.npc.command;

import com.elevatemc.elib.eLib;
import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.param.Parameter;
import com.elevatemc.elib.npc.NPC;
import com.elevatemc.elib.npc.NPCManager;
import com.elevatemc.elib.skin.MojangSkin;
import com.elevatemc.elib.util.UUIDFetcher;
import com.elevatemc.elib.npc.entry.NPCEntry;
import com.elevatemc.elib.util.message.MessageBuilder;
import com.elevatemc.elib.util.message.MessageColor;
import com.elevatemc.elib.util.message.MessageTranslator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


import java.util.ArrayList;
import java.util.UUID;

public class NPCCommand {


    @Command(names = {"npc create"}, permission = "core.command.npc", async = true, description = "Create an npc")
    public static void npcCreateCommand(Player player,
                                        @Parameter(name = "id") int id,
                                        @Parameter(name = "Display Name", wildcard = true) String displayName) {
        if (displayName.length() > 16) {
            player.sendMessage(MessageColor.RED + "That name is too long!");
            return;
        }

        if (eLib.getInstance().getFakeEntityHandler().getEntityById(id) != null) {
            player.sendMessage(MessageColor.RED + "There is already a fake entity with that id registered");
            return;
        }

        NPCEntry npcEntry = new NPCEntry(id, MessageTranslator.translate(displayName),
                null, player.getLocation(), new ItemStack[5], null, new ArrayList<>(), false);
        NPCManager.getInstance().registerNPC(npcEntry);

        String message = MessageBuilder.standard("You have successfully created an npc named {} with the id {}.")
                .element(displayName)
                .element(id)
                .build();
        player.sendMessage(message);
    }


    @Command(names = {"npc remove"}, permission = "core.command.npc", async = true, description = "Remove / Delete an npc")
    public static void npcRemoveCommand(Player player, @Parameter(name = "id") NPC npc) {
        eLib.getInstance().getFakeEntityHandler().removeFakeEntity(npc.getEntityId());
        npc.hideFromAll();
        player.sendMessage(MessageBuilder.construct("You have successfully removed the npc with the id {}.", npc.getId()));
    }


    @Command(names = {"npc sethelditem"}, permission = "core.command.npc", async = true, description = "Set the held item of the npc")
    public static void npcSetHeldItemCommand(Player player, @Parameter(name = "id")  NPC npc) {
        npc.setEquipment(0, player.getItemInHand());
        player.sendMessage(MessageBuilder.construct("NPC {} is now holding a {}.", npc.getId(), player.getItemInHand()));
    }


    @Command(names = {"npc sethelmet"}, permission = "core.command.npc", async = true, description = "Set the helmet item of the npc")
    public static void setHelmetCommand(Player player,  @Parameter(name = "id") NPC npc) {
        npc.setEquipment(4, player.getItemInHand());
        player.sendMessage(MessageBuilder.construct("NPC {} is now wearing a {}.", npc.getId(), player.getItemInHand()));
    }


    @Command(names = {"npc setchestplate"}, permission = "core.command.npc", async = true, description = "Set the chestplate item of the npc")
    public static void setChestplateCommand(Player player,  @Parameter(name = "id") NPC npc) {
        npc.setEquipment(3, player.getItemInHand());
        player.sendMessage(MessageBuilder.construct("NPC {} is now wearing a {}.", npc.getId(), player.getItemInHand()));
    }


    @Command(names = {"npc setleggings"}, permission = "core.command.npc", async = true, description = "Set the leggings item of the npc")
    public static void setLegginsCommand(Player player,  @Parameter(name = "id") NPC npc) {
        npc.setEquipment(2, player.getItemInHand());
        player.sendMessage(MessageBuilder.construct("NPC {} is now wearing a {}.", npc.getId(), player.getItemInHand()));
    }


    @Command(names = {"npc setboots"}, permission = "core.command.npc", async = true, description = "Set the boots item of the npc")
    public static void setBootsCommand(Player player,  @Parameter(name = "id") NPC npc) {
        npc.setEquipment(1, player.getItemInHand());
        player.sendMessage(MessageBuilder.construct("NPC {} is now wearing a {}.", npc.getId(), player.getItemInHand()));
    }


    @Command(names = {"npc setcommand"}, permission = "core.command.npc", async = true, description = "Setup the command executed when interacted with the npc")
    public static void npcSetCommand(Player player, @Parameter(name = "id") NPC npc, @Parameter(name = "Command", wildcard = true) String command) {
        npc.setCommand(command);
        player.sendMessage(MessageBuilder.construct("You have updated the command of npc {}.", npc.getId()));
    }


    @Command(names = {"npc setskin"}, permission = "core.command.npc", async = true, description = "Update npcs skin")
    public static void npcSetSkinCommand(Player player, @Parameter(name = "id")  NPC npc, @Parameter(name = "Skin Name") String skinName) {
        UUID uuid = UUIDFetcher.getUUID(skinName);
        player.sendMessage(MessageBuilder.construct("Attempting to fetch skin with name {}.", skinName));
        MojangSkin skin = eLib.getInstance().getMojangSkinHandler().getMojangSkin(uuid);

        if (skin == null) {
            player.sendMessage(MessageBuilder.construct("Failed to find a skin with the name {}.", skinName));
            return;
        }

        npc.setMojangSkin(skin);
        npc.showToAll();
        player.sendMessage(MessageBuilder.construct("Successfully set npc {} skin to {}.", npc.getId(), skinName));
    }

    @Command(names = "npc teleport", permission = "core.command.npc", async = true, description = "Teleport an npc to you")
    public static void npcTeleportCommand(Player player, @Parameter(name = "id")  NPC npc) {
        npc.teleport(player.getLocation());
        player.sendMessage(MessageBuilder.construct("You have teleported npc {} to your location.", npc.getId()));
    }


    @Command(names = "npc addline", permission = "core.command.npc", async = true, description = "Add a hologram line to an npc")
    public static void npcAddLineCommand(Player player, @Parameter(name = "id")  NPC npc, @Parameter(name = "Line", wildcard = true) String line) {
        npc.addLine(line);
        player.sendMessage(MessageBuilder.construct("You have added a hologram line to npc {}.", npc.getId()));
    }


    @Command(names = "npc removeline", permission = "core.command.npc", async = true, description = "Remove a hologram line from an npc")
    public static void npcRemoveLineCommand(Player player, @Parameter(name = "id")  NPC npc, @Parameter(name = "index") int index) {
        npc.removeLine(index);
        player.sendMessage(MessageBuilder.construct("You have removed a hologram line from npc {}.", npc.getId()));
    }


    @Command(names = "npc sit", permission = "core.command.npc", async = true, description = "Make an npc sitdown or standup")
    public static void sitCommand(Player player, @Parameter(name = "id")  NPC npc) {
        npc.setSitting(!npc.isSitting());
        player.sendMessage(MessageBuilder.construct("Successfully {} this npc.", (npc.isSitting() ? "sat" : "stood")));
    }
}
