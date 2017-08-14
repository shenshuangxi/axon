package com.sundy.axon.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sundy.axon.common.Assert;

/**
 * 聚合关联的事件容器，容器将包裹领域消息事件中的事件和元数据，并自动给事件分配一个聚合的标识符已及下一个序列号
 * <p/>
 * 一旦事件被注册到该容器以备发布。容器会处理EventRegistrationCallbacks 的调用
 * <p/>
 * 该容器的实例时线程非安全。只能在锁中使用。一般来说，只会有一个线程在给定时间处理聚合
 * @author Administrator
 *
 */
public class EventContainer implements Serializable {

	private final List<DomainEventMessage> events = new ArrayList<DomainEventMessage>();
	private final Object aggregateIdentifier;
	private Long lastCommittedSequenceNumber;
	private transient Long lastSequenceNumber;
	private transient List<EventRegistrationCallback> registrationCallbacks;
	
	public EventContainer(Object aggregateIdentifier) {
		this.aggregateIdentifier = aggregateIdentifier;
	}
	
	/**
	 * 向容器添加一个事件
	 * <p/>
	 * 应将事件分配给具有与此容器相同的标识符的聚合，或者尚未分配聚合。 如果事件具有分配的序列号，则它必须直接遵循先前添加的事件的序列号。
	 * @param <T> 该容器所有事件的载体类型
	 * @param domainEventMessage 添加到容器的消息事件
	 * @return 返回将添加的事件
	 */
	public <T> DomainEventMessage<T> addEvent(MetaData metaData, T payload){
		return addEvent(new GenericDomainEventMessage<T>(aggregateIdentifier, newSequenceNumber(),payload,metaData));
	}
	
	/**
	 * 向容器添加一个事件
	 * <p/>
	 * 事件已经被聚合分配一个和容器一样的识别码
	 * 如果一个事件已经有一个序列号，那这个序列号必须高于之前已添加的事件
	 * @param <T> 该容器所有事件的载体类型
	 * @param domainEventMessage 添加到容器的消息事件
	 * @return 返回将添加的事件
	 */
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
	
	/**
	 * 获取事件容器中未发布的事件，并将事件包装成事件流返回
	 * @return
	 */
	public DomainEventStream getEventStream() {
        return new SimpleDomainEventStream(events);
    }
	
	public Object getAggregateIdentifier() {
        return aggregateIdentifier;
    }
	
	/**
	 * 对于即将进来的事件，需要先向容器获取序列号
	 * @param lastKnownSequenceNumber
	 */
	public void initializeSequenceNumber(Long lastKnownSequenceNumber) {
        Assert.state(events.size() == 0, "Cannot set first sequence number if events have already been added");
        lastCommittedSequenceNumber = lastKnownSequenceNumber;
    }

	/**
	 * 返回最近提交的事件的序列号，如果为空，表示还没有事件被提交
	 * @return
	 */
	public Long getLastCommittedSequenceNumber() {
        return lastCommittedSequenceNumber;
    }
	
	/**
	 * 清空本容器内的所有事件，该方法不能改变序列号
	 */
	public void commit(){
		lastCommittedSequenceNumber = getLastCommittedSequenceNumber();
		events.clear();
		if(registrationCallbacks!=null){
			registrationCallbacks.clear();
		}
	}
	
	/**
	 * 返回本容器内当前还有多少事件
	 * @return
	 */
	public int size() {
        return events.size();
    }
	
	/**
	 * 返回一个不能修改的事件列表
	 * @return
	 */
	public List<DomainEventMessage> getEventList(){
		return Collections.unmodifiableList(events);
	}
	
	/**
	 * 添加一个事件注册回调实例，当聚合向其拥有的容器注册一个事件时执行。
	 * 事件提交后，这些实例将被清理掉
	 * @param eventRegistrationCallback
	 */
	public void addEventRegistrationCallback(EventRegistrationCallback eventRegistrationCallback){
		if(registrationCallbacks==null){
			registrationCallbacks = new ArrayList<EventRegistrationCallback>();
		}
		this.registrationCallbacks.add(eventRegistrationCallback);
		for (int i = 0; i < events.size(); i++) {
			events.set(i, eventRegistrationCallback.onRegisteredEvent(events.get(i)));
		}
		
	}

	/**
	 * 获取最后一个添加到容器的事件的序列号
	 */
	public Long getLastSequenceNumber() {
		if(events.isEmpty()){
			return lastCommittedSequenceNumber;
		}else if(lastSequenceNumber == null){
			lastSequenceNumber = events.get(events.size()-1).getSequenceNumber();
		}
		return lastSequenceNumber;
	}
	
	private long newSequenceNumber() {
		Long currentSequenceNumber = getLastSequenceNumber();
		if(currentSequenceNumber==null){
			return 0;
		}
		return currentSequenceNumber+1;
	}
	
	
	
	
}
