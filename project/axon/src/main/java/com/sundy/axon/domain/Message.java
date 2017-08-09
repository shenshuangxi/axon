package com.sundy.axon.domain;

import java.io.Serializable;
import java.util.Map;

/**
 * 一条消息，包含载体和元数据，一帮用于表示命令或事件
 * 作为接口，一帮用他的子接口  如{@link CommandMessage}和{@link EventMessage}来替换 
 * @author Administrator
 *
 * @param <T>
 */

public interface Message<T> extends Serializable {

	/**
	 * 返回消息的唯一标识符，如果两条消息有一个相同的标识符，可以认为这两条消息只是一条消息的不同表现形式，
	 * 在这种情况下，元数据可以不同，但载体可能是相同的
	 * @return 消息的唯一标识符
	 */
	String getIdentifier();
	
	/**
	 * 返回事件的元数据，该元数据时key-value结构，当key是string类型，那么value应该是一个可以被序列化的对象
	 * @return
	 */
	MetaData getMetaData();
	
	/**
	 * 返回事件的载体，载体携带有应用的具体信息的
	 * @return 事件的载体
	 */
	T getPayload();
	
	/**
	 * 返回事件载体的类型。
	 * 该方法类似于<code>getPayload().getClass()</code>，但是允许继承者，可以使用懒加载或反序列化来优化这个实现
	 * @return
	 */
	Class getPayloadType();
	
	/**
	 * 返回消息的副本，用给定参数替换掉元数据，但是载体不变
	 * <p/>
	 * 注意的是，尽管这个方法的实现类可能不同，但是必须保证，返回的消息类型是一致的
	 * @param metaData 给定元数据
	 * @return 消息副本
	 */
	Message<T> withMetaData(Map<String, ?> metaData);
	
	/**
	 * 返回消息副本，用给定的元数据合并已有元数据，信息载体不变。
	 * @param metaData 元数据
	 * @return	消息副本
	 */
	Message<T> andMetaData(Map<String, ?> metaData);
}
