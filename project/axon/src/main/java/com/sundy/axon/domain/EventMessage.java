package com.sundy.axon.domain;

import java.util.Map;
import org.joda.time.DateTime;

/**
 * 一个包裹事件的消息。通过载体的实现。一个事件代表当前一个在系统发生的事件。它包含一个关联时间
 * @author Administrator
 *
 * @param <T>
 */
public interface EventMessage<T> extends Message<T> {

	/**
	 * 事件标识符，每一个消息都有一个唯一标识符，
	 */
	String getIdentifier();
	
	/**
	 * 获取事件发生的时间
	 * @return
	 */
	DateTime getTimeStamp();
	
	/**
	 * 返回一个消息的副本，元数据用给定参数替换，但载体不变
	 */
	EventMessage<T> withMetaData(Map<String, ?> metaData);
	
	/**
	 * 返回一个消息的副本，合并给定的元数据和已有元数据，但载体不变
	 */
	EventMessage<T> andMetaData(Map<String, ?> metaData);

}
