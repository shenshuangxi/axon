package com.sundy.axon.upcasting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.sundy.axon.common.Assert;
import com.sundy.axon.serializer.ChainingConverterFactory;
import com.sundy.axon.serializer.ContentTypeConverter;
import com.sundy.axon.serializer.ConverterFactory;
import com.sundy.axon.serializer.SerializedObject;
import com.sundy.axon.serializer.SerializedType;

/**
 * UpcasterChain接口的抽象实现。 该实现负责迭代过程，并提供实用功能来转换内容类型。
 * @author Administrator
 *
 */
public abstract class AbstractUpcasterChain implements UpcasterChain {

	private final List<Upcaster> upcasters;
	private final ConverterFactory converterFactory;
	
	protected AbstractUpcasterChain(List<Upcaster> upcasters){
		this(new ChainingConverterFactory(),upcasters);
	}
	
	protected AbstractUpcasterChain(ConverterFactory converterFactory, List<Upcaster> upcasters) {
        Assert.notNull(converterFactory, "converterFactory may not be null");
        this.upcasters = upcasters;
        this.converterFactory = converterFactory;
    }
	
	public List<SerializedObject> upcast(SerializedObject serializedObject,
			UpcastingContext upcastingContext) {
		if (upcasters.isEmpty()) {
            return Collections.singletonList(serializedObject);
        }
        Iterator<Upcaster> upcasterIterator = upcasters.iterator();
        return upcastInternal(Collections.singletonList(serializedObject), upcasterIterator, upcastingContext);
	}
	
	protected <S, T> SerializedObject<T> ensureCorrectContentType(SerializedObject<S> serializedObject,
            Class<T> expectedContentType) {
		if (!expectedContentType.isAssignableFrom(serializedObject.getContentType())) {
			ContentTypeConverter<S, T> converter = converterFactory.getConverter(serializedObject.getContentType(),
		                           expectedContentType);
			return converter.convert(serializedObject);
		}
		//noinspection unchecked
		return (SerializedObject<T>) serializedObject;
	}
	
	/**
	 * 在给定的sourceObject上执行给定的upcaster的实际向上播放。 返回的序列化对象列表必须表示给定sourceObject的upcast版本。
	 * <p/>
	 * 返回的SerializedObject列表中的每个项目必须与给定的targetTypes列表匹配。 这些类型通过调用Upcaster.upcast（org.axonframework.serializer.SerializedType）来返回。
	 * @param upcaster
	 * @param sourceObject
	 * @param targetTypes
	 * @param context
	 * @return
	 */
	protected abstract <T> List<SerializedObject<?>> doUpcast(Upcaster<T> upcaster, SerializedObject<?> sourceObject,
            List<SerializedType> targetTypes,
            UpcastingContext context);
	
	private List<SerializedObject> upcastInternal(List<SerializedObject> serializedObjects,
            Iterator<Upcaster> upcasterIterator,
            UpcastingContext context) {
		if (!upcasterIterator.hasNext()) {
			return serializedObjects;
		}
		List<SerializedObject> upcastObjects = new ArrayList<SerializedObject>();
		Upcaster<?> currentUpcaster = upcasterIterator.next();
		for (SerializedObject serializedObject : serializedObjects) {
		if (currentUpcaster.canUpcast(serializedObject.getType())) {
		List<SerializedType> upcastTypes;
		if (currentUpcaster instanceof ExtendedUpcaster) {
			upcastTypes = ((ExtendedUpcaster) currentUpcaster).upcast(serializedObject.getType(), serializedObject);
		} else {
			upcastTypes = currentUpcaster.upcast(serializedObject.getType());
		}
			upcastObjects.addAll(doUpcast(currentUpcaster, serializedObject, upcastTypes, context));
		} else {
			upcastObjects.add(serializedObject);
		}
		}
		return upcastInternal(upcastObjects, upcasterIterator, context);
	}


}
