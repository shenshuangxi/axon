package com.sundy.axon.commandhandling.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ReflectPermission;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sundy.axon.common.ParameterResolver;
import com.sundy.axon.common.ParameterResolverFactory;
import com.sundy.axon.common.ReflectionUtils;
import com.sundy.axon.common.annotation.AbstractMessageHandler;
import com.sundy.axon.common.annotation.MethodMessageHandlerInspector;
import com.sundy.axon.common.property.Property;
import com.sundy.axon.common.property.PropertyAccessStrategy;
import com.sundy.axon.domain.AggregateRoot;
import com.sundy.axon.domain.CommandMessage;
import com.sundy.axon.domain.Message;

/**
 * 处理器的侦查器，用于查找所有有注解的构造函数和方法，以便于生成聚合或者处理器的方法
 * @author Administrator
 *
 * @param <T> 本类所要查看的聚合
 */
public class AggregateCommandHandlerInspector<T extends AggregateRoot> {

	private static final Logger logger = LoggerFactory.getLogger(AggregateCommandHandlerInspector.class);
	
	private final List<ConstructorCommandMessageHandler<T>> constructorCommandMessageHandlers = new LinkedList<ConstructorCommandMessageHandler<T>>();
	private final List<AbstractMessageHandler> handlers;
	
	public AggregateCommandHandlerInspector(Class<T> targetType, ParameterResolverFactory parameterResolverFactory) {
		MethodMessageHandlerInspector inspector = MethodMessageHandlerInspector.getInstance(targetType,
				CommandHandler.class,
				parameterResolverFactory,
				true);
		handlers = new ArrayList<AbstractMessageHandler>(inspector.getHandlers());
		processNestedEntityCommandHandlers(targetType, parameterResolverFactory, new RootEntityAccessor(targetType));
		for (Constructor constructor : targetType.getConstructors()) {
			if(constructor.isAnnotationPresent(CommandHandler.class)){
				constructorCommandMessageHandlers.add(ConstructorCommandMessageHandler.forConstructor(constructor, parameterResolverFactory));
			}
		}
	}
	
	private interface EntityAccessor {
		
		Object getInstance(Object aggregate, CommandMessage<?> commandMessage);
		
		Class<?> entityType();
		
	}
	
	private static class EntityForwardingMethodMessageHandler extends AbstractMessageHandler {
		
		private final AbstractMessageHandler handler;
		private final EntityAccessor entityAccessor;
		public EntityForwardingMethodMessageHandler( EntityAccessor entityAccessor, AbstractMessageHandler handler) {
			super(handler);
			this.handler = handler;
			this.entityAccessor = entityAccessor;
		}
		@Override
		public Object invoke(Object target, Message message)
				throws InvocationTargetException, IllegalAccessException {
			Object entity = entityAccessor.getInstance(target, (CommandMessage<?>)message);
			if (entity == null) {
                throw new IllegalStateException("No appropriate entity available in the aggregate. "
                                                        + "The command cannot be handled.");
            }
			return handler.invoke(target, message);
		}
		@Override
		public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
			return handler.getAnnotation(annotationType);
		}
	}
	
	
	private static class EntityFieldAccessor implements EntityAccessor {
		
		private final EntityAccessor entityAccessor;
		private final Field field;
		
		public EntityFieldAccessor(EntityAccessor entityAccessor, Field field) {
			this.entityAccessor = entityAccessor;
			this.field = field;
		}

		public Object getInstance(Object aggregate,CommandMessage<?> commandMessage) {
			Object entity = entityAccessor.getInstance(aggregate, commandMessage);
			return entity;
		}

		public Class<?> entityType() {
			return field.getType();
		}
		
	}
	
	private static class RootEntityAccessor implements EntityAccessor {
		private final Class<?> entityType;

		private RootEntityAccessor(Class<?> entityType) {
			this.entityType = entityType;
		}

		public Object getInstance(Object aggregate, CommandMessage<?> commandMessage) {
			return aggregate;
		}

		public Class<?> entityType() {
			return entityType;
		}
	}
	
	
	private abstract class MultipleEntityFieldAccessor<T> implements EntityAccessor {
		
		private final Class<?> entityType;
		private final EntityAccessor entityAccessor;
		private final Field field;
		private String commandTargetProperty;
		
		public MultipleEntityFieldAccessor(Class<?> entityType,
				EntityAccessor entityAccessor, Field field,
				String commandTargetProperty) {
			this.entityType = entityType;
			this.entityAccessor = entityAccessor;
			this.field = field;
			this.commandTargetProperty = commandTargetProperty;
		}

		public Object getInstance(Object aggregate,
				CommandMessage<?> commandMessage) {
			final Object parentEntity = entityAccessor.getInstance(aggregate, commandMessage);
			if(parentEntity == null){
				return null;
			}
			T entityCollection = (T) ReflectionUtils.getFieldValue(field, parentEntity);
			Property<Object> commandProperty = PropertyAccessStrategy.getProperty(commandMessage.getPayloadType(),commandTargetProperty);
			if(commandProperty == null){
				return null;
			}
			Object commandId = commandProperty.getValue(commandMessage.getPayload());
			if (commandId == null) {
                return null;
            }
			return getEntity(entityCollection, commandId);
		}
		
		protected abstract Object getEntity(T entities,	Object commandId);

		public Class<?> entityType() {
			return entityType;
		}
		
		
	}
	
	private class EntityCollectionFieldAccessor extends MultipleEntityFieldAccessor<Collection<?>>{

		private final Property<Object> entityProperty;
		
		public EntityCollectionFieldAccessor(Class<?> entityType, CommandHandlerMemberCollection annotation,EntityAccessor entityAccessor, Field field) {
			super(entityType, entityAccessor, field, commandTargetProperty)
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
