## 上传library到jcenter

1. 注册jcenter账号并创建仓库和包.

	注册选个人使用
    
    仓库对应maven地址，在仓库页面右上角可以直接复制 maven {url $仓库地址}
    
    package对应每个依赖包的存放地址。即每个远程依赖的url
2. 项目中进行配置，java library和Android library

	1. 在project/build.gradle下配置

			dependencies{
            	...
                // 将项目发布到JCenter 所需要的jar   添加依赖
                classpath 'com.jfrog.bintray.gradle:gradle-bintray-plugin:1.0'
                classpath 'com.github.dcendents:android-maven-gradle-plugin:1.5'
            }
	2. 在module/build.gradle下配置, 注意下边必填的
			
            // Android library要添加的插件
			apply plugin: 'com.github.dcendents.android-maven'
            
            // 应用插件
            apply plugin: 'com.jfrog.bintray'
            apply plugin: 'maven-publish'

            def baseUrl = 'https://xxx' // 可空
            def siteUrl = baseUrl
            def gitUrl = "${baseUrl}/shugo" //可空
            def issueUrl = "${baseUrl}/issues" // 可空

            install {
                repositories {
                    mavenInstaller {
                        // This generates POM.xml with proper paramters
                        pom.project {

                            //添加项目描述
                            name 'Gradle Plugin for Android'
                            url siteUrl

                            //设置开源证书信息
                            licenses {
                                license {
                                    name 'The Apache Software License, Version 2.0'
                                    url 'http://www.apache.org/licenses/LICENSE-2.0.txt'
                                }
                            }
                            //添加开发者信息
                            developers {
                                developer {
                                    name 'sparky'
                                    email 'douyingnan@gmail.com'
                                }
                            }

                            scm {
                                connection gitUrl
                                developerConnection gitUrl
                                url siteUrl
                            }
                        }
                    }

                }
            }

            //配置上传Bintray相关信息
            bintray {
                user = "your user name" // * 必填
                key = "your api key" // * 必填

                configurations = ['archives']
                pkg {
                    repo = 'shugo' // * 上传到中央仓库的名称,就是你创建的仓库的名称
                    name = 'shugo-plugin' // 上传到jcenter 的项目名称，就是你创建的package的名字
                    desc = 'shugo plugin' // 项目描述
                    websiteUrl = siteUrl
                    issueTrackerUrl = issueUrl
                    vcsUrl = gitUrl
                    labels = ['gradle', 'plugin']
                    licenses = ['Apache-2.0']
                    publish = true
                }
            }

            
	3. 执行gradlew install && gradlew bintrayUpload，也可以sync之后，在右侧边栏gradle 菜单中找到module/other/install和module/publish/bintrayUpload执行

#### 问题:

##### 1. http 1.0 code=409
version设置的问题，升高version
##### 2. Could not find method install() for arguments [build_e1nl4kouz9zx1zz91yxnbsvqn$_run_closure2@444f28b4] on project ':shugo-aspect' of type org.gradle.api.Project.
要上传Android library库需要配置android-maven和bintray.请确保android-maven配置正确

	classpath 'com.github.dcendents:android-maven-gradle-plugin:1.5' // 不同版本的plugin name和dependecence path略有不同，请到github查看用法
    
    apply plugin: 'com.android.library'
	apply plugin: 'com.github.dcendents.android-maven'
    
##### 3. java.lang.NoSuchMethodError: Lcom/dou/shugo/shugo_aspect/SHugoAspect;.aspectOf
aop项目本地依赖正常运行，做成依赖之后，运行报错
原因未知 搞了一天没有得到任何结果蠢。如果不能做成远程依赖就不好用了 ctm

------------------

解决: 还是早上工作脑袋清醒，找到了问题。主要是配置shugo-aspect项目配置aop的时候，有一句代码是设置release项目不再添加aop配置直接return，因为当时是copy过来的配置，没看仔细，啊啊啊

	// 把下边这句代码在library中注释掉即可，app module中可保存
    /*if (!variant.buildType.isDebuggable()) {
            log.debug("Skipping non-debuggable build type '${variant.buildType.name}'.")
            return;
        }*/


##### 4. Error:(27, 20) Failed to resolve: com.dou.shugo:shugo-annotation:1.0.26
因为我们的项目只是上传到了jcenter自己建的仓库里，并没有放在中央仓库中，所以如果你在项目中用到类似
implementation 'com.dou.shugo:shugo-annotation:1.0.26'这样的话需要在该build.gradle文件中加reposities或者在project/build.gradle中添加

	allprojects{
    	repositories {
        	...
            maven {
            	url '对应的你的jcenter仓库地址'
            }
        }
    }