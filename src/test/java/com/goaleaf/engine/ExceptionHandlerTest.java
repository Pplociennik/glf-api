package com.goaleaf.engine;

import org.junit.Test;

public class ExceptionHandlerTest {

    @Test(expected = RuntimeException.class)
    public void shouldThrownException() {
        throw new RuntimeException("Hello, my name is TestException. Nice to meet you!");
    }
}
