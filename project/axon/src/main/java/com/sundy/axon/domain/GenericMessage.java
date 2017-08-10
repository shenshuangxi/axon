package com.sundy.axon.domain;

import java.util.Map;

/**
 * 消息接口的通用接口实例
 * @author Administrator
 *
 * @param <T>
 */
public class GenericMessage<T> implements Message<T> {

	private static final long serialVersionUID = -843930474387330459L;
	private final String identifier;
	private final MetaData metaData;
	private final Class payloadType;
	private final T payload;
	
	public GenericMessage(T payload) {
		this(payload, MetaData.emptyInstance());
	}

	public GenericMessage(T payload, Map<String, ?> metaData) {
		this(IdentifierFactory.getInstance().generateIdentifier(), payload, MetaData.from(metaData));
	}

	public GenericMessage(String identifier, T payload, Map<String, ?> metaDate) {
		this.identifier = identifier;
		this.metaData = MetaData.from(metaDate);
		this.payload = payload;
		this.payloadType = payload.getClass();
	}
	
	private GenericMessage(GenericMessage<T> original, Map<String, ?> metaData) {
        this.identifier = original.getIdentifier();
        this.payload = original.getPayload();
        this.payloadType = payload.getClass();
        this.metaData = MetaData.from(metaData);
    }

	public String getIdentifier() {
		return identifier;
	}

	public MetaData getMetaData() {
		return metaData;
	}

	public T getPayload() {
		return payload;
	}

	public Class getPayloadType() {
		return payloadType;
	}

	public Message<T> withMetaData(Map<String, ?> metaData) {
		if (this.metaData.equals(metaData)) {
            return this;
        }
        return new GenericMessage<T>(this, metaData);
	}

	public Message<T> andMetaData(Map<String, ?> metaData) {
		if (metaData.isEmpty()) {
            return this;
        }
        return new GenericMessage<T>(this, this.metaData.mergedWith(metaData));
	}

}
