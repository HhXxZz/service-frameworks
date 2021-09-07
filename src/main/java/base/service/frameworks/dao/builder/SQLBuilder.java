package base.service.frameworks.dao.builder;


import base.service.frameworks.dao.builder.annotation.Extendable;
import base.service.frameworks.dao.builder.annotation.PrimaryKey;
import base.service.frameworks.dao.builder.annotation.Table;
import base.service.frameworks.utils.BeanUtil;
import com.google.common.base.Joiner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * Created by someone on 2018-08-07.
 *
 * </pre>
 */
@SuppressWarnings({"unused"})
public class SQLBuilder {
    // ===========================================================
    // Constants
    // ===========================================================
    private static final Logger LOG = LogManager.getLogger(SQLBuilder.class);

    private static final String DEFAULT_PRIMARY_KEY = "seqid";

    // ===========================================================
    // Fields
    // ===========================================================
    private Object mEntity;
    private Class<?> mClass;
    private String mTableName;
    private List<String> mIncludeFields;
    private List<String> mExcludeFields;
    private List<String> mConditionsFields;
    private List<String> mDuplicates;

    // ===========================================================
    // Constructors
    // ===========================================================
    public SQLBuilder(@NonNull Object pEntity){
        this.mEntity = pEntity;
        this.mClass = mEntity.getClass();
        this.mTableName = getTableName();
        this.mIncludeFields = new ArrayList<>();
        this.mExcludeFields = new ArrayList<>();
        this.mConditionsFields = new ArrayList<>();
        this.mDuplicates = new ArrayList<>();
    }

    // ===========================================================
    // Getter &amp; Setter
    // ===========================================================


    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================


    @Override
    public String toString() {
        return "class[ " + this.mEntity.getClass().getName() + " ] table[ " + this.mTableName + " ]";
    }

    // ===========================================================
    // Methods
    // ===========================================================
    /**
     * 包含字段
     * @param pFields 字段名列表
     * @return {@link SQLBuilder}
     */
    public SQLBuilder includeFields(String ... pFields){
        if(pFields != null) {
            for (String fieldName : pFields) {
                if (!this.mIncludeFields.contains(fieldName)) {
                    this.mIncludeFields.add(fieldName);
                }
            }
        }
        return this;
    }

    /**
     * 排除字段
     * @param pFields 字段名列表
     * @return {@link SQLBuilder}
     */
    public SQLBuilder excludeFields(String ... pFields){
        if(pFields != null) {
            for (String fieldName : pFields) {
                if (!this.mExcludeFields.contains(fieldName)) {
                    this.mExcludeFields.add(fieldName);
                }
            }
        }
        return this;
    }

    /**
     * 条件字段，仅 update 和 delete 会使用，当设置条件时，不再使用主键作为条件
     * 条件均使用等式，即字段等于对象中的值
     * @param pFields 字段名列表
     * @return {@link SQLBuilder}
     */
    public SQLBuilder conditions(String ... pFields){
        if(pFields != null) {
            for (String fieldName : pFields) {
                if (!this.mConditionsFields.contains(fieldName)) {
                    this.mConditionsFields.add(fieldName);
                }
            }
        }
        return this;
    }

    /**
     * 冲突更新字段，仅 insert 会使用( ON DUPLICATE KEY UPDATE )
     * @param pFields 字段名列表
     * @return {@link SQLBuilder}
     */
    public SQLBuilder onDuplicateKeyUpdate(String ... pFields){
        if(pFields != null) {
            for (String fieldName : pFields) {
                if (!this.mDuplicates.contains(fieldName)) {
                    this.mDuplicates.add(fieldName);
                }
            }
        }
        return this;
    }

