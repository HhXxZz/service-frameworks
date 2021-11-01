package org.hxz.service.frameworks.utils;

import org.hxz.service.frameworks.vo.Unzipped;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by someone on 2017-02-23.
 * 
 * 文件工具类
 */
@SuppressWarnings({"unused", "WeakerAccess", "UnusedReturnValue"})
public class FileUtil {
    // ===========================================================
    // Constants
    // ===========================================================


    // ===========================================================
    // Fields
    // ===========================================================


    // ===========================================================
    // Constructors
    // ===========================================================
    private static final Logger LOG = LogManager.getLogger(FileUtil.class);

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
     * 获取文件后缀名, 如 ".png" 或 ".jpg".
     *
     * @param pURI 文件URI
     * @return 包含(.)的后缀名; 无后缀或uri为空则返回""
     */
    public static String getExtension(String pURI) {
        if (!StringUtil.isEmpty(pURI)) {
            int sep = pURI.lastIndexOf(File.separator);
            int dot = pURI.lastIndexOf(".");
            if (dot >= 0 && sep < dot) {
                return pURI.substring(dot);
            }
        }
        return "";
    }

    /**
     * 获取文件后缀名, 如 "png" 或 "jpg".
     *
     * @param pURI 文件URI
     * @return 不包含(.)的后缀名; 无后缀或uri为空则返回""
     */
    public static String getExtensionWithoutDot(String pURI) {
        if (!StringUtil.isEmpty(pURI)) {
            int sep = pURI.lastIndexOf(File.separator);
            int dot = pURI.lastIndexOf(".");
            if (dot >= 0 && sep < dot) {
                return pURI.substring(dot + 1);
            }
        }
        return "";
    }

