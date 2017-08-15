package com.sundy.axon.serializer;

public abstract class AbstractContentTypeConverter<S, T> implements ContentTypeConverter<S, T> {

	public SerializedObject<T> convert(SerializedObject<S> original) {
		return new SimpleSerializedObject<T>(convert(original.getData()), targetType(), original.getType());
	}


}
