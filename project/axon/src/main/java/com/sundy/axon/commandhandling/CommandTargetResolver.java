package com.sundy.axon.commandhandling;

import com.sundy.axon.domain.CommandMessage;

/**
 * 接口描述一种机制，用于获取聚合中的识别码和版本号
 * @author Administrator
 *
 */
public interface CommandTargetResolver {

	/**
	 * 返回命令中包含的识别码和版本号
	 * @param command
	 * @return
	 */
	VersionedAggregateIdentifier resolveTarget(CommandMessage<?> command);
	
}
