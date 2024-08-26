.SUFFIXES: .java .class
.java.class:
	javac $<

CWD=$(shell pwd)
BUILD_DIR=$(CWD)/build

SRC=$(CWD)/src
ANTLR_SRC=$(SRC)/antlr
JAVA_SRC=$(SRC)/java

TEST=$(CWD)/test
TEST_DATA=$(TEST)/data
TEST_QUERIES=$(TEST)/queries

JAVA_COMPILER=javac
JAVA_COMPILER_FLAGS= -g
JVM_INVOCATION=java

ENTRYPOINT=Main

# change me to the path of your local ANTLR4 jar file. this is the only variable you should have to change
ANTLR4_JAR=/home/d1trieu/antlr-4.13.1-complete.jar



all:
	if [ ! -d $(BUILD_DIR) ]; then mkdir $(BUILD_DIR) $(BUILD_DIR)/output ; fi
	$(JVM_INVOCATION) -cp $(ANTLR4_JAR) org.antlr.v4.Tool $(ANTLR_SRC)/XQuery.g4 -visitor
	mv $(ANTLR_SRC)/*.interp $(BUILD_DIR)
	mv $(ANTLR_SRC)/*.tokens $(BUILD_DIR)
	mv $(ANTLR_SRC)/*.java $(BUILD_DIR)
	$(JAVA_COMPILER) -cp .:$(ANTLR4_JAR):$(BUILD_DIR) -d $(BUILD_DIR) $(JAVA_SRC)/XVisitor.java $(JAVA_SRC)/QueryTransformer.java $(JAVA_SRC)/Main.java


# have not figured out how to add a testing case with Makefile stuff yet


clean:
	rm -rf $(BUILD_DIR)