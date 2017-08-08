package com.sundy.axon.common.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.sundy.axon.common.ParameterResolver;
import com.sundy.axon.common.ParameterResolverFactory;
import com.sundy.axon.common.ReflectionUtils;
import com.sundy.axon.domain.Message;

public final class MethodMessageHandler extends AbstractMessageHandler {

	private final Method method;
	
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
	
	@Override
	public Object invoke(Object target, Message message)
			throws InvocationTargetException, IllegalAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
		// TODO Auto-generated method stub
		return null;
	}

}
