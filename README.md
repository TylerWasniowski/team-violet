Build Instructions:

In the same directory as build.xml include your own build.properties file with the following lines:

```
#!java
version.number=0.16c
jdk.home=/Library/Java/JavaVirtualMachines/jdk1.8.x.jdk/Contents/Home
javaws.jar=${jdk.home}/jre/lib/javaws.jar
activemq.jar=lib/apache-activemq-5.14.5/activemq-all-5.14.5.jar
jnlp.codebase=http://horstmann.com/violet/

```

Change jdk.home to the proper path to your jdk.

With build.properties properly configured you can simply run ant in the directory with build.xml