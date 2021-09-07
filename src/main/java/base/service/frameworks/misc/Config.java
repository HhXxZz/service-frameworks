package base.service.frameworks.misc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by someone on 2017-02-08.
 *
 */
@SuppressWarnings({"unused", "WeakerAccess", "FieldCanBeLocal"})
public class Config {
    // ===========================================================
    // Constants
    // ===========================================================
    private static final Logger LOG = LogManager.getLogger(Config.class);

    private static final AtomicBoolean Initialed = new AtomicBoolean(false);

    private static final String CFG_PORT              = "Port";
    private static final String CFG_DEBUG             = "Debug";
    private static final String CFG_POOL_CORE         = "Pool.Core";
    private static final String CFG_POOL_MAX          = "Pool.Max";
    private static final String CFG_POOL_KEEPALIVE    = "Pool.KeepAlive";
    private static final String CFG_POOL_QUEUE        = "Pool.Queue";
    private static final String CFG_DAO_ENABLED       = "DAO.Enabled";

    private static int     mPort             = -1;
    private static boolean mDebug            = true;
    private static int     mPoolCore         = 4;
    private static int     mPoolMax          = 8;
    private static int     mPoolKeepAlive    = 30;
    private static int     mPoolQueue        = 200;
    private static boolean mDaoEnabled       = false;

    private static Properties mConfigs;

    static {
        loadConfig();
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
    public static boolean isInitialed() {return Initialed.get(); }

    public static int getPort() {
        ready("getPort");
        return mPort;
    }

    public static boolean isDebug(){
        ready("isDebug");
        return mDebug;
    }

    public static boolean isDAOEnabled(){
        ready("isDAOEnabled");
        return mDaoEnabled;
    }


    public static int getPoolCore() {
        ready("getPoolCore");
        return mPoolCore;
    }

    public static int getPoolMax() {
        ready("getPoolMax");
        return mPoolMax;
    }

    public static int getPoolKeepAlive() {
        ready("getPoolKeepAlive");
        return mPoolKeepAlive;
    }

    public static int getPoolQueue() {
        ready("getPoolQueue");
        return mPoolQueue;
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================


    // ===========================================================
    // Methods
    // ===========================================================
    private static void loadConfig() {
        if(Initialed.compareAndSet(false, true)) {
            InputStream ins = null;
            try {
                ins = Config.class.getClassLoader().getResourceAsStream("server.properties");
                mConfigs = new Properties();
                if(ins != null){
                    mConfigs.load(ins);
                }else{
                    LOG.error("MISSING : server.properties !");
                    Initialed.set(false);
                    mConfigs.load(new StringReader(""));
                }
                mPort = Integer.parseInt(mConfigs.getProperty(CFG_PORT, String.valueOf(mPort)));
                mDebug = Boolean.parseBoolean(mConfigs.getProperty(CFG_DEBUG, String.valueOf(mDebug)));
                mDaoEnabled = Boolean.parseBoolean(mConfigs.getProperty(CFG_DAO_ENABLED, String.valueOf(mDaoEnabled)));
                mPoolCore = Integer.parseInt(mConfigs.getProperty(CFG_POOL_CORE, String.valueOf(mPoolCore)));
                mPoolMax = Integer.parseInt(mConfigs.getProperty(CFG_POOL_MAX, String.valueOf(mPoolMax)));
                mPoolKeepAlive = Integer.parseInt(mConfigs.getProperty(CFG_POOL_KEEPALIVE, String.valueOf(mPoolKeepAlive)));
                mPoolQueue = Integer.parseInt(mConfigs.getProperty(CFG_POOL_QUEUE, String.valueOf(mPoolQueue)));
                LOG.info("{ port:%d, debug:%s, dao:%s, pool:{core:%d, max:%d, keepalive:%d, queue:%d}}",
                        mPort, mDebug, mDaoEnabled, mPoolCore, mPoolMax, mPoolKeepAlive, mPoolQueue);
            } catch (IOException ex) {
                LOG.error("Parsing Error : server.properties !",ex);
            } finally {
                if (ins != null) {
                    try {
                        ins.close();
                    } catch (IOException e) {
                        LOG.error("Close fail : server.properties !",e);
                    }
                }
            }
        }
    }

    public static String getProperty(String pKey, String pDefault){
        ready(pKey);
        if(Initialed.get()){
            if(mConfigs != null){
                return mConfigs.getProperty(pKey, pDefault);
            }
        }
        return pDefault;
    }

    public static int getIntProperty(String pKey, int pDefault){
        ready(pKey);
        if(Initialed.get()){
            if(mConfigs != null){
                try {
                    return Integer.parseInt(mConfigs.getProperty(pKey));
                } catch (NumberFormatException ignore) {}
            }
        }
        return pDefault;
    }

    public static long getLongProperty(String pKey, long pDefault){
        ready(pKey);
        if(Initialed.get()){
            if(mConfigs != null){
                try {
                    return Long.parseLong(mConfigs.getProperty(pKey));
                } catch (NumberFormatException ignore) {}
            }
        }
        return pDefault;
    }

    public static boolean getBooleanProperty(String pKey, boolean pDefault){
        ready(pKey);
        if(Initialed.get()){
            if(mConfigs != null){
                try {
                    String value = mConfigs.getProperty(pKey, pDefault ? "true" : "false").toLowerCase();
                    return "true".equals(value) || "on".equals(value) || "1".equals(value);
                } catch (NumberFormatException ignore) {}
            }
        }
        return pDefault;
    }

    private static void ready(String pFunction){
        if(!Initialed.get()){
           // LOG.warn("Server configs unavailable! Using default [%s]", pFunction);
        }
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
