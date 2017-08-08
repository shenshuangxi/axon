package com.sundy.axon.common;

import com.sundy.axon.domain.Message;

/**
 * 该接口提供一种机制用于将消息中的参数解析为命令处理器中的入口参数。
 * @author Administrator
 *
 * @param <T> 解析器解析消息后返回的参数
 */
public interface ParameterResolver<T> {

	/**
	 * 将给定消息解析为指定的返回参数类型
	 * @param message 用于解析的消息
	 * @return
	 */
	T resolveParameterValue(Message message);
	
	/**
	 * 判断该解析器是否能够解析该消息
	 * @param message
	 * @return
	 */
	boolean matches(Message message);
	
}
