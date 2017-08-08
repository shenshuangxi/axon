package com.sundy.axon.common.annotation;

import java.util.ArrayList;
import java.util.List;

/**
 * 工具类，用于获取在给定类和处理器定义上的命令处理器，对于每一处理器，都提供追踪这个方法处理器以便于描述这个方法的功能
 * @author Administrator
 *
 */
public final class MethodMessageHandlerInspector {

	private final Class<?> targetType;
	private final List<MethodMessageHandler> handlers = new ArrayList<E>();
	
	
}
