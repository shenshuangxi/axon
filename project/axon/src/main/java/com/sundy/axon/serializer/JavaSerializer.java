package com.sundy.axon.serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.sundy.axon.common.Assert;
import com.sundy.axon.common.io.IOUtils;

/**
 * 使用java自有的默认的序列化来序列化对象，
 * <p/>
 * 该被序列化的对象需要继承接口{@link Serializable}
 * @author Administrator
 *
 */
public class JavaSerializer implements Serializer {

	private final ConverterFactory converterFactory = new ChainingConverterFactory();
    private final RevisionResolver revisionResolver;
	
    public JavaSerializer() {
        this(new SerialVersionUIDRevisionResolver());
    }
    
    public JavaSerializer(RevisionResolver revisionResolver) {
        Assert.notNull(revisionResolver, "revisionResolver may not be null");
        this.revisionResolver = revisionResolver;
    }
    
    public <T> SerializedObject<T> serialize(Object instance, Class<T> expectedType) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            try {
                oos.writeObject(instance);
            } finally {
                oos.flush();
            }
        } catch (IOException e) {
            throw new SerializationException("An exception occurred writing serialized data to the output stream", e);
        }
        T converted = converterFactory.getConverter(byte[].class, expectedType)
                                      .convert(baos.toByteArray());
        return new SimpleSerializedObject<T>(converted, expectedType, instance.getClass().getName(), revisionOf(instance.getClass()));
    }
    
    public <T> boolean canSerializeTo(Class<T> expectedRepresentation) {
        return (converterFactory.hasConverter(byte[].class, expectedRepresentation));
    }
    
    public <S, T> T deserialize(SerializedObject<S> serializedObject) {
        SerializedObject<InputStream> converted = converterFactory.getConverter(serializedObject.getContentType(),
                                                                                InputStream.class)
                                                                  .convert(serializedObject);
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(converted.getData());
            return (T) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new SerializationException("An error occurred while deserializing: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new SerializationException("An error occurred while deserializing: " + e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(ois);
        }
    }
    
    public Class classForType(SerializedType type) {
        try {
            return Class.forName(type.getName());
        } catch (ClassNotFoundException e) {
            throw new UnknownSerializedTypeException(type, e);
        }
    }

    public SerializedType typeForClass(Class type) {
        return new SimpleSerializedType(type.getName(), revisionOf(type));
    }

    public ConverterFactory getConverterFactory() {
        return converterFactory;
    }

    private String revisionOf(Class<?> type) {
        return revisionResolver.revisionOf(type);
    }
	
}
