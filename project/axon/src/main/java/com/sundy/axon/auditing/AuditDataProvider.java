package com.sundy.axon.auditing;

import java.util.Map;

import com.sundy.axon.domain.CommandMessage;

/**
 * 该接口用于提供相关联的审计信息，这些审计信息会附加到所有被{@link AuditingInterceptor}处理的事件
 * @author Administrator
 *
 */
public interface AuditDataProvider {

	/**
	 * 该方法返回所给命令锁携带的信息。这个方法每次都是在命令被分发的时候调用
	 * @param command 将要被分发的命令
	 * @return 一张hash表，包含所有命令信息，以及审核的日志信息
	 */
	Map<String, Object> provideAuditDataFor(CommandMessage<?> command);
	
}
