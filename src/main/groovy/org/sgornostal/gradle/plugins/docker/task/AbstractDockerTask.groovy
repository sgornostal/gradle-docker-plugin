package org.sgornostal.gradle.plugins.docker.task

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.sgornostal.gradle.plugins.docker.PluginExtension

/**
 * @author Slava Gornostal
 */
abstract class AbstractDockerTask extends DefaultTask {

    public static final String LOCAL_HOST = 'tcp://localhost:2375'

    @Input
    String imageName

    protected PluginExtension docker

    AbstractDockerTask() {
        this.docker = project.extensions.findByName('docker') as PluginExtension
    }

    protected String getImage() {
        getImageName() ?: project.name
    }

    protected String getTag() {
        docker.getTags() ? docker.getTags()[0] : 'latest'
    }

    protected String getFullImageName() {
        getImage() + ":" + getTag()
    }

    @TaskAction
    void executeTask() {

        def configBuilder = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(docker.host ?: LOCAL_HOST)

        if (docker.tlsVerify) {
            configBuilder.withDockerTlsVerify(docker.tlsVerify as Boolean)
        }
        if (docker.certPath) {
            configBuilder.withDockerCertPath(docker.certPath)
        }
        def client = DockerClientBuilder.getInstance(configBuilder.build()).build()

        execWith(client)

    }

    abstract void execWith(DockerClient client)

}
