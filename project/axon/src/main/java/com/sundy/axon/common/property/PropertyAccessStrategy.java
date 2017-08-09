package com.sundy.axon.common.property;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * 抽象策略，能够访问所有PropertyAccessStrategy的实现类
 * <p/>
 * 应用开发者可以通过ServiceLoader加载自定义的PropertyAccessStrategy
 * 为了实现自定义，在META-INF/services文件夹中，创建一个org.axonframework.common.property.PropertyAccessStrategy文件，并将定义的类全名写入其中
 * <p/>
 * 继承类必须为public修饰，并且非抽象，且有一个默认的构造函数 继承PropertyAccessStrategy
 * <p/>
 * 注意，该类提供的接口不能作为公共api接口使用，因为在不同的版本中可能发生不兼容的更改
 * @author Administrator
 *
 */
public abstract class PropertyAccessStrategy implements Comparable<PropertyAccessStrategy> {

	private static final ServiceLoader<PropertyAccessStrategy> LOADER = ServiceLoader.load(PropertyAccessStrategy.class);
	
	private static final SortedSet<PropertyAccessStrategy> STRATEGIES = new ConcurrentSkipListSet<PropertyAccessStrategy>();
	
	static {
		for (PropertyAccessStrategy factory : LOADER){
			STRATEGIES.add(factory);
		}
	}
	
	/**
	 * 在运行当中注册一个PropertyAccessStrategy
	 * @param strategy
	 */
	public static void register(PropertyAccessStrategy strategy){
		STRATEGIES.add(strategy);
	}
	
	/**
	 * 移除所有已注册的 PropertyAccessStrategy
	 * @param strategy
	 */
	public static void unregister(PropertyAccessStrategy strategy){
		STRATEGIES.remove(strategy);
	}
	
	/**
	 * 根据自然序列遍历所有已知PropertyAccessStrategy的实例，根据传入参数 创建{@link Property}实例，将创建第一个找到的合适的，Property
	 * @param targetClass
	 * @param propertyName
	 * @return
	 */
	public static <T> Property<T> getProperty(Class<T> targetClass, String propertyName){
		Property<T> property = null;
		Iterator<PropertyAccessStrategy> strategies = STRATEGIES.iterator();
		while(property == null && strategies.hasNext()){
			property = strategies.next().propertyFor(targetClass, propertyName);
		}
		return property;
	}
	
	/**
	 * 获取策略的优先级，一般情况下，能够根据属性名提供合适的属性，具有更高的优先级。当两个的优先级一样的时候，按自然顺序获取
	 * <p/>
	 * 该策略属性的属性值为0，在策略前确保评估，请使用高于该值的属性值，否则会低于这个值
	 * @return
	 */
	protected abstract int getPriority();
	
	/**
	 * 根据给定的属性名从目标类中获取<code>Property</code>  如果目标类中没有，则返回null
	 * @param targetClass
	 * @param property
	 * @return
	 */
	protected abstract <T> Property<T> propertyFor(Class<T> targetClass, String property);
	
	public int compareTo(PropertyAccessStrategy o) {
		if (o == this) {
            return 0;
        }
        final int diff = o.getPriority() - getPriority();
        if (diff == 0) {
            // we don't want equality...
            return getClass().getName().compareTo(o.getClass().getName());
        }
        return diff;
	}

}
