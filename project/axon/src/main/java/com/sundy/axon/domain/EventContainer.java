package com.sundy.axon.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sundy.axon.common.Assert;

public class EventContainer implements Serializable {

	private final List<DomainEventMessage> events = new ArrayList<DomainEventMessage>();
	private final Object aggregateIdentifier;
	private Long lastCommittedSequenceNumber;
	private transient Long lastSequenceNumber;
	private transient List<EventRegistrationCallback> registrationCallbacks;
	
	public EventContainer(Object aggregateIdentifier) {
		this.aggregateIdentifier = aggregateIdentifier;
	}
	
	public <T> DomainEventMessage<T> addEvent(MetaData metaData, T payload){
		return addEvent(new GenericDomainEventMessage<T>(aggregateIdentifier, newSequenceNumber(),payload,metaData);
	}
	
	public <T> DomainEventMessage<T> addEvent(DomainEventMessage<T> domainEventMessage){
		if(domainEventMessage.getAggregateIdentifier()==null){
			domainEventMessage = new GenericDomainEventMessage<T>(domainEventMessage.getIdentifier(),
                    domainEventMessage.getTimeStamp(),
                    aggregateIdentifier,
                    domainEventMessage.getSequenceNumber(),
                    domainEventMessage.getPayload(),
                    domainEventMessage.getMetaData());
		}
		if(registrationCallbacks !=null){
			for (EventRegistrationCallback callback : registrationCallbacks) {
                domainEventMessage = callback.onRegisteredEvent(domainEventMessage);
            }
		}
		lastSequenceNumber = domainEventMessage.getSequenceNumber();
        events.add(domainEventMessage);
        return domainEventMessage;
	}
	
	public DomainEventStream getEventStream() {
        return new SimpleDomainEventStream(events);
    }
	
	public Object getAggregateIdentifier() {
        return aggregateIdentifier;
    }
	
	public void initializeSequenceNumber(Long lastKnownSequenceNumber) {
        Assert.state(events.size() == 0, "Cannot set first sequence number if events have already been added");
        lastCommittedSequenceNumber = lastKnownSequenceNumber;
    }

	private long newSequenceNumber() {
		Long currentSequenceNumber = getLastSequenceNumber();
		if(currentSequenceNumber==null){
			return 0;
		}
		return currentSequenceNumber+1;
	}

	private Long getLastSequenceNumber() {
		if(events.isEmpty()){
			return lastCommittedSequenceNumber;
		}else if(lastSequenceNumber == null){
			lastSequenceNumber = events.get(events.size()-1).getSequenceNumber();
		}
		return lastSequenceNumber;
	}
	
	
	
	
}
