package org.sgornostal.gradle.plugins.docker.task

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.exception.NotModifiedException
import org.gradle.api.tasks.Input

/**
 * @author Slava Gornostal
 */
class StopDockerContainer extends AbstractDockerTask {

    @Input
    String containerName

    @Override
    void execWith(DockerClient client) {

        client.listContainersCmd()
                .withLabelFilter(['name': getContainerName()])
                .exec().each {
            logger.info('Stopping container with id {}', it.id)
            try {
                client.stopContainerCmd(it.id).exec()
            } catch (NotModifiedException ignored) {
                logger.info('Container {} already stopped', it.id)
            }
        }
    }
}
