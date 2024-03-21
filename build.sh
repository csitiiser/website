#!/bin/bash

mkdir build

javac ./compiler/WebsiteCompiler.java
java compiler.WebsiteCompiler prod

cp -r ./assets/* ./build
