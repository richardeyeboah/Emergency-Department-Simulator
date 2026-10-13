#!/bin/sh
cd "$(dirname "$0")" || exit 1
mkdir -p build
javac --release 21 -d build src/*.java test/*.java || exit 1
java -Djava.awt.headless=true -cp build EmergencyDepartmentTest || exit 1
java -Djava.awt.headless=true -cp build AssignmentTests