    /**
     * 构造 insert 语句
     * @param pAutoIncrement 是否需要插入自增量
     * @return insert 语句
     */
    public String insert(boolean pAutoIncrement){
        try {
            List<String> fields  = new ArrayList<>();
            List<String> values  = new ArrayList<>();
            List<String> updates = new ArrayList<>();

            for (Field field : this.mClass.getDeclaredFields()) {
                field.setAccessible(true);
                String   name  = field.getName();
                Class<?> type  = field.getType();
                Object   value = field.get(mEntity);

                // 如果属性标记为Extendable，则不将其值保存到数据库
                if (field.getAnnotation(Extendable.class) != null || Modifier.isStatic(field.getModifiers())) {
                    //LOG.dd("insert %s ignore %s @Extendable or static", mClass.getSimpleName(), name);
                    continue;
                }

                // 自增主键不需要插入值，另外seqid默认为自增主键，
                if (field.getAnnotation(PrimaryKey.class) != null && pAutoIncrement) {
                    //LOG.dd("insert %s ignore %s @PrimaryKey", mClass.getSimpleName(), name);
                    continue;
                }

                // 如果存在排除字段列表，判断是否在其中，否则跳过
                if (this.mIncludeFields.size() > 0 && !this.mIncludeFields.contains(name)) {
                    //LOG.dd("insert %s ignore %s not in includes [%s]", mClass.getSimpleName(), name, Joiner.on(",").join(this.mIncludeFields));
                    continue;
                }

                // 如果存在包含字段列表，判断是否不在其中，否则跳过
                if (this.mExcludeFields.size() > 0 && this.mExcludeFields.contains(name)) {
                    //LOG.dd("insert %s ignore %s in excludes [%s]", mClass.getSimpleName(), name, Joiner.on(",").join(this.mExcludeFields));
                    continue;
                }

                // 冲突更新字段
                if (this.mDuplicates.contains(name)) {
                    updates.add("`" + name + "`=VALUES(`" + name + "`)");
                }

                // 插入内容
                fields.add(name);

                // 对应的值
                if (BeanUtil.isNumber(type)) {
                    // 数字类型的值
                    values.add(value == null ? "0" : String.valueOf(value));
                } else if (BeanUtil.isBoolean(type)){
                    // 布尔类型的值
                    values.add(value == null ? "false" : String.valueOf(value));
                } else {
                    // 其他类型的值
                    values.add(value == null ? "''" : quote(value.toString(), true));
                }
            }
            if(fields.size() == 0){
                throw new RuntimeException("无法保存数据，请检测SQL是否正确");
            }
            if(updates.size() > 0){
                return "INSERT INTO " + mTableName + " (`" + Joiner.on("`,`").join(fields) + "`) " +
                       "VALUES (" + Joiner.on(",").join(values) + ") " +
                       "ON DUPLICATE KEY UPDATE " + Joiner.on(",").join(updates);
            }else{
                return "INSERT INTO " + mTableName + " (`" + Joiner.on("`,`").join(fields) + "`) VALUES (" + Joiner.on(",").join(values) + ")";
            }
        } catch (Exception e) {
            LOG.error("build insert SQL error", e);
            throw new RuntimeException("cannot build insert SQL, please check the error.");
        }
    }

