package nextstep.study.di.stage4.annotations;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 스프링의 BeanFactory, ApplicationContext에 해당되는 클래스
 */
class DIContainer {

    private final Set<Object> beans;

    public DIContainer(final Set<Class<?>> classes) {
        this.beans = classes.stream()
                .map(this::getInstance)
                .collect(Collectors.toSet());
        beans.forEach(this::setFields);
    }

    public static DIContainer createContainerForPackage(final String rootPackageName) {
        final Set<Class<?>> allClassesInPackage = ClassPathScanner.getAllClassesInPackage(rootPackageName);
        return new DIContainer(allClassesInPackage);
    }

    private Object getInstance(final Class<?> targetClass) {
        try {
            final Constructor<?> constructor = targetClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void setFields(final Object bean) {
        final Field[] declaredFields = bean.getClass().getDeclaredFields();
        final List<Field> injectFields = Arrays.stream(declaredFields)
                .filter(field -> field.isAnnotationPresent(Inject.class))
                .collect(Collectors.toList());
        for (Field field : injectFields) {
            setField(bean, field);
        }
    }

    private void setField(final Object bean, final Field field) {
        try {
            field.setAccessible(true);
            field.set(bean, getBean(field.getType()));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(final Class<T> aClass) {
        return (T) beans.stream()
                .filter(it -> it.getClass().equals(aClass) || aClass.isAssignableFrom(it.getClass()))
                .findFirst()
                .orElseGet(() -> null);
    }
}
