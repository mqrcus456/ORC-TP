#!/bin/bash

# Lancer un service et enregistrer son PID
start_service() {
    local dir=$1
    cd "$dir" || exit 1
    mvn spring-boot:run &
    pid=$!
    echo "Service dans $dir lancé avec PID $pid"
    echo $pid >> pids.txt
    cd - > /dev/null
}

# Arrêter tous les services enregistrés
stop_services() {
    if [ -f pids.txt ]; then
        while read pid; do
            echo "Arrêt du service PID $pid"
            kill $pid
        done < pids.txt
        rm pids.txt
    else
        echo "Aucun service à arrêter."
    fi
}

# Lancer les services
start_service "/ORC-TP1/ms-product"
echo "Attente 30 secondes pour s'assurer que ms-product est bien lancé"
sleep 30  # Attendre 30 secondes pour donner du temps au service product

start_service "/ORC-TP1/ms-order"
echo "Attente 30 secondes pour s'assurer que ms-order est bien lancé"
sleep 30  # Attendre 30 secondes pour donner du temps au service order

start_service "/ORC-TP1/ms-membership"
echo "Attente 30 secondes pour s'assurer que ms-membership est bien lancé"
sleep 30  # Attendre 30 secondes pour donner du temps au service membership

