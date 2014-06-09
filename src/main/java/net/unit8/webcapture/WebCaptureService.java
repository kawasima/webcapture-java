package net.unit8.webcapture;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

/**
 * The service for capturing web pages.
 *
 * @author kawasima
 */
public class WebCaptureService {
    private static final Logger logger = LoggerFactory.getLogger(WebCaptureService.class);
    private ExecutorService executor;
    private Set<Process> processes = new HashSet<Process>();

    public WebCaptureService() {
        executor = Executors.newFixedThreadPool(5);
    }

    public WebCaptureService(int poolSize) {
        executor = Executors.newFixedThreadPool(poolSize);
    }

    public void capture(String url) {
        Future<String> future = executor.submit(
                new WebCaptureTask(url, this));
        try {
            logger.info("submitted");
            String pdfName = future.get(10, TimeUnit.SECONDS);
            logger.debug(String.format("Captured %s to %s.", url, pdfName));
        } catch (InterruptedException e) {
            logger.error("interrupted", e);
        } catch (ExecutionException e) {
            logger.error("execution exception", e);
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    public void addProcess(Process p) {
        processes.add(p);
    }

    public void disposeProcess(Process p) {
        if (processes.remove(p)) {
            IOUtils.closeQuietly(p.getInputStream());
            IOUtils.closeQuietly(p.getErrorStream());
            IOUtils.closeQuietly(p.getOutputStream());
            p.destroy();
        }
    }

    public void shutdown() {
        executor.shutdown();

        try {
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}
