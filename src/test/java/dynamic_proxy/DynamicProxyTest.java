package dynamic_proxy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DisplayName("JDK Dynamic Proxy 테스트")
public class DynamicProxyTest {

    @Test
    @DisplayName("테스트 케이스: 소문자가 대문자로 변환되는지 확인")
    public void testUpperCaseConversion() {
        // given
        Hello helloTarget = 프록시_객체_생성(new HelloHandler(new HelloTarget()));

        // when
        String result_hello = helloTarget.sayHello("world");
        String result_hi = helloTarget.sayHi("world");
        String result_world = helloTarget.sayThankYou("world");

        // then
        assertSoftly(softly -> {
            softly.assertThat(result_hello).isEqualTo("HELLO WORLD");
            softly.assertThat(result_hi).isEqualTo("HI WORLD");
            softly.assertThat(result_world).isEqualTo("THANK YOU WORLD");
        });
    }

    @Test
    @DisplayName("테스트 케이스: 혼합된 대소문자가 모두 대문자로 변환되는지 확인")
    public void testUpperCaseConversionWithMixedCase() {
        // given
        HelloHandler helloHandler = new HelloHandler(new HelloTarget());
        Hello helloTarget = 프록시_객체_생성(helloHandler);

        // when
        String result_hello = helloTarget.sayHello("WorLd");
        String result_hi = helloTarget.sayHi("WorLd");
        String result_world = helloTarget.sayThankYou("WorLd");

        // then
        assertSoftly(softly -> {
            softly.assertThat(result_hello).isEqualTo("HELLO WORLD");
            softly.assertThat(result_hi).isEqualTo("HI WORLD");
            softly.assertThat(result_world).isEqualTo("THANK YOU WORLD");
        });
    }

    @Test
    @DisplayName("테스트 케이스: 테스트 케이스: 빈 문자열이 그대로 반환되는지 확인")
    public void testEmptyString() {
        // given
        HelloHandler helloHandler = new HelloHandler(new HelloTarget());
        Hello helloTarget = 프록시_객체_생성(helloHandler);

        // when
        String result_hello = helloTarget.sayHello("");
        String result_hi = helloTarget.sayHi("");
        String result_world = helloTarget.sayThankYou("");

        // then
        assertSoftly(softly -> {
            softly.assertThat(result_hello).isEqualTo("HELLO");
            softly.assertThat(result_hi).isEqualTo("HI");
            softly.assertThat(result_world).isEqualTo("THANK YOU");
        });
    }

    @Test
    @DisplayName("테스트 케이스: 이미 대문자인 문자열이 그대로 반환되는지 확인")
    public void testAlreadyUpperCase() {
        // given
        Hello helloTarget = 프록시_객체_생성(new HelloHandler(new HelloTarget()));

        // when
        String result_hello = helloTarget.sayHello("WORLD");
        String result_hi = helloTarget.sayHi("WORLD");
        String result_world = helloTarget.sayThankYou("WORLD");

        // then
        assertSoftly(softly -> {
            softly.assertThat(result_hello).isEqualTo("HELLO WORLD");
            softly.assertThat(result_hi).isEqualTo("HI WORLD");
            softly.assertThat(result_world).isEqualTo("THANK YOU WORLD");
        });
    }

    private <T extends InvocationHandler> Hello 프록시_객체_생성(T invocationHandler) {
        return (Hello) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class<?>[]{Hello.class}, invocationHandler);
    }
}
