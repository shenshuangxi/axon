package com.sundy.axon.serializer;

import com.sundy.axon.common.Assert;

/**
 * 将所有属性作为构造函数参数的SerializedObject实现。
 * @author Administrator
 *
 * @param <T>
 */
public class SimpleSerializedObject<T> implements SerializedObject<T> {

	private final T data;
    private final SerializedType type;
    private final Class<T> dataType;
    
    /**
     * 用给定的数据和序列化类型来实例化
     * @param data
     * @param serializedType
     * @param dataType
     */
	public SimpleSerializedObject(T data, Class<T> dataType, SerializedType serializedType) {
		Assert.notNull(data, "Data for a serialized object cannot be null");
        Assert.notNull(serializedType, "The type identifier of the serialized object");
        this.data = data;
        this.dataType = dataType;
        this.type = serializedType;
	}
	
	/**
	 * 用给定的数据和序列化类型来实例化
	 * @param data
	 * @param dataType
	 * @param type
	 * @param revision
	 */
	public SimpleSerializedObject(T data, Class<T> dataType, String type, String revision) {
        this(data, dataType, new SimpleSerializedType(type, revision));
    }

	public Class<T> getContentType() {
		return dataType;
	}

	public SerializedType getType() {
		return type;
	}

	public T getData() {
		return data;
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SimpleSerializedObject that = (SimpleSerializedObject) o;

        if (data != null ? !data.equals(that.data) : that.data != null) {
            return false;
        }
        if (dataType != null ? !dataType.equals(that.dataType) : that.dataType != null) {
            return false;
        }
        if (type != null ? !type.equals(that.type) : that.type != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = data != null ? data.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (dataType != null ? dataType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("SimpleSerializedObject [%s]", type);
    }

}
