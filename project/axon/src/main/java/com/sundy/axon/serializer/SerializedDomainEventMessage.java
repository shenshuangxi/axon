package com.sundy.axon.serializer;

import java.util.Map;

import org.joda.time.DateTime;

import com.sundy.axon.domain.DomainEventMessage;
import com.sundy.axon.domain.GenericDomainEventMessage;
import com.sundy.axon.domain.MetaData;

public class SerializedDomainEventMessage<T> implements DomainEventMessage<T>,SerializationAware {

	private static final long serialVersionUID = 1946981128830316529L;

    private final long sequenceNumber;
    private final Object aggregateIdentifier;
    private final SerializedEventMessage<T> eventMessage;
    
    public SerializedDomainEventMessage(SerializedDomainEventData domainEventData, Serializer serializer) {
        eventMessage = new SerializedEventMessage<T>(
                domainEventData.getEventIdentifier(), domainEventData.getTimestamp(),
                domainEventData.getPayload(), domainEventData.getMetaData(), serializer);
        aggregateIdentifier = domainEventData.getAggregateIdentifier();
        sequenceNumber = domainEventData.getSequenceNumber();
    }
    
    public SerializedDomainEventMessage(SerializedEventMessage<T> eventMessage, Object aggregateIdentifier,
            long sequenceNumber) {
		this.eventMessage = eventMessage;
		this.aggregateIdentifier = aggregateIdentifier;
		this.sequenceNumber = sequenceNumber;
	}
    
    private SerializedDomainEventMessage(SerializedDomainEventMessage<T> original, Map<String, ?> metaData) {
        eventMessage = original.eventMessage.withMetaData(metaData);
        this.aggregateIdentifier = original.getAggregateIdentifier();
        this.sequenceNumber = original.getSequenceNumber();
    }
	
	public String getIdentifier() {
		return eventMessage.getIdentifier();
	}

	public DateTime getTimeStamp() {
		return eventMessage.getTimeStamp();
	}

	public MetaData getMetaData() {
		return eventMessage.getMetaData();
	}

	public T getPayload() {
		return eventMessage.getPayload();
	}

	public Class getPayloadType() {
		return eventMessage.getPayloadType();
	}

	public long getSequenceNumber() {
		return sequenceNumber;
	}

	public Object getAggregateIdentifier() {
		return aggregateIdentifier;
	}

	public DomainEventMessage<T> withMetaData(Map<String, ?> metaData) {
		if (eventMessage.isPayloadDeserialized()) {
            return new GenericDomainEventMessage<T>(getIdentifier(), getTimeStamp(),
                                                    aggregateIdentifier, sequenceNumber,
                                                    getPayload(), metaData);
        } else {
            return new SerializedDomainEventMessage<T>(this, metaData);
        }
	}

	public DomainEventMessage<T> andMetaData(Map<String, ?> metaData) {
		MetaData newMetaData = getMetaData().mergedWith(metaData);
        return withMetaData(newMetaData);
	}

	public <T> SerializedObject<T> serializePayload(Serializer serializer,
			Class<T> expectedRepresentation) {
		return eventMessage.serializePayload(serializer, expectedRepresentation);
	}

	public <T> SerializedObject<T> serializeMetaData(Serializer serializer,
			Class<T> expectedRepresentation) {
		return eventMessage.serializeMetaData(serializer, expectedRepresentation);
	}

}
