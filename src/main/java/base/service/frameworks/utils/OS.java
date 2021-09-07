package base.service.frameworks.utils;

/**
 * <pre>
 * Created by someone on 2018-10-23.
 *
 * </pre>
 */
@SuppressWarnings("unused")
public class OS {
    // ===========================================================
    // Constants
    // ===========================================================


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
    public static String osName() {
        return System.getProperty("os.name").toLowerCase();
    }

    public static String osVersion() {
        return System.getProperty("os.version").toLowerCase();
    }

    public static boolean isLinux() {
        return osName().contains("linux");
    }

    public static boolean isMacOS() {
        String os = osName();
        return os.contains("mac") && os.contains("os") && !os.contains("x");
    }

    public static boolean isMacOSX() {
        String os = osName();
        return os.contains("mac") && os.contains("os") && os.contains("x");
    }

    public static boolean isWindows() {
        return osName().contains("windows");
    }

    public static boolean isOS2() {
        return osName().contains("os/2");
    }

    public static boolean isSolaris() {
        return osName().contains("solaris");
    }

    public static boolean isSunOS() {
        return osName().contains("sunos");
    }

    public static boolean isMPEiX() {
        return osName().contains("mpe/ix");
    }

    public static boolean isHPUX() {
        return osName().contains("hp-ux");
    }

    public static boolean isAix() {
        return osName().contains("aix");
    }

    public static boolean isOS390() {
        return osName().contains("os/390");
    }

    public static boolean isFreeBSD() {
        return osName().contains("freebsd");
    }

    public static boolean isIrix() {
        return osName().contains("irix");
    }

    public static boolean isDigitalUnix() {
        String os = osName();
        return os.contains("digital") && os.contains("unix");
    }

    public static boolean isNetWare() {
        return osName().contains("netware");
    }

    public static boolean isOSF1() {
        return osName().contains("osf1");
    }

    public static boolean isOpenVMS() {
        return osName().contains("openvms");
    }


    public static void main(String[] args){
        String version = osVersion();
        System.out.println(osName());
        System.out.println(osVersion());
        System.out.println(version.substring(0, version.indexOf("-")));

    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
