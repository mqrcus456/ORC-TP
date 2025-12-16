#!/bin/bash

# Répertoire racine du projet (là où se trouve ce script)
ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"

# Vérification de la structure du projet
for service in ms-product ms-membership ms-order; do
    if [ ! -d "$ROOT_DIR/$service" ]; then
        echo "Erreur : dossier $service introuvable dans $ROOT_DIR"
        exit 1
    fi
done

# Lancer un service et enregistrer son PID
start_service() {
    local dir=$1
    cd "$dir" || exit 1
    mvn spring-boot:run &
    pid=$!
    echo "Service dans $dir lancé avec PID $pid"
    echo $pid >> "$ROOT_DIR/pids.txt"
    cd - > /dev/null
}

# Arrêter tous les services enregistrés
stop_services() {
    if [ -f "$ROOT_DIR/pids.txt" ]; then
        while read pid; do
            echo "Arrêt du service PID $pid"
            kill $pid
        done < "$ROOT_DIR/pids.txt"
        rm "$ROOT_DIR/pids.txt"
    else
        echo "Aucun service à arrêter."
    fi
}

# ===== Gestion des arguments =====
if [ "$1" == "stop" ]; then
    stop_services
    exit 0
fi

# ===== Lancer les services =====
start_service "$ROOT_DIR/ms-product"
echo "Attente 30 secondes pour s'assurer que ms-product est bien lancé"
sleep 30

start_service "$ROOT_DIR/ms-order"
echo "Attente 30 secondes pour s'assurer que ms-order est bien lancé"
sleep 30

start_service "$ROOT_DIR/ms-membership"
echo "Attente 30 secondes pour s'assurer que ms-membership est bien lancé"
sleep 30
