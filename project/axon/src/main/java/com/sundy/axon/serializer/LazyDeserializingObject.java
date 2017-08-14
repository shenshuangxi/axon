package com.sundy.axon.serializer;

import com.sundy.axon.common.Assert;

/**
 * 表示可以根据请求反序列化对象的序列化对象。 通常用作一个包装类，用于将SerializedObject及其Serializer保持在一起。
 * @author Administrator
 *
 * @param <T> 反序列化后的对象
 */
public class LazyDeserializingObject<T> {

	private final Serializer serializer;
    private final SerializedObject<?> serializedObject;
    private final Class<?> deserializedObjectType;
    private volatile T deserializedObject;
	
    public LazyDeserializingObject(T deserializedObject) {
        Assert.notNull(deserializedObject, "The given deserialized instance may not be null");
        this.serializedObject = null;
        this.serializer = null;
        this.deserializedObject = deserializedObject;
        this.deserializedObjectType = deserializedObject.getClass();
    }
    
    public LazyDeserializingObject(SerializedObject<?> serializedObject, Serializer serializer) {
        Assert.notNull(serializedObject, "The given serializedObject may not be null");
        Assert.notNull(serializer, "The given serializer may not be null");
        this.serializedObject = serializedObject;
        this.serializer = serializer;
        this.deserializedObjectType = serializer.classForType(serializedObject.getType());
    }
    
    /**
     * 反序列化后的对象类型
     * @return
     */
    public Class<?> getType() {
        return deserializedObjectType;
    }
    
    /**
     * 获取反序列化后的对象，使用之前先判断，如果没有该对象，则用序列化器对已有的序列化对象执行反序列化
     * @return
     */
    public T getObject() {
        if (!isDeserialized()) {
            deserializedObject = serializer.deserialize(serializedObject);
        }
        return deserializedObject;
    }
    
    /**
     * 反序列化后的对象是否存在
     * @return
     */
    public boolean isDeserialized() {
        return deserializedObject != null;
    }
    
    /**
     * 获取序列化器
     * @return
     */
    public Serializer getSerializer() {
        return serializer;
    }
    
    /**
     * 获取序列化对象
     * @return
     */
    public SerializedObject<?> getSerializedObject() {
        return serializedObject;
    }
    
    
}
