package co.tomlee.gradle.plugins.standard.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

import java.nio.file.Files
import java.nio.file.attribute.PosixFilePermission
import java.nio.file.attribute.PosixFilePermissions

class SelfContainedScript extends DefaultTask {
    List<String> javaOpts = []

    @InputFile
    File inputFile

    @OutputFile
    File outputFile

    @TaskAction
    void generateSelfContainedScript() {
        final String shell = '''
#!/usr/bin/env bash

SCRIPTDIR="$(dirname "$0")"
SCRIPTDIR="$(cd "$SCRIPTDIR" && pwd)"

if [ -z "${JAVA_OPTS}" ]; then
    JAVA_OPTS="''' + javaOpts.collect { "${it.replace('"', '\\"')}" }.join(" ") + '''"
fi

if [ -z "${JAVA}" ]; then
    if [ -n "${JAVA_HOME}" ]; then
        JAVA="${JAVA_HOME}/bin/java"
    else
        JAVA="$(which java 2>/dev/null)"
        if [ -z "${JAVA}" ]; then
            echo "error: java not found on your $PATH" >&2
            exit 1
        fi
    fi
fi

if [ ! -x "${JAVA}" ];  then
    echo "error: ${JAVA} is not executable" >&2
    exit 1
fi

exec "${JAVA}" ${JAVA_OPTS} "-Dscript.dir=${SCRIPTDIR}" -jar "$0" "$@"
'''

        final byte[] jarBytes = inputFile.bytes
        final byte[] shellBytes = shell.getBytes("UTF-8")
        final byte[] data = new byte[jarBytes.length + shellBytes.length]
        System.arraycopy(shellBytes, 0, data, 0, shellBytes.length)
        System.arraycopy(jarBytes, 0, data, shellBytes.length, jarBytes.length)

        if (!outputFile.parentFile.exists()) {
            if (!outputFile.parentFile.mkdirs()) {
                throw new GradleException("Failed to create output directory for ${outputFile}")
            }
        }
        outputFile.bytes = data

        final Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxr-xr-x")
        Files.setPosixFilePermissions(outputFile.toPath(), perms)
    }
}
