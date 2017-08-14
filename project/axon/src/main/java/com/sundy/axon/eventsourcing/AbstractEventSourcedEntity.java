package com.sundy.axon.eventsourcing;

import java.util.Collection;

import com.sundy.axon.common.Assert;
import com.sundy.axon.domain.DomainEventMessage;
import com.sundy.axon.domain.MetaData;

/*
 * 事件源的基础实现类但不能作为聚合根存在，为了跟踪自身未提交的事件，这些实体都关联到一个聚合根上，一个领域事件的发布 都是整合到聚合中的所有需要发布的事件中
 */
public abstract class AbstractEventSourcedEntity implements EventSourcedEntity {
	
	private volatile AbstractEventSourcedAggregateRoot aggregateRoot;

	public void registerAggregateRoot(
			AbstractEventSourcedAggregateRoot aggregateRootToRegister) {
		if (this.aggregateRoot != null && this.aggregateRoot != aggregateRootToRegister) {
            throw new IllegalStateException("Cannot register new aggregate. "
                                                    + "This entity is already part of another aggregate");
        }
		this.aggregateRoot = aggregateRootToRegister;
	}

	public void handleRecursively(DomainEventMessage eventMessage) {
		handle(eventMessage);
        Collection<? extends EventSourcedEntity> childEntities = getChildEntities();
        if (childEntities != null) {
            for (EventSourcedEntity entity : childEntities) {
                if (entity != null) {
                    entity.registerAggregateRoot(aggregateRoot);
                    entity.handleRecursively(eventMessage);
                }
            }
        }
	}
	
	/**
	 * 返回实体关联的所有事件源实体。如果没有子实体，那么返回null
	 * @return
	 */
	protected abstract Collection<? extends EventSourcedEntity> getChildEntities();
	
	/**
	 * 根据传入的事件改变实体的状态。
	 * <p/>
	 * 注意: 该方法的实现可能没有做校验
	 * @param event
	 */
	protected abstract void handle(DomainEventMessage event);
	
	/**
	 * 应用提供的事件，意味着将事件添加到非提交事件列表，并交给事件处理器处理
	 * @param event
	 */
	protected void apply(Object event) {
        apply(event, MetaData.emptyInstance());
    }
	
	/**
	 * 应用提供的事件，意味着将事件添加到非提交事件列表，并交给事件处理器处理
	 * @param event
	 */
	protected void apply(Object event, MetaData metaData) {
        Assert.notNull(aggregateRoot, "The aggregate root is unknown. "
                + "Is this entity properly registered as the child of an aggregate member?");
        aggregateRoot.apply(event, metaData);
    }
	
	protected AbstractEventSourcedAggregateRoot getAggregateRoot() {
        return aggregateRoot;
    }

}
