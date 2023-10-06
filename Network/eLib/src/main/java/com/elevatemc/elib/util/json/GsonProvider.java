package com.elevatemc.elib.util.json;

import com.elevatemc.elib.util.json.map.AllowNullConstructorConstructor;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class GsonProvider {

    private static final GsonBuilder gsonBuilder = new GsonBuilder()
            .enableComplexMapKeySerialization()
            .disableHtmlEscaping();
    private static final GsonBuilder prettyGsonBuilder = new GsonBuilder()
            .enableComplexMapKeySerialization()
            .disableHtmlEscaping()
            .setPrettyPrinting();
    private static final GsonBuilder hierarchyAbsentBuilder = new GsonBuilder()
            .enableComplexMapKeySerialization()
            .disableHtmlEscaping()
            .setPrettyPrinting();

    private static final Map<Type, InstanceCreator<?>> instanceCreators = new HashMap<>();

    private static boolean builderChanged = true;
    private static Gson gson;
    private static Gson prettyGson;
    private static Gson hierarchyAbsentGson;

    private static void checkForChanges() {
        if (!builderChanged) {
            return;
        }
        gson = gsonBuilder.create();
        prettyGson = prettyGsonBuilder.create();
        hierarchyAbsentGson = hierarchyAbsentBuilder.create();
        builderChanged = false;
    }

    public static <T> byte[] toBinary(T element) {
        return toJson(element).getBytes(StandardCharsets.UTF_8);
    }

    public static <T> T fromBinary(byte[] bytes, Type type) {
        return fromJson(new String(bytes, StandardCharsets.UTF_8), type);
    }

    public static <T> T fromBinary(byte[] bytes, Class<T> clazz) {
        return fromJson(new String(bytes, StandardCharsets.UTF_8), clazz);
    }

    public static <T> String toJson(T element) {
        checkForChanges();
        return gson.toJson(element);
    }

    public static <T> String toJsonPretty(T element) {
        checkForChanges();
        return prettyGson.toJson(element);
    }

    public static <T> JsonElement toJsonTree(T element) {
        checkForChanges();
        return gson.toJsonTree(element);
    }

    public static <T> JsonElement toJsonTreePretty(T element) {
        checkForChanges();
        return prettyGson.toJsonTree(element);
    }

    public static <T> T fromJson(String json, Class<T> type) {
        checkForChanges();
        return gson.fromJson(json, type);
    }

    public static <T> T fromJson(String json, Type type) {
        checkForChanges();
        return gson.fromJson(json, type);
    }

    private static void apply(Consumer<GsonBuilder> builderConsumer) {
        apply(builderConsumer, false);
    }

    public static void apply(Consumer<GsonBuilder> builderConsumer, boolean skipHierarchyAbsent) {
        builderConsumer.accept(gsonBuilder);
        builderConsumer.accept(prettyGsonBuilder);
        if (!skipHierarchyAbsent) {
            builderConsumer.accept(hierarchyAbsentBuilder);
        }
        builderChanged = true;
    }

    public static <T> void registerInterface(Class<T> clazz) {
        registerTypeHierarchyAdapter(clazz, new AbstractClassAdapter());
    }

    public static <T> void registerAbstractClass(Class<T> clazz) {
        registerTypeHierarchyAdapter(clazz, new AbstractClassAdapter());
    }

    public static void registerTypeAdapterFactory(TypeAdapterFactory factory) {
        apply(builder -> builder.registerTypeAdapterFactory(factory));
    }

    public static void registerTypeAdapter(Type type, Object typeAdapter) {
        apply(builder -> {
            if (typeAdapter instanceof InstanceCreator) {
                instanceCreators.put(type, (InstanceCreator<?>) typeAdapter);
            }
            builder.registerTypeAdapter(type, typeAdapter);
        });
    }

    public static Gson getGson() {
        checkForChanges();
        return gson;
    }

    public static Gson getGsonPretty() {
        checkForChanges();
        return prettyGson;
    }

    public static AllowNullConstructorConstructor createConstructor() {
        return new AllowNullConstructorConstructor(instanceCreators, true);
    }

    public static Gson getGsonHierarchyAbsent() {
        checkForChanges();
        return hierarchyAbsentGson;
    }

    public static void disableHtmlEscaping() {
        apply(GsonBuilder::disableHtmlEscaping);
    }

    public static void registerTypeHierarchyAdapter(Class<?> type, Object adapterFactory) {
        apply(builder -> builder.registerTypeHierarchyAdapter(type, adapterFactory), true);
    }

    public static <T> TypeAdapter<T> getAdapter(TypeToken<T> typeToken) {
        checkForChanges();
        return gson.getAdapter(typeToken);
    }
}