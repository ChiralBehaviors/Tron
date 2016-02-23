Tron
====

A simple framework for creating sophisticated Finite State Machines

====

See the [Tron wiki](https://github.com/ChiralBehaviors/Tron/wiki) for useage and design notes.

Tron is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/).

Tron is built using Maven 3.x and requires Java 7.

To build, cd to the top level directory and do:

    mvn clean install

For prebuilt versions of Tron, add the ChiralBehaviors cloudbees repositories to your project's pom.xml:

For snapshots: 

    <repository>
        <id>chiralbehaviors-snapshots</id>
        <name>ChiralBehaviors Snapshots </name>
        <url>http://repository-chiralbehaviors.forge.cloudbees.com/snapshot/</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
    
For releases: 

    <repository>
        <id>chiralbehaviors-releases</id>
        <name>ChiralBehaviors Releases </name>
        <url>http://repository-chiralbehaviors.forge.cloudbees.com/release/</url>
    </repository>

The current version of Tron is 0.0.4.  To use Tron in your project, add the following dependency in your project's pom.xml:


    <dependency>
        <groupId>com.chiralbehaviors</groupId>
        <artifactId>tron</artifactId>
        <version>0.0.4</version>
    </dependency>
    
