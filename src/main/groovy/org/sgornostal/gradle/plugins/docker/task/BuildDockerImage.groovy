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
    @InputFile
    File dockerFile
    @Input
    @Optional
    Map<String, String> buildArgs
    @Input
    @Optional
    Set<String> tags

    @Override
    void execWith(DockerClient client) {

        def tags = getTags() ?: [project.version]
        def firstTag = tags[0]

        def existingImages = client.listImagesCmd()
                .withImageNameFilter(image)
                .exec()

        def build = client.buildImageCmd(getBuildDir())
                .withDockerfile(getDockerFile())
                .withTag(image + ":" + firstTag)

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

        logger.info('Execute build image {} with args {} and version tag = {}', image, getBuildArgs(), firstTag)

        def imageId = build.exec(buildCallback).awaitImageId()

        if (tags.size() > 1) {
            tags.findAll { it != firstTag }.each {
                logger.info('Tagging image {} with versions {}', image, it)
                client.tagImageCmd(imageId, image, it as String)
                        .withForce(true).exec()
            }
        }

        if (existingImages) {
            existingImages.findAll { !it.id.replace('sha256:', '').startsWith(imageId) }.each {
                logger.info('Removing old image {}', it.id)
                client.removeImageCmd(it.id)
                        .withForce(true)
                        .exec()
            }
        }

    }
}
