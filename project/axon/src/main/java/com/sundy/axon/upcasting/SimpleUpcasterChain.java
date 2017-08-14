package com.sundy.axon.upcasting;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.sundy.axon.serializer.ConverterFactory;
import com.sundy.axon.serializer.SerializedObject;
import com.sundy.axon.serializer.SerializedType;

/**
 * 代表一系列upcasters，它们组合起来将一个org.axonframework.serializer.SerializedObject上传到该有效载荷的最新版本。 每个上推者所需的中间表示是使用converterFactory提供的转换器进行转换的。
 * <p/>
 * 只要可以保证相关上位者的顺序，不同对象类型的上传者可以合并为单个链。
 * @author Administrator
 *
 */
public class SimpleUpcasterChain extends AbstractUpcasterChain {

	public static final UpcasterChain EMPTY = new SimpleUpcasterChain(Collections.<Upcaster>emptyList());
	
	public SimpleUpcasterChain(List<Upcaster> upcasters) {
        super(upcasters);
    }
	
	public SimpleUpcasterChain(ConverterFactory converterFactory, List<Upcaster> upcasters) {
        super(converterFactory, upcasters);
    }
	
	public SimpleUpcasterChain(ConverterFactory converterFactory, Upcaster... upcasters) {
        this(converterFactory, Arrays.asList(upcasters));
    }
	
	@Override
	protected <T> List<SerializedObject<?>> doUpcast(Upcaster<T> upcaster,
			SerializedObject<?> sourceObject, List<SerializedType> targetTypes,
			UpcastingContext context) {
		 SerializedObject<T> converted = ensureCorrectContentType(sourceObject, upcaster.expectedRepresentationType());
		 return upcaster.upcast(converted, targetTypes, context);
	}

}
