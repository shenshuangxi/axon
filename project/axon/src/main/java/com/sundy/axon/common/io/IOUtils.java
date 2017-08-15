package com.sundy.axon.common.io;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * 用于IO操作的工具类
 * @author Administrator
 *
 */
public final class IOUtils {

	public static final Charset UTF8 = Charset.forName("UTF-8");

    private IOUtils() {
    }
    
    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) { // NOSONAR - empty catch block on purpose
                // ignore
            }
        }
    }
    
    public static void closeQuietlyIfCloseable(Object closeable) {
        if (closeable instanceof Closeable) {
            closeQuietly((Closeable) closeable);
        }
    }
    
    public static void closeIfCloseable(Object closeable) throws IOException {
        if (closeable instanceof Closeable) {
            ((Closeable) closeable).close();
        }
    }
	
}
