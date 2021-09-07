package base.service.frameworks.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by someone on 2017-02-09.
 *
 */
@SuppressWarnings("unused")
public class ThreadPoolUtil {
    // ===========================================================
    // Constants
    // ===========================================================
    private static final Logger LOG = LogManager.getLogger(ThreadPoolUtil.class);

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
    public static void shutdownPoolGracefully(ThreadPoolExecutor pPool, String pPoolName){
        shutdownPoolGracefully(pPool, pPoolName, true);
    }
    public static void shutdownPoolGracefully(ThreadPoolExecutor pPool, String pPoolName, boolean pShowLogs){
        if (pPool != null && !pPool.isShutdown()) {
            pPool.shutdown();
            int checkCount;
            try {
                while(!pPool.awaitTermination(1000, TimeUnit.MILLISECONDS)){
                    if(pShowLogs){
                        LOG.info("wait {} finish {}", pPoolName==null?"NoNamePool":pPoolName, pPool.toString());
                    }
                    checkCount = pPool.getActiveCount() + pPool.getQueue().size();
                    if(checkCount == 0){
                        break;
                    }
                }
            } catch (InterruptedException ignored) {
            }
        }
    }

    public static void shutdownPoolGracefully(ThreadPoolExecutor pPool, String pPoolName, long pTimeout, Future<?>... pFutures){
        shutdownPoolGracefully(pPool, pPoolName, pTimeout, true, pFutures);
    }
    public static void shutdownPoolGracefully(ThreadPoolExecutor pPool, String pPoolName, long pTimeout, boolean pShowLogs, Future<?>... pFutures){
        if (pPool != null && !pPool.isShutdown()) {
            pPool.shutdown();
            long timeout = pTimeout < 2000 ? 2000 : (pTimeout/1000*1000);
            int checkCount;
            try {
                while(!pPool.awaitTermination(1000, TimeUnit.MILLISECONDS)){
                    if(pShowLogs){
                        LOG.info("wait {} finish {}", pPoolName==null?"NoNamePool":pPoolName, pPool.toString());
                    }
                    checkCount = pPool.getActiveCount() + pPool.getQueue().size();
                    timeout -= 1000;
                    if(timeout <= 0 || checkCount == 0){
                        break;
                    }
                }
                if(timeout <= 0){
                    if(pShowLogs){
                        LOG.info("wait {} finish timeout(%d) {}", pPoolName==null?"NoNamePool":pPoolName, pTimeout, pPool.toString());
                    }
                    if(pFutures != null){
                        for(Future<?> future : pFutures){
                            if(future != null){
                                future.cancel(true);
                            }
                        }
                    }
                }
            } catch (InterruptedException ignored) {
            }
        }
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
