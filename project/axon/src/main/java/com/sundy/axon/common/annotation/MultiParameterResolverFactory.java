package com.sundy.axon.common.annotation;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.sundy.axon.common.ParameterResolver;
import com.sundy.axon.common.ParameterResolverFactory;

public class MultiParameterResolverFactory implements ParameterResolverFactory{

	private final ParameterResolverFactory[] factories;
	
	public MultiParameterResolverFactory(ParameterResolverFactory... factories) {
		this.factories = Arrays.copyOf(factories, factories.length);
	}
	
	public MultiParameterResolverFactory(List<ParameterResolverFactory> factories) {
        this.factories = factories.toArray(new ParameterResolverFactory[factories.size()]);
    }

	public ParameterResolver createInstance(Annotation[] memberAnnotations,
			Class<?> parameterType, Annotation[] parameterAnnotations) {
		for(ParameterResolverFactory factory :  factories){
			ParameterResolver resolver = factory.createInstance(memberAnnotations, parameterType, parameterAnnotations);
			if(resolver != null){
				return resolver;
			}
		}
		return null;
	}

	public static ParameterResolverFactory ordered(ParameterResolverFactory... factories) {
		return ordered(Arrays.asList(factories));
	}
	
	public static ParameterResolverFactory ordered(List<ParameterResolverFactory> factories) {
		return new MultiParameterResolverFactory(flatten(factories));
	}

	private static ParameterResolverFactory[] flatten(List<ParameterResolverFactory> factories) {
		List<ParameterResolverFactory> flattened = new ArrayList<ParameterResolverFactory>(factories.size());
		for(ParameterResolverFactory parameterResolverFactory : factories){
			if(parameterResolverFactory instanceof MultiParameterResolverFactory){
				flattened.addAll(((MultiParameterResolverFactory)parameterResolverFactory).getDelegates());
			}else{
				flattened.add(parameterResolverFactory);
			}
		}
		Collections.sort(flattened, PriorityAnnotationComparator.getInstance());
		return flattened.toArray(new ParameterResolverFactory[flattened.size()]);
	}
	
	public List<ParameterResolverFactory> getDelegates() {
        return Arrays.asList(factories);
    }

}
