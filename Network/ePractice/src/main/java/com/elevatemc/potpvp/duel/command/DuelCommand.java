package com.elevatemc.potpvp.duel.command;

import com.elevatemc.potpvp.PotPvPLang;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.arena.menu.select.SelectArenaMenu;
import com.elevatemc.potpvp.arena.menu.select.teamfight.SelectArenaTeamfightCategoryMenu;
import com.elevatemc.potpvp.duel.DuelHandler;
import com.elevatemc.potpvp.duel.DuelInvite;
import com.elevatemc.potpvp.duel.PartyDuelInvite;
import com.elevatemc.potpvp.duel.PlayerDuelInvite;
import com.elevatemc.potpvp.gamemode.GameMode;
import com.elevatemc.potpvp.gamemode.GameModes;
import com.elevatemc.potpvp.gamemode.menu.select.SelectGameModeMenu;
import com.elevatemc.potpvp.lobby.LobbyHandler;
import com.elevatemc.potpvp.party.Party;
import com.elevatemc.potpvp.party.PartyHandler;
import com.elevatemc.potpvp.validation.PotPvPValidation;
import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.param.Parameter;
import com.elevatemc.elib.util.PlayerUtils;
import com.elevatemc.elib.util.UUIDUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class DuelCommand {

    @Command(names = {"duel"}, permission = "")
    public static void duel(Player sender, @Parameter(name = "player") Player target) {
        if (sender == target) {
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You cannot duel yourself.");
            return;
        }

        PartyHandler partyHandler = PotPvPSI.getInstance().getPartyHandler();
        LobbyHandler lobbyHandler = PotPvPSI.getInstance().getLobbyHandler();

        Party senderParty = partyHandler.getParty(sender);
        Party targetParty = partyHandler.getParty(target);

        if (senderParty != null && targetParty != null) {
            // party dueling party (legal)
            if (!PotPvPValidation.canSendDuel(senderParty, targetParty, sender)) {
                return;
            }

            new SelectGameModeMenu(gameMode -> {
                if (gameMode.equals(GameModes.TEAMFIGHT) || gameMode.equals(GameModes.TEAMFIGHT_DEBUFF)) {
                    new SelectArenaTeamfightCategoryMenu(category -> {
                        new SelectArenaMenu(gameMode, category, arenaName -> {
                            sender.closeInventory();

                            // reassign these fields so that any party changes
                            // (kicks, etc) are reflectednow
                            Party newSenderParty = partyHandler.getParty(sender);
                            Party newTargetParty = partyHandler.getParty(target);

                            if (newSenderParty != null && newTargetParty != null) {
                                if (newSenderParty.isLeader(sender.getUniqueId())) {
                                    duel(sender, newSenderParty, newTargetParty, gameMode, arenaName);
                                } else {
                                    sender.sendMessage(PotPvPLang.NOT_LEADER_OF_PARTY);
                                }
                            }
                        }).openMenu(sender);
                    }).openMenu(sender);
                } else {
                    new SelectArenaMenu(gameMode, arenaName -> {
                        sender.closeInventory();

                        // reassign these fields so that any party changes
                        // (kicks, etc) are reflectednow
                        Party newSenderParty = partyHandler.getParty(sender);
                        Party newTargetParty = partyHandler.getParty(target);

                        if (newSenderParty != null && newTargetParty != null) {
                            if (newSenderParty.isLeader(sender.getUniqueId())) {
                                duel(sender, newSenderParty, newTargetParty, gameMode, arenaName);
                            } else {
                                sender.sendMessage(PotPvPLang.NOT_LEADER_OF_PARTY);
                            }
                        }
                    }).openMenu(sender);
                }

            }, "Select gamemode").openMenu(sender);
        } else if (senderParty == null && targetParty == null) {
            // player dueling player (legal)
            if (!PotPvPValidation.canSendDuel(sender, target)) {
                return;
            }

            if (target.hasPermission("core.media") && System.currentTimeMillis() - lobbyHandler.getLastLobbyTime(target) < 3_000) {
                sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "Please wait a few moments before executing this command again.");
                return;
            }

            new SelectGameModeMenu(gameMode -> {
                if (gameMode.equals(GameModes.TEAMFIGHT) || gameMode.equals(GameModes.TEAMFIGHT_DEBUFF)) {
                    new SelectArenaTeamfightCategoryMenu(category -> {
                        new SelectArenaMenu(gameMode, category, arenaName -> {
                            sender.closeInventory();
                            duel(sender, target, gameMode, arenaName);
                        }).openMenu(sender);
                    }).openMenu(sender);
                } else {
                    new SelectArenaMenu(gameMode, arenaName -> {
                        sender.closeInventory();
                        duel(sender, target, gameMode, arenaName);
                    }).openMenu(sender);
                }
            }, "Select gamemode").openMenu(sender);
        } else if (senderParty == null) {
            // player dueling party (illegal)
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You must create a party to duel " + target.getName() + "'s party.");
        } else {
            // party dueling player (illegal)
            sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You must leave your party to duel " + target.getName() + ".");
        }
    }

    public static void duel(Player sender, Player target, GameMode gameMode, String arenaName) {
        if (!PotPvPValidation.canSendDuel(sender, target)) {
            return;
        }

        DuelHandler duelHandler = PotPvPSI.getInstance().getDuelHandler();
        DuelInvite autoAcceptInvite = duelHandler.findInvite(target, sender);

        // if two players duel each other for the same thing automatically
        // accept it to make their life a bit easier.
        if (autoAcceptInvite != null && autoAcceptInvite.getGameMode() == gameMode && autoAcceptInvite.getArenaName() == arenaName) {
            AcceptCommand.accept(sender, target);
            return;
        }

        DuelInvite alreadySentInvite = duelHandler.findInvite(sender, target);

        if (alreadySentInvite != null) {
            if (alreadySentInvite.getGameMode() == gameMode) {
                sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You have already invited " + ChatColor.RED + target.getName() + ChatColor.RED + " to a " + gameMode.getName() + ChatColor.RED + " duel.");
                return;
            } else {
                // if an invite was already sent (with a different kit type)
                // just delete it (so /accept will accept the 'latest' invite)
                duelHandler.removeInvite(alreadySentInvite);
            }
        }

        target.sendMessage(ChatColor.DARK_AQUA + "⚔ " + sender.getName()  + ChatColor.GRAY + " (" + PlayerUtils.getPing(sender) + "ms)" + ChatColor.AQUA + " has sent you a " + ChatColor.DARK_AQUA + gameMode.getName() + (arenaName != null ? (" (" + PotPvPSI.getInstance().getArenaHandler().getSchematic(arenaName).getDisplayName() + ")") : "") + ChatColor.AQUA + " duel.");
        target.spigot().sendMessage(createInviteNotification(sender.getName()));
        sender.sendMessage(ChatColor.DARK_AQUA + "⚔ " + ChatColor.AQUA + "You have sent a " + ChatColor.DARK_AQUA + gameMode.getName() + ChatColor.AQUA + " duel invite to " + ChatColor.DARK_AQUA + target.getName() + ChatColor.AQUA + ".");
        duelHandler.insertInvite(new PlayerDuelInvite(sender, target, gameMode, arenaName));
    }

    public static void duel(Player sender, Party senderParty, Party targetParty, GameMode gameMode, String arenaName) {
        if (!PotPvPValidation.canSendDuel(senderParty, targetParty, sender)) {
            return;
        }

        DuelHandler duelHandler = PotPvPSI.getInstance().getDuelHandler();
        DuelInvite autoAcceptInvite = duelHandler.findInvite(targetParty, senderParty);
        String targetPartyLeader = UUIDUtils.name(targetParty.getLeader());

        // if two players duel each other for the same thing automatically
        // accept it to make their life a bit easier.
        if (autoAcceptInvite != null && autoAcceptInvite.getGameMode() == gameMode && autoAcceptInvite.getArenaName().equals(arenaName)) {
            AcceptCommand.accept(sender, Bukkit.getPlayer(targetParty.getLeader()));
            return;
        }

        DuelInvite alreadySentInvite = duelHandler.findInvite(senderParty, targetParty);

        if (alreadySentInvite != null) {
            if (alreadySentInvite.getGameMode() == gameMode) {
                sender.sendMessage(ChatColor.DARK_RED + "✖ " + ChatColor.RED + "You have already invited " + ChatColor.DARK_AQUA + targetPartyLeader + "'s party" + ChatColor.YELLOW + " to a " + ChatColor.DARK_AQUA + gameMode.getName() + ChatColor.AQUA + " duel.");
                return;
            } else {
                // if an invite was already sent (with a different kit type)
                // just delete it (so /accept will accept the 'latest' invite)
                duelHandler.removeInvite(alreadySentInvite);
            }
        }

        targetParty.message(ChatColor.DARK_AQUA + "⚔ " + sender.getName() + "'s Party (" + senderParty.getMembers().size() + ")" + ChatColor.AQUA + " has sent you a " + ChatColor.DARK_AQUA + gameMode.getName() + (arenaName != null ? (" (" + PotPvPSI.getInstance().getArenaHandler().getSchematic(arenaName).getDisplayName() + ")") : "") + ChatColor.AQUA + " duel.");
        Bukkit.getPlayer(targetParty.getLeader()).spigot().sendMessage(createInviteNotification(sender.getName()));

        sender.sendMessage(ChatColor.DARK_AQUA + "⚔ " + ChatColor.AQUA + "You have sent a " + ChatColor.DARK_AQUA + gameMode.getName() + ChatColor.AQUA + " duel invite to " + ChatColor.DARK_AQUA + targetPartyLeader + "'s party" + ChatColor.AQUA + ".");
        duelHandler.insertInvite(new PartyDuelInvite(senderParty, targetParty, gameMode, arenaName));
    }

    private static TextComponent[] createInviteNotification(String sender) {
        TextComponent firstPart = new TextComponent("[Click Here To Accept]");

        firstPart.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);

        ClickEvent.Action runCommand = ClickEvent.Action.RUN_COMMAND;
        HoverEvent.Action showText = HoverEvent.Action.SHOW_TEXT;

        firstPart.setClickEvent(new ClickEvent(runCommand, "/accept " + sender));
        firstPart.setHoverEvent(new HoverEvent(showText, new BaseComponent[] { new TextComponent(ChatColor.GREEN + "Click here to accept this duel") }));

        return new TextComponent[] { firstPart };
    }

}