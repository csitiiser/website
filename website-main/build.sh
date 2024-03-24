#!/bin/bash

mkdir build

javac ./compiler/WebsiteCompiler.java
java compiler.WebsiteCompiler prod

mkdir ./build/assets
cp -r ./assets/* ./build/assets
