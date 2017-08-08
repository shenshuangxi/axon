package com.sundy.axon.commandhandling.annotation;

import java.util.Map;

import com.sundy.axon.commandhandling.CommandBus;
import com.sundy.axon.commandhandling.CommandHandler;
import com.sundy.axon.commandhandling.CommandTargetResolver;
import com.sundy.axon.common.Assert;
import com.sundy.axon.common.ParameterResolverFactory;
import com.sundy.axon.common.Subscribable;
import com.sundy.axon.common.annotation.ClasspathParameterResolverFactory;
import com.sundy.axon.domain.AggregateRoot;
import com.sundy.axon.domain.CommandMessage;
import com.sundy.axon.repository.Repository;
import com.sundy.axon.unitofwork.UnitOfWork;

public class AggregateAnnotationCommandHandler<T extends AggregateRoot> implements Subscribable,CommandHandler<Object> {

	private final CommandBus commandBus;
	private final Repository<T> repository;
	
	private final CommandTargetResolver commandTargetResolver;
	private final Map<String, CommandHandler<Object>> handlers;
	private final ParameterResolverFactory parameterResolverFactory;
	
	public AggregateAnnotationCommandHandler(Class<T> aggregateType, Repository<T> repository){
		this(aggregateType, repository, new AnnotationCommandTargetResolver());
	}
	
	public AggregateAnnotationCommandHandler(Class<T> aggregateType,
			Repository<T> repository,
			CommandTargetResolver commandTargetResolver) {
		this(aggregateType, repository, commandTargetResolver, ClasspathParameterResolverFactory.forClass(aggregateType));
	}

	public AggregateAnnotationCommandHandler(Class<T> aggregateType,
			Repository<T> repository,
			CommandTargetResolver commandTargetResolver,
			ParameterResolverFactory parameterResolverFactory) {
		this.parameterResolverFactory = parameterResolverFactory;
        Assert.notNull(aggregateType, "aggregateType may not be null");
        Assert.notNull(repository, "repository may not be null");
        Assert.notNull(commandTargetResolver, "commandTargetResolver may not be null");
        this.repository = repository;
        this.commandBus = null;
        this.commandTargetResolver = commandTargetResolver;
        this.handlers = initializeHandlers(new AggregateCommandHandlerInspector<T>(aggregateType, parameterResolverFactory));
	}

	private Map<String, CommandHandler<Object>> initializeHandlers(
			AggregateCommandHandlerInspector<T> aggregateCommandHandlerInspector) {
		
		return null;
	}

	public Object handle(CommandMessage<Object> commandMessage,
			UnitOfWork unitOfWork) throws Throwable {
		// TODO Auto-generated method stub
		return null;
	}

	public void unsubscribe() {
		// TODO Auto-generated method stub
		
	}

	public void subscribe() {
		// TODO Auto-generated method stub
		
	}

}
