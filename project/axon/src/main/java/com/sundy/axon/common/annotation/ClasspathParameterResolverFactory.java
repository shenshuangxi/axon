package com.sundy.axon.common.annotation;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.WeakHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sundy.axon.common.ParameterResolverFactory;

public final class ClasspathParameterResolverFactory {

	private static final Logger logger = LoggerFactory.getLogger(ClasspathParameterResolverFactory.class);
	private static final Object monitor = new Object();
	private static final Map<ClassLoader, WeakReference<ParameterResolverFactory>> FACTORIES = new WeakHashMap<ClassLoader, WeakReference<ParameterResolverFactory>>();
	
	public static ParameterResolverFactory forClass(Class<?> clazz){
		return forClassLoader(clazz == null ? null : clazz.getClassLoader());
	}

	private static ParameterResolverFactory forClassLoader(ClassLoader classLoader) {
		synchronized (monitor) {
			ParameterResolverFactory factory;
			if(!FACTORIES.containsKey(classLoader)){
				factory = MultiParameterResolverFactory.ordered(findDelegates(classLoader));
				FACTORIES.put(classLoader, new WeakReference<ParameterResolverFactory>(factory));
				return factory;
			}
			factory = FACTORIES.get(classLoader).get();
			if(factory == null){
				factory = MultiParameterResolverFactory.ordered(findDelegates(classLoader));
				FACTORIES.put(classLoader, new WeakReference<ParameterResolverFactory>(factory));
			}
			return factory;
		}
		
	}

	private static List<ParameterResolverFactory> findDelegates(ClassLoader classLoader) {
		if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
		Iterator<ParameterResolverFactory> iterator = ServiceLoader.load(ParameterResolverFactory.class, classLoader).iterator();
		final List<ParameterResolverFactory> factories = new ArrayList<ParameterResolverFactory>();
        while (iterator.hasNext()) {
            try {
                ParameterResolverFactory factory = iterator.next();
                factories.add(factory);
            } catch (ServiceConfigurationError e) {
                logger.info("ParameterResolverFactory instance ignored, as one of the required classes is not available"
                                    + "on the classpath: {}", e.getMessage());
            } catch (NoClassDefFoundError e) {
                logger.info("ParameterResolverFactory instance ignored. It relies on a class that cannot be found: {}", e.getMessage());
            }
        }
        return factories;
	}
	
}
