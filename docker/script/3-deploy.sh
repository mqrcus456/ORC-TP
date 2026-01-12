#!/bin/bash
if [ $# -ne 1 ]; then
    echo "usage $0 <docker_hub_username>"           #pour moi baguettebaguette
    exit 0
fi

dos2unix build.conf
cd ..

while IFS= read -r image; do
    if [ $? -eq 0 ]; then

        docker pull $1/$image:latest

        docker compose up
    fi
done < script/build.conf
