## Android AOP基础
### AOP和AspectJ
通过预编译方式或者运行期动态代理实现程序功能的统一维护的一种技术。

面向对象编程主要用于为同一对象层面的公用行为建模。弱点是不能将公共行为用在多个无关对象之间。而面向切片编程最大的优点就是将公共行为用在不同的对象模块上。

AspectJ是一种语言来支持aop的，但是他也提供了java的方式使用AspectJ。

[AspectJ的基本用法](https://blog.csdn.net/vonnie_jade/article/details/68955248)
- joinpoint 
- pointcut
- advice

### 使用aop的步骤
#### 1. 添加配置
在module下的build.gradle文件中添加：

    buildscript{

        repositories {
            mavenCentral()
        }

        dependencies{
            classpath 'org.aspectj:aspectjtools:1.8.9'
            classpath 'org.aspectj:aspectjweaver:1.8.9'
        }
    }
    
    ... // 省略
    
    dependencies {
        ...
        implementation 'org.aspectj:aspectjrt:1.8.9'
	}
    
    ... // 省略
    
    import org.aspectj.bridge.IMessage
    import org.aspectj.bridge.MessageHandler
    import org.aspectj.tools.ajc.Main
    final def log = project.logger
    final def variants = project.android.applicationVariants

    variants.all { variant ->
        if (!variant.buildType.isDebuggable()) {
            log.debug("Skipping non-debuggable build type '${variant.buildType.name}'.")
            return;
        }

        JavaCompile javaCompile = variant.javaCompile
        javaCompile.doLast {
            String[] args = ["-showWeaveInfo",
                             "-1.8",
                             "-inpath", javaCompile.destinationDir.toString(),
                             "-aspectpath", javaCompile.classpath.asPath,
                             "-d", javaCompile.destinationDir.toString(),
                             "-classpath", javaCompile.classpath.asPath,
                             "-bootclasspath", project.android.bootClasspath.join(File.pathSeparator)]
            log.debug "ajc args: " + Arrays.toString(args)

            MessageHandler handler = new MessageHandler(true);
            new Main().run(args, handler);
            for (IMessage message : handler.getMessages(null, true)) {
                switch (message.getKind()) {
                    case IMessage.ABORT:
                    case IMessage.ERROR:
                    case IMessage.FAIL:
                        log.error message.message, message.thrown
                        break;
                    case IMessage.WARNING:
                        log.warn message.message, message.thrown
                        break;
                    case IMessage.INFO:
                        log.info message.message, message.thrown
                        break;
                    case IMessage.DEBUG:
                        log.debug message.message, message.thrown
                        break;
                }
            }
        }
    }
    
#### 2. 创建切片类

    @Aspect
    public class TestAspect {

        @Pointcut("execution(* com.dou.demo.aop_demo.MainActivity.test(..))")
        public void pointcut(){}

        @Around("pointcut()")
        public void test(ProceedingJoinPoint point) throws Throwable{
            System.out.println("@test start");
            point.proceed();
            System.out.println("@test end");
        }
    }

### 实践(对方法进行耗时计算/权限检查)
定义annotation

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.CLASS)
    public @interface Hugo {
    }

使用注解

       @Hugo
        private void time_consumer() {
            for (int i = 0; i < 100; i++){
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

定义切片

    @Aspect
    public class HugoAspect {
        @Pointcut("execution(@com.dou.demo.aop_demo.Hugo * *(..))")
        public void hugo(){}

        @Around("hugo()")
        public void timer(ProceedingJoinPoint point) throws Throwable{
            System.out.println("@hugo start");
            MethodSignature signature = (MethodSignature) point.getSignature();
            long starttime = System.currentTimeMillis();
            point.proceed();
            long endtime = System.currentTimeMillis();
            System.out.println(signature.getMethod().getName() + " 方法耗时： " + (endtime - starttime) + " ms");
            System.out.println("@hugo end");
        }
    }

重新clean,run

其它一个实例见源码

### 源码
[aop-demo](https://github.com/douyn/aop-demo.git)

### 参考
[深入理解Android之AOP](https://blog.csdn.net/innost/article/details/49387395)

[Android AOP-North_2016](https://www.jianshu.com/nb/1529181)

[Android进阶系列之AOP面向切面编程](https://blog.csdn.net/sw5131899/article/details/53885957)

[AspectJ的基本用法](https://blog.csdn.net/vonnie_jade/article/details/68955248)