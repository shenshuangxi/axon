package com.sundy.axon.correlation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class CorrelationDataHolder {

	private static ThreadLocal<Map<String, ?>> correlationData = new ThreadLocal<Map<String,?>>();
	
	private CorrelationDataHolder(){}
	
	public static Map<String, ?> getCorrelationData() {
        final Map<String, ?> data = correlationData.get();
        if (data == null) {
            return Collections.emptyMap();
        }
        return data;
    }
	
	public static void setCorrelationData(Map<String, ?> data) {
        if (data == null) {
            clear();
        } else {
            correlationData.set(new HashMap<String, Object>(data));
        }
    }
	
	public static void clear() {
        correlationData.remove();
    }
	
}
