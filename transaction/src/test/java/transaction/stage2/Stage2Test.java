package transaction.stage2;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 트랜잭션 전파(Transaction Propagation)란?
 * 트랜잭션의 경계에서 이미 진행 중인 트랜잭션이 있을 때 또는 없을 때 어떻게 동작할 것인가를 결정하는 방식을 말한다.
 *
 * FirstUserService 클래스의 메서드를 실행할 때 첫 번째 트랜잭션이 생성된다.
 * SecondUserService 클래스의 메서드를 실행할 때 두 번째 트랜잭션이 어떻게 되는지 관찰해보자.
 *
 * https://docs.spring.io/spring-framework/docs/current/reference/html/data-access.html#tx-propagation
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class Stage2Test {

    private static final Logger log = LoggerFactory.getLogger(Stage2Test.class);

    @Autowired
    private FirstUserService firstUserService;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    /**
     * 생성된 트랜잭션이 몇 개인가? 1개.
     * 왜 그런 결과가 나왔을까?
     * 부모 트랜잭션, 자식 트랜잭션이 모두 Required로 설정되었기 때문에,
     * 부모 트랜잭션이 자식에게로 전파된다. 그러므로 총 1개의 트랜잭션이 동작한다.
     */
    @Test
    void testRequired() {
        final var actual = firstUserService.saveFirstTransactionWithRequired();

        log.info("transactions : {}", actual);
        assertThat(actual)
                .hasSize(1)
                .containsExactly("transaction.stage2.FirstUserService.saveFirstTransactionWithRequired");
    }

    /**
     * 생성된 트랜잭션이 몇 개인가?
     * 왜 그런 결과가 나왔을까?
     */
    @Test
    void testRequiredNew() {
        final var actual = firstUserService.saveFirstTransactionWithRequiredNew();

        log.info("transactions : {}", actual);
        assertThat(actual)
                .hasSize(2)
                .containsExactly("transaction.stage2.SecondUserService.saveSecondTransactionWithRequiresNew",
                        "transaction.stage2.FirstUserService.saveFirstTransactionWithRequiredNew");
    }

    /**
     * firstUserService.saveAndExceptionWithRequiredNew()에서 강제로 예외를 발생시킨다.
     * REQUIRES_NEW 일 때 예외로 인한 롤백이 발생하면서 어떤 상황이 발생하는 지 확인해보자.
     * 부모, 자식 트랜잭션에서 user를 각각 add하고 있다.
     * 자식 트랜잭션이 Requires_new이므로 독립적인 트랜잭션이 새롭게 생겨날 것이다.
     * 이 때 부모 트랜잭션이 롤백되면, 자식 트랜잭션에게 영향을 미치지 않으므로 1개의 user만 등록이 될 것이다.
     */
    @Test
    void testRequiredNewWithRollback() {
        assertThat(firstUserService.findAll()).hasSize(0);

        assertThatThrownBy(() -> firstUserService.saveAndExceptionWithRequiredNew())
                .isInstanceOf(RuntimeException.class);

        assertThat(firstUserService.findAll()).hasSize(1);
    }

    /**
     * FirstUserService.saveFirstTransactionWithSupports() 메서드를 보면 @Transactional이 주석으로 되어 있다.
     * 주석인 상태에서 테스트를 실행했을 때와 주석을 해제하고 테스트를 실행했을 때 어떤 차이점이 있는지 확인해보자.
     */
    @Test
    void testSupports() {
        final var actual = firstUserService.saveFirstTransactionWithSupports();

        log.info("transactions : {}", actual);
        assertThat(actual)
                .hasSize(1)
                .containsExactly("transaction.stage2.FirstUserService.saveFirstTransactionWithSupports");
        /*
        * 주석 적용 :
        * 예상 결과 - transaction support는 부모 트랜잭션이 없으면 트랜잭션이 없는 것과 동일하게 동작한다고 해서, currentTransactionName이 존재하지 않을 것이라고 생각하여
        * hasSize가 0일 것이라고 생각했다.
        * 실제 결과 - 실제로는 hasSize가 1이다. 그런데 Transaction의 active상태를 찍어보니 false라는 값이 뜨는 것을 보면 메소드 단위에 @Transactional이 있어서 currentTransactionName이 잡히지만,
        * 실제 유효한 트랜잭션은 아니라고 생각하면 될 것 같다.
        * */
        /*
        * 주석 해제 :
        * support는 부모 트랜잭션에 종속된다고 했으니, 부모 트랜잭션이 존재한다면 required와 동일하게 동작한다.
        * */
    }

    /**
     * FirstUserService.saveFirstTransactionWithMandatory() 메서드를 보면 @Transactional이 주석으로 되어 있다.
     * 주석인 상태에서 테스트를 실행했을 때와 주석을 해제하고 테스트를 실행했을 때 어떤 차이점이 있는지 확인해보자.
     * SUPPORTS와 어떤 점이 다른지도 같이 챙겨보자.
     */
    @Test
    void testMandatory() {
        final var actual = firstUserService.saveFirstTransactionWithMandatory();

        log.info("transactions : {}", actual);
        assertThat(actual)
                .hasSize(1)
                .containsExactly("transaction.stage2.FirstUserService.saveFirstTransactionWithMandatory");
        /*
        * 주석 유지 :
        * Mandatory는 이미 트랜잭션이 존재하지 않으면 예외가 발생한다.
        * Support는 이미 트랜잭션이 존재하지 않아도 예외가 발생하지 않고 트랜잭션이 없는 것 처럼 실행된다.
        * */
        /*
        * 주석 해제 :
        * 이미 트랜잭션이 존재하면 해당 트랜잭션에 종속된다.
        * */
    }

    /**
     * 아래 테스트는 몇 개의 물리적 트랜잭션이 동작할까?
     * FirstUserService.saveFirstTransactionWithNotSupported() 메서드의 @Transactional을 주석 처리하자.
     * 다시 테스트를 실행하면 몇 개의 물리적 트랜잭션이 동작할까?
     * 0개! 논리적 트랜잭션은 1개.
     *
     * 스프링 공식 문서에서 물리적 트랜잭션과 논리적 트랜잭션의 차이점이 무엇인지 찾아보자.
     * 물리적 트랜잭션 : 실제로 동작하는 트랜잭션
     * 논리적 트랜잭션 : 실제 동작할 트랜잭션을 판별하기 위해서 생성한 가상의 트랜잭션.
     * 부모와 자식에 대한 Propagation 과정을 판별하기 위해서는 일단 트랜잭션이 무조건 존재한다고 가정하는 것이 편할 것 같다.
     */
    @Test
    void testNotSupported() {
        final var actual = firstUserService.saveFirstTransactionWithNotSupported();

        log.info("transactions : {}", actual);
        assertThat(actual)
                .hasSize(1)
                .containsExactly("transaction.stage2.SecondUserService.saveSecondTransactionWithNotSupported");
        /*
        * 주석 적용:
        * 비지니스 로직을 트랜잭션 없이 수행한다.
        * */
        /*
        * 주석 해제:
        * 이미 트랜잭션이 존재하면 해당 트랜잭션을 대기시키고 비지니스 로직을 트랜잭션 없이 수행한다.
         * */
    }

    /**
     * 아래 테스트는 왜 실패할까?
     * JpaDialect does not support savepoints - check your JPA provider's capabilities
     * Jpa가 savepoints를 지원하지 않는다고 한다.
     * FirstUserService.saveFirstTransactionWithNested() 메서드의 @Transactional을 주석 처리하면 어떻게 될까?
     * 논리적 트랜잭션이 1개 잡힌다.
     */
    @Test
    void testNested() {
        final var actual = firstUserService.saveFirstTransactionWithNested();

        log.info("transactions : {}", actual);
        assertThat(actual)
                .hasSize(1)
                .containsExactly("transaction.stage2.SecondUserService.saveSecondTransactionWithNested");
    }

    /**
     * 마찬가지로 @Transactional을 주석처리하면서 관찰해보자.
     */
    @Test
    void testNever() {
        final var actual = firstUserService.saveFirstTransactionWithNever();

        log.info("transactions : {}", actual);
        assertThat(actual)
                .hasSize(1)
                .containsExactly("transaction.stage2.SecondUserService.saveSecondTransactionWithNever");
        /*
        * 주석 해제 : Never 는 트랜잭션이 적용되면 안될 때 사용한다. 즉 이미 실행중인 트랜잭션이 있으면 예외를 발생한다.
        * */
        /*
        * 주석 적용 : 이미 트랜잭션이 존재하지 않으면 트랜잭션이 적용되지 않고 로직을 수행한다.
        * */
    }
}
