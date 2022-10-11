package nextstep.study.di.stage3.context;

import java.util.Set;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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

    // 빈 내부에 선언된 필드를 각각 셋팅한다.
    // 각 필드에 빈을 대입(assign)한다.
    private void setFields(final Object bean) {
        final Class<?> aClass = bean.getClass();
        final Field[] declaredFields = aClass.getDeclaredFields();
        for (Field field : declaredFields) {
            try {
                field.setAccessible(true);
                final Object fieldBean = getBean(field.getType());
                if (fieldBean != null) {
                    field.set(bean, fieldBean);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
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
