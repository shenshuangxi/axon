package com.sundy.axon.serializer;

/**
 * 接口用于描述一种序列化机制，该接口的实现类能够将将对象序列化成给定的类型，也可以将给定类型反序列化为对象
 * @author Administrator
 *
 */
public interface Serializer {

	/**
	 * 将给定对象序列化成我们期望的类型
	 * <p/>
	 * use {@link #canSerializeTo(Class)} 来判断expectedRepresentation是否该序列化器支持
	 * @param object	被序列化的对象
	 * @param expectedRepresentation 期望的序列化锁包含的对象
	 * @return 序列化所代表的类
	 */
	<T> SerializedObject<T> serialize(Object object, Class<T> expectedRepresentation);
	
	/**
	 * 判断该序列化类型能否被当前序列化器支持
	 * @param expectedRepresentation
	 * @return
	 */
	<T> boolean canSerializeTo(Class<T> expectedRepresentation);
	
	/**
	 * 反序列化，将给定的序列化对象，反序列化为原对象类型
	 * @param serializedObject
	 * @return
	 */
	<S,T> T deserialize(SerializedObject<S> serializedObject);
	
	/**
	 * 返回给定类型标识符的类。 该方法的结果必须保证具有给定类型的反序列化的SerializedObject是返回的类的一个实例。
	 * <p/>
	 * 如果一个类无法解析（即由于该类在此JVM的类路径上不可用），则此方法将抛出UnknownSerializedTypeException。
	 * @param type
	 * @return
	 * @throws UnknownSerializedTypeException
	 */
	Class classForType(SerializedType type) throws UnknownSerializedTypeException;
	
	/**
	 * 返回给定类的类型标识符。 这是serialize（Object，Class）返回的Serialized对象的类型标识符。
	 * @param type
	 * @return
	 */
	SerializedType typeForClass(Class type);
	
	/**
	 * 返回此Serializer使用的转换器工厂在序列化表示之间进行转换。 通常，此ConverterFactory取决于串行器序列化的数据类型。
	 * @return
	 */
	ConverterFactory getConverterFactory();
	
}
