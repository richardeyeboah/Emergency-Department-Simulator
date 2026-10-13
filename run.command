#!/bin/sh
cd "$(dirname "$0")" || exit 1
mkdir -p build
javac --release 21 -d build src/*.java || exit 1
java -cp build Main "$@"
