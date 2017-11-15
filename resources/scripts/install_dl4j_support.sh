#!/bin/bash

# Install dependencies of ND4J / DL4J / Word2Vec (investigate further)
echo -n 'Running `apt-get install gfortran libopenblas-dev liblapack-dev`... '
sudo apt-get install -qq gfortran libopenblas-dev liblapack-dev
echo 'done'