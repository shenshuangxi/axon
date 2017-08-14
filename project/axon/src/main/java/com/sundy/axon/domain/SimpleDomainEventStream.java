package com.sundy.axon.domain;

import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;

/**
 * 创建一个容纳事件消息的领域事件流
 * @author Administrator
 *
 */
public class SimpleDomainEventStream implements DomainEventStream {

	private static final DomainEventStream EMPTY_STREAM = new SimpleDomainEventStream();
	
	private int nextIndex;
	
	private final DomainEventMessage[] events;
	
	public SimpleDomainEventStream(Collection<? extends DomainEventMessage> events){
		this(events.toArray(new DomainEventMessage[events.size()]));
	}
	
	
	public SimpleDomainEventStream(DomainEventMessage... events){
		this.events = Arrays.copyOfRange(events, 0, events.length);
	}
	
	public boolean hasNext() {
		return events.length>nextIndex;
	}

	public DomainEventMessage next() {
		if(!hasNext()){
			throw new NoSuchElementException("Trying to peek beyond the limits of this stream.");
		}
		return events[nextIndex++];
	}

	public DomainEventMessage peek() {
		if(!hasNext()){
			throw new NoSuchElementException("Trying to peek beyond the limits of this stream.");
		}
		return events[nextIndex];
	}
	
	public static DomainEventStream emptyStream() {
        return EMPTY_STREAM;
    }

}
