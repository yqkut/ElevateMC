package com.elevatemc.elib.util.json.map;

import com.google.gson.*;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.internal.JsonReaderInternalAccess;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.internal.Streams;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("ALL")
public final class AllowNullMapTypeAdapterFactory implements TypeAdapterFactory {
    private final AllowNullConstructorConstructor constructorConstructor;
    final boolean complexMapKeySerialization;

    public AllowNullMapTypeAdapterFactory(AllowNullConstructorConstructor constructorConstructor, boolean complexMapKeySerialization) {
        this.constructorConstructor = constructorConstructor;
        this.complexMapKeySerialization = complexMapKeySerialization;
    }

    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        Type type = typeToken.getType();
        Class<? super T> rawType = typeToken.getRawType();
        if (!Map.class.isAssignableFrom(rawType)) {
            return null;
        } else {
            Class<?> rawTypeOfSrc = $Gson$Types.getRawType(type);
            Type[] keyAndValueTypes = $Gson$Types.getMapKeyAndValueTypes(type, rawTypeOfSrc);
            TypeAdapter<?> keyAdapter = this.getKeyAdapter(gson, keyAndValueTypes[0]);
            TypeAdapter<?> valueAdapter = gson.getAdapter(TypeToken.get(keyAndValueTypes[1]));
            ObjectConstructor<T> constructor = this.constructorConstructor.get(typeToken);
            TypeAdapter<T> result = new Adapter(gson, keyAndValueTypes[0], keyAdapter, keyAndValueTypes[1], valueAdapter, constructor);
            return result;
        }
    }

    private TypeAdapter<?> getKeyAdapter(Gson context, Type keyType) {
        return keyType != Boolean.TYPE && keyType != Boolean.class ? context.getAdapter(TypeToken.get(keyType)) : TypeAdapters.BOOLEAN_AS_STRING;
    }

    private final class Adapter<K, V> extends TypeAdapter<Map<K, V>> {
        private final TypeAdapter<K> keyTypeAdapter;
        private final TypeAdapter<V> valueTypeAdapter;
        private final ObjectConstructor<? extends Map<K, V>> constructor;

        public Adapter(Gson context, Type keyType, TypeAdapter<K> keyTypeAdapter, Type valueType, TypeAdapter<V> valueTypeAdapter, ObjectConstructor<? extends Map<K, V>> constructor) {
            this.keyTypeAdapter = new AllowNullTypeAdapterRuntimeTypeWrapper<>(context, keyTypeAdapter, keyType);
            this.valueTypeAdapter = new AllowNullTypeAdapterRuntimeTypeWrapper<>(context, valueTypeAdapter, valueType);
            this.constructor = constructor;
        }

        public Map<K, V> read(JsonReader in) throws IOException {
            JsonToken peek = in.peek();
            if (peek == JsonToken.NULL) {
                in.nextNull();
                return null;
            } else {
                Map<K, V> map = this.constructor.construct();
                if (peek == JsonToken.BEGIN_ARRAY) {
                    in.beginArray();

                    while(in.hasNext()) {
                        in.beginArray();

                        K key;
                        try {
                            key = this.keyTypeAdapter.read(in);
                        } catch (Exception e){
                            key = null;
                        }
                        if (key == null || key.equals("null")) {
                            in.skipValue();
                            continue;
                        }
                        V value = this.valueTypeAdapter.read(in);
                        Object replaced = map.put(key, value);
                        if (replaced != null) {
                            throw new JsonSyntaxException("duplicate key: " + key);
                        }

                        in.endArray();
                    }

                    in.endArray();
                } else {
                    in.beginObject();

                    while(in.hasNext()) {
                        JsonReaderInternalAccess.INSTANCE.promoteNameToValue(in);

                        K key;
                        try {
                            key = this.keyTypeAdapter.read(in);
                        } catch (Exception e){
                            key = null;
                        }
                        if (key == null || key.equals("null")) {
                            in.skipValue();
                            continue;
                        }
                        V value = this.valueTypeAdapter.read(in);
                        Object replaced = map.put(key, value);
                        if (replaced != null) {
                            throw new JsonSyntaxException("duplicate key: " + key);
                        }
                    }

                    in.endObject();
                }

                return map;
            }
        }

        public void write(JsonWriter out, Map<K, V> map) throws IOException {
            if (map == null) {
                out.nullValue();
            } else if (!AllowNullMapTypeAdapterFactory.this.complexMapKeySerialization) {
                out.beginObject();

                for (Entry<K, V> entry : map.entrySet()) {
                    out.name(String.valueOf(entry.getKey()));
                    this.valueTypeAdapter.write(out, entry.getValue());
                }

                out.endObject();
            } else {
                boolean hasComplexKeys = false;
                List<JsonElement> keys = new ArrayList<>(map.size());
                List<V> values = new ArrayList<>(map.size());

                JsonElement keyElement;
                for(Iterator<Entry<K, V>> var6 = map.entrySet().iterator(); var6.hasNext(); hasComplexKeys |= keyElement.isJsonArray() || keyElement.isJsonObject()) {
                    Entry<K, V> entry = var6.next();
                    keyElement = this.keyTypeAdapter.toJsonTree(entry.getKey());
                    keys.add(keyElement);
                    values.add(entry.getValue());
                }

                int i;
                int size;
                if (hasComplexKeys) {
                    out.beginArray();
                    i = 0;

                    for(size = keys.size(); i < size; ++i) {
                        out.beginArray();
                        Streams.write(keys.get(i), out);
                        this.valueTypeAdapter.write(out, values.get(i));
                        out.endArray();
                    }

                    out.endArray();
                } else {
                    out.beginObject();
                    i = 0;

                    for(size = keys.size(); i < size; ++i) {
                        keyElement = keys.get(i);
                        out.name(this.keyToString(keyElement));
                        this.valueTypeAdapter.write(out, values.get(i));
                    }

                    out.endObject();
                }

            }
        }

        private String keyToString(JsonElement keyElement) {
            if (keyElement.isJsonPrimitive()) {
                JsonPrimitive primitive = keyElement.getAsJsonPrimitive();
                if (primitive.isNumber()) {
                    return String.valueOf(primitive.getAsNumber());
                } else if (primitive.isBoolean()) {
                    return Boolean.toString(primitive.getAsBoolean());
                } else if (primitive.isString()) {
                    return primitive.getAsString();
                } else {
                    throw new AssertionError();
                }
            } else if (keyElement.isJsonNull()) {
                return "null";
            } else {
                throw new AssertionError();
            }
        }
    }
}
