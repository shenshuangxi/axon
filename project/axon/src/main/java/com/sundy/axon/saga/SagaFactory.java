package com.sundy.axon.saga;

/**
 * 接口用于创建某种类型的Saga的saga工厂
 * @author Administrator
 *
 */
public interface SagaFactory {

	/**
	 * 根据给定类型的saga创建实例。该实例必须被初始化以及提供所有初始化该实例的资源
	 * @param sagaType
	 * @return
	 */
	<T extends Saga> T createSaga(Class<T> sagaType);
	
	/**
	 * 判断该工厂是否支持创建该类型的saga实例
	 * @param sagaType
	 * @return
	 */
	boolean supports(Class<? extends Saga> sagaType);
	
}
