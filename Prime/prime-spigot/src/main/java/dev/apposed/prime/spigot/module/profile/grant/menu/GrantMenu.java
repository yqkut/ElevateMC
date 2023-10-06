package dev.apposed.prime.spigot.module.profile.grant.menu;

import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.menu.Menu;
import com.google.common.collect.ImmutableList;
import dev.apposed.prime.spigot.Prime;
import dev.apposed.prime.spigot.module.profile.Profile;
import dev.apposed.prime.spigot.module.profile.ProfileHandler;
import dev.apposed.prime.spigot.module.rank.Rank;
import dev.apposed.prime.spigot.module.rank.RankHandler;
import dev.apposed.prime.spigot.module.rank.meta.RankMeta;
import dev.apposed.prime.spigot.util.Color;
import dev.apposed.prime.spigot.util.time.DurationUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GrantMenu extends Menu {

    private final Prime plugin = Prime.getInstance();
    private final RankHandler rankHandler = plugin.getModuleHandler().getModule(RankHandler.class);
    private final ProfileHandler profileHandler = plugin.getModuleHandler().getModule(ProfileHandler.class);

    private final Profile profile;

    public GrantMenu(Profile profile) {
        this.profile = profile;
    }

    @Override
    public String getTitle(Player player) {
        return "Choose a Rank";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();

        final AtomicInteger slot = new AtomicInteger(0);
        this.rankHandler.getCache()
                .stream()
                .sorted(Comparator.comparingInt(Rank::getWeight).reversed())
                .filter(rank -> !rank.hasMeta(RankMeta.DEFAULT, true))
                .filter(rank -> player.hasPermission("prime.grant.create." + rank.getName()))
                .forEach(rank -> buttons.put(slot.getAndIncrement(), new RankButton(rank)));

        return buttons;
    }

    private final class RankButton extends Button {

        private final Rank rank;

        public RankButton(Rank rank) {
            this.rank = rank;
        }

        @Override
        public String getName(Player player) {
            return Color.translate(rank.getColoredDisplay());
        }

        @Override
        public List<String> getDescription(Player player) {
            return Color.translate(ImmutableList.of(
                    Color.SPACER_LONG,
                    "&7Click to grant &r" + profile.getColoredName() + " &7the &r" + rank.getColoredDisplay() + "&7 rank.",
                    Color.SPACER_LONG
            ));
        }

        @Override
        public Material getMaterial(Player player) {
            return rank.getWool().getType();
        }

        @Override
        public byte getDamageValue(Player player) {
            return (byte) rank.getWool().getDurability();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            player.closeInventory();
            ConversationFactory factory = new ConversationFactory(plugin).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

                @Override
                public String getPromptText(ConversationContext conversationContext) {
                    return Color.translate("&ePlease type a duration for this grant, or type &c\"cancel\" &eto cancel.");
                }

                @Override
                public Prompt acceptInput(ConversationContext cc, String duration) {
                    if(duration.equalsIgnoreCase("cancel")) {
                        cc.getForWhom().sendRawMessage(ChatColor.RED + "Granting cancelled.");
                        return END_OF_CONVERSATION;
                    }

                    (new BukkitRunnable() {
                        @Override
                        public void run() {
                            GrantMenu.this.executeReason(player, rank, DurationUtils.fromString(duration));
                        }
                    }).runTask(plugin);
                    return END_OF_CONVERSATION;
                }
            }).withLocalEcho(false).withEscapeSequence("/cancel").withTimeout(60).thatExcludesNonPlayersWithMessage("Player's only.");

            Conversation conversation = factory.buildConversation(player);
            player.beginConversation(conversation);
        }
    }

    public void executeReason(Player player, Rank rank, long duration) {
        ConversationFactory factory = new ConversationFactory(plugin).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

            @Override
            public String getPromptText(ConversationContext conversationContext) {
                return Color.translate("&ePlease type a reason for this grant to be created, or type &c\"cancel\" &eto cancel.");
            }

            @Override
            public Prompt acceptInput(ConversationContext cc, String reason) {
                if(reason.equalsIgnoreCase("cancel")) {
                    cc.getForWhom().sendRawMessage(ChatColor.RED + "Granting cancelled.");
                    return END_OF_CONVERSATION;
                }

                (new BukkitRunnable(){
                    @Override
                    public void run() {
                        new ScopesMenu(profile, rank, duration, reason).openMenu(player);
                    }
                }).runTask(plugin);

                return Prompt.END_OF_CONVERSATION;
            }
        }).withLocalEcho(false).withEscapeSequence("/cancel").withTimeout(60).thatExcludesNonPlayersWithMessage("Player's only.");

        Conversation conversation = factory.buildConversation(player);
        player.beginConversation(conversation);
    }
}
