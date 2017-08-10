package com.sundy.axon.domain;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MetaData implements Map<String, Object>, Serializable {

	private static final long serialVersionUID = -7892913866303912970L;
	
	private static final MetaData EMPTY_META_DATA = new MetaData();
	
	private static final String UNSUPPORTED_MUTATION_MSG = "Event meta-data is immutable";
	
	private final Map<String, Object> values;
	
	public static MetaData emptyInstance(){
		return EMPTY_META_DATA;
	}
	
	private MetaData(){
		values = Collections.emptyMap();
	}
	
	public MetaData(Map<String,?> items){
		values = Collections.unmodifiableMap(new HashMap<String, Object>(items));
	}
	
	/**
	 * 用给定的map数据 得到一个metaData实例，如果给定的参数就是metaData实例，直接返回，如果给定参数为空或为null返回一个空元数据实例
	 * 否则返回包含该数据的元数据
	 * @param metaDataEntries
	 * @return
	 */
	public static MetaData from(Map<String,?> metaDataEntries){
		if(metaDataEntries instanceof MetaData){
			return (MetaData) metaDataEntries;
		}else if(metaDataEntries == null || metaDataEntries.isEmpty()){
			return MetaData.EMPTY_META_DATA;
		}
		return new MetaData(metaDataEntries);
	}
	
	public int size() {
		return values.size();
	}

	public boolean isEmpty() {
		return values.isEmpty();
	}

	public boolean containsKey(Object key) {
		return values.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return values.containsValue(value);
	}

	public Object get(Object key) {
		return values.get(key);
	}

	public Object put(String key, Object value) {
		throw new UnsupportedOperationException(UNSUPPORTED_MUTATION_MSG);
	}

	public Object remove(Object key) {
		throw new UnsupportedOperationException(UNSUPPORTED_MUTATION_MSG);
	}

	public void putAll(Map<? extends String, ? extends Object> m) {
		throw new UnsupportedOperationException(UNSUPPORTED_MUTATION_MSG);
	}

	public void clear() {
		throw new UnsupportedOperationException(UNSUPPORTED_MUTATION_MSG);
	}

	public Set<String> keySet() {
		return values.keySet();
	}

	public Collection<Object> values() {
		return values.values();
	}

	public Set<Map.Entry<String, Object>> entrySet() {
		return values.entrySet();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Map)) {
            return false;
        }

        Map that = (Map) o;

        return values.equals(that);
    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }
    
    /**
     * 合并元数据，如果键值相同，则以给定的参数为准
     * @param additionEntries
     * @return
     */
    public MetaData mergedWith(Map<String, ?> additionEntries){
    	if(additionEntries.isEmpty()){
    		return this;
    	}
    	Map<String, Object> merged = new HashMap<String, Object>(values);
    	merged.putAll(additionEntries);
    	return new MetaData(merged);
    }
    
    public MetaData withoutKeys(Set<String> keys){
    	if(keys.isEmpty()){
    		return this;
    	}
    	Map<String, ?> modified = new HashMap<String, Object>(values);
    	for(String key : keys){
    		modified.remove(key);
    	}
    	return new MetaData(modified);
    }
    
    protected Object readResolve(){
    	if(isEmpty()){
    		return MetaData.emptyInstance();
    	}
    	return this;
    }
    
    
    
    
    
    
    
    
    

}
