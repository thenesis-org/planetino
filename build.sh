#!/bin/bash
# Usage: type ./build.sh --help

# Default commands and library locations
JAVAC_CMD=javac
JAR_CMD=jar
JAR_FLAGS=cvf

DIST_HOME=`pwd`

# The file to use if you do not built it
# yourself or the target name if it is
# build
CLDC_JAR=${DIST_HOME}/lib/cldc1.1.jar
MIDP2_JAR=${DIST_HOME}/lib/midpath.jar

CLDC_FLAGS="-source 1.3 -target 1.1"

DIST_CLASSES=${DIST_HOME}/dist/classes
PLANETINO_JAR=${DIST_HOME}/dist/planetino2.jar
RESOURCES_DIR=${DIST_HOME}/demos/resources

# Builds mmake-managed Java sources and install classes in a directory.
#
# $1 - yes/no = whether the build should be done or not
# $2 - source directory
# $3 - target directory for classes (must be absolute!)
# $4 - auxiliary bootclasspath entries (optional)
#      containing a leading colon (:) character
build_java ()
{
  if [ $1 = yes ]
  then
    local srcdir=$2
    local classdir=$3
    local auxbcp=$4

    make -C $srcdir \
      JAVAC=$JAVAC_CMD \
      JAVAC_FLAGS="-bootclasspath ${CLDC_JAR}:${MIDP2_JAR}$auxbcp -sourcepath . $CLDC_FLAGS" || exit 1

    make install -C $srcdir \
      JAVAC=$JAVAC_CMD \
      JAVAC_FLAGS="-bootclasspath ${CLDC_JAR}:${MIDP2_JAR}$auxbcp -sourcepath . $CLDC_FLAGS" \
      CLASS_DIR="$classdir" || exit 1
  else
    echo "skipping: $2"
  fi
}


build_java yes core $DIST_CLASSES

build_java yes engine $DIST_CLASSES :$DIST_CLASSES

build_java yes demos/java $DIST_CLASSES :$DIST_CLASSES

${JAR_CMD} cvf $PLANETINO_JAR -C classes .

if [ $FASTJAR_ENABLED = yes ]; then
  # fastjar needs to get the file list via stdin
  ( cd $RESOURCES_DIR && find -type f | grep -v "/.svn" | $JAR_CMD uvf $PLANETINO_JAR -E -M -@ )
else
  # Sun's jar has trouble with the first entry when using @ and -C
  echo "ignore_the_error" > resources.list
  # all other jar commands handle the resources via a file
  find $RESOURCES_DIR -type f | grep -v "/.svn" >> resources.list
  $JAR_CMD uvf $PLANETINO_JAR -C $RESOURCES_DIR @resources.list
fi

$JAR_CMD uvmf demos/resources/META-INF/MANIFEST.MF $PLANETINO_JAR

