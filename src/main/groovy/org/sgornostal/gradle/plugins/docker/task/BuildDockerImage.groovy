package org.sgornostal.gradle.plugins.docker.task

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.model.BuildResponseItem
import com.github.dockerjava.core.command.BuildImageResultCallback
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional

/**
 * @author Slava Gornostal
 */
class BuildDockerImage extends AbstractDockerTask {

    @InputDirectory
    File buildDir
    @Input
    @Optional
    String dockerFileName
    @Input
    @Optional
    Map<String, String> buildArgs
    @Input
    @Optional
    Set<String> tags

    @Override
    void execWith(DockerClient client) {

        def tags = getTags() ?: ['latest']

        def existingImages = client.listImagesCmd()
                .withImageNameFilter(image)
                .exec()

        def dockerFile = new File(getBuildDir(), getDockerFileName() ?: 'Dockerfile')

        def build = client.buildImageCmd(dockerFile)
                .withTags([image + ":" + tag] as Set)

        if (getBuildArgs()) {
            getBuildArgs().each {
                build.withBuildArg(it.key, it.value)
            }
        }

        def buildCallback = new BuildImageResultCallback() {

            @Override
            void onNext(BuildResponseItem item) {
                if (item.stream)
                    println item.stream
                super.onNext(item)
            }
        }

        logger.info('Execute build image {} with args {} and tag {}', image, getBuildArgs(), tag)

        def imageId = build.exec(buildCallback).awaitImageId()

        if (tags.size() > 1) {
            tags.findAll { it != tag }.each {
                logger.info('Tagging image {} with versions {}', image, it)
                client.tagImageCmd(imageId, image, it as String)
                        .withForce(true).exec()
            }
        }

        if (existingImages) {
            existingImages.findAll { !it.id.replace('sha256:', '').startsWith(imageId) }.each {
                logger.info('Removing old image {}', it.id)
                try {
                    client.removeImageCmd(it.id)
                            .withForce(true)
                            .exec()
                } catch (Throwable th) {
                    logger.warn('Could not delete old image {}, error: {}', it.id, th.message)
                }
            }
        }

    }
}
