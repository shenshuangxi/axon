package com.sundy.axon.serializer;

/**
 * 接口用于描述一个结构化的序列化对象
 * @author Administrator
 *
 * @param <T> 代表被已序列化对象的数据类型
 */
public interface SerializedObject<T> {

	/**
	 * 返回数据的类型
	 * @return
	 */
	Class<T> getContentType();
	
	/**
	 * 返回所容纳数据的类型
	 * @return
	 */
	SerializedType getType();
	
	/**
	 * 返回序列化数据
	 * @return
	 */
	T getData();
	
}
