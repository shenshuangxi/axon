package com.sundy.axon.saga;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sundy.axon.common.ReflectionUtils;

/**
 * 向saga注入资源的简单实现
 * @author Administrator
 *
 */
public class SimpleResourceInjector implements ResourceInjector {

	private static final Logger logger = LoggerFactory.getLogger(SimpleResourceInjector.class);
    private final Iterable<?> resources;
    
    public SimpleResourceInjector(Object... resources) {
        this(Arrays.asList(resources));
    }
    
    public SimpleResourceInjector(Collection<?> resources) {
        this.resources = new ArrayList<Object>(resources);
    }
	
	@Override
	public void injectResources(Saga saga) {
		for(Method method : ReflectionUtils.methodsOf(saga.getClass())){
			if(isSetter(method)){
				Class<?> requiredType = method.getParameterTypes()[0];
				for(Object resource : resources){
					if(requiredType.isInstance(resource)){
						injectResource(saga, method, resource);
					}
				}
			}
		}
	}
	
	private void injectResource(Saga saga, Method setterMethod, Object resource){
		try {
			ReflectionUtils.ensureAccessible(setterMethod);
			setterMethod.invoke(saga, resource);
		} catch (IllegalAccessException e) {
            logger.warn("Unable to inject resource. Exception while invoking setter: ", e);
        } catch (InvocationTargetException e) {
            logger.warn("Unable to inject resource. Exception while invoking setter: ", e.getCause());
        }
	}

	private boolean isSetter(Method method) {
		return method.getParameterTypes().length==1 && method.getName().startsWith("set");
	}

}
