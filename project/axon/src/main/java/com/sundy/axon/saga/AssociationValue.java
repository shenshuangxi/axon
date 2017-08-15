package com.sundy.axon.saga;

import java.io.Serializable;

import com.sundy.axon.common.Assert;

/**
 * 一个可以被saga找到的key和value的集合.saga可以被有属性关联的事件触发。 一个关联属性可能触发多个saga，一个saga可能与多个属性关联
 * <p/>
 * 当它们的键和值都相等时，两个关联值被认为是相等的。 例如，Saga管理订单可以具有关键字“orderId”和订单标识符作为值的AssociationValue。
 * @author Administrator
 *
 */
public class AssociationValue implements Serializable {

	private static final long serialVersionUID = 3573690125021875389L;
	
	private final String propertyKey;
	private final String propertyValue;
	
	public AssociationValue(String key, String value) {
        Assert.notNull(key, "Cannot associate a Saga with a null key");
        Assert.notNull(value, "Cannot associate a Saga with a null value");
        this.propertyKey = key;
        this.propertyValue = value;
    }
	
	
	
    public String getKey() {
		return propertyKey;
	}



	public String getValue() {
		return propertyValue;
	}



	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssociationValue that = (AssociationValue) o;

        if (!propertyKey.equals(that.propertyKey)) {
            return false;
        }
        if (!propertyValue.equals(that.propertyValue)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = propertyKey.hashCode();
        result = 31 * result + propertyValue.hashCode();
        return result;
    }

}
