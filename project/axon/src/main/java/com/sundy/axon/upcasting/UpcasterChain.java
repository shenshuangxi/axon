package com.sundy.axon.upcasting;

import java.util.List;

import com.sundy.axon.serializer.SerializedObject;

/**
 * 代表一系列upcasters，它们组合起来将一个{@link SerializedObject}转换为该有效载荷的最新版本。
 *  每个转换器所需的中间表示是使用converterFactory提供的转换器进行转换的。
 *  
 *  <p/>
 *  只要可以保证相关上位者的顺序，不同对象类型的上传者可以合并为单个链。
 * @author Administrator
 *
 */
public interface UpcasterChain {

	/**
	 * 给定序列化对象通过转换器链条，返回序列化选项的最新版本和载体
	 * @param serializedObject 需要向上转的序列化对象
	 * @param upcastingContext 消息的上下文信息
	 * @return	转换过的序列化数据
	 */
	List<SerializedObject> upcast(SerializedObject serializedObject, UpcastingContext upcastingContext);
	
}
