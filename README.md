Build Instructions:

In the violet-0.16c directory include your own build.properties file with the following lines:

```
#!xml
version.number=0.16c
jdk.home=/Library/Java/JavaVirtualMachines/jdk1.8.x.jdk/Contents/Home
javaws.jar=${jdk.home}/jre/lib/javaws.jar
activemq.jar=lib/apache-activemq-5.14.5/activemq-all-5.14.5.jar
jnlp.codebase=http://horstmann.com/violet/

```

Change jdk.home to the proper path to your jdk.


### Building without checkstyle ###
With build.properties properly configured you can simply run ant in the directory with build.xml, this will not run the checkstyle script.

### Building & Running Checkstyle ###
If you wish to run checkstyle after building the project you can configure the bash script build, which is found in the violet-0.16c directory. 

Simply modify the following line of the script to the location of your checkstyle.

```
#!bash
CHECKSTYLE_HOME=~/checkstyle-7.5.1
```

You can then build the project by running the command 
```
#!bash
bash build

```
in the violet-0.16c directory.