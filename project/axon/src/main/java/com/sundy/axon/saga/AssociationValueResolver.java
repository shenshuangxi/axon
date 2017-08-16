package com.sundy.axon.saga;

import java.util.Set;

import com.sundy.axon.domain.EventMessage;

/**
 * 接口用于从事件消息中抽取关联值，这个关联值可以用于寻找可能的saga
 * @author Administrator
 *
 */
public interface AssociationValueResolver {

	/**
	 * 从给定的事件消息中抽取关联值
	 * @param event
	 * @return
	 */
	Set<AssociationValue> extractAssociationValues(EventMessage event);
	
}
