package com.sundy.axon.saga.annotation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.sundy.axon.common.AxonConfigurationException;
import com.sundy.axon.common.MessageHandlerInvocationException;
import com.sundy.axon.common.annotation.MethodMessageHandler;
import com.sundy.axon.common.property.Property;
import com.sundy.axon.common.property.PropertyAccessStrategy;
import com.sundy.axon.domain.EventMessage;
import com.sundy.axon.saga.AssociationValue;
import com.sundy.axon.saga.SagaCreationPolicy;

/**
 * 包含SagaEventHandler注释方法信息的数据持有者。
 * @author Administrator
 *
 */
public class SagaMethodMessageHandler implements Comparable<SagaMethodMessageHandler> {

	private static final SagaMethodMessageHandler NO_HANDLER_CONFIGURATION = new SagaMethodMessageHandler(SagaCreationPolicy.NONE, null, null, null, null);
	
	/**
	 * 返回一个SagaMethodMessageHandler 该处理器专门用于查找方法为 *not* 的SagaEventHandler
	 * @return
	 */
	public static SagaMethodMessageHandler noHandler(){
		return NO_HANDLER_CONFIGURATION;
	}
	
	private final SagaCreationPolicy creationPolicy;
    private final MethodMessageHandler handlerMethod;
    private final String associationKey;
    private final String associationPropertyName;
    private final Property associationProperty;
    
    /**
     * 为给定的methodHandler创建一个SagaMethodMessageHandler。 SagaMethodMessageHandler添加特定于Sagas行为的信息，如关联值和创建策略。
     * @param methodHandler
     * @return
     */
    @SuppressWarnings("unchecked")
	public static SagaMethodMessageHandler getInstance(MethodMessageHandler methodHandler) {
        Method handlerMethod = methodHandler.getMethod();
        SagaEventHandler handlerAnnotation = handlerMethod.getAnnotation(SagaEventHandler.class);
        String associationPropertyName = handlerAnnotation.associationProperty();
        Property associationProperty = PropertyAccessStrategy.getProperty(methodHandler.getPayloadType(), associationPropertyName);
        if (associationProperty == null) {
            throw new AxonConfigurationException(String.format("SagaEventHandler %s.%s defines a property %s that is not "
                                                                + "defined on the Event it declares to handle (%s)",
                                                        methodHandler.getMethod().getDeclaringClass().getName(),
                                                        methodHandler.getMethodName(), associationPropertyName,
                                                        methodHandler.getPayloadType().getName()
            ));
        }
        String associationKey = handlerAnnotation.keyName().isEmpty()
                ? associationPropertyName
                : handlerAnnotation.keyName();
        StartSaga startAnnotation = handlerMethod.getAnnotation(StartSaga.class);
        SagaCreationPolicy sagaCreationPolicy;
        if (startAnnotation == null) {
            sagaCreationPolicy = SagaCreationPolicy.NONE;
        } else if (startAnnotation.forceNew()) {
            sagaCreationPolicy = SagaCreationPolicy.ALWAYS;
        } else {
            sagaCreationPolicy = SagaCreationPolicy.IF_NONE_FOUND;
        }

        return new SagaMethodMessageHandler(sagaCreationPolicy, methodHandler, associationKey, associationPropertyName, associationProperty);
    }
    
    protected SagaMethodMessageHandler(SagaCreationPolicy creationPolicy, MethodMessageHandler handler,
            String associationKey, String associationPropertyName,
            Property associationProperty) {
		this.creationPolicy = creationPolicy;
		this.handlerMethod = handler;
		this.associationKey = associationKey;
		this.associationPropertyName = associationPropertyName;
		this.associationProperty = associationProperty;
	}
    
    /**
     * 判断该方法是否为事件处理器
     * @return
     */
    public boolean isHandlerAvailable() {
        return handlerMethod != null;
    }
    
    /**
     * 通过给定参数eventMessage中的载体从saga中获取关联值
     * @param eventMessage
     * @return
     */
    public AssociationValue getAssociationValue(EventMessage eventMessage) {
        if (associationProperty == null) {
            return null;
        }

        Object associationValue = associationProperty.getValue(eventMessage.getPayload());
        return associationValue == null ? null : new AssociationValue(associationKey, associationValue.toString());
    }
    
    /**
     * 返回创建策略
     * @return
     */
    public SagaCreationPolicy getCreationPolicy() {
        return creationPolicy;
    }
    
    /**
     * 判断该处理器是否为处理给定的消息
     * @param message
     * @return
     */
    public boolean matches(EventMessage message) {
        return handlerMethod != null && handlerMethod.matches(message);
    }
    
    /**
     * 判断处理器是否为saga生命周期的尾部
     * @return
     */
    public boolean isEndingHandler() {
        return handlerMethod != null && handlerMethod.getMethod().isAnnotationPresent(EndSaga.class);
    }
    
    public int compareTo(SagaMethodMessageHandler o) {
        if (this.handlerMethod == null && o.handlerMethod == null) {
            return 0;
        } else if (this.handlerMethod == null) {
            return -1;
        } else if (o.handlerMethod == null) {
            return 1;
        }
        int handlerEquality = handlerMethod.compareTo(o.handlerMethod);
        if (handlerEquality == 0) {
            handlerEquality = o.handlerMethod.getMethod().getParameterTypes().length
                    - this.handlerMethod.getMethod().getParameterTypes().length;
        }
        if (handlerEquality == 0) {
            handlerEquality = associationKey.compareTo(o.associationKey);
        }
        if (handlerEquality == 0) {
            handlerEquality = associationPropertyName.compareTo(o.associationPropertyName);
        }
        return handlerEquality;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SagaMethodMessageHandler that = (SagaMethodMessageHandler) o;

        return this.compareTo(that) == 0;
    }

    @Override
    public int hashCode() {
        return handlerMethod != null ? handlerMethod.hashCode() : 0;
    }
    
    /**
     * 根据给定的对象和消息执行交易
     * @param target
     * @param message
     */
    public void invoke(Object target, EventMessage message) {
        if (!isHandlerAvailable()) {
            return;
        }
        try {
            handlerMethod.invoke(target, message);
        } catch (IllegalAccessException e) {
            throw new MessageHandlerInvocationException("Access to the message handler method was denied.", e);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            }
            throw new MessageHandlerInvocationException("An exception occurred while invoking the handler method.", e);
        }
    }
    
    /**
     * 获取处理器的名称
     * @return
     */
    public String getName() {
        return handlerMethod.getMethodName();
    }
	
	
}
