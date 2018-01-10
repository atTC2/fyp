# tbc452's FYP: Extracting Key Phrases and Relations from Scientific Publications

Taking on the [ScienceIE](https://scienceie.github.io/) task.

## Set up and run
### Prerequisites
* Java 8 (64-bit)
* Maven 3
* For Word2Vec, some [pretrained models](https://github.com/3Top/word2vec-api) (download `Google News`, `Freebase IDs`, `Freebase Names` and `DBPedia vectors (wiki2vec)`).

### What to run
* Firstly, cd to the root of the Java code (`cd java/FYP/`)
* The system can be built by running `./build.sh`.
* To run tests, execute `./test.sh <test class name>`. Test classes can be found under `java/FYP/src/test/java`.

## Results (as of 18.12.2017)
### Task 1
#### SVM

```
Overall statistics (gen): Accuracy: 0.92321915 Precision: 0.89226759 Recall: 0.73287345 F1: 0.80475382
Overall statistics (inc): Accuracy: 0.89303223 Precision: 0.78783593 Recall: 0.60559935 F1: 0.68480098
Overall statistics (str): Accuracy: 0.83590440 Precision: 0.31666667 Recall: 0.20171499 F1: 0.24644550
```

#### SVM - with Word2Vec
Using Google News:

```
Overall statistics (gen): Accuracy: 0.90517241 Precision: 0.84073403 Recall: 0.78556308 F1: 0.81221274
Overall statistics (inc): Accuracy: 0.86898336 Precision: 0.72829498 Recall: 0.68326791 F1: 0.70506329
Overall statistics (str): Accuracy: 0.77861141 Precision: 0.18391324 Recall: 0.17550668 F1: 0.17961165
```

Using Freebase names AND Freebase IDs (identical results):

```
Overall statistics (gen): Accuracy: 0.91859574 Precision: 0.89226759 Recall: 0.71617852 F1: 0.79458414
Overall statistics (inc): Accuracy: 0.88922933 Precision: 0.78783593 Recall: 0.59239564 F1: 0.67627865
Overall statistics (str): Accuracy: 0.83886618 Precision: 0.31666667 Recall: 0.20721477 F1: 0.25050710
```

### SVM - with Word2Vec and TF-IDF filter (threshold = 0.02)
Using Google News:

```
Overall statistics (gen): Accuracy: 0.91219922 Precision: 0.87105060 Recall: 0.76065500 F1: 0.81211831
Overall statistics (inc): Accuracy: 0.88728406 Precision: 0.79970972 Recall: 0.67940814 F1: 0.73466667
Overall statistics (str): Accuracy: 0.79908785 Precision: 0.22053232 Recall: 0.17507546 F1: 0.19519231
```

Using Freebase:

```
Overall statistics (gen): Accuracy: 0.91808821 Precision: 0.90629897 Recall: 0.68988550 F1: 0.78342137
Overall statistics (inc): Accuracy: 0.89572081 Precision: 0.82576322 Recall: 0.59020756 F1: 0.68839230
Overall statistics (str): Accuracy: 0.84636354 Precision: 0.34739803 Recall: 0.20721477 F1: 0.25959012
```

### Task 2
#### SVM 

```
Overall statistics (gen): Accuracy: 0.90511886 Precision: 0.49532710 Recall: 0.02314410 F1: 0.04422194
Overall statistics (inc): Accuracy: 0.90489209 Precision: 0.43000000 Recall: 0.01884312 F1: 0.03610411
Overall statistics (str): Accuracy: 0.90434350 Precision: 0.22500000 Recall: 0.00795053 F1: 0.01535836
```

#### SVM - with Word2Vec
Using Google News:

```
Overall statistics (gen): Accuracy: 0.90625261 Precision: 0.69142857 Recall: 0.05224525 F1: 0.09714974
Overall statistics (inc): Accuracy: 0.90453540 Precision: 0.47200000 Recall: 0.02586585 F1: 0.04904406
Overall statistics (str): Accuracy: 0.90336450 Precision: 0.21428571 Recall: 0.00929615 F1: 0.01781926
```

#### W2V Classifier - based on average distance from tokens to class
Using Google News:

```
Overall statistics: Accuracy: 0.47904483 Precision: 0.47904483 Recall: 1.00000000 F1: 0.64777595
Specific results were: tp: 983.0 fp: 1069.0 tn: 0.0 fn: 0.0
```

When if a key phrase cannot be classified, the default is `Material`:

```
Overall statistics: Accuracy: 0.54580897 Precision: 0.54580897 Recall: 1.00000000 F1: 0.70617907
Specific results were: tp: 1120.0 fp: 932.0 tn: 0.0 fn: 0.0
```

#### W2V Classifier - based on closest distance from tokens to class
Using Google News:

```
Overall statistics: Accuracy: 0.46198830 Precision: 0.46198830 Recall: 1.00000000 F1: 0.63200000
Specific results were: tp: 948.0 fp: 1104.0 tn: 0.0 fn: 0.0
```

When if a key phrase cannot be classified, the default is `Material`:

```
Specific results were: tp: 1085.0 fp: 967.0 tn: 0.0 fn: 0.0
```

### Task 3
#### SVM based on Word2Vec - sum of tokens in KP
Using Google News:

```
Overall statistics: Accuracy: 0.99417070 Precision: 0.13402062 Recall: 0.06341463 F1: 0.08609272
Specific results were: tp: 13.0 fp: 84.0 tn: 47058.0 fn: 192.0
```

#### SVM based on Word2Vec - vector of root noun in KP
Using Google News:

```
Overall statistics: Accuracy: 0.99507961 Precision: 0.06666667 Recall: 0.00966184 F1: 0.01687764
Specific results were: tp: 2.0 fp: 28.0 tn: 47119.0 fn: 205.0
```
