package com.sundy.axon.unitofwork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sundy.axon.domain.AggregateRoot;
import com.sundy.axon.domain.EventMessage;
import com.sundy.axon.eventhandling.EventBus;

/**
 * AbstractOfWork接口的抽象实现。 提供必要的实现来支持任何工作单元所需的大多数操作，例如管理注册CurrentUnitOfWork并支持嵌套工作单元。
 * @author Administrator
 *
 */
public abstract class NestableUnitOfWork implements UnitOfWork {

	private static final Logger logger = LoggerFactory.getLogger(NestableUnitOfWork.class);
	private boolean isStarted;
	private UnitOfWork outerUnitOfWork;
	private final List<NestableUnitOfWork> innerUnitsOfWork = new ArrayList<NestableUnitOfWork>();
	private boolean isCommitted = false;
	private final Map<String, Object> resources = new HashMap<String, Object>();
	private final Map<String, Object> inheritedResources = new HashMap<String, Object>();
	
	public void commit() {
		logger.debug("Committing Unit Of Work");
		assertStarted();
		try {
			notifyListenersPrepareCommit();
			saveAggregates();
			isCommitted = true;
			if(outerUnitOfWork==null){
				logger.debug("This Unit Of Work is not nested. Finalizing commit...");
				doCommit();
				stop();
				performCleanup();
			} else {
				if (logger.isDebugEnabled()) {
			        logger.debug("This Unit Of Work is nested. Commit will be finalized by outer Unit Of Work.");
			    }
			    registerScheduledEvents(outerUnitOfWork);
			}
		} catch (RuntimeException e) {
			logger.debug("An error occurred while committing this UnitOfWork. Performing rollback...", e);
            doRollback(e);
            stop();
            if (outerUnitOfWork == null) {
                performCleanup();
            }
            throw e;
		}finally {
            logger.debug("Clearing resources of this Unit Of Work.");
            clear();
        }
	}
	
	/**
	 * 当子工作单元向给定的工作单元注册调用事件执行
	 * @param unitOfWork
	 */
	protected abstract void registerScheduledEvents(UnitOfWork unitOfWork);
	
	private void performCleanup(){
		for(NestableUnitOfWork unitOfWork : innerUnitsOfWork){
			unitOfWork.performCleanup();
		}
		notifyListenersCleanup();
	}
	
	/**
	 * 向所有已注册的监听器发送清理通知
	 */
	protected abstract void notifyListenersCleanup();
	
	/**
	 * 向所有已注册的监听器发送回滚通知
	 */
	protected abstract void notifyListenersRollback(Throwable cause);
	
	public void rollback() {
		rollback(null);
	}

	public void rollback(Throwable cause) {
		if (cause != null && logger.isInfoEnabled()) {
            logger.info("Rollback requested for Unit Of Work due to exception. ", cause);
        } else if (logger.isInfoEnabled()) {
            logger.info("Rollback requested for Unit Of Work for unknown reason.");
        }
		
		try {
			if(isStarted()){
				for (NestableUnitOfWork inner : innerUnitsOfWork) {
			        CurrentUnitOfWork.set(inner);
			        inner.rollback(cause);
			    }
			    doRollback(cause);
			}
		} finally {
            if (outerUnitOfWork == null) {
                performCleanup();
            }
            clear();
            stop();
        }
	}
	
	public void start() {
		logger.debug("Starting Unit Of Work.");
        if (isStarted) {
            throw new IllegalStateException("UnitOfWork is already started");
        }
        
        doStart();
        if(CurrentUnitOfWork.isStarted()){
        	this.outerUnitOfWork = CurrentUnitOfWork.get();
        	this.outerUnitOfWork.attachInheritedResources(this);
        	if (outerUnitOfWork instanceof NestableUnitOfWork) {
                ((NestableUnitOfWork) outerUnitOfWork).registerInnerUnitOfWork(this);
            } else {
                outerUnitOfWork.registerListener(new CommitOnOuterCommitTask());
            }
        }
        logger.debug("Registering Unit Of Work as CurrentUnitOfWork");
        CurrentUnitOfWork.set(this);
        isStarted = true;
	}
	
