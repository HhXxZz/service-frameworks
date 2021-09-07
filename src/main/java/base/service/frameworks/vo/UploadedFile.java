package base.service.frameworks.vo;

import com.google.gson.annotations.Expose;

import java.nio.charset.Charset;
import java.nio.file.Path;

/**
 * <pre>
 * Created by someone on 2019-05-22.
 *
 * </pre>
 */
public class UploadedFile {
    // ===========================================================
    // Constants
    // ===========================================================


    // ===========================================================
    // Fields
    // ===========================================================
    public String    content;
    public String    originName;
    public String    extension;
    public byte[]    raw;
    public Charset   charset;
    // 以下内容建议在完成文件罗盘后填充
    public String    name;
    @Expose
    public long      length;
    @Expose
    public String    size;
    @Expose
    public String    url;
    @Expose
    public Thumbnail thumbnail;
    public Path      path;
    public boolean   isWritten;


    // ===========================================================
    // Constructors
    // ===========================================================


    // ===========================================================
    // Getter &amp; Setter
    // ===========================================================


    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @Override
    public String toString() {
        return "{name:\"" + originName + "\", ext:\"" + extension + "\", size:" + (raw != null ? raw.length : 0) + ", charset:\"" + charset + "\"}";
    }


    // ===========================================================
    // Methods
    // ===========================================================


    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
