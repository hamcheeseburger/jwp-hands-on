package reflection;

import annotation.Controller;
import annotation.Repository;
import annotation.Service;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.reflections.scanners.Scanners.TypesAnnotated;

class ReflectionsTest {

    private static final Logger log = LoggerFactory.getLogger(ReflectionsTest.class);

    @Test
    void showAnnotationClass() throws Exception {
        Reflections reflections = new Reflections("examples");
        Set<Class<?>> controllers = reflections.get(TypesAnnotated.with(Controller.class).asClass());
        final List<String> controllerClassNames = controllers.stream()
                .map(Class::getSimpleName)
                .collect(Collectors.toList());

        final Set<Class<?>> services = reflections.get(TypesAnnotated.with(Service.class).asClass());
        final List<String> servicesClassNames = services.stream()
                .map(Class::getSimpleName)
                .collect(Collectors.toList());

        final Set<Class<?>> repositories = reflections.get(TypesAnnotated.with(Repository.class).asClass());
        final List<String> repositoryClassNames = repositories.stream()
                .map(Class::getSimpleName)
                .collect(Collectors.toList());

        log.info("Controllers : {}", Arrays.toString(controllerClassNames.toArray()));
        log.info("Services : {}", Arrays.toString(servicesClassNames.toArray()));
        log.info("Repositories : {}", Arrays.toString(repositoryClassNames.toArray()));
    }
}
