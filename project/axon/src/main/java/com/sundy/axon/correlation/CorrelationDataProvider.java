package com.sundy.axon.correlation;

import java.util.Map;

import com.sundy.axon.domain.Message;

/**
 * 定义来自Message的数据的对象，该消息应作为相关数据附加到由该消息处理结果生成的消息中。
 * @author Administrator
 *
 * @param <T>
 */
public interface CorrelationDataProvider<T extends Message> {

	/**
	 * 在处理给定的消息时，提供具有作为相关数据附加的生成消息的条目的映射。
	 * <p/>
	 * 此方法不应返回null。
	 * @param message
	 * @return
	 */
	Map<String, ?> correlationDataFor(T message);
	
}
