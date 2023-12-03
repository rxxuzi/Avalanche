#!/bin/bash

cd ..

# output/media 内のファイルを削除
rm -rf output/media/*

# log/ 内のファイルを削除
rm -rf log/*

# output/html 内のファイルを削除
rm -rf output/html/*

# output/txt 内のファイルを削除
rm -rf output/txt/*

# output/ 内のすべてのファイルを削除（サブディレクトリは除く）
find output/ -type f -exec rm {} +

echo "All specified files have been deleted."
