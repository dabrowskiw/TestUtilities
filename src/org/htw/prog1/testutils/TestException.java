package org.htw.prog1.testutils;

public class TestException extends Exception {
    private String testExceptionMessage;

    public TestException(String message) {
        testExceptionMessage = message;
    }

    public String getTestExceptionMessage() {
        return testExceptionMessage;
    }
}