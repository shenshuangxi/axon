package com.sundy.axon.serializer;

import org.joda.time.DateTime;

/**
 * 描述序列化领域事件消息的属性，事件存储需要实现该接口来进行事件存储
 * @author Administrator
 *
 * @param <T> 序列化数据的类型
 */
public interface SerializedDomainEventData<T> {

	/**
	 * 返回序列化事件的识别码
	 * @return
	 */
	String getEventIdentifier();
	
	/**
	 * 获取事件发生的聚合的识别码
	 */
	Object getAggregateIdentifier();
	
	/**
	 * 获取事件在聚合中的序列号
	 * @return
	 */
	long getSequenceNumber();
	
	/**
	 * 获取事件创建的时间
	 * @return
	 */
	DateTime getTimestamp();
	
	/**
	 * 获取事件消息的元数据
	 * @return
	 */
	SerializedObject<T> getMetaData();
	
	/**
	 * 获取事件的消息载体
	 * @return
	 */
	SerializedObject<T> getPayload();
	
	
}
