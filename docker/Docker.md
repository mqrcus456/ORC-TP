- Liste des commandes Docker utilisées :

docker build -t <repository>:<tag> .
docker tag <repository>:<tag> <username>/<repository>:<tag>
docker images
docker push <username>/<repository>:<tag>
docker pull <username>/<repository>:<tag>
docker-compose up

Instructions pour recréer la plateforme depuis zéro:
Pour Executer le projet il faut executer le script 3-deploy.sh pour pull les images et lancer les container.

Apres avoir modifier le projet il faut executer les script 1 et 2 pour push les images sur docker hub.
