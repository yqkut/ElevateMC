package com.elevatemc.elib.skin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.mojang.authlib.properties.Property;

import java.util.UUID;

@SuppressWarnings("DuplicateStringLiteralInspection")
@Data @AllArgsConstructor @NoArgsConstructor
public class MojangSkin {
    private UUID uuid;
    private String value;
    private String signature;
    public Property toProperty() {
        return new Property("textures", this.value, this.signature);
    }
}
