package net.unit8.webcapture;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Test of capturing.
 */
public class WebCaptureServiceTest {
    @Test
    public void test() throws InterruptedException {
        final WebCaptureService service = new WebCaptureService(2);

        ExecutorService clients = Executors.newFixedThreadPool(10);
        for(int i=0; i < 30; i++) {
            final int currentIter = i;
            clients.submit(new Runnable() {
                @Override
                public void run() {
                    long t1 = System.currentTimeMillis();
                    service.capture("http://ja.wikipedia.org/wiki/%E7%89%B9%E5%88%A5:Random");
                    System.err.println(currentIter +":"+(System.currentTimeMillis() - t1));
                }
            });
            System.err.println(currentIter + ":submitted");
        }
        clients.shutdown();
        while (!clients.awaitTermination(1, TimeUnit.SECONDS)) {
            System.err.println("Executing...");
        }
        service.shutdown();
    }
}
