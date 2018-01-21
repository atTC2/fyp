# tbc452's FYP: Extracting Key Phrases and Relations from Scientific Publications

Taking on the [ScienceIE](https://scienceie.github.io/) task.

## Set up and run
### Prerequisites
* Java 8 (64-bit)
* Maven 3
* For Word2Vec, some [pretrained models](https://github.com/3Top/word2vec-api) (download `Google News`, `Freebase IDs`, `Freebase Names` and `DBPedia vectors (wiki2vec)`).

### Running the system
The 2 Java projects under the `java` directory are `FYP-NLP`, which includes all NLP code, and `FYP-GUI`, which includes the GUI code making the project usable as a service. Scripts are provided for convenience:

| Java Project | Script | Description |
|--------------|--------------------------|---------------------------------------------------------------------------------------------------------|
| FYP-NLP | `./build.sh` | Compiles the system, without running any tests. |
| FYP-NLP | `./install.sh` | Compiles the system, without running any tests, and installs the project to the local Maven repository. |
| FYP-NLP | `./test.sh <test class>` | Compiles the system and runs the JUnit test class given. |
| FYP-GUI | `./build.sh` | Compiles the GUI and runs all JUnit tests. |
| FYP-GUI | `./run.sh` | Compiles the GUI, without running any tests, and launches the GUI. |

## Current Status

In terms of NLP...
* For Task 1, the existing SVM is not doing bad, at least as far as my own evaluation is concerned. I believe the ScienceIE scripts to be scoring lower than I am calculating as I think they include matching the exact boundaries rather than just matching phrases, but clearly some more work is needed. The plan for future SVM development is to use the parse trees of sentences, to a) try to make key phrases that are actually phrases (and not just a string of tokens that the system deems to be key phrases) and b) to use some of this information as support vectors to further help the boundary and key phrase identification. Clustering was atempted (using Word2Vec to help calculate distance) but this didn't seem to be very good, usually making just 1 giant cluster which swallowed up single tokens at a time.
* For Task 2, the existing method of using Word2Vec with a simple averaging algorithm seems fairly effective (over 50% of classification on gold data is correct). The current SVM attempt is awful compared. It may be interesting to try a CRF on it and a gazetteer (trained off of existing data and WordNet potentially).
* For Task 3, the existing SVM using Word2Vec is not very good at all. A limited number of words appear in WordNet meaning that may not be a way to work on this task either. A rule engine is expensive to build and probably unachievable, although potentially dynamic searching of Wikipedia/Freebase (resources used by the best solution at SemEval 2017) may be a good solution to at least produce some good output).

In terms of making the system into a product...
* A GUI has been constructed, allowing submission and automatic analysis of papers (although currently the paper already has to be on the local system to work, web extraction needs to be worked on).
* Bootstrap makes it look quite nice!
* Papers can be viewed (with key phrases drawn on) and annotations downloaded. A way to view hyponyms and synonyms needs to be implemented, although with the current relation extraction system in place there isn't much to view yet anyway.
* The search page shows papers well, and while the search boxes are there and the back end receives anything submitted, the search information is not actually used yet and all papers in the database are shown. PageRank should be researched and implemented, but this should be a short process given PageRank isn't outside the taught syllabus at UoB.
* Being able to search by task/process/material is enough to reach my initial goals of the GUI, but it may be nice to expand it further to something more interesting.

## Road Map

Compared to my initial time table plan, I am still on time. I gave myself until the end of January for NLP development (with no product/GUI development up until that point) and then February to build the GUI and complete all NLP and GUI testing ready for the demo at the start of March.

I think that plan is still valid and sensible. The focus now is to implement the suggestions above for the NLP by the end of January as planned and complete searching in GUI either along side or into the first week of so of February. The main write up should begin around the start of February (hence all NLP should be done in January) allowing time to complete it to draft status for around the demonstration. Given the other studies I am completing at the moment and I keep to my schedule, this should resolve in a good solution being produced very soon.

## Results (as of 12/01/2018)
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

### SVM - as above with TF-IDF filter (threshold = 0.02)
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

### SVM - as above with relative sentence parse depth

Using Google News:

```
Overall statistics (gen): Accuracy: 0.91136351 Precision: 0.87198007 Recall: 0.77114537 F1: 0.81846873
Overall statistics (inc): Accuracy: 0.88463994 Precision: 0.79621668 Recall: 0.68508015 F1: 0.73647932
Overall statistics (str): Accuracy: 0.79383915 Precision: 0.23368421 Recall: 0.18966254 F1: 0.20938458
Overall statistics (rls): Accuracy: 0.78771437 Precision: 0.09939394 Recall: 0.07877041 F1: 0.08788853
Boundary statistics: Accuracy: 0.67848835 Precision: 0.44310719 Recall: 0.53591336 F1: 0.48511150
```

Using Freebase:

```
Overall statistics (gen): Accuracy: 0.91592032 Precision: 0.91006233 Recall: 0.71103896 F1: 0.79833355
Overall statistics (inc): Accuracy: 0.89296974 Precision: 0.83458378 Recall: 0.61344538 F1: 0.70712880
Overall statistics (str): Accuracy: 0.83577832 Precision: 0.36969697 Recall: 0.22555464 F1: 0.28017351
Overall statistics (rls): Accuracy: 0.82919604 Precision: 0.16151203 Recall: 0.08973747 F1: 0.11537281
Boundary statistics: Accuracy: 0.71281884 Precision: 0.48996445 Recall: 0.39410830 F1: 0.43683975
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

### ScienceIE Scripts
Evaluating the annotation data supplied by ScienceIE with the ScienceIE scripts produces these results:

```
tom@tom-redline:~/FYP/testing$ python eval.py gold predicted rel
           precision   recall f1-score  support

    Process     0.05     0.04     0.05      954
   Material     0.05     0.07     0.06      904
       Task     0.03     0.02     0.02      193

avg / total     0.05     0.05     0.05     2051


tom@tom-redline:~/FYP/testing$ python eval.py gold predicted types
           precision   recall f1-score  support

KEYPHRASE-NOTYPES     0.09     0.09     0.09     2051

avg / total     0.09     0.09     0.09     2051


tom@tom-redline:~/FYP/testing$ python eval.py gold predicted keys
/usr/local/lib/python2.7/dist-packages/sklearn/metrics/classification.py:1135: UndefinedMetricWarning: Precision and F-score are ill-defined and being set to 0.0 in labels with no predicted samples.
  'precision', 'predicted', average, warn_for)
           precision   recall f1-score  support

 Hyponym-of     0.00     0.00     0.00       95
 Synonym-of     0.00     0.00     0.00      112

avg / total     0.00     0.00     0.00      207


tom@tom-redline:~/FYP/testing$ python eval.py gold predicted
/usr/local/lib/python2.7/dist-packages/sklearn/metrics/classification.py:1135: UndefinedMetricWarning: Precision and F-score are ill-defined and being set to 0.0 in labels with no predicted samples.
  'precision', 'predicted', average, warn_for)
           precision   recall f1-score  support

    Process     0.05     0.04     0.05      954
   Material     0.05     0.07     0.06      904
       Task     0.03     0.02     0.02      193
 Synonym-of     0.00     0.00     0.00      112
 Hyponym-of     0.00     0.00     0.00       95

avg / total     0.05     0.05     0.05     2258
```

