package co.tomlee.gradle.plugins.standard

import co.tomlee.gradle.plugins.standard.base.StandardJavaBasePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.plugins.ide.idea.IdeaPlugin

class StandardIdeaPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.apply plugin: IdeaPlugin

        project.idea.module {
            jdkName = StandardJavaBasePlugin.DEFAULT_JAVA_VERSION
            downloadJavadoc = true
            downloadSources = true
            outputDir = project.file("${project.buildDir}/classes/main")
            testOutputDir = project.file("${project.buildDir}/classes/test")
        }

        if (!project.parent) {
            project.idea.project {
                jdkName = StandardJavaBasePlugin.DEFAULT_JAVA_VERSION
                languageLevel = StandardJavaBasePlugin.DEFAULT_JAVA_VERSION
            }
        }
    }
}
