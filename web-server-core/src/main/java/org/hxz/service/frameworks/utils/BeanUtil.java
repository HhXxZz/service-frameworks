package org.hxz.service.frameworks.utils;

import org.hxz.service.frameworks.misc.Parameters;
import org.hxz.service.frameworks.vo.StatCommon;
import com.google.gson.annotations.SerializedName;
import org.springframework.util.StringUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@SuppressWarnings("unused")
public class BeanUtil {
    // ===========================================================
    // Constants
    // ===========================================================
    private final static Set<Class<?>> NUMBER_PRIMITIVES;
    private final static Set<Class<?>> INTEGER_PRIMITIVES;
    private final static Set<Class<?>> FLOATING_PRIMITIVES;

    static {
        NUMBER_PRIMITIVES = new HashSet<>();
        NUMBER_PRIMITIVES.add(byte.class);
        NUMBER_PRIMITIVES.add(short.class);
        NUMBER_PRIMITIVES.add(int.class);
        NUMBER_PRIMITIVES.add(long.class);
        NUMBER_PRIMITIVES.add(float.class);
        NUMBER_PRIMITIVES.add(double.class);

        INTEGER_PRIMITIVES = new HashSet<>();
        INTEGER_PRIMITIVES.add(byte.class);
        INTEGER_PRIMITIVES.add(short.class);
        INTEGER_PRIMITIVES.add(int.class);
        INTEGER_PRIMITIVES.add(long.class);

        FLOATING_PRIMITIVES = new HashSet<>();
        FLOATING_PRIMITIVES.add(float.class);
        FLOATING_PRIMITIVES.add(double.class);
    }
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


    /**
     * 整合同类型数组
     *
     * @param pArrayA 数组A
     * @param pArrayB 数组B
     * @param <T>     数组类型
     * @return 整合后的结果
     */
    public static <T> T[] concatenate(T[] pArrayA, T[] pArrayB) {
        int aLen = pArrayA.length;
        int bLen = pArrayB.length;

        @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(pArrayA.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(pArrayA, 0, c, 0, aLen);
        System.arraycopy(pArrayB, 0, c, aLen, bLen);

        return c;
    }

    /**
     * javaBean 转 Map
     *
     * @param object 需要转换的javabean
     * @return 转换结果map
     */
    public static Map<String, Object> bean_to_map(Object object) throws Exception {
        Map<String, Object> map    = new HashMap<>();
        Class<?>            cls    = object.getClass();
        Field[]             fields = cls.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            map.put(field.getName(), field.get(object));
        }
        return map;
    }

    /**
     * @param map 需要转换的map
     * @param clazz 目标javaBean的类对象
     * @return 目标类object
     */
    public static Object map_to_bean(Map<String, Object> map, Class<?> clazz) throws Exception {
        Object object = clazz.newInstance();
        for (String key : map.keySet()) {
            Field field = clazz.getDeclaredField(key);
            field.setAccessible(true);
            field.set(object, map.get(key));
        }
        return object;
    }

    public static <T> T convertMapToBean(Map<String, Object> map, Class<T> clazz) throws Exception {
        T object = clazz.newInstance();
        for (String key : map.keySet()) {
            Field field = clazz.getDeclaredField(key);
            field.setAccessible(true);
            field.set(object, map.get(key));
        }
        return object;
    }

    public static TreeMap<String, Object> convertStringToMap(String str) {
        if (str == null){
            return new TreeMap<>();
        }
        if (str.length() == 0){
            return new TreeMap<>();
        }
        String[] keyValueArray = str.split( "&");
        TreeMap<String, Object> map = new TreeMap<String, Object>();
        for(String keyValue : keyValueArray) {
            String[] s = StringUtils.split(keyValue, "=");
            if (s == null || s.length != 2){
                continue;
            }
            map.put(s[0], s[1]);
        }
        return map;
    }

    public static boolean isNumber(Class<?> pType) {
        return NUMBER_PRIMITIVES.contains(pType)
                || Number.class.isAssignableFrom(pType);
    }

    public static boolean isBoolean(Class<?> pType) {
        return Boolean.class.isAssignableFrom(pType)
                || boolean.class == pType;
    }

    public static boolean isIntegerNumber(Class<?> pType) {
        return INTEGER_PRIMITIVES.contains(pType)
                || Byte.class.isAssignableFrom(pType)
                || Short.class.isAssignableFrom(pType)
                || Integer.class.isAssignableFrom(pType)
                || Long.class.isAssignableFrom(pType);
    }

