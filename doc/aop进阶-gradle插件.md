1. variants分两种分别是libraryVariants和applicationVariants
		final def variants = project.android.libraryVariants
        final def variants = project.android.applicationVariants
        
2. 如果aop的代码在另外一个module中，那么你的app module也需要添加aspectj的相关依赖，并且aop module的依赖和app module的依赖只有variants不同。
3. 我们要像引用第三方插件那样引用这个包，如果要添加这么多配置的话会很麻烦，所以我们可以开发一个gradle插件，引用插件来配置这些依赖。

创建groovy项目：(如果是idea的话直接创建groovy项目即可)
1. 创建module，类型为Android library
2. 修改module下的java目录为groovy目录
3. 删除res目录下的所有子文件和目录，并修改res目录名为resources
4. 在resources下创建META-INF/gradle-plugins/$(plugin-name).properties。例如我们要创建的插件名为com.sparky.plugin,那么在app module下引用就是 apply plugin: 'com.sparky.plugin',这里需要定义的文件名就是com.sparky.plugin.properties
5. 在xx.properties文件下注册plugin文件，对应groovy包下的全包路径类名。例如定义plugin的类为com.dou.plugin.TestPlugin.groovy,则这里要添加implementation-class=com.dou.plugin.TestPlugin
6. 创建plugin文件并编辑，要实现Plugin<Project>接口，这些依赖包需要导入

		compile gradleApi()
        compile localGroovy()
        implementation 'com.android.tools.build:gradle:3.1.2'
        //aspectj需要到的类
    	implementation 'org.aspectj:aspectjtools:1.8.5'
        
   > 注意: 如果出错一定要仔细看log，例如我plugin文件用到AppPlugin和LibraryPlugin,没有自动导包,上传到maven仓库的时候不会报错，apply plugin之后编译的时候会报错，有日志
7. 定义maven仓库的task

		apply plugin: "maven"
        group = 'com.dou.aopplugin'
        version = '1.0.0'

		uploadArchives{
        	repositories{
            	mavenDeployer{
                	repository(url:uri("../repo"))
                }
            }
        }
8. 项目中引用，注意添加 mavenLocal

		project build.gradle下:
        buildscript{
        	repositories{
            	...
                mavenLocal() // 本地maven需要这行，非本地要删除
                maven{
                	url "/home/dou/work/workspace/demos/aopdemo/repo"
                }
            }
        }
        
        dependencies{
        	...
        	classpath 'com.dou.aopplugin:aopplugin:1.0.0'
        }