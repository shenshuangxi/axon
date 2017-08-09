package com.sundy.axon.common.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import com.sundy.axon.common.Assert;
import com.sundy.axon.common.ParameterResolver;
import com.sundy.axon.common.ParameterResolverFactory;
import com.sundy.axon.common.ReflectionUtils;
import com.sundy.axon.domain.EventMessage;
import com.sundy.axon.domain.Message;

public final class MethodMessageHandler extends AbstractMessageHandler {

	private final Method method;
	
	public MethodMessageHandler(Method method, ParameterResolver[] resolvers, Class<?> payloadType) {
		super(payloadType, method.getDeclaringClass(), resolvers);
		this.method = method;
	}

	public static MethodMessageHandler createFor(Method method, Class<?> explicitPayloadType, ParameterResolverFactory parameterResolverFactory){
		ParameterResolver[] resolvers = findResolvers(parameterResolverFactory, method.getAnnotations(),method.getParameterTypes(),method.getParameterAnnotations(),explicitPayloadType==null);
		Class<?> payloadType = explicitPayloadType;
		if(explicitPayloadType == null){
			Class<?> firstParameter = method.getParameterTypes()[0];
			if(Message.class.isAssignableFrom(firstParameter)){
				payloadType = Object.class;
			}else {
				payloadType = firstParameter;
			}
		}
		ReflectionUtils.ensureAccessible(method);
        validate(method, resolvers);
        return new MethodMessageHandler(method, resolvers, payloadType);
	}
	
	private static void validate(Method method, ParameterResolver[] resolvers) {
		for (int i = 0; i < method.getParameterTypes().length; i++) {
			if(resolvers[i] ==null) {
				throw new UnsupportedHandlerException(
                        String.format("On method %s, parameter %s is invalid. It is not of any format supported by a provided"
                                       + "ParameterValueResolver.",
                               method.toGenericString(), i + 1), method);
			}
		}
		/**
		 * 特殊例子：跟EventListener有相同方法签名，不能通过，
		 * 防止干扰代理机制
		 */
		if(method.getName().equals("handle")
				&& Arrays.equals(method.getParameterTypes(), new Class[] {EventMessage.class})) {
			throw new UnsupportedHandlerException(String.format(
                    "Event Handling class %s contains method %s that has a naming conflict with a "
                            + "method on the EventHandler interface. Please rename the method.",
                    method.getDeclaringClass().getSimpleName(),
                    method.getName()), method);
		}
		
	}

	@Override
	public Object invoke(Object target, Message message)
			throws InvocationTargetException, IllegalAccessException {
		Assert.isTrue(method.getDeclaringClass().isInstance(target),
                "Given target is not an instance of the method's owner.");
		Assert.notNull(message, "Event may not be null");
		Object[] parameterValues = new Object[getParameterValueResolvers().length];
		for (int i = 0; i < parameterValues.length; i++) {
			parameterValues[i] = getParameterValueResolvers()[i].resolveParameterValue(message);
		}
		return method.invoke(target, parameterValues);
	}

	@Override
	public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
		return method.getAnnotation(annotationType);
	}

	public String getMethodName() {
		return method.getName();
	}

}
