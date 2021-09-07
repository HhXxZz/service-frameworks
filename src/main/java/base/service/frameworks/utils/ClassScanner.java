package base.service.frameworks.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by someone on 2020/12/1 15:25.
 */
public class ClassScanner {
    // ===========================================================
    // Constants
    // ===========================================================
    private static final Logger LOG = LogManager.getLogger(ClassScanner.class);


    private static final String CLASS_FILE_NAME_EXTENSION = ".class";

    // ===========================================================
    // Fields
    // ===========================================================
    public String                      mPackage;
    public Class<? extends Annotation> mAnnotation;
    public boolean                     mEnableLogger;

    // ===========================================================
    // Constructors
    // ===========================================================
    private ClassScanner(String pPackage) {
        this.mPackage = pPackage;
    }

    // ===========================================================
    // Getter &amp; Setter
    // ===========================================================


    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================


    // ===========================================================
    // Methods
    // ===========================================================
    public static ClassScanner in(String pPackageName) {
        return new ClassScanner(pPackageName);
    }

    public ClassScanner withAnnotation(Class<? extends Annotation> pAnnotation) {
        this.mAnnotation = pAnnotation;
        return this;
    }

    public ClassScanner enableLogger(boolean pEnableLogger) {
        this.mEnableLogger = pEnableLogger;
        return this;
    }

    public List<Class<?>> scan() {
        return scanInherited(Object.class);
    }

    public <T> List<Class<? extends T>> scanInherited(Class<T> pType) {
        List<Class<? extends T>> classes = new ArrayList<>();
        ClassLoader    loader  = ClassScanner.class.getClassLoader();
        try {
            String           packagePath = this.mPackage.replace(".", "/");
            Enumeration<URL> urls        = loader.getResources(packagePath);
            while (urls != null && urls.hasMoreElements()) {
                URL    url      = urls.nextElement();
                String protocol = url.getProtocol();
                if ("jar".equals(protocol)) {
                    String path    = url.toString();
                    String jarPath = url.toString().substring(OS.isWindows()?10:9, path.length() - this.mPackage.length() - 2);
                    classes.addAll(scanJar(Paths.get(jarPath), pType));
                } else if ("file".equals(protocol)) {
                    classes.addAll(scanDirectory(Paths.get(url.toURI()), this.mPackage, pType));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }

    private <T> List<Class<? extends T>> scanDirectory(Path pPath, String pPackage, Class<T> pType) throws IOException {
        List<Class<? extends T>> classes = new ArrayList<>();
        if (!Files.exists(pPath)) {
            return classes;
        }

        Files.list(pPath).forEach(path -> {
            String name        = path.getFileName().toString();
            String packageName = pPackage + (StringUtil.isEmpty(pPackage) ? "" : ".") + name;
            if (Files.isDirectory(path)) {
                try {
                    classes.addAll(scanDirectory(path, packageName, pType));
                } catch (Exception e) {
                    LOG.warn("read path: {} failed. {}", path.toFile().getPath());
                }
            } else if (path.toString().endsWith(CLASS_FILE_NAME_EXTENSION)) {
                try {
                    Class<? extends T> clazz = convertToClass(packageName, pType);
                    if (clazz != null) {
                        classes.add(clazz);
                    }
                } catch (Exception e) {
                    LOG.warn("class not found: {}", packageName);
                }
            }
        });
        return classes;
    }

    private <T> List<Class<? extends T>> scanJar(Path pPath, Class<T> pType) {
        List<Class<? extends T>> classes = new ArrayList<>();
        JarFile        jar;
        try {
            jar = new JarFile(pPath.toFile());
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry    = entries.nextElement();
                String   fileName = entry.getName().replace("/", ".");

                Class<? extends T> clazz = convertToClass(fileName, pType);
                if (clazz != null) {
                    classes.add(clazz);
                }
            }
        } catch (IOException e) {
            LOG.error("not a jar file: {}", pPath.toString());
        }
        return classes;
    }

    private <T> Class<? extends T> convertToClass(String pFileName, Class<T> pType) {
        if (!StringUtil.isEmpty(pFileName) && pFileName.endsWith(CLASS_FILE_NAME_EXTENSION) && pFileName.startsWith(this.mPackage)) {
            try {
                Class<?> clazz = Class.forName(pFileName.substring(0, pFileName.length() - 6));
                if (this.mAnnotation != null && !clazz.isAnnotationPresent(this.mAnnotation)) {
                    return null;
                }
                if (pType != null && !pType.isAssignableFrom(clazz)) {
                    return null;
                }
                if (this.mEnableLogger) {
                    LOG.debug("    class: {}", pFileName);
                }

                //noinspection unchecked
                return (Class<? extends T>) clazz;
            } catch (Exception e) {
                LOG.error("class not found: {}", pFileName);
            }
        }
        return null;
    }

    public static void main(String[] args) {
        List<Class<?>> classes = ClassScanner.in("base.service.frameworks.web.server.core.utils").enableLogger(true).scan();
        long           start   = System.nanoTime();
        classes = ClassScanner.in("base.service.frameworks.web.server.core.utils").enableLogger(true).scan();
        long           end     = System.nanoTime();
        LOG.debug("find {} classes, cost {}", classes.size(), (end - start) / 1000000d);
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}