# AtomRepair

An Automated Program Repair Method Using Atomic Replacement for Java Programs

## I. Requirement
- Java 1.8
- GZoltar 0.1.1
- [Defects4J](https://github.com/rjust/defects4j)
## II.Run AtomRepair
1. Clone and run [Defects4J](https://github.com/rjust/defects4j)
2. Clone AtomRepair
```
git clone https://github.com/huyenhuyen1204/AtomRepair.git
```
3. Compile project
```
sh compile.sh
```
4. Edit config in "run.sh"
```
#!/bin/bash
bug=<bug_ID>
d4jPath=<path_to_defects4j>
df4jData=<path_of_containing_defects4J_bugs>

java -Xmx4g -cp "target/classes:target/dependency/*" main.Runner $bug df4jData $d4jPath
```
Example:
```
#!/bin/bash
bug=Chart_1
df4jPath=/home/huyenhuyen/Desktop/APR/defects4j/
df4jData=/home/huyenhuyen/projects/

java -Xmx4g -cp "target/classes:target/dependency/*" main.Runner $bug df4jData $d4jPath
```
5. Run
```
sh run.sh
```


