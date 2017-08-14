package com.sundy.axon.eventsourcing;

import java.util.ArrayDeque;
import java.util.Queue;

import javax.persistence.MappedSuperclass;

import com.sundy.axon.common.Assert;
import com.sundy.axon.domain.AbstractAggregateRoot;
import com.sundy.axon.domain.DomainEventMessage;
import com.sundy.axon.domain.DomainEventStream;
import com.sundy.axon.domain.GenericDomainEventMessage;
import com.sundy.axon.domain.MetaData;

/**
 * 抽象方便班由所有聚集根延伸。 AbstractEventSourcedAggregateRoot跟踪所有未提交的事件。 它还提供了方便的方法来初始化基于{@link DomainEventStream}的聚合根的状态，可用于事件朔源。
 * @author Administrator
 * @param <I> 识别码类型
 */
@MappedSuperclass
public abstract class AbstractEventSourcedAggregateRoot<I> extends AbstractAggregateRoot<I> implements EventSourcedAggregateRoot<I> {

	private static final long serialVersionUID = -2433160610404759928L;
	
	private transient boolean inReplay = false;
	
	private transient boolean applyingEvents = false;
	private transient Queue<PayloadAndMetaData> eventsToApply = new ArrayDeque<PayloadAndMetaData>();
	
	public void initializeState(DomainEventStream domainEventStream) {
		Assert.state(getUncommittedEventCount() == 0, "Aggregate is already initialized");
		inReplay = true;
		long lastSequenceNumber = -1;
		while(domainEventStream.hasNext()){
			DomainEventMessage eventMessage = domainEventStream.next();
			lastSequenceNumber = eventMessage.getSequenceNumber();
			handleRecursively(eventMessage);
		}
		initializeEventStream(lastSequenceNumber);
		inReplay = false;
	}
	
	/**
	 * 通过该方法提供事件，该事件添加到未提交事件队列，并且驱使{@link #handle(org.axonframework.domain.DomainEventMessage)} 事件处理器} 来处理
	 * <p/>
	 * 该事件应用于此聚合的所有实体部分。
	 * @param eventPayload
	 */
	protected void apply(Object eventPayload){
		apply(eventPayload, MetaData.emptyInstance());
	}

	/**
	 * 通过该方法提供事件，该事件添加到未提交事件队列，并且驱使{@link #handle(org.axonframework.domain.DomainEventMessage)} 事件处理器} 来处理
	 * <p/>
	 * 该事件应用于此聚合的所有实体部分。
	 * @param eventPayload
	 */
	protected void apply(Object eventPayload, MetaData metaData) {
		if(inReplay){
			return ;
		}
		
		boolean wasNested = applyingEvents;
		applyingEvents = true;
		try {
			if(getIdentifier()==null){
				Assert.state(!wasNested,
			            "Applying an event in an @EventSourcingHandler is allowed, but only *after* the "
			                    + "aggregate identifier has been set");
				if (getUncommittedEventCount() > 0 || getVersion() != null) {
			        throw new IncompatibleAggregateException("The Aggregate Identifier has not been initialized. "
			                                                         + "It must be initialized at the latest when the "
			                                                         + "first event is applied.");
			    }
				final GenericDomainEventMessage<Object> message = new GenericDomainEventMessage<Object>(null,0,eventPayload,metaData);
				handleRecursively(message);
			    registerEventMessage(message);
			} else {
				if(eventsToApply==null){
					eventsToApply = new ArrayDeque<PayloadAndMetaData>();
				}
				eventsToApply.add(new PayloadAndMetaData(eventPayload, metaData));
				while (!wasNested && eventsToApply != null && !eventsToApply.isEmpty()) {
			        final PayloadAndMetaData payloadAndMetaData = eventsToApply.poll();
			        handleRecursively(registerEvent(payloadAndMetaData.metaData, payloadAndMetaData.payload));
			    }
			}
		} finally {
			applyingEvents = wasNested;
		}
	}
	
	@Override
    public void commitEvents() {
        applyingEvents = false;
        if (eventsToApply != null) {
            eventsToApply.clear();
        }
        super.commitEvents();
    }
	
	/**
	 * 指示此聚合是否处于“实时”模式。 当聚合完全初始化并准备好处理命令时就是这种情况。
	 * <p/>
	 * 通常，该方法用于在处理事件时检查聚合的状态。 当聚合处理事件来重建其当前状态时，isLive（）返回false。 如果由于正在执行当前命令而应用了事件，则返回true。
	 * <p/>
	 * isLive（）可以用于在事件采集时防止昂贵的计算。
	 * @return
	 */
	protected boolean isLive() {
        return !inReplay;
    }

	private void handleRecursively(DomainEventMessage eventMessage) {
		handle(eventMessage);
		Iterable<? extends EventSourcedEntity> childEntities = getChildEntities();
		if(childEntities!=null){
			for(EventSourcedEntity entity : childEntities){
				if(entity!=null){
					entity.registerAggregateRoot(this);
					entity.handleRecursively(eventMessage);
				}
			}
		}
		
	}

	/**
	 * 返回由该实体直接引用的事件源实体的集合。 可以返回null或一个空列表，以表示没有子实体可用。 集合也可能包含空值。 
	 * <p/>
	 * 事件按照返回值的迭代器提供的顺序传播给孩子。
	 * @return
	 */
	protected abstract Iterable<? extends EventSourcedEntity> getChildEntities();
	
	/**
	 * 通过给定事件改变聚合状态
	 * @param event
	 */
	protected abstract void handle(DomainEventMessage event);

	@Override
    public Long getVersion() {
        return getLastCommittedEventSequenceNumber();
    }
	
	private static class PayloadAndMetaData {

        private final Object payload;
        private final MetaData metaData;

        private PayloadAndMetaData(Object payload, MetaData metaData) {
            this.payload = payload;
            this.metaData = metaData;
        }
    }

}
