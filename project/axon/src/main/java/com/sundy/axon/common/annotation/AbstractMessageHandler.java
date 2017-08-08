package com.sundy.axon.common.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import com.sundy.axon.common.Assert;
import com.sundy.axon.common.ParameterResolver;
import com.sundy.axon.common.ParameterResolverFactory;
import com.sundy.axon.domain.Message;

/**
 * 注解基础消息处理器的父类，该处理器可以对这些处理器进行排序，处理无关负载（即没有父子关系）的处理程序根据其有效负载类型的类名进行排序。
 * <p/>
 * 处理程序调用者应该先评估第一个（最小的）适当的处理程序，然后再评估下一个。
 * @author Administrator
 *
 */
public abstract class AbstractMessageHandler implements Comparable<AbstractMessageHandler> {

	private final Score score;
	private final Class<?> payloadType;
	private final ParameterResolver[] parameterResolvers;
	
	
	protected AbstractMessageHandler(Class<?> payloadType, Class<?> declaringClass,
			ParameterResolver... parameterResolvers) {
		this.score = new Score(payloadType, declaringClass);
		this.payloadType = payloadType;
		this.parameterResolvers = Arrays.copyOf(parameterResolvers, parameterResolvers.length);
	}
	
	protected AbstractMessageHandler(AbstractMessageHandler delegate) {
        this.score = delegate.score;
        this.payloadType = delegate.payloadType;
        this.parameterResolvers = delegate.parameterResolvers;
    }
	
	public boolean matches(Message message) {
        Assert.notNull(message, "Event may not be null");
        if (payloadType != null && !payloadType.isAssignableFrom(message.getPayloadType())) {
            return false;
        }
        for (ParameterResolver parameterResolver : parameterResolvers) {
            if (!parameterResolver.matches(message)) {
                return false;
            }
        }
        return true;
    }
	
	public abstract Object invoke(Object target, Message message) throws InvocationTargetException, IllegalAccessException;
	
	public abstract <T extends Annotation> T getAnnotation(Class<T> annotationType);

	public Class getPayloadType() {
        return payloadType;
    }
	
	protected ParameterResolver[] getParameterValueResolvers() {
        return parameterResolvers;
    }

    public int compareTo(AbstractMessageHandler o) {
        return score.compareTo(o.score);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof AbstractMessageHandler)
                && ((AbstractMessageHandler) obj).score.equals(score);
    }

    @Override
    public int hashCode() {
        return score.hashCode();
    }
    
    /**
     * 根据给定的成员细节获取解析器，有多少个参数类型，则返回多少个解析器，之间一一对应
     * @param parameterResolverFactory  生成解析器的工厂
     * @param memberAnnotations			成员上的注解
     * @param parameterTypes			成员所有的参数类型
     * @param parameterAnnotations		参数类型上所对应的注解
     * @param resolvePayload
     * @return
     */
    protected static ParameterResolver[] findResolvers(
			ParameterResolverFactory parameterResolverFactory,
			Annotation[] memberAnnotations, Class<?>[] parameterTypes,
			Annotation[][] parameterAnnotations, boolean resolvePayload) {
		int parameters = parameterTypes.length;
		ParameterResolver[] parameterValueResolvers = new ParameterResolver[parameters];
		for(int i=0;i<parameters;i++){
			final boolean isPayloadParameter = resolvePayload && i == 0;
			// 目前的情况,第一参数视为载荷参数，payload
			if(isPayloadParameter && !Message.class.isAssignableFrom(parameterTypes[i])){
				parameterValueResolvers[i] = new PayloadParameterResolver(parameterTypes[i]);
			}else{
				parameterValueResolvers[i] = parameterResolverFactory.createInstance(memberAnnotations, parameterTypes[i], parameterAnnotations[i]);
			}
		}
		return parameterValueResolvers;
	}
    
    private static class PayloadParameterResolver implements ParameterResolver {

    	private final Class<?> payloadType;
    	
		protected PayloadParameterResolver(Class<?> payloadType) {
			this.payloadType = payloadType;
		}

		public Object resolveParameterValue(Message message) {
			return message.getPayload();
		}

		public boolean matches(Message message) {
			return message.getPayloadType() != null && payloadType.isAssignableFrom(message.getPayloadType());
		}
    	
    }

	private static final class Score implements Comparable<Score> {
		private final int declarationDepth;
		private final int payloadDepth;
		private final String payloadName;
		
		private Score(Class payloadType, Class<?> declaringClass){
			declarationDepth = superClassCount(declaringClass, 0);
			payloadDepth = superClassCount(payloadType, -255);
			payloadName = payloadType.getName();
		}
		
		private int superClassCount(Class<?> declaringClass, int interfaceScore){
			if(declaringClass.isInterface()){
				return interfaceScore;
			}
			int superClasses = 0;
			while(declaringClass != null){
				superClasses ++;
				declaringClass = declaringClass.getSuperclass();
			}
			return superClasses;
		}
		

        public int compareTo(Score o) {
            if (declarationDepth != o.declarationDepth) {
                return (o.declarationDepth < declarationDepth) ? -1 : 1;
            } else if (payloadDepth != o.payloadDepth) {
                return (o.payloadDepth < payloadDepth) ? -1 : 1;
            } else {
                return payloadName.compareTo(o.payloadName);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Score score = (Score) o;
            return declarationDepth == score.declarationDepth
                    && payloadDepth == score.payloadDepth
                    && payloadName.equals(score.payloadName);
        }

        @Override
        public int hashCode() {
            int result = declarationDepth;
            result = 31 * result + payloadDepth;
            result = 31 * result + payloadName.hashCode();
            return result;
        }
		
	}
	
}
