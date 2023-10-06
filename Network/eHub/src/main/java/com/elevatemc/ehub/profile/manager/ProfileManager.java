package com.elevatemc.ehub.profile.manager;


import com.elevatemc.ehub.profile.Profile;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class ProfileManager {
    private final Map<UUID, Profile> profiles;

    public ProfileManager() {
        profiles = new HashMap<>();
    }

    public Profile getByUuid(UUID uuid) {
        return profiles.get(uuid);
    }
}
