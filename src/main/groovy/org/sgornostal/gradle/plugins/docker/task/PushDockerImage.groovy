package org.sgornostal.gradle.plugins.docker.task

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.model.AuthConfig
import com.github.dockerjava.api.model.PushResponseItem
import com.github.dockerjava.core.command.PushImageResultCallback
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

/**
 * @author Slava Gornostal
 */
class PushDockerImage extends AbstractDockerTask {

    static final String HUB_REGISTRY_URL = 'index.docker.io'

    @Input
    @Optional
    String url
    @Input
    @Optional
    String email
    @Input
    @Optional
    String username
    @Input
    @Optional
    String password

    @Override
    void execWith(DockerClient client) {

        def url = getUrl() ?: HUB_REGISTRY_URL
        def username = getUsername()
        def password = getPassword()
        def email = getEmail()

        if (!username || !password) {
            logger.info('Resolving registry credentials')
            def console = System.console()
            if (!console)
                throw new GradleException("Couldn't resolve registry credentials")
            email = console.readLine('> Please, enter email for %s: ', getUrl())
            username = console.readLine('> Please, enter username for %s: ', getUrl())
            password = new String(console.readPassword('> Please, enter password for %s: ', getUrl()))
        }

        def remoteImage = url.equalsIgnoreCase(HUB_REGISTRY_URL) ? username : url + '/' + image

        // tagging images for registry
        client.listImagesCmd()
                .withImageNameFilter(image)
                .exec().each {
            it.repoTags.each { tag ->
                def (_, version) = tag.tokenize(':')
                logger.info('Tagging image {} with version {} for registry push', image, version)
                client.tagImageCmd(it.id, remoteImage, version as String).exec()
            }
        }

        def pushCallback = new PushImageResultCallback() {

            @Override
            void onNext(PushResponseItem item) {
                if (item.stream)
                    println item.stream
                super.onNext(item)
            }
        }

        logger.info('Pushing image {} to registry {}', remoteImage, url)

        //@formatter:off
        client.pushImageCmd(remoteImage)
            .withAuthConfig(new AuthConfig()
                .withEmail(email)
                .withUsername(username)
                .withPassword(password))
            .exec(pushCallback)
            .awaitSuccess()
        //@formatter:on
    }
}
