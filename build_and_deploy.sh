#!/usr/bin/env bash

set -e

sbt assembly
eval $(minikube docker-env)
docker build -t pacman-backend:v1 .
kubectl delete po -l app=backend
kubectl apply -f <(istioctl kube-inject --debug -f kube/pacman.yaml)
