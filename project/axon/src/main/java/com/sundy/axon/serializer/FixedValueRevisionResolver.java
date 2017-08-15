package com.sundy.axon.serializer;

/**
 * RevisionResolver实现返回固定值作为版本号，而不管所涉及的序列化对象的类型。 例如，当使用应用程序版本作为版本时，这可能很有用。
 * @author Administrator
 *
 */
public class FixedValueRevisionResolver implements RevisionResolver {

	private final String revision;
	
	public FixedValueRevisionResolver(String revision) {
        this.revision = revision;
    }
	
    public String revisionOf(Class<?> payloadType) {
        return revision;
    }
	
}
