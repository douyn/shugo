import org.gradle.api.Action
import org.gradle.internal.impldep.org.sonatype.maven.polyglot.groovy.builder.factory.ObjectFactory

class SuperExtension {
    def String name
    def String id

    def SubExtension subExtension

    public SuperExtension (ObjectFactory objectFactory){
        subExtension = objectFactory.newInstance(SubExtension)
    }

    void subExtension(Action<? super SubExtension> action){
        action.execute(subExtension)
    }

    class SubExtension {
        def String subName
    }
}