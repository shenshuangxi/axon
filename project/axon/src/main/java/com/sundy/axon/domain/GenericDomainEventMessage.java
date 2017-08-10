package com.sundy.axon.domain;

import java.util.Map;

import org.joda.time.DateTime;

/**
 * 领域事件的简单实现类，简单的持有载体和元数据的引用。以及保有实体的标识符和序列号
 * @author Administrator
 *
 * @param <T>
 */
public class GenericDomainEventMessage<T> extends GenericEventMessage<T> implements DomainEventMessage<T> {

	private static final long serialVersionUID = 2799518229894567465L;
	private final Object aggregateIdentifier;
	private final long sequenceNumber;
	
	public GenericDomainEventMessage(Object aggregateIdentifier, long sequenceNumber, T payload){
		this(aggregateIdentifier, sequenceNumber, payload, MetaData.emptyInstance());
	}
	
	public GenericDomainEventMessage(Object aggregateIdentifier, long sequenceNumber,
            T payload, Map<String, ?> metaData) {
		super(payload, metaData);
		this.aggregateIdentifier = aggregateIdentifier;
		this.sequenceNumber = sequenceNumber;
	}
	
	public GenericDomainEventMessage(String identifier, DateTime timestamp, Object aggregateIdentifier,
            long sequenceNumber, T payload, Map<String, ?> metaData) {
		super(identifier, timestamp, payload, metaData);
		this.aggregateIdentifier = aggregateIdentifier;
		this.sequenceNumber = sequenceNumber;
	}
	
	private GenericDomainEventMessage(GenericDomainEventMessage<T> original, Map<String, ?> metaData) {
        super(original.getIdentifier(), original.getTimeStamp(), original.getPayload(), metaData);
        this.aggregateIdentifier = original.getAggregateIdentifier();
        this.sequenceNumber = original.getSequenceNumber();
    }
	
	public long getSequenceNumber() {
		return sequenceNumber;
	}

	public Object getAggregateIdentifier() {
		return aggregateIdentifier;
	}

	public DomainEventMessage<T> withMetaData(Map<String, ?> metaData) {
		if(getMetaData().equals(metaData)){
			return this;
		}
		return new GenericDomainEventMessage<T>(this, metaData);
	}

	public DomainEventMessage<T> andMetaData(Map<String, ?> metaData) {
		if(metaData.isEmpty()){
			return this;
		}
		return new GenericDomainEventMessage<T>(this, getMetaData().mergedWith(metaData));
	}

}
