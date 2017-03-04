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
    Set<Task> dependencies = []

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
        if (args) {
            args.each { dependencies << project.tasks.findByName(it) }
        }
    }

    String getResolveImageName() {
        imageName ?: project.name
    }

    String getResolveDockerFileName() {
        new File(dockerFile).name
    }

}
