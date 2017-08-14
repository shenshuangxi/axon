package com.sundy.axon.serializer;

/**
 * 该接口用于描述一种机制，该机制用于转换序列化对象的中间数据用于类型转换。不同的类型转换器可能需要不同的数据类型。
 * @author Administrator
 *
 * @param <S> 期望的元数据类型
 * @param <T> 输出数据类型
 */
public interface ContentTypeConverter<S, T> {

	/**
	 * 期望的输入数据类型
	 * @return	期望的中间数据类型
	 */
	Class<S> expectedSourceType();
	
	/**
	 * 返回的中间类型数据
	 * @return
	 */
	Class<T> targetType();
	
	/**
	 * 将输入的原数据转换为目标中间类型的格式数据
	 * @param original
	 * @return
	 */
	SerializedObject<T> convert(SerializedObject<S> original);
	
	/**
	 * 将输入数据转为目标类型的数据
	 * @param original 输入的原数据
	 * @return	转换后的数据
	 */
	T convert(S original);
	
}