    /**
     * 构造 update 语句
     * @return update 语句
     */
    public String update(){
        try {
            List<String> sets         = new ArrayList<>();
            List<String> conditions   = new ArrayList<>();
            String       primaryKey   = DEFAULT_PRIMARY_KEY;
            long         primaryValue = 0;

            for (Field field : this.mClass.getDeclaredFields()) {
                field.setAccessible(true);
                String   name  = field.getName();
                Class<?> type  = field.getType();
                Object   value = field.get(mEntity);

                // 条件字段，不参与更新，最终作为 WHERE 条件
                if (this.mConditionsFields.contains(name)) {
                    conditions.add(equation(name, value, type));
                    continue;
                }

                // 如果属性标记为Extendable，则不将其值保存到数据库
                if (field.getAnnotation(Extendable.class) != null || Modifier.isStatic(field.getModifiers())) {
                    //LOG.dd("update %s ignore %s @Extendable or static", mClass.getSimpleName(), name);
                    continue;
                }

                // 自增主键不需要插入值，另外seqid默认为自增主键，
                if (field.getAnnotation(PrimaryKey.class) != null) {
                    //LOG.dd("update %s ignore %s @PrimaryKey", mClass.getSimpleName(), name);
                    primaryKey = name;
                    primaryValue = BeanUtil.isNumber(type) ? ((Number)value).longValue() : 0;
                    continue;
                }
                // 如果存在排除字段列表，判断是否在其中，否则跳过
                if (this.mIncludeFields.size() > 0 && !this.mIncludeFields.contains(name)) {
                    //LOG.dd("update %s ignore %s not in includes [%s]", mClass.getSimpleName(), name, Joiner.on(",").join(this.mIncludeFields));
                    continue;
                }

                // 如果存在包含字段列表，判断是否不在其中，否则跳过
                if (this.mExcludeFields.size() > 0 && this.mExcludeFields.contains(name)) {
                    //LOG.dd("update %s ignore %s in excludes [%s]", mClass.getSimpleName(), name, Joiner.on(",").join(this.mExcludeFields));
                    continue;
                }

                // 更新内容
                sets.add(equation(name, value, type));
            }
            if(sets.size() == 0){
                throw new RuntimeException("无法保存数据，请检测SQL是否正确");
            }
            if(conditions.size() > 0){
                // 存在条件时，不使用主键条件
                return "UPDATE " + mTableName + " SET " + Joiner.on(",").join(sets) + " WHERE " + Joiner.on(" AND ").join(conditions);
            }else{
                return "UPDATE " + mTableName + " SET " + Joiner.on(",").join(sets) + " WHERE `" + primaryKey + "`=" + primaryValue;
            }
        } catch (Exception e) {
            LOG.error("build update SQL error", e);
            throw new RuntimeException("cannot build update SQL, please check the error.");
        }
    }

    /**
     * 构造 delete 语句
     * @return delete 语句
     */
    public String delete(){
        try {
            String       primaryKey   = DEFAULT_PRIMARY_KEY;
            List<String> conditions   = new ArrayList<>();
            long         primaryValue = 0;

            for (Field field : this.mClass.getDeclaredFields()) {
                field.setAccessible(true);
                String   name  = field.getName();
                Class<?> type  = field.getType();
                Object   value = field.get(mEntity);

                // 条件字段，不参与更新，最终作为 WHERE 条件
                if (this.mConditionsFields.contains(name)) {
                    conditions.add(equation(name, value, type));
                    continue;
                }

                if (field.getAnnotation(PrimaryKey.class) != null) {
                    //LOG.dd("delete %s ignore %s @PrimaryKey", mClass.getSimpleName(), name);
                    primaryKey = name;
                    primaryValue = BeanUtil.isNumber(type) ? ((Number)value).longValue() : 0;
                }
            }
            if(conditions.size() > 0){
                // 存在条件时，不使用主键条件
                return "DELETE FROM " + mTableName + " WHERE " + Joiner.on(" AND ").join(conditions);
            }else{
                return "DELETE FROM " + mTableName + " WHERE `" + primaryKey + "`=" + primaryValue;
            }

        } catch (Exception e) {
            LOG.error("build delete SQL error", e);
            throw new RuntimeException("cannot build delete SQL, please check the error.");
        }
    }

