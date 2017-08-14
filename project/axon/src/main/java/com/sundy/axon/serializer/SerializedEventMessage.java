package com.sundy.axon.serializer;

import java.util.Map;

import org.joda.time.DateTime;

import com.sundy.axon.domain.EventMessage;
import com.sundy.axon.domain.GenericEventMessage;
import com.sundy.axon.domain.MetaData;

public class SerializedEventMessage<T> implements EventMessage<T>, SerializationAware {

	private static final long serialVersionUID = -4704515337335869770L;
    private final DateTime timestamp;
    private final SerializedMessage<T> message;
    
    public SerializedEventMessage(String eventIdentifier, DateTime timestamp, SerializedObject<?> serializedPayload,
            SerializedObject<?> serializedMetaData, Serializer serializer) {
    	message = new SerializedMessage<T>(eventIdentifier, serializedPayload, serializedMetaData, serializer);
    	this.timestamp = timestamp;
	}
	
	private SerializedEventMessage(SerializedEventMessage<T> original, Map<String, ?> metaData) {
		message = original.message.withMetaData(metaData);
		this.timestamp = original.getTimeStamp();
	}
	
	public MetaData getMetaData() {
		return message.getMetaData();
	}

	public T getPayload() {
		return message.getPayload();
	}

	public Class getPayloadType() {
		return message.getPayloadType();
	}

	public String getIdentifier() {
		return message.getIdentifier();
	}

	public DateTime getTimeStamp() {
		return timestamp;
	}

    public SerializedEventMessage<T> withMetaData(Map<String, ?> newMetaData) {
        if (getMetaData().equals(newMetaData)) {
            return this;
        } else {
            return new SerializedEventMessage<T>(this, newMetaData);
        }
    }

    public EventMessage<T> andMetaData(Map<String, ?> additionalMetaData) {
        MetaData newMetaData = getMetaData().mergedWith(additionalMetaData);
        return withMetaData(newMetaData);
    }

	public <T> SerializedObject<T> serializePayload(Serializer serializer,
			Class<T> expectedRepresentation) {
		return message.serializePayload(serializer, expectedRepresentation);
	}

	public <T> SerializedObject<T> serializeMetaData(Serializer serializer,
			Class<T> expectedRepresentation) {
		return message.serializeMetaData(serializer, expectedRepresentation);
	}
	
	/**
     * 判断该消息的消息载荷是否已被反序列化
     */
    public boolean isPayloadDeserialized() {
        return message.isPayloadDeserialized();
    }

    /**
     * Java序列化API方法提供替代序列化，因为此实例中包含的字段本身不可序列化。
     */
    protected Object writeReplace() {
        return new GenericEventMessage<T>(getIdentifier(), getTimeStamp(), getPayload(), getMetaData());
    }

}
