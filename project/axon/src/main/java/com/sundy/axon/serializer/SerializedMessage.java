package com.sundy.axon.serializer;

import java.util.Map;

import com.sundy.axon.domain.GenericMessage;
import com.sundy.axon.domain.Message;
import com.sundy.axon.domain.MetaData;

/**
 * 针对序列化的有效载荷和元数据进行优化的消息实现。 有效载荷和元数据将仅在请求时反序列化。
 * <p/>
 * 这个实现是根据Java规范的Serializable。 在将数据写入OutputStream之前，MetaData和Payload都将被反序列化。
 * @author Administrator
 *
 * @param <T>
 */
public class SerializedMessage<T> implements Message<T>, SerializationAware {

	private static final long serialVersionUID = 6332429891815042291L;
    private static final ConverterFactory CONVERTER_FACTORY = new ChainingConverterFactory();

    private final String identifier;
    private final LazyDeserializingObject<MetaData> serializedMetaData;
    private final LazyDeserializingObject<T> serializedPayload;
    
    public SerializedMessage(String identifier, SerializedObject<?> serializedPayload,
            SerializedObject<?> serializedMetaData, Serializer serializer) {
		this.identifier = identifier;
		this.serializedMetaData = new LazyDeserializingObject<MetaData>(serializedMetaData, serializer);
		this.serializedPayload = new LazyDeserializingObject<T>(serializedPayload, serializer);
	}
	
	private SerializedMessage(SerializedMessage<T> message, Map<String, ?> metaData) {
		this.identifier = message.getIdentifier();
		this.serializedMetaData = new LazyDeserializingObject<MetaData>(MetaData.from(metaData));
		this.serializedPayload = message.serializedPayload;
	}
	
	public <T> SerializedObject<T> serializePayload(Serializer serializer,
			Class<T> expectedRepresentation) {
		if (serializer.equals(serializedPayload.getSerializer())) {
            final SerializedObject serializedObject = serializedPayload.getSerializedObject();
            return CONVERTER_FACTORY.getConverter(serializedObject.getContentType(), expectedRepresentation)
                    .convert(serializedObject);
        }
        return serializer.serialize(serializedPayload.getObject(), expectedRepresentation);
	}

	public <T> SerializedObject<T> serializeMetaData(Serializer serializer,
			Class<T> expectedRepresentation) {
		if (serializer.equals(serializedMetaData.getSerializer())) {
            final SerializedObject serializedObject = serializedMetaData.getSerializedObject();
            return CONVERTER_FACTORY.getConverter(serializedObject.getContentType(), expectedRepresentation)
                                    .convert(serializedObject);
        }
        return serializer.serialize(serializedMetaData.getObject(), expectedRepresentation);
	}

	public String getIdentifier() {
		return identifier;
	}

	public MetaData getMetaData() {
		MetaData metaData = serializedMetaData.getObject();
        return metaData == null ? MetaData.emptyInstance() : metaData;
	}

	public T getPayload() {
		return serializedPayload.getObject();
	}

	public Class getPayloadType() {
		return serializedPayload.getType();
	}

	public SerializedMessage<T> withMetaData(Map<String, ?> metaData) {
		if (this.serializedMetaData.getObject().equals(metaData)) {
            return this;
        }
        return new SerializedMessage<T>(this, metaData);
	}

	public SerializedMessage<T> andMetaData(Map<String, ?> metaData) {
		if (metaData.isEmpty()) {
            return this;
        }
        return new SerializedMessage<T>(this, getMetaData().mergedWith(metaData));
	}

	/**
     * 判断该消息的消息载荷是否已被反序列化
     */
    public boolean isPayloadDeserialized() {
        return serializedPayload.isDeserialized();
    }

    /**
     * Java序列化API方法提供替代序列化，因为此实例中包含的字段本身不可序列化。
     */
    protected Object writeReplace() {
        return new GenericMessage<T>(identifier, getPayload(), getMetaData());
    }

}
