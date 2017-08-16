package com.sundy.axon.saga;

import java.util.Set;

/**
 * 该接口用于实现存储saga的机制
 * @author Administrator
 *
 */
public interface SagaRepository {

	/**
	 * 通过saga类型以及关联值来获取saga
	 * <p/>
	 * 返回的saga必须是{@link #commit(Saga)}处理后的
	 * @param type
	 * @param associationValue
	 * @return
	 */
	Set<String> find(Class<? extends Saga> type, AssociationValue associationValue);
	
	/**
	 * 通过标识符来加载saga
	 * @param sagaIdentifier
	 * @return
	 */
	Saga load(String sagaIdentifier);
	
	/**
	 * 提交已改变saga实例。如果一个saga已标记为inactive，那么存储会删掉该saga，并且移掉该saga的关联值
	 * @param saga
	 */
	void commit(Saga saga);
	
	/**
	 * 添加一个新的saga实例，如果该saga是inactive，那么会添加失败
	 * @param saga
	 */
	void add(Saga saga);
	
}
