package net.unit8.webcapture;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collections;
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
    private File captureDirectory;
    private long timeout = 10L;

    public WebCaptureService() {
        executor = Executors.newFixedThreadPool(5);
    }

    public WebCaptureService(int poolSize) {
        executor = Executors.newFixedThreadPool(poolSize);
    }

    public void capture(String url) throws TimeoutException, InterruptedException, ExecutionException {
        WebCaptureTask task = new WebCaptureTask(url, captureDirectory, this);
        Future<String> future = executor.submit(task);
        try {
            logger.info("submitted");
            String pdfName = future.get(timeout, TimeUnit.SECONDS);
            logger.debug(String.format("Captured %s to %s.", url, pdfName));
        } catch (InterruptedException ex) {
            logger.error("interrupted", ex);
            throw ex;
        } catch (ExecutionException ex) {
            logger.error("execution exception", ex);
            throw ex;
        } catch (TimeoutException ex) {
            logger.error("timeout", ex);
            throw ex;
        }
    }

    public void addProcess(Process p) {
        processes.add(p);
    }

    public void disposeProcess(Process p) {
        if (processes.remove(p)) {
            logger.debug("dispose process: " + p);
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
        } finally {
            for (Process p : Collections.unmodifiableSet(new HashSet<Process>(processes))) {
                disposeProcess(p);
            }
        }
    }

    /**
     * Get the seconds to timeout.
     *
     * @return seconds to timeout
     */
    public long getTimeout() {
        return timeout;
    }

    /**
     * Set the seconds to timeout.
     *
     * @param timeout seconds to timeout
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public File getCaptureDirectory() {
        return captureDirectory;
    }

    public void setCaptureDirectory(File captureDirectory) {
        this.captureDirectory = captureDirectory;
        if (!captureDirectory.exists()) {
            captureDirectory.mkdirs();
        }
    }
}