    /**
     * 转换byte大小为可阅读大小字符串
     *
     * @param pBytes    bytes大小
     * @param pIsSIUnit 是否使用SI标准
     * @return 可阅读的大小字符串, 如：<br/>
     * <table border="0" cellpadding="2" cellspacing="0" style="padding-right:10px;">
     * <tr style="font-weight:bold;"><td>pIsSIUnit</td><td>false</td><td>true</td></tr>
     * <tr><td>0</td><td>0 B</td><td>0 B</td></tr>
     * <tr><td>27</td><td>27 B</td><td>27 B</td></tr>
     * <tr><td>999</td><td>999 B</td><td>999 B</td></tr>
     * <tr><td>1000</td><td>1000 B</td><td>1.00 kB</td></tr>
     * <tr><td>1023</td><td>1023 B</td><td>1.02 kB</td></tr>
     * <tr><td>1024</td><td>1.00 KiB</td><td>1.02 kB</td></tr>
     * <tr><td>1728</td><td>1.69 KiB</td><td>1.73 kB</td></tr>
     * <tr><td>110592</td><td>108.00 KiB</td><td>110.59 kB</td></tr>
     * <tr><td>7077888</td><td>6.75 MiB</td><td>7.08 MB</td></tr>
     * <tr><td>452984832</td><td>432.00 MiB</td><td>452.98 MB</td></tr>
     * <tr><td>28991029248</td><td>27.00 GiB</td><td>28.99 GB</td></tr>
     * <tr><td>1855425871872</td><td>1.69 TiB</td><td>1.86 TB</td></tr>
     * <tr><td>9223372036854775807</td><td>8.00 EiB</td><td>9.22 EB</td></tr>
     * </table>
     */
    public static String getHumanReadableSize(long pBytes, boolean pIsSIUnit) {
        StringBuilder result = new StringBuilder();
        int           unit   = pIsSIUnit ? 1000 : 1024;
        if (pBytes < unit) {
            result.append(pBytes).append(" B");
        } else {
            int exp = (int) (Math.log(pBytes) / Math.log(unit));

            // true 为SI标准，使用Gigabyte单位，原本单位需要增加i，入GiB，考虑老百姓的认知，故去掉i
            // String pre = (pIsSIUnit ? "kMGTPE" : "KMGTPE").charAt(exp - 1) +
            // (pIsSIUnit ? "" : "i");
            String pre = (pIsSIUnit ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (pIsSIUnit ? "" : "");
            result.append(String.format(Locale.getDefault(), "%.1f %sB", pBytes / Math.pow(unit, exp), pre));
        }
        return result.toString();
    }

    /**
     * 判断文件是否存在
     *
     * @param pPath                 路径
     * @param pAlsoCheckIfDirectory 是否同时判断为目录
     * @return true为存在
     */
    public static boolean isExists(String pPath, boolean pAlsoCheckIfDirectory) {
        if (pAlsoCheckIfDirectory) {
            return Files.isDirectory(Paths.get(pPath));
        } else {
            return Files.exists(Paths.get(pPath));
        }
    }

    /**
     * 写文件
     *
     * @param pPath    文件 path 对象
     * @param pContent 文件二进制内容
     * @param pOptions 写入选项，详见 {@link StandardOpenOption}
     * @return true 为写入成功
     */
    public static boolean write(Path pPath, byte[] pContent, OpenOption... pOptions) {
        try {
            if (!Files.isDirectory(pPath.getParent())) {
                boolean mkdir = pPath.getParent().toFile().mkdirs();
                if (!mkdir) {
                    LOG.error("write file failed {} mkdirs parent failed", pPath.toFile().getPath());
                    return false;
                }
            }
            Files.write(pPath, pContent, pOptions);
            return true;
        } catch (IOException e) {
            LOG.error("write file failed", e);
        }
        return false;
    }

    /**
     * 删除文件
     *
     * @param pPath 文件path对象
     */
    public static void delete(Path pPath) {
        try {
            Files.walk(pPath)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(file -> {
                        boolean deleted = file.delete();
                        LOG.debug("delete file : {} ({})", file.getAbsolutePath(), deleted);
                    });
        } catch (IOException e) {
            LOG.error("delete file failed", e);
        }
    }

    public static void main(String[] args) {
        delete(Paths.get("D:\\Docs.EFG"));
    }

    /**
     * 获取zip包中的内容，并分装到map中
     *
     * @param pZipBuffer zip包内容
     * @return 压缩文件map
     */
    public static Map<String, Unzipped> unzip(byte[] pZipBuffer) {
        return unzip(pZipBuffer, true);
    }

    /**
     * 获取zip包中的内容，并分装到map中
     *
     * @param pZipBuffer zip包内容
     * @return 压缩文件map
     */
    public static Map<String, Unzipped> unzip(byte[] pZipBuffer, boolean pShowLog) {
        Map<String, Unzipped> unzippedMap = new HashMap<>();
        if (pZipBuffer != null) {
            InputStream    input    = new BufferedInputStream(new ByteArrayInputStream(pZipBuffer));
            ZipInputStream zipInput = new ZipInputStream(input);

            while (true) {
                try {
                    ZipEntry entry = zipInput.getNextEntry();
                    if (entry == null) {
                        break;
                    }
                    if (entry.getSize() > 0) {
                        Unzipped file = new Unzipped();
                        file.name = entry.getName();
                        file.length = entry.getSize();
                        file.size = FileUtil.getHumanReadableSize(file.length, false);
                        // 调整文件分隔符为linux分隔符/
                        file.name = file.name.replace("\\", "/");
                        ByteArrayOutputStream contentByteArray = new ByteArrayOutputStream();
                        byte[]                content          = new byte[1024 * 1024];
                        int                   read;
                        long                  start            = System.nanoTime();
                        while ((read = zipInput.read(content)) > -1) {
                            contentByteArray.write(content, 0, read);
                        }
                        long end = System.nanoTime();
                        file.raw = contentByteArray.toByteArray();
                        file.hash = MD5.getMD5(file.raw);
                        if (pShowLog) {
                            LOG.debug("read entry size[ {} ] hash[{}] - {}-50s cost[{} ns]",
                                    file.size, file.hash, file.name, end - start);
                        }
                        unzippedMap.put(file.name, file);
                    }
                } catch (IOException e) {
                    LOG.error("read zip entry failed", e);
                }
            }
            try {
                zipInput.closeEntry();
                zipInput.close();
            } catch (IOException e) {
                LOG.error("close zip stream failed", e);
            }
        }
        return unzippedMap;
    }

    public static File toFile(URL pURL) {
        if(pURL.getProtocol().equals("file")) {
            try {
                return new File(pURL.toURI()); // Accepts escaped characters like %20.
            } catch (URISyntaxException e) { // URL.toURI() doesn't escape chars.
                return new File(pURL.getPath()); // Accepts non-escaped chars like space.
            }
        }
        throw new IllegalArgumentException();
    }

    public static byte[] input2byte(InputStream inStream)
            throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        int rc = 0;
        while ((rc = inStream.read(buff, 0, 1024)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        return swapStream.toByteArray();
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}