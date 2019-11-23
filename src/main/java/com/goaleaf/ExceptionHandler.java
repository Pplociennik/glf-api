package com.goaleaf;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    public void uncaughtException(Thread t, Throwable e) {
        System.out.println("Toż to wyjątek! Skąd się tu wziąłeś nicponiu?!");
        System.out.println("Poszukałbym tutaj!:");
        System.out.println(e.getMessage());
        System.out.println(t.toString());
    }

}
