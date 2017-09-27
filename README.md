# Jenkins Environment Checker (env-checker)

# Modules

1. Core
 - Contains whole checker functionality (runnable instance) along with check classes
2. Reporter
 - Contains classes responsible for report generation in various formats

*Note*: Core is dependent on reporter module (not the other way).

# Build
[Maven](https://maven.apache.org/) is used as a building tool. In project root directory run:

```
$ mvn clean install
```

# Run
There are two ways of running `env-checker`:
 - maven exec plugin,
 - standalone `jar` application.

## Maven exec
Maven exec plugin just runs `com.hradecek.jenkins.Bootstrap` class. For more information see `pom.xml` in project root directory.
```
$ mvn exec:java -Dkey1=value1 -Dkey2=value2
```

## Standalone JAR
During building step was generated runnable `jar`, so called `fat-jar` under `core/target`, which can be run simple as:
```
$ java -Dkey1=value1 -Dkey2=value2 -jar "*env-checker*-fat.jar"
```

# Configuration
Most of configuration is done either via external JSON configuration file or system properties passed on command line.

## Running specific checks
In order to run specific check set `checks` system property to full class names of checks separated by `;`.

Example:
```
$ mvn exec:java -Dchecks="com.hradecek.Check1;org.mycompany.Check2" ...
```

Check options has to be set in config file under `checks` key in root under full class name.
Example:
```
{
    ...
    "checks": {
        "com.hradecek.Check1": {
            "stringOption": "value1",
            "arrayOption": [ "a1", "a2", "a3" ]
        },
        ...
    }
    ...
}
```

In case no `checks` property is set, all checks found in configuration file will be run.

## SSH configuration
It's necessary to provide private and public key for SSH authentication.
Configuration can done via:
 - system properties (`-Dprivate.key=/path/to/private_key -Dpublic.key=/path/to/public_key)
 - in configuration file in root under `ssh` key

Example of configuration
```
{
    ...
    "ssh": {
        "privateKey": "/path/to/private_key",
        "publicKey": "/path/to/public_key"
    },
    ...
}
```

You can define key pair also specific per check as follows:
```
{
    ...
    "checks": {
        "com.hradecek.Check1": {
            ...
            "ssh" {
                "privateKey": "/path/to/private_key",
                "publicKey": "/path/to/public_key"
            },
            ...
        },
    },
    ...
}
```

In case of key pairs are defined more than one way, the precedencs is:
 - system properties
 - in config per check (local)
 - in config in root (global)

---
Maintainer: [ivohradek@gmail.com](mailto:ivohradek@gmail.com)
