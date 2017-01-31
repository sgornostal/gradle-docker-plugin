package org.sgornostal.gradle.plugins.docker.task

import com.github.dockerjava.api.DockerClient
import org.gradle.api.tasks.Input

/**
 * @author Slava Gornostal
 */
class CleanDockerImage extends AbstractDockerTask {

    @Input
    Boolean force

    @Override
    void execWith(DockerClient client) {

        client.listImagesCmd()
                .withImageNameFilter(image)
                .exec().each {
            logger.info('Removing image {}, force = {}', it.id, force)
            client.removeImageCmd(it.id)
                    .withForce(force)
                    .exec()
        }

    }
}
