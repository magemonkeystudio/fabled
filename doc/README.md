[![Build](https://github.com/promcteam/proskillapi/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/promcteam/proskillapi/actions/workflows/maven.yml/badge.svg)

# ${project.name}
Our fork is based on the original skillapi and the forked skillapi by Sentropic.
* Includes all  premium features from the original premium version of Skillapi found on spigot.

## New dynamic editor
You'll need to use this editor in order for your classes and skills to be compatible with ${project.name}.
* Online Editor: https://promcteam.github.io/proskillapi/

## Downloads
You can download ${project.name} form our marketplace
* https://promcteam.com/resources/

## PROMCTEAM:
* Discord | https://discord.gg/6UzkTe6RvW

# Development 

If you wish to use ${project.name} as a dependency in your projects, include the following in your `pom.xml`

```xml
<repository>
    <id>github</id>
    <url>https://maven.pkg.github.com/promcteam/promccore</url>
</repository>
        ...
<dependency>
    <groupId>${project.groupId}</groupId>
    <artifactId>${project.artifactId}</artifactId>
    <version>${project.version}</version>
</dependency>
```

Additionally, you'll need to make sure that you have properly configured [Authentication with GitHub Packages](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry#authenticating-to-github-packages).
