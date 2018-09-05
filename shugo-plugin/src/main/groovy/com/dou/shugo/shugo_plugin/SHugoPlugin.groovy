package com.dou.shugo.shugo_plugin

import org.aspectj.bridge.IMessage
import org.aspectj.bridge.MessageHandler
import org.aspectj.tools.ajc.Main
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin

class SHugoPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        System.out.println("=== 编译plugin ===")

        def hasApp = project.plugins.withType(AppPlugin)
        def hasLib = project.plugins.withType(LibraryPlugin)

        final def variants

        if (hasApp) {
            variants = project.android.applicationVariants
        } else if (hasLib) {
            variants = project.android.libraryVariants
        } else {
            throw new IllegalStateException("should be application or lib");
        }

        project.repositories {
            google()
            jcenter()
            mavenCentral()
            maven {
                url 'https://dl.bintray.com/sparky/shugo'
            }
        }

        project.dependencies {
            implementation 'com.dou.shugo:shugo-annotation:1.0.33'
            implementation 'org.aspectj:aspectjrt:1.8.6'
            implementation 'com.dou.shugo:shugo-aspect:1.0.33'
        }

        final def log = project.logger

        project.extensions.create("hugo", HugoExtension)

        variants.all { variant ->
            if (!variant.buildType.isDebuggable()) {
                log.debug("Skipping non-debuggable build type '${variant.buildType.name}'.")
                return;
            } else if (!project.hugo.enable) {
                log.debug("Hugo is not enable.")
                return
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
    }
}