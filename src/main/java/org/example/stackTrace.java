package org.example;

import java.util.Map;
import java.util.Scanner;

/*
* 这是一个堆栈——调用方法信息的Demo
* */
public class stackTrace {
    /*
    * factorial是一个阶乘函数
    * */
    public static int factorial(int n){
        System.out.println("factorial("+n+"):");
        printStackTrace3();

        int r;
        if(n<=1) r = 1;
        else  r= n*factorial(n-1);
        System.out.println("return "+r);
        return r;
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("Enter n: ");
        int n = in.nextInt();
        factorial(n);
    }

    public static void printStackTrace3(){
        Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();
        for(Thread t :map.keySet()){
            StackTraceElement[] frames = map.get(t);
            for(StackTraceElement f :frames){
                System.out.println(f);
            }
        }

    }
}
