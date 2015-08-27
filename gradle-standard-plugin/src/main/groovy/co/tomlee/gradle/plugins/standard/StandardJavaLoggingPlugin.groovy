package co.tomlee.gradle.plugins.standard

import co.tomlee.gradle.plugins.standard.base.StandardJavaBasePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class StandardJavaLoggingPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.apply plugin: StandardJavaBasePlugin

        project.configurations.all {
            exclude module: 'slf4j-log4j12'
            exclude group: 'log4j', module: 'log4j'
            exclude module: 'commons-logging'
        }

        final String slf4jVersion = '1.7.12'
        final String logbackVersion = '1.1.3'
        project.dependencies {
            compile "org.slf4j:slf4j-api:${slf4jVersion}"

            runtime "org.slf4j:log4j-over-slf4j:${slf4jVersion}"
            runtime "org.slf4j:jcl-over-slf4j:${slf4jVersion}"
            runtime "ch.qos.logback:logback-classic:${logbackVersion}"
        }
    }
}
