cls
cd framework/java

rmdir classes /s /q
mkdir classes

javac -d classes annotation/*.java controller/*.java

java -cp classes annotation.Main