package com.sundy.axon.domain;

import java.util.Iterator;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 抽象识别码工厂，用于提供接口访问所有的识别码工厂。 识别码工厂的主要作用是给聚合或者时间生成唯一识别码。
 * <p/>
 * 本抽象类使用{@link ServiceLoader} 机制来加载所有的该类的实现类
 * 如果没有找到，那么本类提供一个默认的实现类，该实现类通过UUID来生成
 * <p/>
 * 为了给开发者提供自定义实现，可以在META-INF/services 文件夹创建一个文件名为<code>org.axonframework.domain.IdentifierFactory</code>的文件。
 * 开发者可以将自己实现类的类全名放入这个文件，注意的是该类必须是public修饰 无参构造函数
 * @author Administrator
 *
 */
public abstract class IdentifierFactory {
	
	private static final Logger logger = LoggerFactory.getLogger(IdentifierFactory.class);
	private static final IdentifierFactory INSTANCE;
	
	static {
		logger.debug("Looking for IdentifierFactory implementation using the context class loader");
		IdentifierFactory factory = locateFactories(Thread.currentThread().getContextClassLoader(), "Context");
		if(factory==null){
			logger.debug("Looking for IdentifierFactory implementation using the IdentifierFactory class loader.");
			factory = locateFactories(IdentifierFactory.class.getClassLoader(), IdentifierFactory.class.getSimpleName());
		}
		if(factory == null){
			factory = new DefaultIdentifierFactory();
			logger.debug("Using default UUID-based IdentifierFactory");
		}else{
			logger.info("Found custom IdentifierFactory implementation: {}", factory.getClass().getName());
		}
		INSTANCE = factory;
	}

	private static IdentifierFactory locateFactories(ClassLoader classLoader, String classLoaderName) {
		IdentifierFactory found = null;
		Iterator<IdentifierFactory> services = ServiceLoader.load(IdentifierFactory.class, classLoader).iterator();
		if(services.hasNext()){
			logger.debug("Found IdentifierFactory implementation using the {} Class Loader", classLoaderName);
			found = services.next();
			if (services.hasNext()) {
                logger.warn("More than one IdentifierFactory implementation was found using the {} "
                                    + "Class Loader. This may result in different selections being made after "
                                    + "restart of the application.", classLoaderName);
            }
		}
		return found;
	}
	
	/**
	 * 返回类加载器中找到的识别码工厂,并返回这个工厂，默认的工厂为 {@link DefaultIdentifierFactory}
	 * @return
	 */
	public static IdentifierFactory getInstance() {
        return INSTANCE;
    }
	
	/**
	 * 生成唯一标识符
	 * @return
	 */
	public abstract String generateIdentifier();
}