    /**
     * 构造 select 语句
     * @return select 语句
     */
    public String find(){
        try {
            String       primaryKey   = DEFAULT_PRIMARY_KEY;
            List<String> fields       = new ArrayList<>();
            List<String> conditions   = new ArrayList<>();
            long         primaryValue = 0;

            for (Field field : this.mClass.getDeclaredFields()) {
                field.setAccessible(true);
                String   name  = field.getName();
                Class<?> type  = field.getType();
                Object   value = field.get(mEntity);

                // 条件字段，不参与更新，最终作为 WHERE 条件
                if (this.mConditionsFields.contains(name)) {
                    conditions.add(equation(name, value, type));
                }

                // 如果属性标记为Extendable，不读取
                if (field.getAnnotation(Extendable.class) != null || Modifier.isStatic(field.getModifiers())) {
                    //LOG.dd("find %s ignore %s @Extendable or static", mClass.getSimpleName(), name);
                    continue;
                }

                if (field.getAnnotation(PrimaryKey.class) != null) {
                    //LOG.dd("find %s ignore %s @PrimaryKey", mClass.getSimpleName(), name);
                    primaryKey = name;
                    primaryValue = BeanUtil.isNumber(type) ? ((Number)value).longValue() : 0;
                }

                // 读取内容
                fields.add(name);
            }
            if(conditions.size() > 0){
                // 存在条件时，不使用主键条件
                return "SELECT `" + Joiner.on("`,`").join(fields) + "` FROM " + mTableName + " WHERE " + Joiner.on(" AND ").join(conditions);
            }else{
                return "SELECT `" + Joiner.on("`,`").join(fields) + "` FROM " + mTableName + " WHERE " + primaryKey + "=" + primaryValue;
            }

        } catch (Exception e) {
            LOG.error("build select SQL error", e);
            throw new RuntimeException("cannot build select SQL, please check the error.");
        }
    }

    private String equation(String pKey, Object pValue, Class<?> pType){
        if (BeanUtil.isNumber(pType)) {
            // 数字类型的值
            return "`" + pKey + "`=" + (pValue == null ? "0" : String.valueOf(pValue));
        } else if (BeanUtil.isBoolean(pType)){
            // 布尔类型的值
            return "`" + pKey + "`=" + (pValue == null ? "false" : String.valueOf(pValue));
        } else {
            // 其他类型的值
            return "`" + pKey + "`=" + (pValue == null ? "''" : quote(pValue.toString(), true));
        }
    }

    private String getTableName() {
        Table table = mClass.getAnnotation(Table.class);
        if (table != null) {
            return table.name();
        }
        return mClass.getSimpleName().toLowerCase();
    }

    /**
     * 在自定义SQL时，用于生成字符型value
     * 会对特殊字符进行处理以防止SQL注入
     * 比如：value为 ABC\'你好\"\r\n 输出后为 'ABC\'你好"\r\n'
     * @param pValue value值
     * @return 处理后的value值
     */
    public static String quote(String pValue, boolean pNeedQuote){
        if(pValue != null){
            // NUL (0x00) --> \0
            // BS  (0x08) --> \b
            // TAB (0x09) --> \t
            // LF  (0x0a) --> \n
            // CR  (0x0d) --> \r
            // SUB (0x1a) --> \Z
            // "   (0x22) --> \"
            // '   (0x27) --> \'
            // \   (0x5c) --> \\
            StringBuilder result = new StringBuilder();
            if(pNeedQuote){result.append("'");}
            char[] chars = pValue.toCharArray();
            for(char c : chars){
                switch((int)c){
                    case 0x00 : // NUL (0x00) --> \0
                        result.append("\\0");
                        break;
                    case 0x08 : // BS  (0x08) --> \b
                        result.append("\\b");
                        break;
                    case 0x09 : // TAB (0x09) --> \t
                        result.append("\\t");
                        break;
                    case 0x0a : // LF  (0x0a) --> \n
                        result.append("\\n");
                        break;
                    case 0x0d : // CR  (0x0d) --> \r
                        result.append("\\r");
                        break;
                    case 0x1a : // SUB (0x1a) --> \Z
                        result.append("\\Z");
                        break;
                    case 0x22 : // "   (0x22) --> \"
                        result.append("\"");
                        break;
                    case 0x27 : // '   (0x27) --> \'
                        result.append("\\\'");
                        break;
                    case 0x5c : // \   (0x5c) --> \\
                        result.append("\\\\");
                        break;
                    default:
                        result.append(c);
                }
            }
            if(pNeedQuote){result.append("'");}
            return result.toString();
        }
        return "''";
    }

    public static void main(String[] args){

    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
