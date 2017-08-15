package com.sundy.axon.serializer;

import com.sundy.axon.common.Assert;
import com.sundy.axon.domain.Message;

/**
 * 围绕提供SerializationAware支持的序列化器。 这个类可以被用作一个Serializer周围的包装器，也可以用作一个静态实用程序类。
 * <p/>
 * 该类上的serializePayload和serializeMetaData方法知道SerializationAware消息。 当Message实现该接口时，将序列化委托给该消息以允许性能优化。
 * <p/>
 * 使用这个类序列化消息的有效载荷和元数据是优于使用Serializer类的序列化。
 * @author Administrator
 *
 */
public class MessageSerializer implements Serializer {

	private final Serializer serializer;
	
	public MessageSerializer(Serializer serializer) {
        Assert.notNull(serializer, "serializer may not be null");
        this.serializer = serializer;
    }
	
	public static <T> SerializedObject<T> serializePayload(Message<?> message, Serializer serializer,
            Class<T> expectedRepresentation) {
		if (message instanceof SerializationAware) {
			return ((SerializationAware) message).serializePayload(serializer, expectedRepresentation);
		}
		return serializer.serialize(message.getPayload(), expectedRepresentation);
	}
	
	public static <T> SerializedObject<T> serializeMetaData(Message<?> message, Serializer serializer,
            Class<T> expectedRepresentation) {
		if (message instanceof SerializationAware) {
			return ((SerializationAware) message).serializeMetaData(serializer, expectedRepresentation);
		}
		return serializer.serialize(message.getMetaData(), expectedRepresentation);
	}
	
	public <T> SerializedObject<T> serializePayload(Message<?> message, Class<T> expectedRepresentation) {
        return serializePayload(message, serializer, expectedRepresentation);
    }
	
	public <T> SerializedObject<T> serializeMetaData(Message<?> message, Class<T> expectedRepresentation) {
        return serializeMetaData(message, serializer, expectedRepresentation);
    }
	
    public <T> SerializedObject<T> serialize(Object object, Class<T> expectedRepresentation) {
        return serializer.serialize(object, expectedRepresentation);
    }

    public <T> boolean canSerializeTo(Class<T> expectedRepresentation) {
        return serializer.canSerializeTo(expectedRepresentation);
    }

    public <S, T> T deserialize(SerializedObject<S> serializedObject) {
        return serializer.deserialize(serializedObject);
    }

    public Class classForType(SerializedType type) {
        return serializer.classForType(type);
    }

    public SerializedType typeForClass(Class type) {
        return serializer.typeForClass(type);
    }

    public ConverterFactory getConverterFactory() {
        return serializer.getConverterFactory();
    }

}
