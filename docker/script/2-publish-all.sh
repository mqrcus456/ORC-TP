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
        docker tag $image:latest $1/$image:latest
        docker push $1/$image:latest
        docker images
    fi
done < script/build.conf
