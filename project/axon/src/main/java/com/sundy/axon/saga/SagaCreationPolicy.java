package com.sundy.axon.saga;

/**
 * 枚举可能创建的saga策略
 * @author Administrator
 *
 */
public enum SagaCreationPolicy {

	/**
	 * 即使不存在，也不创建saga实例
	 */
	NONE,
	/**
	 * 只有在没有找到的情况下，才创建
	 */
	IF_NONE_FOUND,
	
	/**
	 * 即使已经存在的情况下，也将创建
	 */
	ALWAYS
	
}
