package com.sundy.axon.auditing;

import java.util.List;

import com.sundy.axon.domain.CommandMessage;
import com.sundy.axon.domain.EventMessage;

public interface AuditLogger {

	/**
	 * 写入一个成功实体到审核日志
	 * <p/>
	 * 该方法可以在调度命令的线程中调用，所以当潜在机制较慢时，可以考虑异步写入
	 * @param command
	 * @param returnValue
	 * @param events
	 */
	void logSuccessful(CommandMessage<?> command, Object returnValue, List<EventMessage> events);
	
	/**
	 *  写入一个成功实体到审核日志,给定的事件可能包含事件，这些事件可能存到事件池或发布到事件总线上
	 * <p/>
	 * 该方法可以在调度命令的线程中调用，所以当潜在机制较慢时，可以考虑异步写入
	 * @param command
	 * @param failureCause
	 * @param events
	 */
	void logFailed(CommandMessage<?> command, Throwable failureCause, List<EventMessage> events);
	
}
