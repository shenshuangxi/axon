package com.sundy.axon.upcasting;

import org.joda.time.DateTime;

import com.sundy.axon.domain.MetaData;

/**
 * 接口用于描述对象类型提升所包含的上下文信息。一帮来说
 * 这些信息应包含 消息所有的对象
 * @author Administrator
 *
 */
public interface UpcastingContext {

	/**
	 * 返回消息对象的识别码，用于类型提升
	 * @return
	 */
	String getMessageIdentifier();
	
	/**
	 * 返回包含事件的的聚合的识别码。如果对象不属于DomainEventMessage 则返回NULL
	 * @return 产生事件的识别码
	 */
	Object getAggregateIdentifier();
	
	/**
	 * 返回事件的序列号
	 * @return
	 */
	Long getSequenceNumber();
	
	/**
	 * 返回事件创建的时间
	 * @return
	 */
	DateTime getTimestamp();
	
	/**
	 * 返回消息所携带的元数据
	 * @return
	 */
	MetaData getMetaData();
	
}
