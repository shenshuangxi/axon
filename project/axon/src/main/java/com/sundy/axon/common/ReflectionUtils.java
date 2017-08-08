package com.sundy.axon.common;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ReflectionUtils {

	public static Iterable<Field> fieldsOf(Class<?> clazz) {
		List<Field> fields = new LinkedList<Field>();
		Class<?> currentClazz = clazz;
		do {
			fields.addAll(Arrays.asList(currentClazz.getFields()));
			currentClazz = currentClazz.getSuperclass();
		} while (currentClazz!=null);
		return Collections.unmodifiableList(fields);
	}
	
	public static Object getFieldValue(Field field, Object object) {
		ensureAccessible(field);
		try {
			return field.get(object);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Unable to access field.", e);
		}
	}
	
	public static Iterable<Method> methodsOf(Class<?>  clazz) {
		List<Method> methods = new LinkedList<Method>();
		Class<?> currentClazz = clazz;
		do {
			methods.addAll(Arrays.asList(currentClazz.getDeclaredMethods()));
			addMethodsOnDeclaredInterfaces(currentClazz, methods);
			currentClazz = currentClazz.getSuperclass();
		} while (currentClazz!=null);
		return Collections.unmodifiableList(methods);
	}

	private static void addMethodsOnDeclaredInterfaces(Class<?> currentClazz,
			List<Method> methods) {
		for(Class iface : currentClazz.getInterfaces()){
			methods.addAll(Arrays.asList(iface.getDeclaredMethods()));
			addMethodsOnDeclaredInterfaces(iface, methods);
		}
	}

	public static <T extends AccessibleObject> T ensureAccessible(T member) {
		if(!isAccessible(member)){
			AccessController.doPrivileged(new MemberAccessibilityCallback(member));
		}
		return member;
	}

	public static boolean isAccessible(AccessibleObject accessibleObject){
		return accessibleObject.isAccessible() || (Member.class.isInstance(accessibleObject) && isNonFinalPublicMember((Member)accessibleObject));
	}

	private static boolean isNonFinalPublicMember(Member member) {
		return (Modifier.isPublic(member.getModifiers()) && Modifier.isPublic(member.getDeclaringClass().getModifiers()) && !Modifier.isFinal(member.getModifiers()));
	}


}
