package org.sgornostal.gradle.plugins.docker

import org.gradle.testfixtures.ProjectBuilder

import org.gradle.api.Project
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue
import static org.junit.Assert.assertEquals


/**
 * @author Slava Gornostal
 */
class DockerPluginTest {

    Project project

    @Before
    void setup() {
        project = ProjectBuilder.builder().build()
        project.apply plugin: 'docker'
        project.docker {
            imageName = 'Test'
            dockerFile = 'Docker'
            buildArgs = ['arg': 'value', 'arg2': 'value2']
            tags = ['tag1', 'tag2']
            run {
                command = ['cm1', 'cm2']
                volumes = ['/vol1:/vol1']
                ports = ['20:20', '10:10']
                env = ['env1', 'env2']
                attach = false
            }
            clean {
                force = false
            }
        }
        project.dockerRegistries {
            reg1 {
                url = 'localhost:8181'
                email = 'test@test'
                username = 'test'
                password = 'pwd'
            }
            reg2 {
                username = 'test2'
                password = 'pwd'
            }
        }
    }

    @Test
    void pluginCreatesBuildDirRef() {
        assertEquals(new File(project.buildDir, 'docker'), project.dockerBuildDir)
    }

    @Test
    void pluginAddExtension() {
        assertNotNull(project.extensions.findByName(DockerPlugin.EXTENSION_NAME))
    }

    @Test
    void pluginResolvesDependenciesFromStringArgs() {
        def task = project.task('myTask') {
        }
        project.docker {
            dependsOn 'myTask'
        }
        assertTrue(project.docker.dependencies.contains(task))
    }

    @Test
    void pluginAddRunExtension() {
        assertNotNull(project.docker.extensions.findByName(DockerPlugin.RUN_EXTENSION_NAME))
    }

    @Test
    void pluginAddCleanExtension() {
        assertNotNull(project.docker.extensions.findByName(DockerPlugin.CLEAN_EXTENSION_NAME))
    }

    @Test
    void pluginAddRegistryExtension() {
        assertNotNull(project.extensions.findByName(DockerPlugin.REGISTRY_EXTENSION_NAME))
    }


    @Test
    void pluginCreatesPrepareTask() {
        def task = project.tasks.findByName(DockerPlugin.PREPARE_TASK)

        assertNotNull(task)
        assertEquals(DockerPlugin.GROUP, task.group)
    }

    @Test
    void pluginCreatesCleanTask() {
        def task = project.tasks.findByName(DockerPlugin.CLEAN_TASK)

        assertNotNull(task)
        assertEquals(DockerPlugin.GROUP, task.group)

        assertEquals('Test', task.imageName)
        assertEquals(false, task.force)
    }

    @Test
    void pluginCreatesBuildTask() {

        def docker = project.extensions.findByName(DockerPlugin.EXTENSION_NAME)

        def task = project.tasks.findByName(DockerPlugin.BUILD_TASK)

        assertNotNull(task)
        assertEquals(DockerPlugin.GROUP, task.group)

        assertEquals(project.dockerBuildDir, task.buildDir)
        assertEquals('Test', task.imageName)
        assertEquals(docker.resolveDockerFileName, task.dockerFileName)
        assertEquals(['arg': 'value', 'arg2': 'value2'], task.buildArgs)
        assertEquals(['tag1', 'tag2'] as Set, task.tags)
    }

    @Test
    void pluginCreatesRunTask() {

        def task = project.tasks.findByName(DockerPlugin.RUN_TASK)

        assertNotNull(task)
        assertEquals(DockerPlugin.GROUP, task.group)

        assertEquals('Test', task.imageName)
        assertEquals(DockerPlugin.RUN_STOP_PREFIX + 'Test', task.containerName)
        assertEquals(['cm1', 'cm2'], task.command)
        assertEquals(['/vol1:/vol1'] as Set, task.volumes)
        assertEquals(['20:20', '10:10'] as Set, task.ports)
        assertEquals(['env1', 'env2'], task.env)
        assertEquals(false, task.attach)
    }

    @Test
    void pluginCreatesStopTask() {

        def task = project.tasks.findByName(DockerPlugin.STOP_TASK)

        assertNotNull(task)
        assertEquals(DockerPlugin.GROUP, task.group)

        assertEquals('Test', task.imageName)
        assertEquals(DockerPlugin.RUN_STOP_PREFIX + 'Test', task.containerName)
    }

    @Test
    void pluginCreatesPushTasks() {

        def task1 = project.tasks.findByName(String.format(DockerPlugin.PUSH_TASK, 'Reg1'))
        def task2 = project.tasks.findByName(String.format(DockerPlugin.PUSH_TASK, 'Reg2'))

        assertNotNull(task1)
        assertNotNull(task2)

        assertEquals('Test', task1.imageName)
        assertEquals('localhost:8181', task1.url)
        assertEquals('test@test', task1.email)
        assertEquals('test', task1.username)
        assertEquals('pwd', task1.password)

    }

}
