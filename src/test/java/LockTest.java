import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by hxz on 2021/7/8 16:01.
 */

public class LockTest {

    private ReentrantLock lock = new ReentrantLock();
    private Condition connected = lock.newCondition();

    private void signalAvailableHandler() {
        lock.lock();
        try {
            connected.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private boolean waitingForHandler() throws InterruptedException {
        lock.lock();
        try {
            return connected.await(5000, TimeUnit.MILLISECONDS);
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        LockTest lockTes = new LockTest();

        try {
            while (true){
                lockTes.waitingForHandler();
                System.out.println("ssss");

            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }


    }

}
