package dynamic_proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orm.util.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class HelloHandler implements InvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(HelloHandler.class);

    private Hello hello;

    public HelloHandler(Hello hello) {
        this.hello = hello;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        logger.info("{} 가 실행됨 {}", method.getName(), args);

        Object result = method.invoke(hello, args);

        if (result instanceof String) {
            String strResult = (String) result;
            return StringUtils.isNotBlank(strResult) ? strResult.toUpperCase().trim() : strResult;
        }

        return result;
    }
}
