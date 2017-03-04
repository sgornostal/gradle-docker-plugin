package org.sgornostal.gradle.plugins.docker

import org.apache.commons.lang.StringUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.Copy
import org.sgornostal.gradle.plugins.docker.task.BuildDockerImage
import org.sgornostal.gradle.plugins.docker.task.CleanDockerImage
import org.sgornostal.gradle.plugins.docker.task.PushDockerImage
import org.sgornostal.gradle.plugins.docker.task.RunDockerContainer
import org.sgornostal.gradle.plugins.docker.task.StopDockerContainer


/**
 * @author Slava Gornostal
 */
class DockerPlugin implements Plugin<Project> {

    private static Logger logger = Logging.getLogger(DockerPlugin)

    public static final String GROUP = 'docker'

    public static final String EXTENSION_NAME = 'docker'
    public static final String CLEAN_EXTENSION_NAME = 'clean'
    public static final String REGISTRY_EXTENSION_NAME = 'dockerRegistries'
    public static final String RUN_EXTENSION_NAME = 'run'

    public static final String PREPARE_TASK = 'prepareDocker'
    public static final String CLEAN_TASK = 'cleanDocker'
    public static final String BUILD_TASK = 'buildDocker'
    public static final String PUSH_TASK = 'pushDockerTo%s'
    public static final String RUN_TASK = 'runDocker'
    public static final String STOP_TASK = 'stopDocker'

    public static final String RUN_STOP_PREFIX = 'gradle_'

    @Override
    void apply(Project project) {

        logger.info('Applying docker plugin to project {}', project.name)

        def dockerExt = project.extensions.create(EXTENSION_NAME, PluginExtension, project)
        def cleanExt = dockerExt.extensions.create(CLEAN_EXTENSION_NAME, CleanExtension)
        def runExt = dockerExt.extensions.create(RUN_EXTENSION_NAME, RunExtension)

        def registries = project.container(RegistryExtension)
        project.extensions.add(REGISTRY_EXTENSION_NAME, registries)

        project.ext {
            dockerBuildDir = new File(project.buildDir, 'docker')
        }

        def prepare = project.task(PREPARE_TASK, type: Copy) {
            group GROUP
            description 'Prepare docker classpath'
        }

        project.afterEvaluate {
            prepare.with {
                dependsOn dockerExt.dependencies
                with dockerExt.copySpec
                from project.file(dockerExt.dockerFile)
                into project.dockerBuildDir
            }
        }

        project.task(CLEAN_TASK, type: CleanDockerImage) {
            group GROUP
            description 'Remove docker images'
            conventionMapping.with {
                imageName = { dockerExt.resolveImageName }
                force = { cleanExt.force }
            }
        }

        project.task(BUILD_TASK, type: BuildDockerImage) {
            group GROUP
            description 'Build docker image'
            dependsOn PREPARE_TASK
            conventionMapping.with {
                buildDir = { project.dockerBuildDir }
                imageName = { dockerExt.resolveImageName }
                dockerFileName = { dockerExt.resolveDockerFileName }
                buildArgs = { dockerExt.buildArgs }
                tags = { dockerExt.tags }
            }
        }

        project.task(RUN_TASK, type: RunDockerContainer) {
            group GROUP
            description 'Run docker container'
            dependsOn BUILD_TASK
            conventionMapping.with {
                containerName = { RUN_STOP_PREFIX + dockerExt.resolveImageName }
                imageName = { dockerExt.resolveImageName }
                command = { runExt.command }
                volumes = { runExt.volumes }
                ports = { runExt.ports }
                env = { runExt.env }
                attach = { runExt.attach }
            }
        }

        project.task(STOP_TASK, type: StopDockerContainer) {
            group GROUP
            description 'Stop docker container'
            conventionMapping.with {
                containerName = { RUN_STOP_PREFIX + dockerExt.resolveImageName }
                imageName = { dockerExt.resolveImageName }
            }
        }

        registries.all {

            def info = delegate

            project.task(String.format(PUSH_TASK, StringUtils.capitalize(info.name as String)), type: PushDockerImage) {
                group GROUP
                description "Push docker image to '${info.name}' registry"
                dependsOn BUILD_TASK
                conventionMapping.with {
                    imageName = { dockerExt.resolveImageName }
                    url = { info.url }
                    email = { info.email }
                    username = { info.username }
                    password = { info.password }
                }
            }
        }

    }


}
