package com.sundy.axon.upcasting;

import org.joda.time.DateTime;

import com.sundy.axon.serializer.SerializedDomainEventData;
import com.sundy.axon.serializer.SerializedObject;

/**
 * SerializedDomainEventData实现，可用于在上传有效负载后重复现有的SerializedDomainEventData实例。
 * @author Administrator
 *
 * @param <T>
 */
public class UpcastSerializedDomainEventData<T> implements SerializedDomainEventData<T> {

	private final SerializedDomainEventData<T> original;
    private final Object identifier;
    private final SerializedObject<T> upcastPayload;
    
    public UpcastSerializedDomainEventData(SerializedDomainEventData<T> original, Object aggregateIdentifier,
            SerializedObject<T> upcastPayload) {
    	this.original = original;
    	this.identifier = aggregateIdentifier;
    	this.upcastPayload = upcastPayload;
	}
	
	public String getEventIdentifier() {
		return original.getEventIdentifier();
	}

	public Object getAggregateIdentifier() {
		return identifier;
	}

	public long getSequenceNumber() {
		return original.getSequenceNumber();
	}

	public DateTime getTimestamp() {
		return original.getTimestamp();
	}

	public SerializedObject<T> getMetaData() {
		return original.getMetaData();
	}

	public SerializedObject<T> getPayload() {
		return original.getPayload();
	}

}
