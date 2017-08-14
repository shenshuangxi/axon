package com.sundy.axon.serializer;

import com.sundy.axon.common.Assert;

/**
 * 将其属性作为构造函数参数的SerializedType实现。
 * @author Administrator
 *
 */
public class SimpleSerializedType implements SerializedType {

	private final String type;
    private final String revisionId;
    
    public SimpleSerializedType(String objectType, String revisionNumber) {
        Assert.notNull(objectType, "objectType cannot be null");
        this.type = objectType;
        this.revisionId = revisionNumber;
    }
	
	public String getName() {
		return type;
	}

	public String getRevision() {
		return revisionId;
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SimpleSerializedType that = (SimpleSerializedType) o;

        if (revisionId != null ? !revisionId.equals(that.revisionId) : that.revisionId != null) {
            return false;
        }
        if (!type.equals(that.type)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + (revisionId != null ? revisionId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("SimpleSerializedType[%s] (revision %s)", type, revisionId);
    }

}
