package com.sundy.axon.unitofwork;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sundy.axon.domain.AggregateRoot;
import com.sundy.axon.domain.DomainEventMessage;
import com.sundy.axon.domain.EventMessage;
import com.sundy.axon.domain.EventRegistrationCallback;
import com.sundy.axon.eventhandling.EventBus;

/**
 * 该工作单元的实现类，所有已缓存的事件必须在工作单元提交后才能发布。所有的聚合在工作单元提交后才能实现保存
 * <p/>
 * 该接口实现类必须实现对外暴露提交和回滚接口的机制
 * @author Administrator
 *
 */
public class DefaultUnitOfWork extends NestableUnitOfWork {

	private static final Logger logger = LoggerFactory.getLogger(DefaultUnitOfWork.class);
	
	private final Map<AggregateRoot, AggregateEntry> registeredAggregates = new LinkedHashMap<AggregateRoot, AggregateEntry>();
	private final Map<EventBus, List<EventMessage<?>>> eventsToPublish = new HashMap<EventBus, List<EventMessage<?>>>();
	private final UnitOfWorkListenerCollection listeners = new UnitOfWorkListenerCollection();
	private Status dispatcherStatus = Status.READY;
	private final TransactionManager transactionManager;
	private Object backingTransaction;
	
	public DefaultUnitOfWork() {
        this(null);
    }
	
	public DefaultUnitOfWork(TransactionManager<?> transactionManager) {
        this.transactionManager = transactionManager;
    }
	
	/**
	 * 启动一个新的工作单元，并将该工作单元注册到CurrentUnitOfWork
	 * <p/>
	 * 注意，本类型的工作单元不能在不同的线程间共享。一个DefaultUnitOfWork实例必须由创建他的线程单独使用
	 * @return 新的工作单元实例
	 */
	public static UnitOfWork startAndGet(){
		DefaultUnitOfWork uow = new DefaultUnitOfWork();
		uow.start();
		return uow;
	}
	
	/**
	 * 启动一个新的工作单元，并将该工作单元注册到CurrentUnitOfWork
	 * <p/>
	 * 注意，本类型的工作单元不能在不同的线程间共享。一个DefaultUnitOfWork实例必须由创建他的线程单独使用
	 * @param transactionManager 该事物管理提供回退事物，如果不使用事物，该事物管理器可以为空
	 * @return 新的工作单元实例
	 */
	public static UnitOfWork startAndGet(TransactionManager<?> transactionManager){
		DefaultUnitOfWork uow = new DefaultUnitOfWork(transactionManager);
		uow.start();
		return uow;
	}
	
	@Override
	protected void doStart() {
		if(isTransactional()){
			this.backingTransaction = transactionManager.startTransaction();
		}
	}
	
	public boolean isTransactional() {
		return transactionManager!=null;
	}
	
	@Override
	protected void doRollback(Throwable cause) {
		registeredAggregates.clear();
		eventsToPublish.clear();
		try {
			if(backingTransaction!=null){
				transactionManager.rollbackTransaction(backingTransaction);
			}
		} finally {
			notifyListenersRollback(cause);
		}
	}
	
	@Override
	protected void doCommit() {
		do {
			publishEvents();
			commitInnerUnitOfWork();
		} while (!this.eventsToPublish.isEmpty());
		if(isTransactional()){
			notifyListenersPrepareCommit();
			transactionManager.commitTransaction(backingTransaction);
		}
		notifyListenersAfterCommit();
	}
	
	@Override
	protected void registerScheduledEvents(UnitOfWork unitOfWork) {
		for(Map.Entry<EventBus, List<EventMessage<?>>> entry : eventsToPublish.entrySet()){
			for(EventMessage<?> eventMessage : entry.getValue()){
				unitOfWork.publishEvent(eventMessage, entry.getKey());
			}
		}
		eventsToPublish.clear();
	}

	public <T extends AggregateRoot> T registerAggregate(T aggregate,
			EventBus eventBus, SaveAggregateCallback<T> saveAggregateCallback) {
		T similarAggregate = (T) findSimilarAggregate(aggregate.getClass(),aggregate.getIdentifier());
		if(similarAggregate!=null){
			if (logger.isInfoEnabled()) {
                logger.info("Ignoring aggregate registration. An aggregate of same type and identifier was already "
                                    + "registered in this Unit Of Work: type [{}], identifier [{}]",
                            aggregate.getClass().getSimpleName(),
                            aggregate.getIdentifier());
            }
            return similarAggregate;
		}
		EventRegistrationCallback eventRegistrationCallback = new UoWEventRegistrationCallback(eventBus);
		registeredAggregates.put(aggregate, new AggregateEntry<T>(aggregate, saveAggregateCallback));
		aggregate.addEventRegistrationCallback(eventRegistrationCallback);
		return aggregate;
	}
	
	private <T> EventMessage<T> invokeEventRegistrationListeners(EventMessage<T> event){
		return listeners.onEventRegistered(this, event);
	}
	
