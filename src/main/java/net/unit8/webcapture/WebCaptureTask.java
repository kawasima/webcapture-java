package net.unit8.webcapture;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;

/**
 * The task for capturing a web page.
 *
 * @author kawasima
 */
public class WebCaptureTask implements Callable<String> {
    private static Logger logger = LoggerFactory.getLogger(WebCaptureTask.class);

    private String url;
    private WebCaptureService executor;
    private File captureDirectory;

    public WebCaptureTask(String url, File captureDirectory, WebCaptureService executor) {
        this.url = url;
        this.captureDirectory = captureDirectory;
        this.executor = executor;
    }

    @Override
    public String call() throws IOException {
        PhantomJSProcess phantomJSProcess = new PhantomJSProcess(captureDirectory);
        Process process = phantomJSProcess.get();
        try {
            process.exitValue();
            throw new IOException("Process exited.");
        } catch(IllegalThreadStateException ex) {
            // ignore
        }
        executor.addProcess(process);

        OutputStream cmdOut = process.getOutputStream();
        InputStream cmdIn = process.getInputStream();

        try {
            IOUtils.write(url + "\n", cmdOut);
            cmdOut.flush();
            logger.debug("request a capture: " + url);
            StringBuilder sb = new StringBuilder();
            int c;
            while ((c = cmdIn.read()) != -1) {
                if (c == '\n') break;
                sb.append((char)c);
            }
            logger.debug("responsed: " + sb.toString());
            return sb.toString();
        } catch (IOException ex) {
            executor.disposeProcess(process);
            phantomJSProcess.remove();
            throw ex;
        }
    }
}
