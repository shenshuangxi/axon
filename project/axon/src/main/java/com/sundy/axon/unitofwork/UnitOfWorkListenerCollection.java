package com.sundy.axon.unitofwork;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sundy.axon.auditing.AuditUnitOfWorkListener;
import com.sundy.axon.domain.AggregateRoot;
import com.sundy.axon.domain.EventMessage;


/**
 * 该类已预先定义好的顺序通知所有已注册的监听器。
 * <p/>
 * 该行为对于审核监听器特别有用。因为{@link AuditUnitOfWorkListener}需要在监听器提交之前写一个实体，在提交之后写入一个实体
 * @author Administrator
 *
 */
public class UnitOfWorkListenerCollection implements UnitOfWorkListener {

	private static final Logger logger = LoggerFactory.getLogger(UnitOfWorkListenerCollection.class);
	private final Deque<UnitOfWorkListener> listeners = new ArrayDeque<UnitOfWorkListener>();
	
	public void afterCommit(UnitOfWork unitOfWork) {
		logger.debug("Notifying listeners after commit");
		Iterator<UnitOfWorkListener> descendingIterator = listeners.iterator();
		while(descendingIterator.hasNext()){
			UnitOfWorkListener listener = descendingIterator.next();
			if (logger.isDebugEnabled()) {
                logger.debug("Notifying listener [{}] after commit", listener.getClass().getName());
            }
			listener.afterCommit(unitOfWork);
		}
	}

	public void onRollback(UnitOfWork unitOfWork, Throwable failureCause) {
		logger.debug("Notifying listeners of rollback");
		Iterator<UnitOfWorkListener> descendingIterator = listeners.iterator();
		while(descendingIterator.hasNext()){
			UnitOfWorkListener listener = descendingIterator.next();
			if (logger.isDebugEnabled()) {
				logger.debug("Notifying listener [{}] of rollback", listener.getClass().getName());
            }
			listener.onRollback(unitOfWork, failureCause);
		}
	}

	public <T> EventMessage<T> onEventRegistered(UnitOfWork unitOfWork,
			EventMessage<T> event) {
		EventMessage<T> newEvent = event;
        for (UnitOfWorkListener listener : listeners) {
            newEvent = listener.onEventRegistered(unitOfWork, newEvent);
        }
        return newEvent;
	}

	public void onPrepareCommit(UnitOfWork unitOfWork,
			Set<AggregateRoot> aggregateRoots, List<EventMessage> events) {
		logger.debug("Notifying listeners of commit request");
        for (UnitOfWorkListener listener : listeners) {
            if (logger.isDebugEnabled()) {
                logger.debug("Notifying listener [{}] of upcoming commit", listener.getClass().getName());
            }
            listener.onPrepareCommit(unitOfWork, aggregateRoots, events);
        }
        logger.debug("Listeners successfully notified");
	}

	
	public void onPrepareTransactionCommit(UnitOfWork unitOfWork,
			Object transaction) {
		logger.debug("Notifying listeners of transaction commit request");
        for (UnitOfWorkListener listener : listeners) {
            if (logger.isDebugEnabled()) {
                logger.debug("Notifying listener [{}] of upcoming transaction commit", listener.getClass().getName());
            }
            listener.onPrepareTransactionCommit(unitOfWork, transaction);
        }
        logger.debug("Listeners successfully notified");
	}

	/**
     * {@inheritDoc}
     * <p/>
     * 以相反的顺序执行监听器
     */
	public void onCleanup(UnitOfWork unitOfWork) {
		try {
            logger.debug("Notifying listeners of cleanup");
            Iterator<UnitOfWorkListener> descendingIterator = listeners.descendingIterator();
            while (descendingIterator.hasNext()) {
                UnitOfWorkListener listener = descendingIterator.next();
                try {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Notifying listener [{}] of cleanup", listener.getClass().getName());
                    }
                    listener.onCleanup(unitOfWork);
                } catch (RuntimeException e) {
                    logger.warn("Listener raised an exception on cleanup. Ignoring...", e);
                }
            }
            logger.debug("Listeners successfully notified");
        } finally {
            listeners.clear();
        }
	}
	
	/**
	 * 往集合添加一个监听器，
	 * <p/>
	 * 注意，注册监听器的顺序，将会在决定工作单元在不同阶段执行的顺序
	 * @param listener
	 */
	public void add(UnitOfWorkListener listener) {
        if (logger.isDebugEnabled()) {
            logger.debug("Registering listener: {}", listener.getClass().getName());
        }
        listeners.add(listener);
    }

}
