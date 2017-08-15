package com.sundy.axon.unitofwork;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;

import com.sundy.axon.domain.AggregateRoot;
import com.sundy.axon.domain.DomainEventMessage;
import com.sundy.axon.domain.EventMessage;
import com.sundy.axon.domain.MetaData;

/**
 * 专业UnitOfWorkListenerAdapter，允许在工作单元的“beforeCommit”阶段修改事件的元数据。 这是所有要发布的事件都是已知的阶段，但尚未被持久化或已发布。
 * @author Administrator
 *
 */
public abstract class MetaDataMutatingUnitOfWorkListenerAdapter extends UnitOfWorkListenerAdapter {

	@Override
	public <T> EventMessage<T> onEventRegistered(UnitOfWork unitOfWork,
			EventMessage<T> event) {
		return wrapped(event);
	}

	@Override
	public void onPrepareCommit(UnitOfWork unitOfWork,
			Set<AggregateRoot> aggregateRoots, List<EventMessage> events) {
		int i = 0;
        for (EventMessage<?> event : events) {
            final Map<String, ?> additionalMetaData = assignMetaData(event, events, i++);
            if (additionalMetaData != null) {
                EventMessage<?> changedEvent = event.andMetaData(additionalMetaData);
                if (changedEvent instanceof MutableEventMessage) {
                    ((MutableEventMessage) changedEvent).makeImmutable();
                }
            }
        }
	}
	
	/**
	 * 定义要分配给给定事件的额外元数据。 作为参考，提供了整个事件列表，其中可以在给定的索引处找到给定的事件。
	 * <p/>
	 * 已存在的任何返回的条目将被返回的Map中的条目覆盖。 添加其他条目。
	 * <p/>
	 * 为每个事件调用此方法。
	 * @param event
	 * @param events
	 * @param index
	 * @return
	 */
	protected abstract Map<String,?> assignMetaData(EventMessage event, List<EventMessage> events, int index);
	
	private <T> EventMessage<T> wrapped(EventMessage<T> event) {
		if(event instanceof DomainEventMessage){
			return new MutableDomainEventMessage((DomainEventMessage) event);
		}else {
			return new MutableEventMessage<T>(event);
		}
	}
	
	private static class MutableEventMessage<T> implements EventMessage<T> {
		
		private static final long serialVersionUID = -5697283646053267959L;
		private final EventMessage<T> event;
		private volatile MetaData metaData;
		private volatile boolean fixed;
		
		public MutableEventMessage(EventMessage<T> event) {
			this.event = event;
			this.metaData = event.getMetaData();
		}

		public MetaData getMetaData() {
			return metaData;
		}

		public T getPayload() {
			return event.getPayload();
		}

		public Class getPayloadType() {
			return event.getPayloadType();
		}

		public String getIdentifier() {
			return event.getIdentifier();
		}

		public DateTime getTimeStamp() {
			return event.getTimeStamp();
		}

		public EventMessage<T> withMetaData(Map<String, ?> metaData) {
			if(fixed){
				return event.withMetaData(metaData);
			}else{
				this.metaData = new MetaData(metaData);
				return this;
			}
		}

		public EventMessage<T> andMetaData(Map<String, ?> metaData) {
			if (fixed) {
                return event.withMetaData(this.metaData).andMetaData(metaData);
            } else {
                Map<String, Object> newMetaData = new HashMap<String, Object>(metaData);
                newMetaData.putAll(this.metaData);
                this.metaData = new MetaData(newMetaData);
                return this;
            }
		}
		
		protected EventMessage<T> getWrappedEvent(){
			return event;
		}
		
		/**
		 * 返回包装事件，此实例上当前配置的元数据。 这确保事件的不可变版本实际上是序列化的。
		 * @return
		 */
		protected Object writeReplace() {
            return event.withMetaData(metaData);
        }

        public void makeImmutable() {
            this.fixed = true;
        }
	}
	
	private static class MutableDomainEventMessage<T> extends MutableEventMessage<T> implements DomainEventMessage<T> {
		
		private static final long serialVersionUID = -1814502500382189837L;

        public MutableDomainEventMessage(DomainEventMessage<T> event) {
            super(event);
        }

		public long getSequenceNumber() {
			return getWrappedEvent().getSequenceNumber();
		}

		public Object getAggregateIdentifier() {
			return getWrappedEvent().getAggregateIdentifier();
		}

		@Override
		public DomainEventMessage<T> withMetaData(Map<String, ?> metaData) {
			return (DomainEventMessage<T>) super.withMetaData(metaData);
		}

		@Override
		public DomainEventMessage<T> andMetaData(Map<String, ?> metaData) {
			return (DomainEventMessage<T>) super.andMetaData(metaData);
		}

		@Override
		protected DomainEventMessage<T> getWrappedEvent() {
			return (DomainEventMessage<T>) super.getWrappedEvent();
		}
        
        
        
	}

	
	
}
