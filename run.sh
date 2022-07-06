#!/bin/bash
# Bug ID
bug=Chart_1
# The path of Defects4J git repository.
df4jPath=/home/huyenhuyen/Desktop/APR/defects4j/
# The path of containing Defects4J bugs.
df4jData=/home/huyenhuyen/projects/

java -Xmx4g -cp "target/classes:target/dependency/*" main.Runner $bug df4jData $d4jPath