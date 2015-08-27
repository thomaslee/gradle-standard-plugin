package co.tomlee.gradle.plugins.standard

import co.tomlee.gradle.plugins.standard.base.StandardJavaBasePlugin
import co.tomlee.gradle.plugins.standard.tasks.SelfContainedScript
import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.distribution.plugins.DistributionPlugin
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.bundling.Tar
import org.gradle.api.tasks.bundling.Zip

class StandardJavaToolPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.apply plugin: StandardJavaBasePlugin
        project.apply plugin: ShadowPlugin
        project.apply plugin: DistributionPlugin

        final String mainClass = optional(project, 'main.class')

        project.tasks.shadowJar.with {
            if (mainClass) {
                manifest {
                    attributes 'Main-Class': mainClass
                }
            }
        }

        SelfContainedScript selfContainedScript = project.tasks.create('selfContainedScript', SelfContainedScript)
        selfContainedScript.group = 'Build'
        selfContainedScript.description = 'Generate a self-contained shell script'
        selfContainedScript.inputFile = project.tasks.shadowJar.outputs.files.singleFile
        selfContainedScript.outputFile = project.file("${project.buildDir}/self-contained/${project.name}")
        selfContainedScript.javaOpts = [
                '-client',
                '-Xms' + optional(project, 'heap.size', '256m'),
                '-Xmx' + optional(project, 'heap.size', '256m')
        ]
        selfContainedScript.dependsOn(project.tasks.shadowJar)

        project.tasks.all { Task task ->
            if (task instanceof Jar || task instanceof Zip || task instanceof Tar) {
                task.duplicatesStrategy = 'FAIL'
            }

            // FIXME better way?
            if (task.name == 'distZip' || task.name == 'distTar' || task.name == 'installDist') {
                task.dependsOn selfContainedScript
            }
        }

        project.distributions.main.contents {
            into('bin')
            from(selfContainedScript.outputs)
        }
    }

    private static String optional(final Project project, final String property, final String defaultValue=null) {
        project.extensions.extraProperties.has(property) ? project[property] : defaultValue
    }
}
