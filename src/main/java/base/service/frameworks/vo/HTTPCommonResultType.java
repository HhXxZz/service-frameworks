package base.service.frameworks.vo;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * <pre>
 * Created by someone on 2018-09-06.
 *
 * </pre>
 */
public class HTTPCommonResultType implements ParameterizedType {
    // ===========================================================
    // Constants
    // ===========================================================


    // ===========================================================
    // Fields
    // ===========================================================
    private Type type;

    // ===========================================================
    // Constructors
    // ===========================================================
    public HTTPCommonResultType(Type type) {
        this.type = type;
    }

    // ===========================================================
    // Getter &amp; Setter
    // ===========================================================


    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    @Override
    public Type[] getActualTypeArguments() {
        return new Type[] {type};
    }

    @Override
    public Type getRawType() {
        return HTTPCommonResult.class;
    }

    @Override
    public Type getOwnerType() {
        return null;
    }

    // ===========================================================
    // Methods
    // ===========================================================


    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
