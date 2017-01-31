package org.sgornostal.gradle.plugins.docker.task

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.exception.NotModifiedException
import com.github.dockerjava.api.model.Bind
import com.github.dockerjava.api.model.ExposedPort
import com.github.dockerjava.api.model.Frame
import com.github.dockerjava.api.model.PortBinding
import com.github.dockerjava.core.command.AttachContainerResultCallback
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

/**
 * @author Slava Gornostal
 */
class RunDockerContainer extends AbstractDockerTask {

    @Input
    String containerName
    @Input
    @Optional
    List<String> command
    @Input
    @Optional
    Set<String> volumes
    @Input
    @Optional
    Set<String> ports
    @Input
    @Optional
    List<String> env
    @Input
    Boolean attach = true

    @Override
    void execWith(DockerClient client) {

        // find stop and remove existing first
        client.listContainersCmd()
                .withShowAll(true)
                .withLabelFilter(['name': getContainerName()])
                .exec().each {
            logger.info('Stopping container with id {}', it.id)
            try {
                client.stopContainerCmd(it.id).exec()
            } catch (NotModifiedException ignored) {
            }
            client.removeContainerCmd(it.id).exec()
        }

        def containerBuilder = client.createContainerCmd(fullImageName)
                .withName(getContainerName())
                .withLabels(['name': getContainerName()])

        if (getVolumes()) {
            containerBuilder.withBinds(getVolumes().collect { Bind.parse(it) })
        }
        if (getPorts()) {
            containerBuilder.withPortBindings(getPorts().collect { PortBinding.parse(it) })
        }
        if (getEnv()) {
            containerBuilder.withEnv(getEnv())
        }
        if (getCommand()) {
            containerBuilder.withCmd(getCommand())
        }

        def container = containerBuilder.exec()

        logger.info('Container for {} created with id {} and name', image, container.id)

        client.startContainerCmd(container.id).exec()


        if (getAttach()) {
            def callback = new AttachContainerResultCallback() {

                @Override
                void onNext(Frame item) {
                    println new String(item.payload)
                }
            }
            client.attachContainerCmd(container.id)
                    .withFollowStream(true)
                    .withStdOut(true)
                    .withStdErr(true)
                    .withLogs(true)
                    .exec(callback)
                    .awaitCompletion()
        }

    }
}
