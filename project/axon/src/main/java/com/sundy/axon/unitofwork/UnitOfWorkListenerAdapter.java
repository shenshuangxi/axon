package com.sundy.axon.unitofwork;

import java.util.List;
import java.util.Set;

import com.sundy.axon.domain.AggregateRoot;
import com.sundy.axon.domain.EventMessage;

public abstract class UnitOfWorkListenerAdapter implements UnitOfWorkListener {

	public void afterCommit(UnitOfWork unitOfWork) {
		
	}

	public void onRollback(UnitOfWork unitOfWork, Throwable failureCause) {
		
	}

	public <T> EventMessage<T> onEventRegistered(UnitOfWork unitOfWork,
			EventMessage<T> event) {
		return event;
	}

	public void onPrepareCommit(UnitOfWork unitOfWork,
			Set<AggregateRoot> aggregateRoots, List<EventMessage> events) {
		
	}

	public void onPrepareTransactionCommit(UnitOfWork unitOfWork,
			Object transaction) {
		
	}

	public void onCleanup(UnitOfWork unitOfWork) {
		
	}

}
