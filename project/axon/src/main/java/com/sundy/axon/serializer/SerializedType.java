package com.sundy.axon.serializer;

/**
 * 描述一个序列化对象的类型，这些信息用于决定怎样反序列化一个对象
 * @author Administrator
 *
 */
public interface SerializedType {

	/**
	 * 返回序列化类型的名称，这个名称可能是序列化对象的类名，也可能是别名
	 * @return
	 */
	String getName();
	
	/**
	 * 返回序列化对象识别码的版本号，该识别码版本号用于 upcatster 决定如何将一个序列化对象反序列化
	 * @return
	 */
	String getRevision();
	
}
