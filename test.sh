#!/usr/bin/env bash

NUM_ARGS=$#
SPECIFIC_QUERIES=( "$@" )

CWD=$(pwd)

if [[ $CWD == *"d1trieu"* ]]; then
    ANTLR4_JAR="/home/d1trieu/antlr-4.13.1-complete.jar"
else
    ANTLR4_JAR="/usr/local/lib/antlr-4.13.1-complete.jar"
fi

BUILD_DIR="$CWD/build"
OUTPUT_DIR="$BUILD_DIR/output"

TEST="$CWD/test"
TEST_QUERIES="$TEST/queries/"
EXPECTED_RESULTS="$TEST/expected-results"

JVM_INVOCATION=$(which java)

ENTRYPOINT=Main

if [[ $NUM_ARGS -gt 0 ]]
then
    QUERIES=()
    for (( i=0; i<${NUM_ARGS}; i++ ));
    do
        QUERIES+=" ${SPECIFIC_QUERIES[$i]/#/$TEST_QUERIES} "
    done

else
    QUERIES=$(find $TEST_QUERIES -type f -name "*.txt" | sort -V)
fi

rm $OUTPUT_DIR/output.txt

for f in $QUERIES;
do
    set -x;
    $JVM_INVOCATION -cp .:$ANTLR4_JAR:$BUILD_DIR $ENTRYPOINT $f >> $OUTPUT_DIR/output.txt 2>&1
    RET=$?
    set +x;

    if [ $RET -ne 0 ]; then
        echo "ERROR = $RET"  >> $OUTPUT_DIR/output.txt 2>&1
        echo "QUERY FAILED: " $f  >> $OUTPUT_DIR/output.txt 2>&1
        #exit
    else
        EXP_RESULT="$EXPECTED_RESULTS/expected-result-$(basename $f)"
        EXP_RESULT=${EXP_RESULT%".txt"}
        EXP_RESULT="$EXP_RESULT.xml"
        echo $EXP_RESULT
        ACTUAL_RESULT="$OUTPUT_DIR/RESULT-$(basename $f).xml"
        if [ -f $EXP_RESULT ]; then
            echo "Number of differing lines between expected query result and actual query result:" >> $OUTPUT_DIR/output.txt 2>&1
            diff -y --ignore-space-change --ignore-blank-lines --ignore-tab-expansion --ignore-all-space --suppress-common-lines $EXP_RESULT $ACTUAL_RESULT | wc -l  >> $OUTPUT_DIR/output.txt 2>&1
        fi
    fi
    echo "####################################################################################################" >> $OUTPUT_DIR/output.txt 2>&1
    echo "####################################################################################################" >> $OUTPUT_DIR/output.txt 2>&1
    echo "####################################################################################################" >> $OUTPUT_DIR/output.txt 2>&1
done

