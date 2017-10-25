#
# Copyright (C) 2015 CMCC, Inc. and others. All rights reserved. (CMCC)
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#         http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
#JAVA_HOME="/home/conductortest/jdk1.7/jdk/linux"

DIRNAME=`dirname $0`
RUNHOME=`cd $DIRNAME/; pwd`
export RUNHOME
echo @RUNHOME@ $RUNHOME
cd $RUNHOME
# JAVA_HOME=$(readlink -f /usr/bin/javac | sed "s:/bin/javac::")
echo @JAVA_HOME@ $JAVA_HOME
JAVA="$JAVA_HOME/bin/java"
echo @JAVA@ $JAVA

JAVA_OPTS="-Xms50m -Xmx128m"

OSNAME=`uname`
if [ $OSNAME = "Linux" ]; then
	PLATFORM=`uname -p`
	if [ $PLATFORM = "x86_64" ] ;then
		if [ -d $JAVA_HOME/jre/lib/i386 ]; then
				echo "This is a 32-bit JVM runs on 64-bit system."
				library="$RUNHOME/conf/system/native/linux"
		else
				echo "This is a 64-bit JVM runs on 64-bit system."
				library="$RUNHOME/conf/system/native/linux-x86-64"
		fi
	elif [ $PLATFORM = "ia64" ] ;then
			library="$RUNHOME/conf/system/native/linux-ia64"
	else
			library="$RUNHOME/conf/system/native/linux"
	fi
fi

if [ $OSNAME = "SunOS" ] ;then
	PLATFORM=`uname -p`
	if [ $PLATFORM = "i386" ] ;then
				library="$RUNHOME/conf/system/native/solaris-x86-32"
	fi

	if [ $PLATFORM = "sparc" ] ;then
		SYS_BIT=`isainfo -b`
		echo $SYS_BIT
		if [ $SYS_BIT = "64" ] ;then
				JAVA_OPTS="$JAVA_OPTS -d64"
				library="$RUNHOME/conf/system/native/solaris64"
	  else
				library="$RUNHOME/conf/system/native/solaris"
		fi
	fi
fi

if [ $OSNAME = "AIX" ] ;then
	PLATFORM=`uname -p`
	if [ $PLATFORM = "powerpc" ] ;then
				library="$RUNHOME/conf/system/native/aix-64"
	fi
fi

JAVA_OPTS="$JAVA_OPTS -Djava.library.path=$library"

date_time_string=`date +%Y-%m-%d-%H-%M-%S`
if [ $OSNAME != "AIX" ] ;then
dump_file_name="dump-dac-$date_time_string.hprof"
JAVA_OPTS="$JAVA_OPTS -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$RUNHOME/logs/$dump_file_name"
else
JAVA_OPTS="$JAVA_OPTS -Xdump:none"
JAVA_OPTS="$JAVA_OPTS -Xdump:system:events=gpf+abort+traceassert,range=1..0,priority=999,request=serial,label=$RUNHOME/logs/core-dac-$date_time_string.dmp"
JAVA_OPTS="$JAVA_OPTS -Xdump:heap:events=systhrow,filter=java/lang/OutOfMemoryError,range=1..1,priority=500,request=exclusive+compact+prepwalk,label=$RUNHOME/logs/dump-dac-$date_time_string.phd"
JAVA_OPTS="$JAVA_OPTS -Xdump:heap:events=user,priority=500,request=exclusive+compact+prepwalk,label=$RUNHOME/logs/dump-dac-user-$date_time_string.phd"
fi

# set remote debug
# port=8306
# JAVA_OPTS="$JAVA_OPTS -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=$port,server=y,suspend=n"
echo @JAVA_OPTS@ $JAVA_OPTS

class_path="$RUNHOME/:$RUNHOME/boco-driver.jar"
echo @class_path@ $class_path
# Start
"$JAVA" $JAVA_OPTS -classpath "$class_path"  org.onap.vfc.nfvo.emsdriver.EmsDriverApplication server "$RUNHOME/conf/emsdriver.yml"