	public void publishEvent(EventMessage<?> event, EventBus eventBus) {
		registerForPublication(event,eventBus,!isCommitted);
	}
	

	/**
	 * 当工作单元提交后，会将已注册的事件发布到事件总线上，该方法只能被外部工作单元执行，这样便于维护发布的事件顺序
	 * @param event
	 * @param eventBus
	 * @param notifyRegistrationHandlers
	 */
	protected abstract void registerForPublication(EventMessage<?> event, EventBus eventBus, boolean notifyRegistrationHandlers);
	
	
	public boolean isStarted() {
		return isStarted;
	}
	
	private void stop(){
		logger.debug("Stopping Unit Of Work");
        isStarted = false;
	}
	
	/**
	 * 启动线程实例的逻辑需求
	 */
	protected abstract void doStart();
	
	/**
	 * 执行工作单元的提交的逻辑需求
	 */
	protected abstract void doCommit();
	
	/**
	 * 执行工作单元回滚的逻辑需求
	 */
	protected abstract void doRollback(Throwable cause);
	
	private void performInnerCommit(){
		logger.debug("Finalizing commit of inner Unit Of Work...");
		CurrentUnitOfWork.set(this);
		try {
			doCommit();
		} catch (RuntimeException e) {
			doRollback(e);
			throw e;
		}finally{
			clear();
			stop();
		}
	}
	
	private void assertStarted(){
		if(!isStarted){
			throw new IllegalStateException("UnitOfWork is not started");
		}
	}
	
	private void clear(){
		CurrentUnitOfWork.clear(this);
	}
	
	protected void commitInnerUnitOfWork(){
		for(int i=0;i<innerUnitsOfWork.size();i++){
			NestableUnitOfWork unitOfWork = innerUnitsOfWork.get(i);
			if(unitOfWork.isStarted()){
				unitOfWork.performInnerCommit();
			}
		}
	}
	
	private void registerInnerUnitOfWork(NestableUnitOfWork unitOfWork){
		if(outerUnitOfWork instanceof NestableUnitOfWork){
			((NestableUnitOfWork) outerUnitOfWork).registerInnerUnitOfWork(unitOfWork);
		} else {
			innerUnitsOfWork.add(unitOfWork);
		}
	}
	
	/**
	 * 调用该方法保存所有已注册的聚合
	 */
	protected abstract void saveAggregates();
	
	/**
	 * 向该工作单元注册的所有监听器发送准备提交的通知
	 */
	protected abstract void notifyListenersPrepareCommit();
	
	
	public void attachResource(String name, Object resource) {
		this.resources.put(name, resource);
		this.inheritedResources.remove(name);
	}

	public void attachResource(String name, Object resource, boolean inherited) {
		this.resources.put(name, resource);
		if(inherited){
			this.inheritedResources.put(name, resource);
		}else {
			this.inheritedResources.remove(name);
		}
	}
	
	public <T> T getResource(String name) {
		return (T) this.resources.get(name);
	}

	public void attachInheritedResources(UnitOfWork inheritingUnitOfWork) {
		for(Map.Entry<String, Object> entry : inheritedResources.entrySet()){
			inheritingUnitOfWork.attachResource(entry.getKey(), entry.getValue(), true);
		}
	}

	private final class CommitOnOuterCommitTask extends UnitOfWorkListenerAdapter{
		@Override
        public void afterCommit(UnitOfWork unitOfWork) {
            performInnerCommit();
        }

        @Override
        public void onRollback(UnitOfWork unitOfWork, Throwable failureCause) {
            CurrentUnitOfWork.set(NestableUnitOfWork.this);
            rollback(failureCause);
        }

        @Override
        public void onCleanup(UnitOfWork unitOfWork) {
            performCleanup();
        }
	}

}
