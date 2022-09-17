package reflection;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class Junit4TestRunner {

    @Test
    void run() throws Exception {
        // given
        Class<Junit4Test> clazz = Junit4Test.class;
        final Junit4Test targetInstance = clazz.getDeclaredConstructor().newInstance();
        final int expectedExecuteCounts = 2;

        // when
        int actualExecuteCounts = 0;
        for(Method method : clazz.getDeclaredMethods()) {
            final MyTest annotation = method.getAnnotation(MyTest.class);
            if(annotation != null) {
                method.invoke(targetInstance);
                actualExecuteCounts++;
            }
        }

        // then
        assertThat(actualExecuteCounts).isEqualTo(expectedExecuteCounts);
    }
}
