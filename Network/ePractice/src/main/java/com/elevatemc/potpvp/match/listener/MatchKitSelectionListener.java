package com.elevatemc.potpvp.match.listener;

import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.gamemode.GameModes;
import com.elevatemc.potpvp.gamemode.kit.GameModeKit;
import com.elevatemc.potpvp.kit.Kit;
import com.elevatemc.potpvp.kit.KitHandler;
import com.elevatemc.potpvp.gamemode.GameMode;
import com.elevatemc.potpvp.match.Match;
import com.elevatemc.potpvp.match.MatchHandler;
import com.elevatemc.potpvp.match.MatchTeam;
import com.elevatemc.potpvp.match.event.MatchCountdownStartEvent;
import com.elevatemc.potpvp.party.Party;
import com.elevatemc.potpvp.pvpclasses.PvPClasses;
import com.elevatemc.potpvp.hctranked.game.RankedGame;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class MatchKitSelectionListener implements Listener {

    public static Map<UUID, Kit> appliedKits = new HashMap<>();

    /**
     * Give players their kits when their match countdown starts
     */
    @EventHandler
    public void onMatchCountdownStart(MatchCountdownStartEvent event) {
        KitHandler kitHandler = PotPvPSI.getInstance().getKitHandler();
        Match match = event.getMatch();
        GameMode gameMode = match.getGameMode();

        if (gameMode.equals(GameModes.SUMO)) return; // no kits for sumo
        if (gameMode.equals(GameModes.SOTW)) return; // no kits for SOTW - it is randomised

        for (Player player : Bukkit.getOnlinePlayers()) {
            MatchTeam team = match.getTeam(player.getUniqueId());

            if (team == null) {
                continue;
            }

            List<Kit> customKits = null;
            ItemStack defaultKitItem = null;

            if (gameMode.equals(GameModes.TEAMFIGHT) || gameMode.equals(GameModes.TEAMFIGHT_DEBUFF)) {
                String kitPrefix = "";
                if (gameMode.equals(GameModes.TEAMFIGHT_DEBUFF)) {
                    kitPrefix = "DEBUFF_";
                }
                GameModeKit diamond = GameModeKit.byId(kitPrefix + "DIAMOND_HCF");
                GameModeKit bard = GameModeKit.byId(kitPrefix + "BARD_HCF");
                GameModeKit archer = GameModeKit.byId(kitPrefix + "ARCHER_HCF");
                GameModeKit rogue = GameModeKit.byId(kitPrefix + "ROGUE_HCF");

                Party party = PotPvPSI.getInstance().getPartyHandler().getParty(player);
                RankedGame game = PotPvPSI.getInstance().getHCTRankedHandler().getGameHandler().getJoinedGame(player);

                if (party == null && game == null) {
                    customKits = kitHandler.getKits(player, diamond);
                    defaultKitItem = Kit.ofDefaultKit(diamond).createSelectionItem();
                    applyPlayerSelectItems(player, customKits, defaultKitItem);
                } else {
                    PvPClasses kit;
                    if (party != null) {
                        kit = party.getKits().getOrDefault(player.getUniqueId(), PvPClasses.DIAMOND);
                    } else if (game != null) {
                        kit = game.getTeam(player).getKits().getOrDefault(player.getUniqueId(), PvPClasses.DIAMOND);
                    } else {
                        kit = PvPClasses.DIAMOND;
                    }


                    switch (kit) {
                        case DIAMOND:
                            customKits = kitHandler.getKits(player, diamond);
                            defaultKitItem = Kit.ofDefaultKit(diamond).createSelectionItem();
                            break;
                        case BARD:
                            customKits = kitHandler.getKits(player, bard);
                            defaultKitItem = Kit.ofDefaultKit(bard).createSelectionItem();
                            break;
                        case ARCHER:
                            customKits = kitHandler.getKits(player, archer);
                            defaultKitItem = Kit.ofDefaultKit(archer).createSelectionItem();
                            break;
                        case ROGUE:
                            customKits = kitHandler.getKits(player, rogue);
                            defaultKitItem = Kit.ofDefaultKit(archer).createSelectionItem();
                            break;
                    }


                }
            } else if (gameMode.equals(GameModes.TRAPPING)) {
                GameModeKit trapping = GameModeKit.byId("HCF_TRAP");
                GameModeKit runningIn = GameModeKit.byId("HCF_RUN");

                if (player.hasMetadata("TRAPPER")) {
                    customKits = kitHandler.getKits(player, trapping);
                    defaultKitItem = Kit.ofDefaultKit(trapping).createSelectionItem();
                } else {
                    customKits = kitHandler.getKits(player, runningIn);
                    defaultKitItem = Kit.ofDefaultKit(runningIn).createSelectionItem();
                }
            }

            if (customKits == null) {
                customKits = kitHandler.getKits(player, GameModeKit.byId(gameMode.getId()));
            }
            if (defaultKitItem == null) {
                defaultKitItem = Kit.ofDefaultKit(GameModeKit.byId(gameMode.getId())).createSelectionItem();
            }

            applyPlayerSelectItems(player, customKits, defaultKitItem);


            player.updateInventory();
        }
    }

    public void applyPlayerSelectItems(Player player, List<Kit> customKits, ItemStack defaultKitItem) {
        if (customKits.isEmpty()) {
            player.getInventory().setItem(0, defaultKitItem);
        } else {
            for (Kit customKit : customKits) {
                // subtract one to convert from 1-indexed kts to 0-indexed inventories
                player.getInventory().setItem(customKit.getSlot() - 1, customKit.createSelectionItem());
            }

            player.getInventory().setItem(8, defaultKitItem);
        }
    }

    /**
     * Don't let players drop their kit selection books via the Q key
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlaying(event.getPlayer());

        if (match == null) {
            return;
        }

        if (event.getItemDrop().getItemStack().getType().equals(Material.ENCHANTED_BOOK)) {
            event.setCancelled(true);
        }
    }

    /**
     * Don't let players drop their kit selection items via death
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlaying(event.getEntity());

        if (match == null) {
            return;
        }

        event.getDrops().removeIf(itemStack -> itemStack.getType().equals(Material.ENCHANTED_BOOK));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasItem() || !event.getAction().name().contains("RIGHT_")) {
            return;
        }

        MatchHandler matchHandler = PotPvPSI.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlaying(event.getPlayer());

        if (match == null) {
            return;
        }

        GameMode gameMode = match.getGameMode();
        if (gameMode.equals(GameModes.SUMO) || gameMode.equals(GameModes.SOTW)) {
            return;
        }

        boolean pearlsAllowed = PotPvPSI.getInstance().getArenaHandler().getSchematic(match.getArena().getSchematic()).isPearlsAllowed();

        KitHandler kitHandler = PotPvPSI.getInstance().getKitHandler();
        ItemStack clickedItem = event.getItem();
        GameModeKit gameModeKit = null;
        Player player = event.getPlayer();

        if (gameMode.equals(GameModes.TEAMFIGHT) || gameMode.equals(GameModes.TEAMFIGHT_DEBUFF)) {
            String kitId = "";

            Party party = PotPvPSI.getInstance().getPartyHandler().getParty(player);
            RankedGame game = PotPvPSI.getInstance().getHCTRankedHandler().getGameHandler().getJoinedGame(player);
            PvPClasses kit;

            if (party != null) {
                kit = party.getKits().getOrDefault(player.getUniqueId(), PvPClasses.DIAMOND);
            } else if (game != null) {
                kit = game.getTeam(player).getKits().getOrDefault(player.getUniqueId(), PvPClasses.DIAMOND);
            } else {
                kit = PvPClasses.DIAMOND;
            }

            switch (kit) {
                case DIAMOND:
                    kitId = "DIAMOND_HCF";
                    break;
                case BARD:
                    kitId = "BARD_HCF";
                    break;
                case ARCHER:
                    kitId = "ARCHER_HCF";
                    break;
                case ROGUE:
                    kitId = "ROGUE_HCF";
                    break;
            }
            if (gameMode.equals(GameModes.TEAMFIGHT_DEBUFF)) {
                kitId = "DEBUFF_" + kitId;
            }
            gameModeKit = GameModeKit.byId(kitId);
        } else if (gameMode.equals(GameModes.TRAPPING)) {
            GameModeKit trapping = GameModeKit.byId("HCF_TRAP");
            GameModeKit runningIn = GameModeKit.byId("HCF_RUN");

            if (player.hasMetadata("TRAPPER")) {
                gameModeKit = trapping;
            } else {
                gameModeKit = runningIn;
            }
        }

        if (gameModeKit == null) {
            gameModeKit = GameModeKit.byId(gameMode.getId());
        }

        for (Kit kit : kitHandler.getKits(player, gameModeKit)) {
            if (kit.isSelectionItem(clickedItem)) {
                kit.apply(player, !pearlsAllowed);
                if (gameMode.equals(GameModes.PEARL_FIGHT)) appliedKits.put(player.getUniqueId(), kit);
                return;
            }
        }

        Kit defaultKit = Kit.ofDefaultKit(gameModeKit);

        if (defaultKit.isSelectionItem(clickedItem)) {
            defaultKit.apply(player, !pearlsAllowed);
            if (gameMode.equals(GameModes.PEARL_FIGHT)) appliedKits.put(player.getUniqueId(), defaultKit);
        }
    }

}