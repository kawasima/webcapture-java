package net.unit8.webcapture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * The process manager for PhantomJS.
 *
 * @author kawasima
 */
public class PhantomJSProcess extends ThreadLocal<Process> {
    private static final Logger logger = LoggerFactory.getLogger(PhantomJSProcess.class);
    @Override
    protected Process initialValue() {
        try {
            Process process = new ProcessBuilder()
                    .redirectErrorStream(true)
                    .command("phantomjs", "src/main/resources/capture.js")
                    .start();
            return process;
        } catch(IOException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
