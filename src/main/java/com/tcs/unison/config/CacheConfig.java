package com.tcs.unison.config;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

    @Bean("customKeyGenerator")
    KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                // Example: ClassName::methodName::param1::param2
                return target.getClass().getSimpleName() + "::" + method.getName() + "::" + Arrays.deepToString(params);
            }
        };
    }
}
