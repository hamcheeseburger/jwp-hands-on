package com.example;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ServletTest {

    @Test
    void testSharedCounter() throws Exception {
        // 톰캣 서버 시작
        final var tomcatStarter = TestHttpUtils.createTomcatStarter();
        tomcatStarter.start();

        // shared-counter 페이지를 3번 호출한다.
        final var PATH = "/shared-counter";
        TestHttpUtils.send(PATH);
        TestHttpUtils.send(PATH);
        final var response = TestHttpUtils.send(PATH);

        // 톰캣 서버 종료
        tomcatStarter.stop();

        assertThat(response.statusCode()).isEqualTo(200);

        // expected를 0이 아닌 올바른 값으로 바꿔보자.
        // 예상한 결과가 나왔는가? 왜 이런 결과가 나왔을까?
        // A. 상태를 공유하는 하나의 서블릿이 요청을 처리하기 때문
        assertThat(Integer.parseInt(response.body())).isEqualTo(3);
    }

    @Test
    void testLocalCounter() throws Exception {
        // 톰캣 서버 시작
        final var tomcatStarter = TestHttpUtils.createTomcatStarter();
        tomcatStarter.start();

        // local-counter 페이지를 3번 호출한다.
        final var PATH = "/local-counter";
        TestHttpUtils.send(PATH);
        TestHttpUtils.send(PATH);
        final var response = TestHttpUtils.send(PATH);

        // 톰캣 서버 종료
        tomcatStarter.stop();

        assertThat(response.statusCode()).isEqualTo(200);

        // expected를 0이 아닌 올바른 값으로 바꿔보자.
        // 예상한 결과가 나왔는가? 왜 이런 결과가 나왔을까?
        // A. 서블릿이 상태를 공유하지 않기 때문
        assertThat(Integer.parseInt(response.body())).isEqualTo(1);
    }
}
