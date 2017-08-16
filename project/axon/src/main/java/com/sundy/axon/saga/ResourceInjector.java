package com.sundy.axon.saga;

/**
 * 该接口用于实现将资源注入到saga实例中
 * @author Administrator
 *
 */
public interface ResourceInjector {

	/**
	 * 将需要的资源注入给定的saga中
	 * @param saga
	 */
	void injectResources(Saga saga);
	
}
