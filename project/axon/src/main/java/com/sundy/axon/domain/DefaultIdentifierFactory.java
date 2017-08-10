package com.sundy.axon.domain;

import java.util.UUID;

/**
 * 默认的识别码生成工厂。该默认工厂不是最好的，依赖于本地jvm， 范围3*10^38个值，只有很小的几率得到重复的值
 * @author Administrator
 *
 */
public class DefaultIdentifierFactory extends IdentifierFactory {

	/**
	 * 实现接口，基于UUID
	 */
	@Override
	public String generateIdentifier() {
		return UUID.randomUUID().toString();
	}

}
