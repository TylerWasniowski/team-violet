## Build Instructions: ##

In the violet-0.16c directory include your own build.properties file with the following lines:

```
#!xml
version.number=0.16c
jdk.home=/Library/Java/JavaVirtualMachines/jdk1.8.x.jdk/Contents/Home
javaws.jar=${jdk.home}/jre/lib/javaws.jar
activemq.jar=lib/apache-activemq-5.14.5/activemq-all-5.14.5.jar
checkstyle.home=/your/path/to/checkstyle-7.5.1/checkstyle-7.5.1-all.jar
jnlp.codebase=http://horstmann.com/violet/

```

Change jdk.home and checkstyle.home to their proper paths for your machine.


### Building the project ###
With build.properties properly configured you can simply run
```
#!bash
ant
```
in the violet-0.16c directory.