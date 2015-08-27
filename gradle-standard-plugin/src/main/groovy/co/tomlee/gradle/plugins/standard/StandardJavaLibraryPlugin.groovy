package co.tomlee.gradle.plugins.standard

import co.tomlee.gradle.plugins.standard.base.StandardJavaBasePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin

class StandardJavaLibraryPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.apply plugin: StandardJavaBasePlugin
        project.apply plugin: MavenPublishPlugin
    }
}