    public static boolean isFloatingNumber(Class<?> pType) {
        return FLOATING_PRIMITIVES.contains(pType)
                || Float.class.isAssignableFrom(pType)
                || Double.class.isAssignableFrom(pType);
    }

    public static void setValue(Field field, Object obj, Object value) throws Exception {
        if (value != null) {
            Class<?> type = field.getType();
            field.setAccessible(true);

            switch (type.getName()) {
                case "java.lang.Integer":
                    field.set(obj, getIntValue(value));
                    break;
                case "int":
                    field.setInt(obj, getIntValue(value));
                    break;
                case "java.lang.Long":
                    field.set(obj, getLongValue(value));
                    break;
                case "long":
                    field.setLong(obj, getLongValue(value));
                    break;
                case "java.lang.Short":
                    field.set(obj, getShortValue(value));
                    break;
                case "short":
                    field.setShort(obj, getShortValue(value));
                    break;
                case "java.lang.Boolean":
                    //noinspection RedundantCast
                    field.set(obj, (Boolean) value);
                    break;
                case "boolean":
                    field.setBoolean(obj, (Boolean) value);
                    break;
                case "java.lang.Character":
                    field.set(obj, getCharValue(value));
                    break;
                case "char":
                    field.setChar(obj, getCharValue(value));
                    break;
                case "java.lang.Double":
                    field.set(obj, getDoubleValue(value));
                    break;
                case "double":
                    field.setDouble(obj, getDoubleValue(value));
                    break;
                case "java.lang.Float":
                    field.set(obj, getFloatValue(value));
                    break;
                case "float":
                    field.setFloat(obj, getFloatValue(value));
                    break;
                case "java.lang.Byte":
                    field.setByte(obj, (Byte) value);
                    break;
                case "byte":
                    //noinspection RedundantCast
                    field.set(obj, (Byte) value);
                    break;
                case "java.lang.String":
                    field.set(obj, value.toString());
                    break;
                case "java.util.concurrent.atomic.AtomicLong":
                    field.set(obj, new AtomicLong(getLongValue(value)));
                    break;
                case "java.util.concurrent.atomic.AtomicInteger":
                    field.set(obj, new AtomicInteger(getIntValue(value)));
                    break;
                case "java.util.concurrent.atomic.AtomicBoolean":
                    field.set(obj, new AtomicBoolean((Boolean) value));
                    break;
                default:
                    field.set(obj, value);
            }
        }
    }

    private static int getIntValue(Object value) {
        switch (value.getClass().getName()) {
            case "java.lang.Integer":
                return (Integer) value;
            case "java.lang.Long":
                return ((Long) value).intValue();
            case "java.lang.Short":
                return ((Short) value).intValue();
            case "java.lang.Float":
                return ((Float) value).intValue();
            case "java.lang.Double":
                return ((Double) value).intValue();
            case "java.math.BigDecimal":
                return ((BigDecimal) value).intValue();
            case "java.lang.String":
                return Integer.parseInt(value.toString());
            case "java.lang.Number":
                return ((Number) value).intValue();
            default:
                throw new IllegalArgumentException("The value '" + value + "' is not a numeric.");
        }
    }

    private static short getShortValue(Object value) {
        switch (value.getClass().getName()) {
            case "java.lang.Short":
                return (Short) value;
            case "java.lang.Integer":
                return ((Integer) value).shortValue();
            case "java.lang.Boolean":
                return ((Boolean) value) ? (short) 1 : (short) 0;
            case "java.lang.Long":
                return ((Long) value).shortValue();
            case "java.lang.Float":
                return ((Float) value).shortValue();
            case "java.lang.Double":
                return ((Double) value).shortValue();
            case "java.math.BigDecimal":
                return ((BigDecimal) value).shortValue();
            case "java.lang.String":
                return Short.parseShort(value.toString());
            case "java.lang.Number":
                return ((Number) value).shortValue();
            default:
                throw new IllegalArgumentException("The value '" + value + "' is not a numeric.");
        }
    }

    private static char getCharValue(Object value) {
        switch (value.getClass().getName()) {
            case "java.lang.Character":
                return (Character) value;
            case "java.lang.Short":
                return (char) ((Short) value).intValue();
            case "java.lang.Integer":
                return (char) ((Integer) value).intValue();
            case "java.lang.Number":
                return (char) ((Number) value).intValue();
            default:
                throw new IllegalArgumentException("The value '" + value + "' is not a character.");
        }
    }

