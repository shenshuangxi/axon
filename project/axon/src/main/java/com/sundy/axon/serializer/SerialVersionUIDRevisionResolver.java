package com.sundy.axon.serializer;

import java.io.ObjectStreamClass;

/**
 * 获取java序列化对象中序列化版本号  {@link ObjectStreamClass}
 * @author Administrator
 *
 */
public class SerialVersionUIDRevisionResolver implements RevisionResolver {

	public String revisionOf(Class<?> payloadType) {
		ObjectStreamClass objectStreamClass = ObjectStreamClass.lookup(payloadType);
		return objectStreamClass == null ? null : Long.toString(objectStreamClass.getSerialVersionUID());
	}

}
