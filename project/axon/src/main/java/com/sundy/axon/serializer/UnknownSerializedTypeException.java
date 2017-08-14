package com.sundy.axon.serializer;

import com.sundy.axon.common.AxonNonTransientException;

/**
 * 异常用于指出该对象不能被序列化，原因是序列化类型不能映射到该类
 * @author Administrator
 *
 */
public class UnknownSerializedTypeException extends AxonNonTransientException {

	private static final long serialVersionUID = 5423163966186330707L;
	
	public UnknownSerializedTypeException(SerializedType serializedType) {
        super(String.format("Could not deserialize a message. The serialized type is unknown: %s (rev. %s)",
                     serializedType.getName(), serializedType.getRevision()));
    }
	
	public UnknownSerializedTypeException(SerializedType serializedType, Throwable cause) {
        super(String.format("Could not deserialize a message. The serialized type is unknown: %s (rev. %s)",
                     serializedType.getName(), serializedType.getRevision()),
              cause);
    }
	
}
