#!/bin/bash
if [ $# -ne 1 ]; then
    echo "usage $0 <docker_hub_username>"           #pour moi baguettebaguette
    exit 0
fi

dos2unix build.conf
cd ..

while IFS= read -r image; do
    cd ../$image
    if [ $? -eq 0 ]; then
        #build du jar
        mvn clean package

        #build de l'image
        docker build -t $image:latest .
    fi
done < script/build.conf
