package com.sundy.axon.upcasting;

import java.util.List;

import com.sundy.axon.serializer.SerializedObject;
import com.sundy.axon.serializer.SerializedType;

/**
 * 接口用于向上转型(将序列化对象反序列化为原来的类型) 转换器用于将序列化的对象转换成新的格式，但序列化器自身是不能适用这种转换的。转换器允许你配置更为复杂的转换结构。
 * <p/>
 * 转换器的工作时将一些中间类型转为我们需要。一般情况下，这些中间类型是字节类型的。当然一些结构化的类型也可能适用。为了更好的应用这些转换器，最好是将这转换器组成链条适用
 * @author Administrator
 *
 * @param <T> 给予转换器适用的事件的数据格式
 */
public interface Upcaster<T> {

	/**
	 * 判断该upcater能否朔源给定的序列化类型。除非该方法返回true，不然upcater 不能将给定类型的序列化对象还原
	 * @param serializedType
	 * @return
	 */
	boolean canUpcast(SerializedType serializedType);
	
	/**
	 * 返回该upcaster期望得到中间类型。序列化器必须保证中间结果具有兼容性
	 * @return 返回期望的中间类型
	 */
	Class<T> expectedRepresentationType();
	
	/**
	 * 将给定的intermediateRepresentation对象转化为0个或多个其他对象，返回的序列化对象必须符合传入的序列化类型
	 * @param intermediateRepresentation 需要向上转换的中间类型
	 * @param expectedTypes	期望返回的序列化对象类型
	 * @param context	向上转型对象的上下文信息
	 * @return 新的中间类型
	 */
	List<SerializedObject<?>> upcast(SerializedObject<T> intermediateRepresentation, List<SerializedType> expectedTypes,UpcastingContext context);
	
	/**
	 * 将给定类型的序列化转为新的序列化类型，这些改变包括版本，有时也可能涉及到类型名称。
	 * 返回的新格式顺序必须和传入的格式顺序是一致的
	 * @param serializedType
	 * @return
	 */
	List<SerializedType> upcast(SerializedType serializedType);
	
}
