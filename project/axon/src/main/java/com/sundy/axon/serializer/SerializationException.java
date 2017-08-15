package com.sundy.axon.serializer;

import com.sundy.axon.common.AxonNonTransientException;

/**
 * 序列化或反序列化抛出的异常
 * @author Administrator
 *
 */
public class SerializationException extends AxonNonTransientException {

	private static final long serialVersionUID = -8873621405484274086L;

	public SerializationException(String message) {
        super(message);
    }
	
	public SerializationException(String message, Throwable cause) {
        super(message, cause);
    }
	
}
