package org.hxz.service.frameworks.vo;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * <pre>
 * Created by someone on 2018-09-06.
 *
 * </pre>
 */
public class HTTPResultType<T,C> implements ParameterizedType {
    // ===========================================================
    // Constants
    // ===========================================================


    // ===========================================================
    // Fields
    // ===========================================================
    private final Class<T> mRawType;
    private final Class<C> mType;

    // ===========================================================
    // Constructors
    // ===========================================================
    public HTTPResultType(Class<T> pRawClass, Class<C> pClass) {
        this.mType = pClass;
        this.mRawType = pRawClass;
    }

    // ===========================================================
    // Getter &amp; Setter
    // ===========================================================


    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    @Override
    public Type[] getActualTypeArguments() {
        return new Type[] {mType};
    }

    @Override
    public Type getRawType() {
        return mRawType;
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