	private <T extends AggregateRoot> T findSimilarAggregate(Class<? extends AggregateRoot> aggregateType,
			Object identifier) {
		for(AggregateRoot aggregate : registeredAggregates.keySet()){
			if(aggregateType.isInstance(aggregate) && aggregate.getIdentifier().equals(identifier)){
				return (T) aggregate;
			}
		}
		return null;
	}
	
	public void registerListener(UnitOfWorkListener listener) {
		listeners.add(listener);
	}
	
	private List<EventMessage<?>> eventsToPublishOn(EventBus eventBus){
		if(!eventsToPublish.containsKey(eventBus)){
			eventsToPublish.put(eventBus, new ArrayList<EventMessage<?>>());
		}
		return eventsToPublish.get(eventBus);
	}
	
	@Override
	protected void registerForPublication(EventMessage<?> event,
			EventBus eventBus, boolean notifyRegistrationHandlers) {
		if (logger.isDebugEnabled()) {
            logger.debug("Staging event for publishing: [{}] on [{}]",
                         event.getPayloadType().getName(),
                         eventBus.getClass().getName());
        }
		if(notifyRegistrationHandlers){
			event = invokeEventRegistrationListeners(event);
		}
		eventsToPublishOn(eventBus).add(event);
	}
	
	@Override
	protected void notifyListenersRollback(Throwable cause) {
		listeners.onRollback(this, cause);
	}
	
	protected void notifyListenersPrepareTransactionCommit(Object transaction){
		listeners.onPrepareTransactionCommit(this, transaction);
	}
	
	protected void notifyListenersAfterCommit(){
		listeners.afterCommit(this);
	}
	
	protected void publishEvents() {
		logger.debug("Publishing events to the event bus");
		if(dispatcherStatus == Status.DISPATCHING) {
			logger.debug("UnitOfWork is already in the dispatch process. "
                    + "That process will publish events instead. Aborting...");
			return;
		}
		dispatcherStatus = Status.DISPATCHING;
		while(!eventsToPublish.isEmpty()){
			Iterator<Map.Entry<EventBus, List<EventMessage<?>>>> iterator = eventsToPublish.entrySet().iterator();
			while(iterator.hasNext()){
				Map.Entry<EventBus, List<EventMessage<?>>> entry = iterator.next();
				List<EventMessage<?>> messageList = entry.getValue();
				EventMessage<?>[] messages = messageList.toArray(new EventMessage<?>[messageList.size()]);
                if (logger.isDebugEnabled()) {
                    for (EventMessage message : messages) {
                        logger.debug("Publishing event [{}] to event bus [{}]",
                                     message.getPayloadType().getName(),
                                     entry.getKey());
                    }
                }
                // remove this entry before publication in case a new event is registered with the UoW while publishing
                iterator.remove();
                entry.getKey().publish(messages);
			}
		}
		
		logger.debug("All events successfully published.");
        dispatcherStatus = Status.READY;
	}
	
	@Override
	protected void saveAggregates() {
		logger.debug("Persisting changes to aggregates");
        for (AggregateEntry entry : registeredAggregates.values()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Persisting changes to [{}], identifier: [{}]",
                             entry.aggregateRoot.getClass().getName(),
                             entry.aggregateRoot.getIdentifier());
            }
            entry.saveAggregate();
        }
        logger.debug("Aggregates successfully persisted");
        registeredAggregates.clear();
	}
	
	@Override
	protected void notifyListenersPrepareCommit() {
		listeners.onPrepareCommit(this, registeredAggregates.keySet(), eventsToPublish());
	}

	@Override
	protected void notifyListenersCleanup() {
		listeners.onCleanup(this);
	}
	
	private List<EventMessage> eventsToPublish(){
		List<EventMessage> events = new ArrayList<EventMessage>();
		for(Map.Entry<EventBus, List<EventMessage<?>>> entry: eventsToPublish.entrySet()){
			events.addAll(entry.getValue());
		}
		return Collections.unmodifiableList(events);
	}
	
	private static enum Status{
		READY,DISPATCHING
	}
	
	private static class AggregateEntry<T extends AggregateRoot>{
		
		private final T aggregateRoot;
		private final SaveAggregateCallback<T> callback;
		
		public AggregateEntry(T aggregateRoot, SaveAggregateCallback<T> callback) {
			this.aggregateRoot = aggregateRoot;
			this.callback = callback;
		}
		
		public void saveAggregate(){
			callback.save(aggregateRoot);
		}
	}
	
	private class UoWEventRegistrationCallback implements EventRegistrationCallback {
		
		private final EventBus eventBus;

		public UoWEventRegistrationCallback(EventBus eventBus) {
			this.eventBus = eventBus;
		}

		public <T> DomainEventMessage<T> onRegisteredEvent(DomainEventMessage<T> event) {
			event = (DomainEventMessage<T>) invokeEventRegistrationListeners(event);
			eventsToPublishOn(eventBus).add(event);
			return event;
		}
		
	}

}
