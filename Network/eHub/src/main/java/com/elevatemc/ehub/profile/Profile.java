package com.elevatemc.ehub.profile;

import com.elevatemc.ehub.eHub;
import com.elevatemc.ehub.type.armor.ArmorType;
import com.elevatemc.ehub.type.particle.ParticleType;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Profile {
    private final eHub instance = eHub.getInstance();

    private final UUID uuid;
    private ArmorType armorType;
    private ParticleType particleType;
    private boolean enchanted, astronaut;
    private boolean[] armors;

    public Profile(UUID uuid) {
        this.uuid = uuid;
        this.armorType = null;
        this.particleType = null;
        this.enchanted = false;
        this.astronaut = false;
        this.armors = new boolean[] {true, true, true, true};

        getInstance().getProfileManager().getProfiles().put(uuid, this);
    }
}
