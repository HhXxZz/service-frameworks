package base.service.frameworks.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 原子相关工具类
 * Created by mi on 2018/10/19
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class AtomicUtil {

    // ===========================================================
    // Constants
    // ===========================================================


    // ===========================================================
    // Fields
    // ===========================================================
    private static CycleCounter mCounter = new CycleCounter(100000);

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

    /**
     * 产生每秒有限唯一ID，每秒产生ID不能超过100000个
     *
     * @return 唯一ID
     */
    public static String generateFiniteIDInSecond(){
        return String.format("%d%05d", System.currentTimeMillis()/1000, mCounter.incrementAndGet());
    }
    @Deprecated
    public static String getLimitUniqueID(){
        return generateFiniteIDInSecond();
    }



    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    public final static class CycleCounter {
        private final int           max;
        private AtomicInteger count;

        CycleCounter(int max) {
            if (max < 1) { throw new IllegalArgumentException(); }
            this.max = max;
            this.count = new AtomicInteger();
        }

        int incrementAndGet() {
            return count.updateAndGet(x -> (x < max - 1) ? x + 1 : 1);
        }
        public int resetAndGet(){
            return count.updateAndGet(x -> 1);
        }
        public void setCount(int pCount){
            count.updateAndGet(x -> pCount);
        }
    }
}
