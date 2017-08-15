package com.sundy.axon.unitofwork;

/**
 * 接口用于管理事务
 * <p/>
 * 一般来说，这些接口用于打开数据库事务或者连接到外部系统
 * @author Administrator
 *
 * @param <T> 反应事务状态的对象类型
 */
public interface TransactionManager<T> {

	/**
	 * 启动一个事务，返回的对象代表一个事务的状态，并且执行{@link #commitTransaction(Object)} 
	 * 或者 {@link #rollbackTransaction(Object)}方法时，该对象必须作为一个参数传入
	 * <p/>
	 * 如果一个事务创建成功，该返回对象不能为null
	 * @return
	 */
	T startTransaction();
	
	/**
	 * 根据给定的事物状态提交事物
	 * @param transactionStatus 代表事物状态的对象
	 */
	void commitTransaction(T transactionStatus);
	
	/**
	 * 根据给定的事物状态回滚事物
	 * @param transactionStatus 代表事物状态的对象
	 */
	void rollbackTransaction(T transactionStatus);
	
}
