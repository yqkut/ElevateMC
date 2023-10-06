package com.elevatemc.potpvp.kit.menu.editkit;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.kit.menu.kits.KitsMenu;
import com.elevatemc.potpvp.util.InventoryUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.elevatemc.potpvp.kit.Kit;
import com.elevatemc.elib.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

final class KitInfoButton extends Button {

    private final Kit kit;

    KitInfoButton(Kit kit) {
        this.kit = Preconditions.checkNotNull(kit, "kit");
    }

    @Override
    public String getName(Player player) {
        return ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Editing " + kit.getName();
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
            ChatColor.AQUA + "Click this to rename this kit"
        );
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        ConversationFactory factory = new ConversationFactory(PotPvPSI.getInstance()).withFirstPrompt(new StringPrompt() {

            @Override
            public String getPromptText(ConversationContext context) {
                return ChatColor.DARK_AQUA + "✎ " + ChatColor.AQUA + "Enter the new name...";
            }

            @Override
            public Prompt acceptInput(ConversationContext ctx, String s) {
                if (s.length() > 20) {
                    ctx.getForWhom().sendRawMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "Kit names can't have more than 20 characters!");
                    return Prompt.END_OF_CONVERSATION;
                }

                kit.setName(s);

                PotPvPSI.getInstance().getKitHandler().saveKitsAsync(player);

                ctx.getForWhom().sendRawMessage(ChatColor.DARK_GREEN.toString() + ChatColor.BOLD + "✔ " + ChatColor.GREEN + "Your kit has been renamed!");
                if (!PotPvPSI.getInstance().getMatchHandler().isPlayingMatch(player)) {
                    new EditKitMenu(kit).openMenu(player);
                }
                return Prompt.END_OF_CONVERSATION;
            }

        }).withLocalEcho(false);

        // Go back to spawn modus
        kit.setInventoryContents(player.getInventory().getContents());
        PotPvPSI.getInstance().getKitHandler().saveKitsAsync(player);

        player.setItemOnCursor(new ItemStack(Material.AIR));

        player.closeInventory();
        InventoryUtils.resetInventoryDelayed(player);

        player.closeInventory();
        player.beginConversation(factory.buildConversation(player));
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.NAME_TAG;
    }

}