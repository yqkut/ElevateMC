package com.elevatemc.elib.hologram;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
public class Hologram {

    private final List<HologramLine> lines;
    private final Set<UUID> setup;
    private Location location;

    public Hologram(Location location) {
        this.lines = new ArrayList<>();
        this.setup = new HashSet<>();
        this.updateLocation(location);
    }

    public void setup(Player player) {
        this.setup.add(player.getUniqueId());
        for (HologramLine line : this.lines) {
            line.setup(player);
        }
    }

    public void update(Player player) {
        if (!setup.contains(player.getUniqueId())) {
            return;
        }
        for (HologramLine line : this.lines) {
            line.update(player);
        }
    }

    public void hide(Player player) {
        if (!setup.contains(player.getUniqueId())) {
            return;
        }
        this.setup.remove(player.getUniqueId());
        for (HologramLine line : this.lines) {
            line.hide(player);
        }
    }

    public boolean isSetup(UUID uuid) {
        return this.setup.contains(uuid);
    }

    public void addLine(String line) {
        this.addLine(lines.size(), line);
    }

    public void addLine(int index, String line) {
        HologramLine hologramLine = new HologramLine(line, location, 0);
        this.lines.add(index, hologramLine);
        this.updateRelativeLocation();
        hologramLine.setupBulk(this.setup);
    }

    public HologramLine getLine(int index) {
        if (index >= this.lines.size()) {
            return null;
        }
        return this.lines.get(index);
    }

    public List<HologramLine> getLines() {
        return this.lines;
    }

    public boolean isEmpty() {
        return this.getLines().isEmpty();
    }

    public void removeLine(int index) {
        if (index >= this.lines.size()) {
            return;
        }
        HologramLine line = this.lines.remove(index);
        if (line == null) {
            return;
        }
        line.hideBulk(this.setup);
        this.updateRelativeLocation();
    }

    public void updateRelativeLocation() {
        int size = this.lines.size();
        if (size <= 0) {
            return;
        }
        for (int i = 0; i < size; i++) {
            HologramLine hologramLine = this.lines.get(i);
            hologramLine.updateLocation(this.location, (size - i) * 0.3);
        }
    }

    public void updateLocation(Location location) {
        this.location = location;
        this.updateRelativeLocation();
    }
}
