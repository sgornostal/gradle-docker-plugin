package org.sgornostal.gradle.plugins.docker

import com.google.common.collect.ImmutableSet
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.CopySpec

/**
 * @author Slava Gornostal
 */
class PluginExtension {

    Project project

    String host
    Boolean tlsVerify
    String certPath

    String imageName
    String dockerFile = 'Dockerfile'
    Map<String, String> buildArgs
    Set<String> tags

    final CopySpec copySpec
    Set<Task> dependencies

    PluginExtension(Project project) {
        this.project = project
        this.copySpec = project.copySpec()
    }

    void tags(String... args) {
        this.tags = ImmutableSet.copyOf(args)
    }

    void files(Object... args) {
        copySpec.from(args)
    }

    void dependsOn(Task... args) {
        this.dependencies = ImmutableSet.copyOf(args)
    }

    void dependsOn(String... args) {
        def tasks = []
        if (args) {
            args.each { tasks << project.tasks.findByName(it) }
        } else {
            this.dependencies = ImmutableSet.of()
        }
    }

    String getResolveImageName() {
        imageName ?: project.name
    }

    File getResolveDockerFile() {
        new File(project.dockerBuildDir as File, dockerFile)
    }

}
