package com.sundy.axon.serializer;

/**
 * 根据载体上的注解获取氮气载体的版本号，注意的是该注解是可继承的，这一位置子类的注解所有的版本号可能来自于其父类
 * <p/>
 * 如果传入参数没有注解，那么会返回null
 * @author Administrator
 *
 */
public class AnnotationRevisionResolver implements RevisionResolver {

	public String revisionOf(Class<?> payloadType) {
		Revision revision = payloadType.getAnnotation(Revision.class);
		return revision!=null?revision.value():null;
	}

}
