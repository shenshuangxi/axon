package com.sundy.axon.serializer;

/**
 * 接口用于描述一种机制，用于提供类型转换器实例，用于将给定的原数据转换为目标数据
 * @author Administrator
 *
 */
public interface ConverterFactory {
	
	/**
	 * 判断是否能将给定的类型的数据，转换为目标类型数据
	 * @param sourceContentType
	 * @param targetContentType
	 * @return
	 */
	<S,T> boolean hasConverter(Class<S> sourceContentType, Class<T> targetContentType);
	
	
	/**
	 * 获取将原数据转换为目标类型的转换器
	 * @param sourceContentType
	 * @param targetContentType
	 * @return
	 */
	<S,T> ContentTypeConverter<S, T> getConverter(Class<S> sourceContentType, Class<T> targetContentType);
	
}
