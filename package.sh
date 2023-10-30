#!/bin/bash

# Just a basic tool to package the distribution.
echo "Packaging CacophonyExporter distribution..."
rm -f CacophonyExporter.zip
rm -rf CacophonyExporter
mkdir CacophonyExporter
cp target/cacophony-exporter* CacophonyExporter/CacophonyExporter.jar
cp -r target/lib CacophonyExporter/lib
cp -r ftl CacophonyExporter/ftl
cp demo.sh CacophonyExporter/demo.sh

zip -r CacophonyExporter.zip CacophonyExporter

