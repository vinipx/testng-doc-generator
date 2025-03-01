#!/bin/bash

# Compile the RunDocGenerator class
javac -cp build/libs/testng-doc-generator-1.0.0.jar:lib/* src/main/java/io/vinipx/testngdoc/RunDocGenerator.java

# Run the RunDocGenerator class with the source directory argument
java -cp src/main/java:build/libs/testng-doc-generator-1.0.0.jar:lib/* io.vinipx.testngdoc.RunDocGenerator src/test/java/com/testngdoc/sample

echo "Documentation generation completed!"
