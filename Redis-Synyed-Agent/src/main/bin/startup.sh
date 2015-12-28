#!/bin/sh

# Synyed-Agent boot shell
 
darwin=false;
linux=false;
case "`uname`" in
  Darwin*) darwin=true
           if [ -z "$JAVA_HOME" ] ; then
             JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.7.0_79.jdk/Contents/Home
           fi
           ;;
  Linux*) linux=true
          if [ -z "$JAVA_HOME" ]; then
              shopt -s nullglob
              jdks=`ls -r1d /usr/java/j* /usr/lib/jvm/* 2>/dev/null`
              for jdk in $jdks; do
                if [ -f "$jdk/bin/java" ]; then
                  JAVA_HOME="$jdk"
                  break
                fi
              done
          fi
          ;;
esac

PRG="$0"
progname=`basename "$0$"`

 while [ -h "$PRG" ] ; do
  	ls=`ls -ld "$PRG"`
   	link=`expr "$ls" : '.*-> \(.*\)$'`
  	if expr "$link" : '/.*' > /dev/null; then
    		PRG="$link"
    	else
    		PRG=`dirname "$PRG"`"/$link"
    	fi
 done

SERVER_HOME=`dirname "$PRG"`/..

SERVER_OPTS="${SERVER_OPTS} -Xms2048m -Xmx3072m -Xmn1024m -XX:SurvivorRatio=2 -XX:PermSize=96m -XX:MaxPermSize=256m -Xss256k -XX:-UseAdaptiveSizePolicy -XX:MaxTenuringThreshold=15 -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:+HeapDumpOnOutOfMemoryError"

SERVER_LIB="${SERVER_HOME}/lib"

if [ -z "$JAVACMD" ] ; then
  	if [ -n "$JAVA_HOME"  ] ; then
    		if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
      			JAVACMD="$JAVA_HOME/jre/sh/java"
    		else
      			JAVACMD="$JAVA_HOME/bin/java"
    		fi
  	else
    		JAVACMD=`which java 2> /dev/null `
    		if [ -z "$JAVACMD" ] ; then
        		JAVACMD=java
    		fi
  	fi
fi

if [ ! -x "$JAVACMD" ] ; then
  	echo "Error: JAVA_HOME is not defined correctly."
  	echo "  We cannot execute $JAVACMD"
  	exit 1
fi


LOCALCLASSPATH=""

for i in $SERVER_LIB/*;
do LOCALCLASSPATH=$i:"$LOCALCLASSPATH";
done
LOCALCLASSPATH="$SERVER_HOME/conf:$LOCALCLASSPATH";
 

exec_command="exec $JAVACMD -server $SERVER_OPTS -classpath \"$LOCALCLASSPATH\" com.wmz7year.synyed.Booter"
eval $exec_command

