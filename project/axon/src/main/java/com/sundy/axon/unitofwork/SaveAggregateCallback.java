package com.sundy.axon.unitofwork;

import com.sundy.axon.domain.AggregateRoot;

/**
 * 回调，用于处理当UnitOfWork希望保持聚合，
 * 该接口的实例可以将实际的存储机制从UnitOfWork中分离出来
 * @author Administrator
 *
 * @param <T>
 */
public interface SaveAggregateCallback<T extends AggregateRoot> {

	/**
	 * 当UnitOfWork希望保持实例时，执行
	 * @param aggregate
	 */
	void save(T aggregate);
	
}
