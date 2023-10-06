package com.elevatemc.elib.nametag;

import com.elevatemc.elib.eLib;
import com.elevatemc.elib.nametag.construct.NameTagComparator;
import com.elevatemc.elib.nametag.construct.NameTagInfo;
import com.elevatemc.elib.nametag.construct.NameTagUpdate;
import com.elevatemc.elib.nametag.listener.NameTagListener;
import com.elevatemc.elib.nametag.provider.DefaultNameTagProvider;
import com.elevatemc.elib.nametag.provider.NameTagProvider;
import com.elevatemc.elib.packet.ScoreboardTeamPacketMod;
import com.elevatemc.elib.util.PlayerUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class NameTagHandler {

    @Getter private Map<String, Map<String, NameTagInfo>> teamMap = new ConcurrentHashMap<>();
    @Getter private List<NameTagInfo> registeredTeams = Collections.synchronizedList(new ArrayList<>());
    @Getter private int teamCreateIndex = 1;
    @Getter private List<NameTagProvider> providers = new ArrayList<>();
    @Getter private boolean nametagRestrictionEnabled = false;
    @Getter private String nametagRestrictBypass = "";
    @Getter @Setter private boolean async = true;
    @Getter @Setter private int updateInterval = 2;
    @Getter @Setter private NameTagInfo INVISIBLE;

    public NameTagHandler() {

        if (eLib.getInstance().getConfig().getBoolean("disableNametags",false)) {
            return;
        }

        this.nametagRestrictionEnabled = eLib.getInstance().getConfig().getBoolean("NametagPacketRestriction.Enabled", false);
        this.nametagRestrictBypass = eLib.getInstance().getConfig().getString("NametagPacketRestriction.BypassPrefix").replace("&", "§");

        eLib.getInstance().getServer().getPluginManager().registerEvents(new NameTagListener(), eLib.getInstance());
        this.registerProvider(new DefaultNameTagProvider());

        eLib.getInstance().getServer().getScheduler().runTaskLater(eLib.getInstance(), () -> {
            setINVISIBLE(eLib.getInstance().getNameTagHandler().getOrCreate("aaa", "aaa"));
        }, 10);

        new NameTagThread().start();
    }

    public void registerProvider(NameTagProvider newProvider) {
        this.providers.add(newProvider);
        Collections.sort(this.providers,new NameTagComparator());
    }

    public void reloadPlayer(Player toRefresh) {

        final NameTagUpdate update = new NameTagUpdate(toRefresh);

        if (this.async) {
            NameTagThread.getPendingUpdates().put(update, true);
        } else {
            this.applyUpdate(update);
        }

    }

    public void reloadOthersFor(Player refreshFor) {

        for (Player toRefresh : eLib.getInstance().getServer().getOnlinePlayers()) {

            if (refreshFor != toRefresh) {
                this.reloadPlayer(toRefresh, refreshFor);
            }

        }

    }

    public void reloadPlayer(Player toRefresh, Player refreshFor) {

        final NameTagUpdate update = new NameTagUpdate(toRefresh, refreshFor);

        if (this.async) {
            NameTagThread.getPendingUpdates().put(update, true);
        } else {
            this.applyUpdate(update);
        }

    }

    public void applyUpdate(NameTagUpdate nametagUpdate) {

        final Player toRefreshPlayer = eLib.getInstance().getServer().getPlayerExact(nametagUpdate.getToRefresh());

        if (toRefreshPlayer != null) {

            if (nametagUpdate.getRefreshFor() == null) {

                for (Player refreshFor : eLib.getInstance().getServer().getOnlinePlayers()) {
                    reloadPlayerInternal(toRefreshPlayer,refreshFor);
                }

            } else {

                final Player refreshForPlayer = eLib.getInstance().getServer().getPlayerExact(nametagUpdate.getRefreshFor());

                if (refreshForPlayer != null) {
                    reloadPlayerInternal(toRefreshPlayer,refreshForPlayer);
                }
            }
        }
    }

    public void reloadPlayerInternal(Player toRefresh, Player refreshFor) {
        if (refreshFor.hasMetadata("eLibNametag-LoggedIn")) {
            
            NameTagInfo provided = null;

            for (int i = 0; provided == null; provided = (this.providers.get(i++).fetchNameTag(toRefresh,refreshFor))) {
                
            }

            if (provided == INVISIBLE || provided.getPrefix().equalsIgnoreCase("aaa") && provided.getSuffix().equalsIgnoreCase("aaa")) {
                if (PlayerUtils.getProtocol(refreshFor) > 5) {
                    final Map<String, NameTagInfo> localTeamMap = teamMap.get(refreshFor.getName());

                    if (localTeamMap == null) {
                        return;
                    }
                    
                    final NameTagInfo nameTagInfo = teamMap.get(refreshFor.getName()).get(toRefresh.getName());

                    if (nameTagInfo == null) {
                        return;
                    }

                    new ScoreboardTeamPacketMod(nameTagInfo.getName(), Collections.singletonList(toRefresh.getName()), 4).sendToPlayer(refreshFor);
                }
                return;
            }

            if (PlayerUtils.getProtocol(refreshFor) > 5 && this.nametagRestrictionEnabled) {
                
                final String prefix = provided.getPrefix();
              
                if (prefix != null && !prefix.equalsIgnoreCase(this.nametagRestrictBypass)) {
                    return;
                }
            }

            Map<String,NameTagInfo> teamInfoMap = new HashMap();
            
            if (this.teamMap.containsKey(refreshFor.getName())) {
                teamInfoMap = this.teamMap.get(refreshFor.getName());
            }

            new ScoreboardTeamPacketMod(provided.getName(), Collections.singletonList(toRefresh.getName()), 3).sendToPlayer(refreshFor);
            
            teamInfoMap.put(toRefresh.getName(),provided);

            this.teamMap.put(refreshFor.getName(), teamInfoMap);
        }
    }

    public void initiatePlayer(Player player) {

        for (NameTagInfo teamInfo : this.registeredTeams) {
            teamInfo.getTeamAddPacket().sendToPlayer(player);
        }

    }

    public NameTagInfo getOrCreate(String prefix,String suffix) {

        for (NameTagInfo teamInfo : this.registeredTeams) {

            if (teamInfo.getPrefix().equals(prefix) && teamInfo.getSuffix().equals(suffix)) {
                return teamInfo;
            }

        }

        final NameTagInfo newTeam = new NameTagInfo(String.valueOf(this.teamCreateIndex++), prefix, suffix);

        this.registeredTeams.add(newTeam);

        final ScoreboardTeamPacketMod addPacket = newTeam.getTeamAddPacket();

        for (Player player : eLib.getInstance().getServer().getOnlinePlayers()) {
            addPacket.sendToPlayer(player);
        }

        return newTeam;
    }

}