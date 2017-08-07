package com.sundy.axon.domain;

import java.util.Map;

/**
 * 代表一条包裹领域事件已及导致领域改变的事件，与常规的EventMessage不一样的是，他包含一条序列号，该序列号用于放置该消息事件产生的位置
 * @author Administrator
 *
 * @param <T>
 */
public interface DomainEventMessage<T> extends EventMessage<T> {

	/**
	 * 返回来自同一聚合生成的序列号
	 * @return
	 */
	long getSequenceNumber();
	
	/**
	 * 返回产生该事件域的聚合的标识符。
	 * @return
	 */
	Object getAggregateIdentifier();
	
	/**
	 * 返回一个消息的副本，元数据用给定参数替换，但载体不变
	 */
	DomainEventMessage<T> withMetaData(Map<String, ?> metaData);
	
	/**
	 * 返回一个消息的副本，合并给定的元数据和已有元数据，但载体不变
	 */
	DomainEventMessage<T> andMetaData(Map<String, ?> metaData);
	
}
