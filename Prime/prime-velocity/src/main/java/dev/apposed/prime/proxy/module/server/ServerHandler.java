package dev.apposed.prime.proxy.module.server;

import dev.apposed.prime.proxy.module.Module;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class ServerHandler extends Module {

    private Set<Server> servers;
    private Set<ServerGroup> serverGroups;

    @Override
    public void onEnable() {
        this.servers = new HashSet<>();
        this.serverGroups = new HashSet<>();
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

            if(serverGroup.isPresent()) {
                return serverGroup.get().getId().equalsIgnoreCase(group.getId());
            }
            return false;
        }).collect(Collectors.toList());
    }

    public Optional<Server> findByName(String name) {
        return this.servers.stream().filter(server -> server.getName().equalsIgnoreCase(name)).findFirst();
    }
}
