package com.sundy.axon.common.property;

/**
 * 该接口提供一种机制，它的实现类可以读取预先定义好属性
 * @author Administrator
 *
 * @param <T>  获取属性的对象类型
 */
public interface Property<T> {

	/**
	 * 返回给定对象的属性值
	 * @param target
	 * @return
	 */
	<V> V getValue(T target);
	
}
