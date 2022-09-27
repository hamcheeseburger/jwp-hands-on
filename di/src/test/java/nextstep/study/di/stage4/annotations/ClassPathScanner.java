package nextstep.study.di.stage4.annotations;

import org.reflections.Reflections;

import java.util.HashSet;
import java.util.Set;

public class ClassPathScanner {

    public static Set<Class<?>> getAllClassesInPackage(final String packageName) {
        final Reflections reflections = new Reflections(packageName);
        final Set<Class<?>> repositories = reflections.getTypesAnnotatedWith(Repository.class);
        final Set<Class<?>> services = reflections.getTypesAnnotatedWith(Service.class);
        final Set<Class<?>> allClasses = new HashSet<>(repositories);
        allClasses.addAll(repositories);
        allClasses.addAll(services);
        return allClasses;
    }
}
