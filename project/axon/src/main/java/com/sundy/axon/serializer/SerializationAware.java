package com.sundy.axon.serializer;

/**
 * 具有特殊序列化意识的消息的标记界面。 通常，实现将通过重复使用跨调用的序列化格式来优化序列化过程。 对于需要使用同一串行器多次序列化对象的情况，这是特别有用的。
 * @author Administrator
 *
 */
public interface SerializationAware {

	/**
	 * 使用给定的serializer序列化此消息的有效负载，使用给定的expectedRepresentation。 当使用相同的序列化程序调用多次时，此方法应返回相同的SerializedObject实例。
	 * @param serializer
	 * @param expectedRepresentation
	 * @return
	 */
	<T> SerializedObject<T> serializePayload(Serializer serializer, Class<T> expectedRepresentation);
	
	/**
	 * 使用给定的serializer序列化此消息的元数据，使用给定的expectedRepresentation。 当使用相同的序列化程序调用多次时，此方法应返回相同的SerializedObject实例。
	 * @param serializer
	 * @param expectedRepresentation
	 * @return
	 */
	<T> SerializedObject<T> serializeMetaData(Serializer serializer, Class<T> expectedRepresentation);
	
}
