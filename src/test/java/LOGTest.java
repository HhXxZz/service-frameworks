import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by hxz on 2021/6/19 15:45.
 */

public class LOGTest {
    static Logger logger = LogManager.getLogger(LOGTest.class);


    public static void main(String[] args) {
        logger.info("info");
        logger.warn("warn");
        logger.error("error");
        logger.debug("debug");

    }

}
