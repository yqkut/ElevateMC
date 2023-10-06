package dev.apposed.prime.spigot.module.profile.identity;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class ProfileIdentity {

    @SerializedName("_id")
    private String ip;


}