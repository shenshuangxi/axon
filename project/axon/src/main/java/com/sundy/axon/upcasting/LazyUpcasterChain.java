package com.sundy.axon.upcasting;

import java.util.ArrayList;
import java.util.List;

import com.sundy.axon.serializer.ChainingConverterFactory;
import com.sundy.axon.serializer.ConverterFactory;
import com.sundy.axon.serializer.SerializedObject;
import com.sundy.axon.serializer.SerializedType;
import com.sundy.axon.serializer.Serializer;

public class LazyUpcasterChain extends AbstractUpcasterChain {

	public LazyUpcasterChain(List<Upcaster> upcasters) {
        this(new ChainingConverterFactory(), upcasters);
    }
	
	public LazyUpcasterChain(Serializer serializer, List<Upcaster> upcasters) {
        super(serializer.getConverterFactory(), upcasters);
    }
	
	public LazyUpcasterChain(ConverterFactory converterFactory, List<Upcaster> upcasters) {
        super(converterFactory, upcasters);
    }
	
	@Override
	protected <T> List<SerializedObject<?>> doUpcast(Upcaster<T> upcaster,
			SerializedObject<?> sourceObject, List<SerializedType> targetTypes,
			UpcastingContext context) {
		LazyUpcastObject<T> lazyUpcastObject = new LazyUpcastObject<T>(sourceObject, targetTypes, upcaster, context);
        List<SerializedObject<?>> upcastObjects = new ArrayList<SerializedObject<?>>(targetTypes.size());
        int t = 0;
        for (SerializedType serializedType : targetTypes) {
            upcastObjects.add(new LazyUpcastingSerializedObject<T>(t, lazyUpcastObject, serializedType));
            t++;
        }
        return upcastObjects;
	}
	
	
	private class LazyUpcastObject<T> {

        private final SerializedObject<?> serializedObject;
        private final List<SerializedType> upcastTypes;
        private final Upcaster<T> currentUpcaster;
        private volatile List<SerializedObject<?>> upcastObjects = null;
        private final UpcastingContext properties;

        public LazyUpcastObject(SerializedObject<?> serializedObject, List<SerializedType> upcastTypes,
                                Upcaster<T> currentUpcaster, UpcastingContext properties) {
            this.serializedObject = serializedObject;
            this.upcastTypes = upcastTypes;
            this.currentUpcaster = currentUpcaster;
            this.properties = properties;
        }

        public List<SerializedObject<?>> getUpcastSerializedObjects() {
            if (upcastObjects == null) {
                SerializedObject<T> converted = ensureCorrectContentType(serializedObject,
                                                                         currentUpcaster.expectedRepresentationType());
                upcastObjects = currentUpcaster.upcast(converted, upcastTypes, properties);
            }
            return upcastObjects;
        }
    }
	
	private static class LazyUpcastingSerializedObject<T> implements SerializedObject {

        private final int index;
        private final LazyUpcastObject<T> lazyUpcastObject;
        private final SerializedType type;

        public LazyUpcastingSerializedObject(int index, LazyUpcastObject<T> lazyUpcastObject, SerializedType type) {
            this.index = index;
            this.lazyUpcastObject = lazyUpcastObject;
            this.type = type;
        }

        public Class<?> getContentType() {
            return lazyUpcastObject.getUpcastSerializedObjects().get(index).getContentType();
        }

        public SerializedType getType() {
            return type;
        }

        public Object getData() {
            return lazyUpcastObject.getUpcastSerializedObjects().get(index).getData();
        }
    }

}
