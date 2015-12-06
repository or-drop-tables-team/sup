# Sup?

![alt text](https://travis-ci.org/or-drop-tables-team/sup.svg?branch=master "Build Status")

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

#### Getting It Into Eclipse
Clone the repository to your local machine. Then, in Eclipse, select
"File -> Import... -> Maven -> Existing Maven Project". Then locate the directory you
cloned and finish the import into Eclipse.

#### Building
To run all the unit tests, select "Run -> Run As -> Maven Test". This will
execute the unit tests for all three modules.

To build executable applications, select "Run -> Run As -> Maven Install". This
will build the .jar files and place them under target directories.

#### Running
To run these applications, we need to provide the name of the compiled .jar, but
also provide definitions for the location and password of the key stores that is
being used (for the server) or the key store that is trusted (for the
clients). The included keystore (not really trustworthy) is at
certs/supcertstore.jks. To run, execute java with the proper definitions and
providing the .jar as an argument. For the server:

    java -Djavax.net.ssl.keyStore=certs/supcertstore.jks -Djavax.net.ssl.keyStorePassword=p@ssword -jar server/target/server-0.0.1.jar

And the client:

    java -Djavax.net.ssl.trustStore=certs/supcertstore.jks -Djavax.net.ssl.trustStorePassword=p@ssword -jar client/target/client-0.0.1.jar

To release in production, you should create a new (secret) key store. See the
wiki.

#### API Documentation
The [latest docs are always available here](http://or-drop-tables-team.github.io/sup/annotated.html).
Doxygen parses our source code on every submission and updates our site on GitHub Pages.
The script run to make these changes for us is [publish_doxygen.sh](https://github.com/or-drop-tables-team/sup/blob/master/publish_doxygen.sh).

#### Continuous Integration
We use [Travis](https://travis-ci.org/or-drop-tables-team/sup.svg?branch=master) to build and run
all unit tests on every commit.
The current build status is ![alt text](https://travis-ci.org/or-drop-tables-team/sup.svg?branch=master "Build Status")
