package com.sundy.axon.saga;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * 一个基本的saga实例工厂，该类通过带有无参构造函数来实例化saga，并将一个无资源的注入器注入saga资源来实现实例化saga的
 * @author Administrator
 *
 */
public class GenericSagaFactory implements SagaFactory {

	private static final String UNSUITABLE_CTR_MSG = "[%s] is not a suitable type for the GenericSagaFactory. ";
    private ResourceInjector resourceInjector = NullResourceInjector.INSTANCE;
	
	@Override
	public <T extends Saga> T createSaga(Class<T> sagaType) {
		try {
            T instance = sagaType.getConstructor().newInstance();
            resourceInjector.injectResources(instance);
            return instance;
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(
                    String.format(UNSUITABLE_CTR_MSG + "It needs an accessible default constructor.",
                           sagaType.getSimpleName()), e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(
            		String.format(UNSUITABLE_CTR_MSG + "The default constructor is not accessible.",
                           sagaType.getSimpleName()), e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(
            		String.format(UNSUITABLE_CTR_MSG + "An exception occurred while invoking the default constructor.",
                           sagaType.getSimpleName()), e);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(
            		String.format(UNSUITABLE_CTR_MSG + "There must be an accessible default (no-arg) constructor.",
                           sagaType.getSimpleName()), e);
        }
	}

	@Override
	public boolean supports(Class<? extends Saga> sagaType) {
		Constructor<?>[] constructors = sagaType.getConstructors();
        for (Constructor constructor : constructors) {
            if (constructor.getParameterTypes().length == 0) {
                return true;
            }
        }
        return false;
	}
	
	private static final class NullResourceInjector implements ResourceInjector {

		public static final NullResourceInjector INSTANCE = new NullResourceInjector();

        private NullResourceInjector() {
        }
		
		@Override
		public void injectResources(Saga saga) {
			// TODO Auto-generated method stub
			
		}
		
	}

}
