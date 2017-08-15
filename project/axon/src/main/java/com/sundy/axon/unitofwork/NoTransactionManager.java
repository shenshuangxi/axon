package com.sundy.axon.unitofwork;

/**
 * TransactionManager实现什么都不做。 是不需要特殊事务管理的情况下的占位符需求。
 * @author Administrator
 *
 */
public class NoTransactionManager implements TransactionManager {

	private static final String STATUS = "NoTransactionStatus";
	
	public Object startTransaction() {
		return STATUS;
	}

	public void commitTransaction(Object transactionStatus) {
		
	}

	public void rollbackTransaction(Object transactionStatus) {
		
	}

}
