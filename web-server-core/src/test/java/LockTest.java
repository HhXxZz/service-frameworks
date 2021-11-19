import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by hxz on 2021/7/8 16:01.
 */

public class LockTest {

    private ReentrantLock lock = new ReentrantLock();
    private Condition connected = lock.newCondition();

    private CountDownLatch countDownLatch = new CountDownLatch(3);
    private CyclicBarrier cyclicBarrier = new CyclicBarrier(3);


    private void signalAvailableHandler() {
        lock.lock();
        System.out.println("1 L");
        try {
            connected.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private boolean waitingForHandler() throws InterruptedException {
        lock.lock();
        System.out.println("2 L");
        try {
//            return connected.await(1000, TimeUnit.MILLISECONDS);
            connected.await();
            System.out.println(Thread.currentThread().getName());
            return false;
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        LockTest lockTes = new LockTest();
        ExecutorService executorService = Executors.newFixedThreadPool(20);


        new Thread(() -> {
            for (int i=0;i<2;i++){
                executorService.submit(()->{
                    try {
                        lockTes.waitingForHandler();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            }
        }).start();

        Thread.sleep(500);
        lockTes.signalAvailableHandler();



    }

}
