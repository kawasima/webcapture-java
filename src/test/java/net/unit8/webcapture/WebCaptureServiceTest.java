package net.unit8.webcapture;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Test of capturing.
 */
public class WebCaptureServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(WebCaptureServiceTest.class);
    @Test
    public void test() throws InterruptedException {
        final WebCaptureService service = new WebCaptureService(5);
        service.setCaptureDirectory(new File("target/capture"));

        ExecutorService clients = Executors.newFixedThreadPool(10);
        for(int i=0; i < 30; i++) {
            final int currentIter = i;
            clients.submit(new Runnable() {
                @Override
                public void run() {
                    long t1 = System.currentTimeMillis();
                    try {
                        //service.capture("http://ja.wikipedia.org/wiki/%E7%89%B9%E5%88%A5:Random");
                        service.capture("http://localhost/");
                    } catch (Exception ex) {

                    }
                    logger.info(currentIter +":"+(System.currentTimeMillis() - t1));
                }
            });
        }
        clients.shutdown();
        while (!clients.awaitTermination(1, TimeUnit.SECONDS)) {
            logger.debug("Executing...");
        }
        service.shutdown();
    }
}
