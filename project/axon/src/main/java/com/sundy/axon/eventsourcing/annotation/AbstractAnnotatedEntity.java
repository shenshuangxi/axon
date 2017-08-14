package com.sundy.axon.eventsourcing.annotation;

import java.util.Collection;

import com.sundy.axon.commandhandling.annotation.AggregateAnnotationCommandHandler;
import com.sundy.axon.domain.DomainEventMessage;
import com.sundy.axon.eventsourcing.AbstractEventSourcedEntity;
import com.sundy.axon.eventsourcing.EventSourcedEntity;

public abstract class AbstractAnnotatedEntity extends AbstractEventSourcedEntity  {

	private transient AggregateAnnotationInspector inspector;
	private transient MessageHandlerInvoker eventHandlerInvoker;
	
	@Override
	protected Collection<? extends EventSourcedEntity> getChildEntities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void handle(DomainEventMessage event) {
		// TODO Auto-generated method stub
		
	}

}
