package com.dou.shugo.shugo_aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * Author: dou
 * Time: 18-9-3  上午11:43
 * Decription:
 */
@Aspect
public class SHugoAspect {

    @Pointcut("execution(@com.dou.shugo.shugo_annotation.SHugo * *(..))")
    public void pointcut(){
        System.out.println("pointcut !!!");
    }

    @Around("pointcut()")
    public void shugo(ProceedingJoinPoint point) throws Throwable{

        MethodSignature signature = (MethodSignature) point.getSignature();

        long starttime =System.currentTimeMillis();
        point.proceed();
        long endtime = System.currentTimeMillis();
        System.out.println(signature.getMethod().getName() + " 耗时: " + (endtime - starttime) + "ms");
    }
}
