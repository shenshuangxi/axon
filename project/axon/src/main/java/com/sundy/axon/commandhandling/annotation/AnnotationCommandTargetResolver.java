package com.sundy.axon.commandhandling.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.sundy.axon.commandhandling.CommandTargetResolver;
import com.sundy.axon.commandhandling.VersionedAggregateIdentifier;
import com.sundy.axon.common.ReflectionUtils;
import com.sundy.axon.domain.CommandMessage;

public class AnnotationCommandTargetResolver implements CommandTargetResolver {

	public VersionedAggregateIdentifier resolveTarget(CommandMessage<?> command) {
		Object aggregateIdentifier;
		Long aggregateVersion;
		try {
			aggregateIdentifier = findIdentifier(command);
			aggregateVersion = findVersion(command);
		} catch (InvocationTargetException e) {
            throw new IllegalArgumentException("An exception occurred while extracting aggregate "
                                                       + "information form a command", e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("The current security context does not allow extraction of "
                                                       + "aggregate information from the given command.", e);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("The value provided for the version is not a number.", e);
        }
        if (aggregateIdentifier == null) {
            throw new IllegalArgumentException(
                    String.format("Invalid command. It does not identify the target aggregate. "
                                   + "Make sure at least one of the fields or methods in the [%s] class contains the "
                                   + "@TargetAggregateIdentifier annotation and that it returns a non-null value.",
                           command.getPayloadType().getSimpleName()));
        }
		return new VersionedAggregateIdentifier(aggregateIdentifier, aggregateVersion);
	}

	@SuppressWarnings("unchecked")
	private <I> I findIdentifier(CommandMessage<?> command) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		for(Method method : ReflectionUtils.methodsOf(command.getPayloadType())){
			if(method.isAnnotationPresent(TargetAggregateIdentifier.class)){
				ReflectionUtils.ensureAccessible(method);
				return (I) method.invoke(command.getPayload());
			}
		}
		for(Field field : ReflectionUtils.fieldsOf(command.getPayloadType())){
			if(field.isAnnotationPresent(TargetAggregateIdentifier.class)){
				ReflectionUtils.ensureAccessible(field);
				return (I) ReflectionUtils.getFieldValue(field, command.getPayload());
			}
		}
		return null;
	}
	
	private Long findVersion(CommandMessage<?> command) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		for(Method method : ReflectionUtils.methodsOf(command.getPayloadType())){
			if(method.isAnnotationPresent(TargetAggregateVersion.class)){
				ReflectionUtils.ensureAccessible(method);
				return asLong(method.invoke(command.getPayload()));
			}
		}
		for(Field field : ReflectionUtils.fieldsOf(command.getPayloadType())){
			if(field.isAnnotationPresent(TargetAggregateVersion.class)){
				ReflectionUtils.ensureAccessible(field);
				return asLong(ReflectionUtils.getFieldValue(field, command.getPayload()));
			}
		}
		return null;
	}
	
	private Long asLong(Object fieldValue){
		if(fieldValue==null){
			return null;
		} else if(Number.class.isInstance(fieldValue)){
			return ((Number)fieldValue).longValue();
		} else {
			return Long.parseLong(fieldValue.toString());
		}
		
	}

}
