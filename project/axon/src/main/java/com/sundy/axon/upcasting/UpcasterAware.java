package com.sundy.axon.upcasting;

/**
 * 说明执行机制知道Upcasters。 Upcasting是将已弃用的域对象（通常为事件）转换为当前格式的过程。 此过程通常发生在存储在事件存储中的域事件。
 * @author Administrator
 *
 */
public interface UpcasterAware {

	/**
	 * 设置允许序列化对象的较旧版本反序列化的UpcasterChain
	 * @param upcasterChain 转换器链条，用于提供转换器能力
	 */
	void setUpcasterChain(UpcasterChain upcasterChain);
	
}
