package base.service.frameworks.misc;

/**
 * Created by someone on 2020/6/17 22:56.
 */
@SuppressWarnings("unused")
public class CustomCode implements Code{
    // ===========================================================
    // Constants
    // ===========================================================


    // ===========================================================
    // Fields
    // ===========================================================
    private final int    mCode;
    private final String mMessage;

    // ===========================================================
    // Constructors
    // ===========================================================
    public CustomCode(int pCode, String pMessage){
        this.mCode = pCode;
        this.mMessage = pMessage;
    }

    // ===========================================================
    // Getter &amp; Setter
    // ===========================================================


    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    @Override
    public int getCode() {
        return mCode;
    }

    @Override
    public String getMessage() {
        return mMessage;
    }

    // ===========================================================
    // Methods
    // ===========================================================


    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
