package dev.apposed.prime.spigot.module.server;

import com.elevatemc.elib.util.Pair;
import com.google.common.collect.ImmutableMap;
import dev.apposed.prime.spigot.module.Module;
import dev.apposed.prime.spigot.module.server.task.ServerHeartbeatTask;
import lombok.Data;
import org.bukkit.ChatColor;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class ServerHandler extends Module {

    private Set<Server> servers;
    private Set<ServerGroup> serverGroups;

    private String currentName;
    private ServerGroup currentScope;
    private final Map<String, Pair<ChatColor, ChatColor>> styles = ImmutableMap.of(
            "Elevate", new Pair<>(ChatColor.DARK_AQUA, ChatColor.WHITE),
            "Gold", new Pair<>(ChatColor.GOLD, ChatColor.WHITE),
            "Purple", new Pair<>(ChatColor.DARK_PURPLE, ChatColor.WHITE),
            "Red", new Pair<>(ChatColor.RED, ChatColor.WHITE),
            "Green", new Pair<>(ChatColor.DARK_GREEN, ChatColor.WHITE)
    );

    @Override
    public void onEnable() {
        super.onEnable();

        this.servers = new HashSet<>();
        this.serverGroups = new HashSet<>();

        this.currentName = getPlugin().getConfig().getString("id");

        this.servers.add(new Server(
                this.currentName,
                getPlugin().getConfig().getString("group")));

        Optional<ServerGroup> activeGroup = this.getGroupById(this.getCurrentServer().getGroup());
        if(!activeGroup.isPresent()) this.createServerGroup(this.getCurrentServer().getGroup());

        if(!this.getGroupById("Global").isPresent()) {
            this.createServerGroup("Global");
        }

        this.currentScope = this.getGroupById(this.getCurrentServer().getGroup()).get();

        // Every 3 seconds we need to send and receive server heartbeats
        new ServerHeartbeatTask(getPlugin()).runTaskTimerAsynchronously(getPlugin(), 0L, 20L * 3);
    }

    public Server getCurrentServer() {
        return this.servers.stream().filter(server -> server.getName().equalsIgnoreCase(this.currentName)).findFirst().orElse(null);
    }

    public void updateServer(Server receivedServer) {
        Server server = this.findByName(receivedServer.getName()).orElse(null);
        if(server == null) {
            server = new Server(receivedServer.getName(), receivedServer.getGroup());
            this.servers.add(server);
        }

        server.setLastHeartbeat(receivedServer.getLastHeartbeat());
        server.setMaxPlayers(receivedServer.getMaxPlayers());
        server.setPlayers(receivedServer.getPlayers());
        server.setWhitelisted(receivedServer.isWhitelisted());

        ServerGroup group = this.getGroupById(server.getGroup()).orElse(null);
        if(group == null) this.serverGroups.add(new ServerGroup(server.getGroup()));
    }

    public void createServerGroup(String name) {
        this.serverGroups.add(new ServerGroup(name));
    }

    public boolean isActive(ServerGroup group) {
        return group.getId().equalsIgnoreCase(this.currentScope.getId()) || group.getId().equalsIgnoreCase("Global");
    }

    public Optional<ServerGroup> resolveServerGroup(Server server) {
        return this.serverGroups.stream().filter(group -> group.getId().equalsIgnoreCase(server.getGroup())).findFirst();
    }

    public Optional<ServerGroup> getGroupById(String id) {
        return this.serverGroups.stream().filter(group -> group.getId().equalsIgnoreCase(id)).findFirst();
    }

    public List<Server> getServersWithGroup(ServerGroup group) {
        if(group.getId().equalsIgnoreCase("Global")) return new ArrayList<>(servers);

        return this.servers.stream().filter(server -> {
            final Optional<ServerGroup> serverGroup = this.resolveServerGroup(server);

            return serverGroup.map(value -> value.getId().equalsIgnoreCase(group.getId())).orElse(false);
        }).collect(Collectors.toList());
    }

    public Optional<Server> findByName(String name) {
        return this.servers.stream().filter(server -> server.getName().equalsIgnoreCase(name)).findFirst();
    }
}
