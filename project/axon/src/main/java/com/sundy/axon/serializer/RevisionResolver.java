package com.sundy.axon.serializer;

/**
 * 获取给定有效载荷类型版本的机制的接口。 基于此版本，组件能够识别有序载荷的序列化版本是否与当前已知版本的有效载荷兼容。
 * @author Administrator
 *
 */
public interface RevisionResolver {

	/**
	 * 返回给定载体的版本号
	 * @param payloadType 载体类型
	 * @return
	 */
	String revisionOf(Class<?> payloadType);
	
}
