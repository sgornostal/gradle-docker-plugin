# Gradle Docker plugin

This plugin for [Gradle](http://www.gradle.org/) adds the capability to build, run and push [Docker](http://docker.io/) images.
Plugin uses Docker remote API that are handled by [Docker Java library](https://github.com/docker-java/docker-java)

[![Download](https://api.bintray.com/packages/sgornostal/gradle-plugins-repo/org.sgornostal%3Agradle-docker-plugin/images/download.svg) ](https://bintray.com/sgornostal/gradle-plugins-repo/org.sgornostal%3Agradle-docker-plugin/_latestVersion)
[![Build Status](https://travis-ci.org/sgornostal/gradle-docker-plugin.svg?branch=master)](https://travis-ci.org/sgornostal/gradle-docker-plugin)

## Usage

#### From jcenter repository
```gradle
buildscript {
    repositories {
       jcenter()
    }
    dependencies {
        classpath 'org.sgornostal:gradle-docker-plugin:1.1'
    }
}
```
#### From gradle plugins repository
```gradle
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "gradle.plugin.org.sgornostal:gradle-docker-plugin:1.1"
  }
}
```

### Apply plugin

```gradle
apply plugin: 'org.sgornostal.docker'
```

or

```gradle
apply plugin: 'docker'
```


### Configure plugin through plugin extension properties
Configuration properties in the plugin extension `docker` and `dockerRegistries` are applied to all Docker tasks.

#### Base configuration

Example:
```gradle
docker {
    imageName = 'my-awesome-image'
    buildArgs = ['ARG_NAME': 'ARG_VALUE']
    tags 'latest', project.version
    files jar.archivePath
    dependsOn build     
}
```

Available properties are:
- `host` - the server URL to connect to via Docker’s remote API. (`string`, optional, default: `tcp://localhost:2375`)
- `tlsVerify` - verify TLS (`boolean`, optional, default: `false`)
- `certPath` - the path to certificates for communicating with Docker over SSL (`string`, optional)
- `imageName` - docker image name (`string`, optional, default: `${project.name}`)
- `dockerFile` - Relative path to dockerfile (`string`, optional, default: `Dockerfile`)
- `buildArgs` - an map of string to string which will set --build-arg arguments to the docker build command (`map<string,string>`, optional)
- `tags` - an arguments list or set of tags to create (`set<string>`, optional)
- `files` - an argument list of files to be included in the Docker build context (`file..arg`, optional)
- `dependsOn` - an argument list of tasks to be executed before build docker image (`string, ref`, optional)

#### Run configuration

Example:
```gradle
docker {
    run {
        command 'ping', 'google.com'
        volumes '/foo:/foo', '/bar:/bar'
        ports '8080:8080', '443:4433'
        env 'MYVAR1=foo', 'MYVAR2=bar'                                       
    }
    ...
}
```

Available properties are:
- `command` - an arguments list or list of container [commands](https://docs.docker.com/engine/reference/commandline/run/#/parent-command) 
(`string..args or list<string>`, optional)
- `volumes` - an arguments list or list of [mounted volumes](https://docs.docker.com/engine/reference/commandline/run/#/mount-volume--v---read-only) 
(`string..args or list<string>`, optional)
- `ports` - an arguments list or list of [publish or exposed ports](https://docs.docker.com/engine/reference/commandline/run/#/publish-or-expose-port--p---expose) 
(`string..args or list<string>`, optional)
- `env` - an arguments list or list of [environment variables](https://docs.docker.com/engine/reference/commandline/run/#/set-environment-variables--e---env---env-file) 
(`string..args or list<string>`, optional)
- `attach` - attach container output to task thread (`boolean`, optional, default: `true`) 

#### Clean configuration

Example:
```gradle
docker {
    clean {
        force = true                                   
    }   
    ...
}
```

Available properties are:
- `force` - force delete of image (`boolean`, optional, default: `true`)

#### Registries configuration

Example:
```gradle
dockerRegistries {
    myRegistry {
        url = 'localhost:18067'
        email = 'email@email.com'
        username = 'user'
        password = 'pwd'
    }
    hub {
        email = 'email@email.com'
        username = 'user'
        password = 'pwd'
    }
    ...
}
```

Available properties are:
- `url` - registry url (`string`, optional, default: `index.docker.io`)
- `email` - registry email (`string`, optional)
- `username` - registry username (`username`, optional)
- `password` - registry password (`password`, optional)

`dockerRegistries` extension will create `push` task for each registry. 
In the example above, tasks `pushDockerToMyRegistry` and `pushDockerToHub` will be created.

### Tasks
- `prepareDocker` - prepare docker classpath. Copying `dockerFile` and `files` if specified in configuration
- `cleanDocker` - remove docker image
- `buildDocker` - build docker image
- `runDocker` - create and run docker container
- `stopDocker` - stop docker container
- `pushDockerTo%s` - push docker to registry, where `%s` is capitalized name of a registry from `dockerRegistries` configuration
 
### More samples
https://github.com/sgornostal/gradle-docker-plugin-samples

