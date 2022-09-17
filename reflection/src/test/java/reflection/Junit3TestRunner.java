package reflection;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class Junit3TestRunner {

    @Test
    void run() throws Exception {
        // given
        Class<Junit3Test> clazz = Junit3Test.class;
        final Junit3Test targetInstance = clazz.getDeclaredConstructor().newInstance();
        final int expectedExecuteCounts = 2;

        // when
        int actualExecuteCounts = 0;
        for (Method method : clazz.getDeclaredMethods()) {
            final String name = method.getName();
            if (name.startsWith("test")) {
                /* 메소드 실행 : invoke의 첫번째 인자에 대상 메소드를 가지고 있는 클래스 인스턴스를 전달해야 한다.
                * 메소드가 클래스 인스턴스 없이 실행될 수 없기 때문이다.
                * 두 번째 인자로는 메소드에 전달되어야 할 파라미터를 지정할 수 있다.*/
                /* Method를 실행했을 때의 반환 값을 Object로 받는다.
                 반환 값이 void 이면 null을 반환한다. */
                final Object result = method.invoke(targetInstance);
                actualExecuteCounts++;
            }
        }
        assertThat(actualExecuteCounts).isEqualTo(expectedExecuteCounts);
    }
}
