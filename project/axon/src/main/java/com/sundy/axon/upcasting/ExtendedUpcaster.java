package com.sundy.axon.upcasting;

import java.util.List;

import com.sundy.axon.serializer.SerializedObject;
import com.sundy.axon.serializer.SerializedType;

/**
 * Upcast接口的扩展接口类，该接口允许将序列化内容转换为中间类型的对象。转换链必须有实现{@link #upcast(org.axonframework.serializer.SerializedType,org.axonframework.serializer.SerializedObject)}
 * @author Administrator
 *
 * @param <T> upcast转换的代表事件的数据格式
 */
public interface ExtendedUpcaster<T> extends Upcaster<T> {

	/**
	 * 将给定的序列化类型转换成新的格式，一般来说，这会涉及到版本修订，有些时候，还需要修改类型名称(有重命名类的情况下),返回列表的顺序和大小必须与转换器的中间类型的顺序一致。
	 * <p/>
	 * 不像{@link #upcast(org.axonframework.serializer.SerializedType)}方法，这个方法可以让你访问upcast的中间序列化对象。这个可以根据消息的载荷来选择序列化类
	 * <p/>
	 * ExtendedUpcaster接口实现必须使用该方法替换{@link #upcast(org.axonframework.serializer.SerializedType)}
	 * @param serializedType
	 * @param serializedObject
	 * @return
	 */ 
	List<SerializedType> upcast(SerializedType serializedType, SerializedObject<T> serializedObject);
	
	/**
	 * 该方法可选择基础实现
	 */
	List<SerializedType> upcast(SerializedType serializedType);
	
}
