# Sup?

![alt text](https://travis-ci.org/or-drop-tables-team/sup.svg "Build Status")

CS 673. Enabling communication all across the world.

### Getting Started Development
This is a Java project built with [Maven](https://maven.apache.org/). Since we plan
to use the Eclipse environment for development the setup will focus on it specifically
when appropriate.

The project structure consists of an organizational project (sup) and three nested modules: 
common, client, and server. The common module is for code that is shared between both the client 
and server. All modules have unit tests. Our source code hierarchy looks like the following:

    | sup
    ├── client
    │   ├── src
    │   │   ├── main
    │   │   ├── test
    |
    ├── common
    │   ├── src
    │   │   ├── main
    │   │   ├── test
    |
    ├── server
    │   ├── src
    │   │   ├── main
    │   │   ├── test

#### Building
To run all the unit tests, select "Run -> Run As -> Maven Test". This will 
execute the unit tests for all three modules.

To build executable applications, select "Run -> Run As -> Maven Install". This
will build the .jar files and place them under target directories. To run these
applications, run java with the .jar as an argument. For example:

    java -jar server/target/server-0.0.1.jar

### Continuous Integration
We use [Travis](https://travis-ci.org/or-drop-tables-team/sup) to build and run 
all unit tests on every commit.
The current build status is ![alt text](https://travis-ci.org/or-drop-tables-team/sup.svg "Build Status")
