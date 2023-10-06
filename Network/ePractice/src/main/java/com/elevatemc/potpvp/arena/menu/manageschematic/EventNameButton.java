package com.elevatemc.potpvp.arena.menu.manageschematic;

import com.elevatemc.elib.menu.Button;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.arena.ArenaSchematic;
import com.elevatemc.potpvp.util.Color;
import com.google.common.collect.ImmutableList;
import org.bukkit.Material;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class EventNameButton extends Button {

    private final ArenaSchematic schematic;

    public EventNameButton(ArenaSchematic schematic) {
        this.schematic = schematic;
    }

    @Override
    public String getName(Player var1) {
        return Color.translate("&e&lEvent Name");
    }

    @Override
    public List<String> getDescription(Player var1) {
        return Color.translate(ImmutableList.of(
                "&eCurrent: &b" + schematic.getEventName(),
                "",
                "&7If this arena is meant to be used for an event",
                "&7set the event id here",
                "",
                "&a&lLEFT-CLICK &ato set the event name",
                "&c&lRIGHT-CLICK &cto remove the event name"
        ));
    }

    @Override
    public Material getMaterial(Player var1) {
        return Material.SIGN;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        if(clickType.isLeftClick()) {
            ConversationFactory factory = new ConversationFactory(PotPvPSI.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

                @Override
                public String getPromptText(ConversationContext conversationContext) {
                    return Color.translate("&ePlease type the name of the event, or type &c\"cancel\" &eto cancel.");
                }

                @Override
                public Prompt acceptInput(ConversationContext cc, String name) {
                    if(name.equalsIgnoreCase("cancel")) {
                        return Prompt.END_OF_CONVERSATION;
                    }

                    schematic.setEventName(name);
                    cc.getForWhom().sendRawMessage(Color.translate("&aSet the event name of " + schematic.getName() + " to " + schematic.getEventName()));

                    (new BukkitRunnable() {
                        @Override
                        public void run() {
                            new ManageSchematicMenu(schematic).openMenu(player);
                        }
                    }).runTask(PotPvPSI.getInstance());

                    return Prompt.END_OF_CONVERSATION;
                }
            }).withLocalEcho(false).withEscapeSequence("/cancel").withTimeout(60).thatExcludesNonPlayersWithMessage("Player's only.");

            Conversation conversation = factory.buildConversation(player);
            player.beginConversation(conversation);
        }else if(clickType.isRightClick()) {
            player.closeInventory();
            player.sendMessage(Color.translate("&aRemoved the event name."));
            schematic.setEventName(null);
        }
    }
}