    private static long getLongValue(Object value) {
        switch (value.getClass().getName()) {
            case "java.lang.Long":
                return (Long) value;
            case "java.lang.Integer":
                return ((Integer) value).longValue();
            case "java.lang.Short":
                return ((Short) value).longValue();
            case "java.lang.Float":
                return ((Float) value).longValue();
            case "java.lang.Double":
                return ((Double) value).longValue();
            case "java.math.BigDecimal":
                return ((BigDecimal) value).longValue();
            case "java.math.BigInteger":
                return ((BigInteger) value).longValue();
            case "java.lang.String":
                return Long.parseLong(value.toString());
            case "java.lang.Number":
                return ((Number) value).longValue();
            default:
                throw new IllegalArgumentException("The value '" + value + "' is not a numeric.");
        }
    }

    private static float getFloatValue(Object value) {
        switch (value.getClass().getName()) {
            case "java.lang.Float":
                return (Float) value;
            case "java.lang.Long":
                return ((Long) value).floatValue();
            case "java.lang.Integer":
                return ((Integer) value).floatValue();
            case "java.lang.Short":
                return ((Short) value).floatValue();
            case "java.lang.Double":
                return ((Double) value).floatValue();
            case "java.math.BigDecimal":
                return ((BigDecimal) value).floatValue();
            case "java.lang.String":
                return Float.parseFloat(value.toString());
            case "java.lang.Number":
                return ((Number) value).floatValue();
            default:
                throw new IllegalArgumentException("The value '" + value + "' is not a numeric.");
        }
    }

    private static double getDoubleValue(Object value) {
        switch (value.getClass().getName()) {
            case "java.lang.Double":
                return (Double) value;
            case "java.lang.Float":
                return ((Float) value).doubleValue();
            case "java.lang.Long":
                return ((Long) value).doubleValue();
            case "java.lang.Integer":
                return ((Integer) value).doubleValue();
            case "java.lang.Short":
                return ((Short) value).doubleValue();
            case "java.math.BigDecimal":
                return ((BigDecimal) value).doubleValue();
            case "java.lang.String":
                return Double.parseDouble(value.toString());
            case "java.lang.Number":
                return ((Number) value).doubleValue();
            default:
                throw new IllegalArgumentException("The value '" + value + "' is not a numeric.");
        }
    }

    public static void setValueWithStringParsed(Field field, Object obj, String value) throws Exception {
        Class<?> type = field.getType();
        field.setAccessible(true);
        switch (type.getName()) {
            case "int":
            case "java.lang.Integer":
                field.setInt(obj, Integer.parseInt(value));
                break;
            case "long":
            case "java.lang.Long":
                field.setLong(obj, Long.parseLong(value));
                break;
            case "short":
            case "java.lang.Short":
                field.setShort(obj, Short.parseShort(value));
                break;
            case "boolean":
            case "java.lang.Boolean":
                field.setBoolean(obj, Boolean.parseBoolean(value));
                break;
            case "double":
            case "java.lang.Double":
                field.setDouble(obj, Double.parseDouble(value));
                break;
            case "float":
            case "java.lang.Float":
                field.setFloat(obj, Float.parseFloat(value));
                break;
            case "byte":
            case "java.lang.Byte":
                field.setByte(obj, Byte.parseByte(value));
                break;
            default:
                field.set(obj, value);
                break;
        }
    }

    public static void printParametersForPostman(Class<?> pClass) {
        if (pClass != null) {
            StringBuilder s = new StringBuilder();
            s.append("[");
            for (Field field : pClass.getDeclaredFields()) {
                field.setAccessible(true);
                if (Modifier.isTransient(field.getModifiers())) {
                    continue;
                }
                if (s.length() > 1) {
                    s.append(",");
                }
                SerializedName sName = field.getAnnotation(SerializedName.class);
                String         name  = field.getName();
                String         key   = sName == null ? field.getName() : sName.value();
                s.append("{\"key\":\"").append(key).append("\",\"value\":\"\",\"type\":\"text\",\"enabled\":true,\"description\":\"").append(name).append("\"}");
            }
            s.append("]");

            System.out.println(s.toString());
        }
    }

    public static void main(String[] args) {
        BeanUtil.printParametersForPostman(StatCommon.class);
        System.out.println(System.currentTimeMillis());

        System.out.println(GsonUtil.toPrettyJson(Parameters.getDefaultInfo()));
    }
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
