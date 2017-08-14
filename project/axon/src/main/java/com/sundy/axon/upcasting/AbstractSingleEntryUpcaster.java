package com.sundy.axon.upcasting;

import java.util.Collections;
import java.util.List;

import com.sundy.axon.serializer.SerializedObject;
import com.sundy.axon.serializer.SerializedType;
import com.sundy.axon.serializer.SimpleSerializedObject;

/**
 * 只需要使用相同的表示将一个序列化对象转换为另一个的类型提升器所需要的抽象实现。
 * <p/>
 * 当类型提升器需要将单个序列化对象转换为多个新的序列化对象时，或者当输出表示类型与预期输入表示类型不同时，此类不适用。
 * @author Administrator
 *
 * @param <T>
 */
public abstract class AbstractSingleEntryUpcaster<T> implements Upcaster<T> {

	public List<SerializedObject<?>> upcast(
			SerializedObject<T> intermediateRepresentation,
			List<SerializedType> expectedTypes, UpcastingContext context) {
		final T upcastObject = doUpcast(intermediateRepresentation, context);
        if (upcastObject == null) {
            return Collections.emptyList();
        }
        return Collections.<SerializedObject<?>>singletonList(
                new SimpleSerializedObject<T>(upcastObject, expectedRepresentationType(), expectedTypes.get(0)));
	}

	public List<SerializedType> upcast(SerializedType serializedType) {
		 final SerializedType upcastType = doUpcast(serializedType);
        if (upcastType == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(upcastType);
	}
	
	/**
	 * 将传入的中间变量，转换为0个或多个其他类型的中间变量。
	 * 返回的序列化类型，必须和给定的序列化类型匹配
	 * @param intermediateRepresentation
	 * @param context
	 * @return
	 */
	protected abstract T doUpcast(SerializedObject<T> intermediateRepresentation, UpcastingContext context);
	
	/**
	 * 将给定的序列化类型提升为新的格式，一般会修改其版本号，也可能修改类名。
	 * @param serializedType
	 * @return
	 */
	protected abstract SerializedType doUpcast(SerializedType serializedType);

}
