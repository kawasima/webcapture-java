package net.unit8.webcapture;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static PhantomJSProcess phantomJSProcess = new PhantomJSProcess();
    private static Logger logger = LoggerFactory.getLogger(WebCaptureTask.class);

    private String url;
    private WebCaptureService executor;

    public WebCaptureTask(String url, WebCaptureService executor) {
        this.url = url;
        this.executor = executor;
    }

    @Override
    public String call() throws IOException {
        Process process = phantomJSProcess.get();
        executor.addProcess(process);
        OutputStream cmdOut = process.getOutputStream();
        InputStream cmdIn = process.getInputStream();

        logger.info("Create process: " + process + "/" + cmdOut);

        try {
            IOUtils.write(url + "\n", cmdOut);
            cmdOut.flush();
            logger.info("request a capture: " + url);
            StringBuilder sb = new StringBuilder();
            int c;
            while ((c = cmdIn.read()) != -1) {
                if (c == '\n') break;
                sb.append((char)c);
            }
            logger.info("responsed: " + sb.toString());
            return sb.toString();
        } catch (IOException ex) {
            executor.disposeProcess(process);
            phantomJSProcess.remove();
            throw ex;
        }
    }
}
