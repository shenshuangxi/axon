package com.sundy.axon.commandhandling.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.sundy.axon.common.ParameterResolver;
import com.sundy.axon.common.ParameterResolverFactory;
import com.sundy.axon.common.ReflectionUtils;
import com.sundy.axon.common.annotation.AbstractMessageHandler;
import com.sundy.axon.common.annotation.UnsupportedHandlerException;
import com.sundy.axon.domain.AggregateRoot;
import com.sundy.axon.domain.Message;

/**
 * 该命令处理器通过执行聚合的构造方法来创建一个新的聚合实例，
 * @author Administrator
 *
 * @param <T> 该命令处理器所有创建的实例
 */
public final class ConstructorCommandMessageHandler<T extends AggregateRoot> extends AbstractMessageHandler  {

	private final Constructor<T> constructor;
	
	public ConstructorCommandMessageHandler(Constructor<T> constructor,
			ParameterResolver[] resolvers, Class payloadType) {
		super(payloadType,constructor.getDeclaringClass(),resolvers);
		this.constructor = constructor;
	}

	public static <T extends AggregateRoot> ConstructorCommandMessageHandler<T> forConstructor(Constructor<T> constructor, ParameterResolverFactory parameterResolverFactory){
		ParameterResolver[] resolvers = findResolvers(parameterResolverFactory, 
				constructor.getAnnotations(), 
				constructor.getParameterTypes(), 
				constructor.getParameterAnnotations(), 
				true);
		Class<?> firstParameter = constructor.getParameterTypes()[0];
		Class payloadType;
		if(Message.class.isAssignableFrom(firstParameter)){
			payloadType = Object.class;
		}else{
			payloadType = firstParameter;
		}
		ReflectionUtils.ensureAccessible(constructor);
		validate(constructor, resolvers);
		return new ConstructorCommandMessageHandler<T>(constructor,resolvers,payloadType);
	}
	
	private static void validate(Constructor constructor, ParameterResolver[] parameterResolvers) {
        for (int i = 0; i < constructor.getParameterTypes().length; i++) {
            if (parameterResolvers[i] == null) {
                throw new UnsupportedHandlerException(
                        String.format("On method %s, parameter %s is invalid. It is not of any format supported by a provided"
                                       + "ParameterValueResolver.",
                               constructor.toGenericString(), i + 1), constructor);
            }
        }
    }
	

	@Override
	public Object invoke(Object target, Message message)
			throws InvocationTargetException, IllegalAccessException {
		Object[] parameterValues = new Object[getParameterValueResolvers().length];
		for(int i=0;i<parameterValues.length;i++){
			parameterValues[i] = getParameterValueResolvers()[i].resolveParameterValue(message);
		}
		try {
			return constructor.newInstance(parameterValues);
		} catch (InstantiationException e) {
			throw new InvocationTargetException(e.getCause()); // NOSONAR
		} 
	}

	@Override
	public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
		return constructor.getAnnotation(annotationType);
	}

}
