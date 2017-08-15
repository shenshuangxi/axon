package com.sundy.axon.saga.annotation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sundy.axon.common.ParameterResolverFactory;
import com.sundy.axon.common.annotation.AbstractAnnotatedHandlerDefinition;
import com.sundy.axon.common.annotation.MethodMessageHandler;
import com.sundy.axon.common.annotation.MethodMessageHandlerInspector;
import com.sundy.axon.domain.EventMessage;
import com.sundy.axon.saga.AssociationValue;

public class SagaMethodMessageHandlerInspector<T extends AbstractAnnotatedSaga> {

	private static final Logger logger = LoggerFactory.getLogger(SagaMethodMessageHandlerInspector.class);

    private static final ConcurrentMap<Class<?>, SagaMethodMessageHandlerInspector> INSPECTORS = new ConcurrentHashMap<Class<?>, SagaMethodMessageHandlerInspector>();

    private final Set<SagaMethodMessageHandler> handlers = new TreeSet<SagaMethodMessageHandler>();
    private final Class<T> sagaType;
    private final ParameterResolverFactory parameterResolverFactory;
    
    /**
     * 根据给定类型的sagaType，返回SagaMethodMessageHandlerInspector，该察看器提供 @SagaEventHandler 注解的方法
     * @param sagaType
     * @param parameterResolverFactory
     * @return
     */
    public static <T extends AbstractAnnotatedSaga> SagaMethodMessageHandlerInspector<T> getInstance(
            Class<T> sagaType, ParameterResolverFactory parameterResolverFactory) {
        SagaMethodMessageHandlerInspector<T> sagaInspector = INSPECTORS.get(sagaType);
        if (sagaInspector == null || sagaInspector.getParameterResolverFactory() != parameterResolverFactory) {
            sagaInspector = new SagaMethodMessageHandlerInspector<T>(sagaType, parameterResolverFactory);

            INSPECTORS.put(sagaType, sagaInspector);
        }
        return sagaInspector;
    }
    
    public SagaMethodMessageHandler findHandlerMethod(AbstractAnnotatedSaga target, EventMessage event) {
        for (SagaMethodMessageHandler handler : getMessageHandlers(event)) {
            final AssociationValue associationValue = handler.getAssociationValue(event);
            if (target.getAssociationValues().contains(associationValue)) {
                return handler;
            } else if (logger.isDebugEnabled()) {
                logger.debug(
                        "Skipping handler [{}], it requires an association value [{}:{}] that this Saga is not associated with",
                        handler.getName(),
                        associationValue.getKey(),
                        associationValue.getValue());
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("No suitable handler was found for event of type", event.getPayloadType().getName());
        }
        return SagaMethodMessageHandler.noHandler();
    }
    
    /**
     * 返回当前saga类型
     * @return
     */
    public Class<T> getSagaType() {
        return sagaType;
    }
    
    /**
     * 返回用于当前处理器的参数解析器
     * @return
     */
    public ParameterResolverFactory getParameterResolverFactory() {
        return parameterResolverFactory;
    }
    
    /**
     * 根据给定类型的saga和参数解析器工厂，创建SagaMethodMessageHandlerInspector实例
     * @param sagaType
     * @param parameterResolverFactory
     */
    protected SagaMethodMessageHandlerInspector(Class<T> sagaType, ParameterResolverFactory parameterResolverFactory) {
        this.parameterResolverFactory = parameterResolverFactory;
        MethodMessageHandlerInspector inspector = MethodMessageHandlerInspector.getInstance(
                sagaType, parameterResolverFactory, true,
                AnnotatedHandlerDefinition.INSTANCE);
        for (MethodMessageHandler handler : inspector.getHandlers()) {
            handlers.add(SagaMethodMessageHandler.getInstance(handler));
        }
        this.sagaType = sagaType;
    }
    
    /**
     * 获取能处理给定消息的saga消息处理器
     * @param event
     * @return
     */
    public List<SagaMethodMessageHandler> getMessageHandlers(EventMessage event) {
        List<SagaMethodMessageHandler> found = new ArrayList<SagaMethodMessageHandler>(1);
        for (SagaMethodMessageHandler handler : handlers) {
            if (handler.matches(event)) {
                found.add(handler);
            }
        }
        return found;
    }
    
    private static final class AnnotatedHandlerDefinition extends AbstractAnnotatedHandlerDefinition<SagaEventHandler> {

    	private static final AnnotatedHandlerDefinition INSTANCE = new AnnotatedHandlerDefinition();
	
		private AnnotatedHandlerDefinition() {
		    super(SagaEventHandler.class);
		}
		
		@Override
		protected Class<?> getDefinedPayload(SagaEventHandler annotation) {
		    return annotation.payloadType();
		}
	}
	
}
