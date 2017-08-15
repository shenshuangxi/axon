package com.sundy.axon.unitofwork;

/**
 * UnitOfWorkFactory类的一个默认实现类，该类用于创建{@link DefaultUnitOfWork} 实例
 * @author Administrator
 *
 */
public class DefaultUnitOfWorkFactory implements UnitOfWorkFactory {

	private final TransactionManager transactionManager;
	
	public DefaultUnitOfWorkFactory(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	public DefaultUnitOfWorkFactory() {
		this(null);
	}

	public UnitOfWork createUnitOfWork() {
		return DefaultUnitOfWork.startAndGet(transactionManager);
	}

}
