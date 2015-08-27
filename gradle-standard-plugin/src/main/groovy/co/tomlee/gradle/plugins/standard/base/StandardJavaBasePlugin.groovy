package co.tomlee.gradle.plugins.standard.base

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

class StandardJavaBasePlugin implements Plugin<Project> {
    static final String DEFAULT_JAVA_VERSION = '1.8'

    @Override
    void apply(Project project) {
        project.apply plugin: JavaPlugin

        project.sourceCompatibility = DEFAULT_JAVA_VERSION
        project.targetCompatibility = DEFAULT_JAVA_VERSION
    }
}
