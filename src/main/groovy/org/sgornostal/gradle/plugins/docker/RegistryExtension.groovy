package org.sgornostal.gradle.plugins.docker

/**
 * @author Slava Gornostal
 */
class RegistryExtension {

    String name

    String url
    String email
    String username
    String password

    RegistryExtension(String name) {
        this.name = name
    }
}
