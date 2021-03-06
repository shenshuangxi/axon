package com.sundy.axon.auditing;

import com.sundy.axon.commandhandling.CommandHandlerInterceptor;
import com.sundy.axon.commandhandling.InterceptorChain;
import com.sundy.axon.domain.CommandMessage;
import com.sundy.axon.unitofwork.UnitOfWork;

public class AuditingInterceptor implements CommandHandlerInterceptor{

	private AuditDataProvider auditDataProvider = EmptyDataProvider.INSTANCE;
	
	private AuditLogger auditLogger = NullAuditLogger.INSTANCE;
	
	public Object handle(CommandMessage<?> commandMessage,
			UnitOfWork unitOfWork, InterceptorChain interceptorChain)
			throws Throwable {
		AuditUnitOfWorkListener auditUnitOfWorkListener = new AuditUnitOfWorkListener(commandMessage, auditDataProvider, auditLogger);
		unitOfWork.registerListener(auditUnitOfWorkListener);
		Object returnValue = interceptorChain.proceed();
		auditUnitOfWorkListener.setReturnValue(returnValue);
		return returnValue;
	}

	public void setAuditDataProvider(AuditDataProvider auditDataProvider) {
		this.auditDataProvider = auditDataProvider;
	}

	public void setAuditLogger(AuditLogger auditLogger) {
		this.auditLogger = auditLogger;
	}
	
	

}
