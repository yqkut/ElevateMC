package dev.apposed.prime.spigot.util.json.annotation;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class HiddenAnnotationExclusionStrategy implements ExclusionStrategy {

    public boolean shouldSkipClass(Class<?> clazz) {
        return clazz.getAnnotation(Hidden.class) != null;
    }

    public boolean shouldSkipField(FieldAttributes f) {
        return f.getAnnotation(Hidden.class) != null;
    }
}