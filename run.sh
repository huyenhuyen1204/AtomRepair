#!/bin/bash
# Bug ID
bug=Chart_8
# The path of Defects4J git repository.
df4jPath=/home/huyenhuyen/Desktop/APR/defects4j/
# The path of containing Defects4J bugs.
df4jData=/home/huyenhuyen/Desktop/APR/benmarks/

java -Xmx4g -cp "target/classes:target/dependency/*" main.Runner $bug $df4jData $df4jPath
