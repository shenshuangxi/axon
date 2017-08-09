package com.sundy.axon.common.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.sundy.axon.common.ParameterResolverFactory;
import com.sundy.axon.common.ReflectionUtils;
import com.sundy.axon.domain.Message;

/**
 * 工具类，用于获取在给定类和处理器定义上的命令处理器，对于每一处理器，都提供追踪这个方法处理器以便于描述这个方法的功能
 * @author Administrator
 *
 */
public final class MethodMessageHandlerInspector {

	private final Class<?> targetType;
	private final List<MethodMessageHandler> handlers = new ArrayList<MethodMessageHandler>();
	private final ParameterResolverFactory parameterResolverFactory;
	
	private static final ConcurrentMap<String, MethodMessageHandlerInspector> INSPECTORS = new ConcurrentHashMap<String, MethodMessageHandlerInspector>();
	
	/**
	 * 初始化，根据给定的注解定义，获取目标类的所有带有该注解的方法
	 * @param parameterResolverFactory 参数解析工厂 用于生成{@link MethodMessageHandler}
	 * @param handlerClass		目标类
	 * @param allowDuplicates	是否允许重复
	 * @param handlerDefinition	注解定义
	 */
	public MethodMessageHandlerInspector(
			ParameterResolverFactory parameterResolverFactory,
			Class<?> handlerClass, boolean allowDuplicates,
			HandlerDefinition<? super Method> handlerDefinition) {
		this.parameterResolverFactory = parameterResolverFactory;
		this.targetType = handlerClass;
		Iterable<Method> methods = ReflectionUtils.methodsOf(handlerClass);
		NavigableSet<MethodMessageHandler> uniqueHandlers = new TreeSet<MethodMessageHandler>();
		for(Method method : methods){
			if(handlerDefinition.isMessageHandler(method)){
				final Class<?> explicitPayloadType = handlerDefinition.resolvePayloadFor(method);
				MethodMessageHandler messageHandler = MethodMessageHandler.createFor(method, explicitPayloadType, parameterResolverFactory);
				handlers.add(messageHandler);
				if(!allowDuplicates&&!uniqueHandlers.add(messageHandler)){
					MethodMessageHandler existHandler = uniqueHandlers.tailSet(messageHandler).first();
					throw new UnsupportedHandlerException(
                            String.format("The class %s contains two handler methods (%s and %s) that listen "
                                                  + "to the same Message type: %s",
                                          method.getDeclaringClass().getSimpleName(),
                                          messageHandler.getMethodName(),
                                          existHandler.getMethodName(),
                                          messageHandler.getPayloadType().getSimpleName()), method);
				}
			}
		}
		Collections.sort(handlers);
	}

	/**
	 * 根据给定参数返回 MethodMessageHandlerInspector
	 * @param handlerClass					包含消息处理方法的类
	 * @param annotationType				定义在方法上的主机
	 * @param parameterResolverFactory		该方法的参数解析工厂
	 * @param allowDuplicates				是否允许重复存在的方法
	 * @return
	 */
	public static <T extends Annotation> MethodMessageHandlerInspector getInstance(Class<?> handlerClass, 
			Class<T> annotationType, 
			ParameterResolverFactory parameterResolverFactory,
			boolean allowDuplicates){
		return getInstance(handlerClass, parameterResolverFactory, allowDuplicates, new AnnotatedHandlerDefinition<T>(annotationType));
	}
	
	/**
	 * 解析目标类，获取所有的处理方法。
	 * @param handlerClass
	 * @param parameterResolverFactory
	 * @param allowDuplicates
	 * @param handlerDefinition
	 * @return
	 */
	public static <T extends Annotation> MethodMessageHandlerInspector getInstance(Class<?> handlerClass, 
			ParameterResolverFactory parameterResolverFactory,
			boolean allowDuplicates,
			HandlerDefinition<? super Method> handlerDefinition){
		String key = handlerDefinition.toString()+"@"+handlerClass.getName();
		MethodMessageHandlerInspector inspector = INSPECTORS.get(key);
		if(inspector==null||!handlerClass.equals(inspector.getTargetType())||!inspector.parameterResolverFactory.equals(parameterResolverFactory)){
			MethodMessageHandlerInspector handlerInspector = new MethodMessageHandlerInspector(parameterResolverFactory,
                    handlerClass,
                    allowDuplicates,
                    handlerDefinition);
			if(inspector==null){
				INSPECTORS.put(key, handlerInspector);
			}else{
				INSPECTORS.replace(key, inspector, handlerInspector);
			}
			inspector = INSPECTORS.get(key);
		}
		return inspector;
	}
	
	/**
	 * 清楚缓存中的处理器，该方法在inspector发生改变时调用。比如发生在多测试环境是，在spring上下文中使用新的实现类
	 */
	public static void clearCache(){
        INSPECTORS.clear();
    }
	
	public MethodMessageHandler findHandlerMethod(final Message message){
		for (MethodMessageHandler methodMessageHandler : handlers) {
			if(methodMessageHandler.matches(message)){
				return methodMessageHandler;
			}
		}
		return null;
	}
	
	
	
	public List<MethodMessageHandler> getHandlers() {
		return new ArrayList<MethodMessageHandler>(handlers);
	}



	private static class AnnotatedHandlerDefinition<T extends Annotation> extends AbstractAnnotatedHandlerDefinition<T>{

		protected AnnotatedHandlerDefinition(Class<T> annotationType) {
			super(annotationType);
		}

		@Override
		public Class<?> getDefinedPayload(T annotation) {
			return null;
		}
		
	}

	public Class<?> getTargetType() {
		return targetType;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
