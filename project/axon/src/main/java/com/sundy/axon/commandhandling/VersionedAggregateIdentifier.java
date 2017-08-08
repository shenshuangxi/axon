package com.sundy.axon.commandhandling;

/**
 * 一个结构用于包含聚合的识别码和版本号
 * @author Administrator
 *
 */
public class VersionedAggregateIdentifier {

	private final Object identifier;
	private final Long version;
	
	public VersionedAggregateIdentifier(Object identifier, Long version) {
		this.identifier = identifier;
		this.version = version;
	}
	
	public Object getIdentifier() {
		return identifier;
	}

	public Long getVersion() {
		return version;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        VersionedAggregateIdentifier that = (VersionedAggregateIdentifier) o;

        if (!identifier.equals(that.identifier)) {
            return false;
        }
        if (version != null ? !version.equals(that.version) : that.version != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = identifier.hashCode();
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }
	
	
}
