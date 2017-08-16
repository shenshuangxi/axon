package com.sundy.axon.correlation;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.sundy.axon.domain.Message;
import com.sundy.axon.domain.MetaData;

public class SimpleCorrelationDataProvider implements CorrelationDataProvider<Message> {

	private final String[] headerNames;
	
	public SimpleCorrelationDataProvider(String... metaDataKeys) {
		this.headerNames = Arrays.copyOf(metaDataKeys, metaDataKeys.length);
	}
	
	@Override
	public Map<String, ?> correlationDataFor(Message message) {
		if(headerNames.length==0){
			return Collections.emptyMap();
		}
		Map<String, Object> data = new HashMap<String, Object>();
        final MetaData metaData = message.getMetaData();
        for (String headerName : headerNames) {
            if (metaData.containsKey(headerName)) {
                data.put(headerName, metaData.get(headerName));
            }
        }
		return data;
	}

}
