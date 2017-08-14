package com.sundy.axon.upcasting;

import org.joda.time.DateTime;

import com.sundy.axon.domain.MetaData;
import com.sundy.axon.serializer.LazyDeserializingObject;
import com.sundy.axon.serializer.SerializedDomainEventData;
import com.sundy.axon.serializer.Serializer;

public class SerializedDomainEventUpcastingContext implements UpcastingContext {

	private final String messageIdentifier;
    private final Object aggregateIdentifier;
    private final Long sequenceNumber;
    private final DateTime timestamp;
    private final LazyDeserializingObject<MetaData> serializedMetaData;
    
    public SerializedDomainEventUpcastingContext(SerializedDomainEventData domainEventData, Serializer serializer) {
        this.messageIdentifier = domainEventData.getEventIdentifier();
        this.aggregateIdentifier = domainEventData.getAggregateIdentifier();
        this.sequenceNumber = domainEventData.getSequenceNumber();
        this.timestamp = domainEventData.getTimestamp();
        this.serializedMetaData = new LazyDeserializingObject<MetaData>(domainEventData.getMetaData(), serializer);
    }
	
	public String getMessageIdentifier() {
		return messageIdentifier;
	}

	public Object getAggregateIdentifier() {
		return aggregateIdentifier;
	}

	public Long getSequenceNumber() {
		return sequenceNumber;
	}

	public DateTime getTimestamp() {
		return timestamp;
	}

	public MetaData getMetaData() {
		return serializedMetaData.getObject();
	}
	
	public LazyDeserializingObject<MetaData> getSerializedMetaData() {
        return serializedMetaData;
    }

}
