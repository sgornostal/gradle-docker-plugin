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
    }

    /*@Test
    void pluginAddExtension() {
        assertTrue(project.docker instanceof PluginExtension)
    }

    @Test
    void pluginAddRegistryExtension() {
        assertTrue(project.docker.registry instanceof RegistryExtension)
    }

    @Test
    void pluginCreatesBuildDirRef() {
        assertEquals(new File(project.buildDir, 'docker'), project.dockerBuildDir)
    }

    @Test
    void pluginCreatesBuildTask() {
        Closure cl = {}
        project.docker {
            imageName = 'Test'
            dockerFile = 'Docker'
            buildArgs = ['arg': 'value']
            tagsVersions = ['tag1', 'tag2']
            res = cl
        }
        def task = project.tasks.findByName('buildDocker')

        assertNotNull(task)
        assertEquals(DockerPlugin.GROUP, task.group)

        assertEquals('Test', task.imageName)
        assertEquals('Docker', task.dockerFile)
        assertEquals(['arg': 'value'], task.buildArgs)
        assertEquals(['tag1', 'tag2'], task.tagsVersions)
        assertEquals(cl, task.res)
    }

    @Test
    void pluginCreatesPushTask() {
        project.docker.registry {
            url = 'someurl'
            email = 'some@email'
            username = 'some_username'
            password = 'some_password'
        }
        def task = project.tasks.findByName('pushDocker')

        assertNotNull(task)
        assertEquals(DockerPlugin.GROUP, task.group)

        assertEquals('someurl', task.url)
        assertEquals('some@email', task.email)
        assertEquals('some_username', task.username)
        assertEquals('some_password', task.password)
    }

    @Test
    void pluginCreatesRunTask() {

    }*/

}
