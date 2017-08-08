package com.sundy.axon.repository;

/**
 * 该接口的实现类用于保存聚合到仓库
 * @author Administrator
 *
 * @param <T>
 */
public interface Repository<T> {

	/**
	 * 通过给定的aggregateIdentifier(聚合唯一标识符)获取聚合，并期望获取给定的版本号的聚合。如果该给定值为空，则对返回的聚合不做验证
	 * <p/>
	 * 如果版本不匹配，该实习类可能会在加载的时候立即抛出异常。或者会在将该聚合注册到工作单元的时候
	 * @param aggregateIdentifier
	 * @param expectedVersion
	 * @throws AggregateNotFoundException 找不到对应识别码的聚合
	 * @throws ConflictingModificationException 版本不一致
	 * @return
	 */
	T load(Object aggregateIdentifier, Long expectedVersion);
	
	/**
	 * 根据聚合识别码获取聚合
	 * @param aggregateIdentifier
	 * @throws AggregateNotFoundException 找不到对应识别码的聚合
	 * @return
	 */
	T load(Object aggregateIdentifier);
	
	/**
	 * 添加聚合到仓库，版本必须为空。这样意味着这个聚合之前没有被持久化
	 * <p/>
	 * 该方法不会再方法执行后立即添加到仓库，而是会将该聚合注册到工作单元，等待工作单元提交后再保存到仓库
	 * 
	 * @throws IllegalArgumentException 表示该聚合不是新建立的，这个意味着 聚合的版本必须为null
	 * @param aggregate
	 */
	void add(T aggregate);
	
}
