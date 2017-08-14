package com.sundy.axon.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.persistence.Version;

/**
 * AggregateRoot 接口的最基础实现类，他提供保持跟踪非提交事件以及维护一个版本号用于产生一批事件的机制
 * @author Administrator
 *
 * @param <I> 聚合的识别码类型
 */

@MappedSuperclass
public abstract class AbstractAggregateRoot<I> implements AggregateRoot<I>,Serializable {

	@Transient
	private volatile EventContainer eventContainer;
	
	@Transient
	private boolean deleted = false;
	
	@Basic(optional=true)
	private Long lastEventSequenceNumber;
	
	@Version
	private Long version;
	
	protected <T> DomainEventMessage<T> registerEvent(T payload){
		return registerEvent(MetaData.emptyInstance(),payload);
	}
	
	/**当聚合被保存时，这些事件将会被注册到发布列表中
	 * 
	 * @param metaData 元数据
	 * @param payload 消息载荷
	 * @return
	 */
	protected <T> DomainEventMessage<T> registerEvent(MetaData metaData, T payload){
		return getEventContainer().addEvent(metaData,payload);
	}
	
	/**
	 * 当聚合被保存时，这些事件将会被注册到发布列表中
	 * @param message 包含载体和元数据的消息
	 * @return 返回领域事件消息
	 */
	protected <T> DomainEventMessage<T> registerEventMessage(DomainEventMessage<T> message){
		return getEventContainer().addEvent(message);
	}

	private EventContainer getEventContainer() {
		if(eventContainer==null){
			Object identifier = getIdentifier();
			if(identifier==null){
				throw new AggregateIdentifierNotInitializedException(
                        "AggregateIdentifier is unknown in [" + getClass().getName() + "]. "
                                + "Make sure the Aggregate Identifier is initialized before registering events.");
			}
			eventContainer = new EventContainer(identifier);
			eventContainer.initializeSequenceNumber(lastEventSequenceNumber);
		}
		return eventContainer;
	}
	
	public boolean isDeleted() {
		return deleted;
	}
	
	/**
	 * 将此聚合标记为已删除，指示存储库在适当的时间删除该聚合。
	 * <p/>
	 * 请注意，不同的Repository实现可能会对标记为删除的聚合产生不同的反应。 通常，事件资源库将忽略标记，并将期望删除作为事件信息的一部分提供。
	 */
	protected void markDeleted(){
		this.deleted = true;
	}
	
	public void addEventRegistrationCallback(EventRegistrationCallback eventRegistrationCallback) {
		getEventContainer().addEventRegistrationCallback(eventRegistrationCallback);
	}
	
	public DomainEventStream getUncommittedEvents() {
		if(eventContainer==null){
			return SimpleDomainEventStream.emptyStream();
		}
		return eventContainer.getEventStream();
	}
	
	public void commitEvents() {
		if(eventContainer !=null){
			lastEventSequenceNumber = eventContainer.getLastSequenceNumber();
			eventContainer.commit();
		}
	}
	
	public int getUncommittedEventCount() {
		return eventContainer!=null?eventContainer.size():0;
	}
	
	/**
	 * 使用最后一个已知事件的给定序列号初始化事件流。 这将导致将新事件附加到该聚合中以分配连续的序列号
	 * @param lastSequenceNumber
	 */
	protected void initializeEventStream(long lastSequenceNumber){
		getEventContainer().initializeSequenceNumber(lastSequenceNumber);
		lastEventSequenceNumber = lastSequenceNumber>=0?lastSequenceNumber:null;
	}
	
	/**
	 * 获取最后已提交的事件的序列号
	 * @return
	 */
	protected Long getLastCommittedEventSequenceNumber(){
		if(eventContainer==null){
			return lastEventSequenceNumber;
		}
		return eventContainer.getLastCommittedSequenceNumber();
	}

	public Long getVersion() {
		return version;
	}


	

}
