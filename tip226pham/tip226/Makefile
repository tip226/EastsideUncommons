JAVAC=javac
JAVA=java
JAR=jar
SRC_DIR=tip226
INTERFACE_DIR=$(SRC_DIR)/interfaces
BUILD_DIR=bin
MANIFEST=Manifest.txt
JAR_NAME=tip226.jar
SOURCES=$(wildcard $(SRC_DIR)/*.java) $(wildcard $(INTERFACE_DIR)/*.java)
CLASSES=$(SOURCES:.java=.class)

all: $(CLASSES)
	javac Test.java
	javac interfaces/*.java
	javac db/*.java
	jar cvfm $(JAR_NAME) Manifest.txt -C . .
	$(JAVA) -jar $(JAR_NAME)

clean:
	rm -f *.class
	rm -f interfaces/*.class
	rm -f db/*.class
	rm -f $(JAR_NAME)