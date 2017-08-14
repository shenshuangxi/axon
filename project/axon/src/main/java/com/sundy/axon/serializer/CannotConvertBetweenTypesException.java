package com.sundy.axon.serializer;

import com.sundy.axon.common.AxonNonTransientException;

/**
 * 异常表示需要upcasters之间的类型转换器，但没有转换器能够进行转换。
 * @author Administrator
 *
 */
public class CannotConvertBetweenTypesException extends
		AxonNonTransientException {

	private static final long serialVersionUID = 4095268910024742475L;

	public CannotConvertBetweenTypesException(String message) {
        super(message);
    }
	
	public CannotConvertBetweenTypesException(String message, Throwable cause) {
        super(message, cause);
    }
	
}
