package org.sgornostal.gradle.plugins.docker

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableSet

/**
 * @author Slava Gornostal
 */
class RunExtension {

    List<String> command
    Set<String> volumes
    Set<String> ports
    List<String> env
    Boolean attach = true

    void command(String... args) {
        this.command = ImmutableList.copyOf(args)
    }

    void volumes(String... args) {
        this.volumes = ImmutableSet.copyOf(args)
    }

    void ports(String... args) {
        this.ports = ImmutableSet.copyOf(args)
    }

    void env(String... args) {
        this.env = ImmutableSet.copyOf(args)
    }

}
