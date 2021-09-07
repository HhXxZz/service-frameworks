package base.service.frameworks.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;


/**
 * Created by someone on 2017-02-27.
 */
@SuppressWarnings({"unused", "WeakerAccess", "SameParameterValue"})
public class GsonUtil {
    // ===========================================================
    // Constants
    // ===========================================================
    private static final Gson GSON_DEFAULT                     = new Gson();
    private static final Gson GSON_DEFAULT_WITH_EXPOSE         = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    private static final Gson GSON_PRETTY_PRINTING             = new GsonBuilder().setPrettyPrinting().create();
    private static final Gson GSON_PRETTY_PRINTING_WITH_EXPOSE = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
    private static final Gson GSON_NUMBER_SAFE                 = getNumberSafeGsonInstance(false);
    private static final Gson GSON_NUMBER_SAFE_WITH_EXPOSE     = getNumberSafeGsonInstance(true);
    private static final Gson GSON_NULL_CHANGE                 = getNullChangeGsonInstance(false);

    // ===========================================================
    // Fields
    // ===========================================================


    // ===========================================================
    // Constructors
    // ===========================================================


    // ===========================================================
    // Getter &amp; Setter
    // ===========================================================


    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================


    // ===========================================================
    // Methods
    // ===========================================================
    private static Gson getNullChangeGsonInstance(boolean pEnableExpose) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Integer.class, new IntegerTypeAdapter());
        builder.registerTypeAdapter(Long.class, new LongTypeAdapter());
        builder.registerTypeAdapter(Float.class, new FloatTypeAdapter());
        builder.registerTypeAdapter(Double.class, new DoubleTypeAdapter());
        builder.registerTypeAdapter(String.class, new StringNullAdapter());
        if (pEnableExpose) {
            builder.excludeFieldsWithoutExposeAnnotation();
        }
        return builder.create();
    }

    private static Gson getNumberSafeGsonInstance(boolean pEnableExpose) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Integer.class, new IntegerTypeAdapter());
        builder.registerTypeAdapter(Long.class, new LongTypeAdapter());
        builder.registerTypeAdapter(Float.class, new FloatTypeAdapter());
        builder.registerTypeAdapter(Double.class, new DoubleTypeAdapter());
        builder.registerTypeAdapter(
                new TypeToken<Map<String, Object>>() {
                }.getType(),
                (JsonDeserializer<Map<String, Object>>) (json, typeOfT, context) -> {

                    Map<String, Object>                 treeMap    = new HashMap<>();
                    JsonObject                          jsonObject = json.getAsJsonObject();
                    Set<Map.Entry<String, JsonElement>> entrySet   = jsonObject.entrySet();
                    for (Map.Entry<String, JsonElement> entry : entrySet) {
                        treeMap.put(entry.getKey(), entry.getValue());
                    }
                    return treeMap;
                });
        if (pEnableExpose) {
            builder.excludeFieldsWithoutExposeAnnotation();
        }
        return builder.create();
    }

    public static <T> T fromJson(String pJson, Class<T> pClass) {
        return fromJson(pJson, pClass, false, false);
    }

    public static <T> T fromJson(String pJson, Class<T> pClass, boolean pEnableExpose) {
        return fromJson(pJson, pClass, pEnableExpose, false);
    }

    public static <T> T fromJsonChangeNull(String pJson, Class<T> pClass, boolean pChangeNull) {
        if (pChangeNull) {
            return GSON_NULL_CHANGE.fromJson(pJson, pClass);
        }
        return GSON_DEFAULT.fromJson(pJson, pClass);
    }

    public static <T> T fromJson(String pJson, Class<T> pClass, boolean pEnableExpose, boolean pEnableNumberSafe) {
        if (pEnableNumberSafe) {
            if (pEnableExpose) {
                return GSON_NUMBER_SAFE_WITH_EXPOSE.fromJson(pJson, pClass);
            }
            return GSON_NUMBER_SAFE.fromJson(pJson, pClass);
        }
        if (pEnableExpose) {
            return GSON_DEFAULT_WITH_EXPOSE.fromJson(pJson, pClass);
        }
        return GSON_DEFAULT.fromJson(pJson, pClass);
    }

    public static <T> T fromJson(String pJson, Type pType) {
        return fromJson(pJson, pType, false, false);
    }

    public static <T> T fromJson(String pJson, Type pType, boolean pEnableExpose) {
        return fromJson(pJson, pType, pEnableExpose, false);
    }

    public static <T> T fromJson(String pJson, Type pType, boolean pEnableExpose, boolean pEnableNumberSafe) {
        if (pEnableNumberSafe) {
            if (pEnableExpose) {
                return GSON_NUMBER_SAFE_WITH_EXPOSE.fromJson(pJson, pType);
            }
            return GSON_NUMBER_SAFE.fromJson(pJson, pType);
        }
        if (pEnableExpose) {
            return GSON_DEFAULT_WITH_EXPOSE.fromJson(pJson, pType);
        }
        return GSON_DEFAULT.fromJson(pJson, pType);
    }

    public static <T> List<T> listFromJson(String pJson, Class<T[]> pClazz) {
        return listFromJson(pJson, pClazz, false, false);
    }

    public static <T> List<T> listFromJson(String pJson, Class<T[]> pClazz, boolean pEnableExpose) {
        return listFromJson(pJson, pClazz, pEnableExpose, false);
    }

    public static <T> List<T> listFromJson(String pJson, Class<T[]> pClazz, boolean pEnableExpose, boolean pEnableNumberSafe) {
        if (pEnableNumberSafe) {
            if (pEnableExpose) {
                return Lists.newArrayList(GSON_NUMBER_SAFE_WITH_EXPOSE.fromJson(pJson, pClazz));
            }
            return Lists.newArrayList(GSON_NUMBER_SAFE.fromJson(pJson, pClazz));
        }
        if (pEnableExpose) {
            return Lists.newArrayList(GSON_DEFAULT_WITH_EXPOSE.fromJson(pJson, pClazz));
        }
        return Lists.newArrayList(GSON_DEFAULT.fromJson(pJson, pClazz));
    }

    public static String toJson(Object pObject) {
        return toJson(pObject, false);
    }

    public static String toJson(Object pObject, boolean pEnableExpose) {
        return toJson(pObject,pEnableExpose,false);
    }

    public static String toJson(Object pObject, boolean pEnableExpose, boolean pEnableNumberSafe) {
        if (pObject != null) {
            if (pEnableNumberSafe) {
                if (pEnableExpose) {
                    return GSON_NUMBER_SAFE_WITH_EXPOSE.toJson(pObject);
                }
                return GSON_NUMBER_SAFE.toJson(pObject);
            }
            if (pEnableExpose) {
                return GSON_DEFAULT_WITH_EXPOSE.toJson(pObject);
            }
            return GSON_DEFAULT.toJson(pObject);
        }
        return "{}";
    }

    public static String toPrettyJson(Object pObject) {
        return toPrettyJson(pObject, false);
    }

    public static String toPrettyJson(Object pObject, boolean pEnableExpose) {
        if (pObject != null) {
            if (pEnableExpose) {
                return GSON_PRETTY_PRINTING_WITH_EXPOSE.toJson(pObject);
            }
            return GSON_PRETTY_PRINTING.toJson(pObject);
        }
        return "{}";
    }

    public static void main(String[] args) {
        String json  = "{\"id\":null, \"spectator\":\"\"}";
        Match  match = fromJsonChangeNull(json, Match.class, true);

        System.out.println(GSON_NULL_CHANGE.toJson(match));
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    private static class Match {
        public String id;
        public Long   spectator;
    }

    private static class IntegerTypeAdapter extends TypeAdapter<Integer> {
        @Override
        public void write(JsonWriter jsonWriter, Integer integer) throws IOException {
            if (integer == null) {
                jsonWriter.value(0);
                return;
            }
            jsonWriter.value(integer);
        }

        @Override
        public Integer read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return 0;
            }
            String stringValue = jsonReader.nextString();
            try {
                return Integer.valueOf(stringValue);
            } catch (NumberFormatException e1) {
                try {
                    return Integer.valueOf(stringValue.replace(",", ""));
                } catch (NumberFormatException e2) {
                    return 0;
                }
            }
        }
    }

    private static class LongTypeAdapter extends TypeAdapter<Long> {
        @Override
        public void write(JsonWriter jsonWriter, Long aLong) throws IOException {
            if (aLong == null) {
                jsonWriter.value(0L);
                return;
            }
            jsonWriter.value(aLong);
        }

        @Override
        public Long read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return 0L;
            }
            String stringValue = jsonReader.nextString();
            try {
                return Long.valueOf(stringValue);
            } catch (NumberFormatException e) {
                return 0L;
            }
        }
    }

    private static class DoubleTypeAdapter extends TypeAdapter<Double> {
        @Override
        public void write(JsonWriter jsonWriter, Double aDouble) throws IOException {
            if (aDouble == null) {
                jsonWriter.value(0D);
                return;
            }
            jsonWriter.value(aDouble);
        }

        @Override
        public Double read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return 0D;
            }
            String stringValue = jsonReader.nextString();
            try {
                return Double.valueOf(stringValue);
            } catch (NumberFormatException e) {
                return 0D;
            }
        }
    }

    private static class FloatTypeAdapter extends TypeAdapter<Float> {
        @Override
        public void write(JsonWriter jsonWriter, Float aFloat) throws IOException {
            if (aFloat == null) {
                jsonWriter.value(0F);
                return;
            }
            jsonWriter.value(aFloat);
        }

        @Override
        public Float read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return 0F;
            }
            String stringValue = jsonReader.nextString();
            try {
                return Float.valueOf(stringValue);
            } catch (NumberFormatException e) {
                return 0F;
            }
        }
    }

    public static class StringNullAdapter extends TypeAdapter<String> {
        @Override
        public String read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return "";
            }
            return reader.nextString();
        }

        @Override
        public void write(JsonWriter writer, String value) throws IOException {
            if (value == null) {
                writer.nullValue();
                return;
            }
            writer.value(value);
        }
    }

}
