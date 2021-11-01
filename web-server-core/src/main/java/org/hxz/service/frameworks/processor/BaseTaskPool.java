package org.hxz.service.frameworks.processor;

import org.hxz.service.frameworks.misc.Config;
import org.hxz.service.frameworks.utils.ThreadPoolUtil;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by SPM on 2017/3/6.
 *
 */
@SuppressWarnings("unused")
public class BaseTaskPool{
    // ===========================================================
    // Constants
    // ===========================================================


    // ===========================================================
    // Fields
    // ===========================================================
    private String mName;
    private ThreadPoolExecutor mPool;
    private final AtomicBoolean mInitialed = new AtomicBoolean(false);
    private int mCore      = Config.getPoolCore();
    private int mMax       = Config.getPoolMax();
    private int mKeepAlive = Config.getPoolKeepAlive();
    private int mQueue     = Config.getPoolQueue();

    // ===========================================================
    // Constructors
    // ===========================================================
    public BaseTaskPool(String pName){
        this.mName = pName;
    }
    public BaseTaskPool(String pName, int pQueueSize){
        this.mQueue = pQueueSize;
        this.mName = pName;
    }
    public BaseTaskPool(String pName, int pCore, int pMax, int pQueueSize){
        this.mCore = pCore;
        this.mMax = pMax;
        this.mQueue = pQueueSize;
        this.mName = pName;
    }

    // ===========================================================
    // Getter &amp; Setter
    // ===========================================================


    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    @Override
    public String toString() {
        return String.format("Pool:%s IdleQueue:%s Waiting:%d Completed:%d",
                mName,
                mPool.getQueue().remainingCapacity(),
                mPool.getTaskCount() - mPool.getCompletedTaskCount(),
                mPool.getCompletedTaskCount());
    }

    // ===========================================================
    // Methods
    // ===========================================================
    public void init() {
        if(mInitialed.compareAndSet(false, true)) {
            mPool = new ThreadPoolExecutor(
                    mCore,
                    mMax,
                    mKeepAlive,
                    TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(mQueue),
                    new ThreadPoolExecutor.CallerRunsPolicy()
            );
        }
    }

    public void release() {
        ThreadPoolUtil.shutdownPoolGracefully(mPool, mName);
    }

    public void release(boolean pShowLogs) {
        ThreadPoolUtil.shutdownPoolGracefully(mPool, mName, pShowLogs);
    }

    public void release(long pTimeout, Future<?>... pFutures) {
        ThreadPoolUtil.shutdownPoolGracefully(mPool, mName, pTimeout, pFutures);
    }

    public void release(long pTimeout, boolean pShowLogs, Future<?>... pFutures) {
        ThreadPoolUtil.shutdownPoolGracefully(mPool, mName, pTimeout, pShowLogs, pFutures);
    }

    public void queue(Runnable pTask) {
        mPool.execute(pTask);
    }

    public Future<?> submit(Runnable pTask) {
        return mPool.submit(pTask);
    }

    public boolean hasTasks(){
        return mPool.getTaskCount() - mPool.getCompletedTaskCount() > 0;
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
