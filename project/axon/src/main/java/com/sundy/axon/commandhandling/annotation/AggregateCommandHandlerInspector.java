package com.sundy.axon.commandhandling.annotation;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sundy.axon.commandhandling.CommandHandler;
import com.sundy.axon.common.ParameterResolverFactory;
import com.sundy.axon.common.annotation.AbstractMessageHandler;
import com.sundy.axon.common.annotation.MethodMessageHandlerInspector;
import com.sundy.axon.domain.AggregateRoot;

/**
 * 处理器的侦查器，用于查找所有有注解的构造函数和方法，以便于生成聚合或者处理器的方法
 * @author Administrator
 *
 * @param <T> 本类所要查看的聚合
 */
public class AggregateCommandHandlerInspector<T extends AggregateRoot> {

	private static final Logger logger = LoggerFactory.getLogger(AggregateCommandHandlerInspector.class);
	
	private final List<ConstructorCommandMessageHandler<T>> conCommandMessageHandlers = new LinkedList<ConstructorCommandMessageHandler<T>>();
	private final List<AbstractMessageHandler> handlers;
	
	public AggregateCommandHandlerInspector(Class<T> targetType, ParameterResolverFactory parameterResolverFactory) {
		MethodMessageHandlerInspector inspector = MethodMessageHandlerInspector.getInstance(targetType,
				CommandHandler.class,
				parameterResolverFactory,
				true);
	}

}
