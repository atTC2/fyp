# tbc452's FYP: Extracting Key Phrases and Relations from Scientific Publications

Taking on the [ScienceIE](https://scienceie.github.io/) task.

## Set up and run
### Prerequisites
* Java 8 (64-bit)
* Maven 3
* For Word2Vec, some [pretrained models](https://github.com/3Top/word2vec-api) (download `Google News` as a minumum).
* It likely works on other Linuxs and probably on Windows if you put enough effort in, although the development and execution was all completed on Ubuntu 16.04.

### Running the system
The 2 Java projects under the [`java`](java) directory are [`FYP-NLP`](java/FYP-NLP), which includes all NLP code, and [`FYP-GUI`](java/FYP-GUI), which includes the GUI code making the project usable as a service. Scripts are provided for convenience:

| Java Project | Script | Description |
|--------------|--------------------------|---------------------------------------------------------------------------------------------------------|
| FYP-NLP | `./build.sh` | Compiles the system, without running any tests. |
| FYP-NLP | `./install.sh` | Compiles the system, without running any tests, and installs the project to the local Maven repository. |
| FYP-NLP | `./test.sh <test class>` | Compiles the system and runs the JUnit test class given. |
| FYP-GUI | `./build.sh` | Compiles the GUI and runs all JUnit tests. |
| FYP-GUI | `./build-and-run.sh` | Compiles the GUI, without running any tests, and launches the GUI. |
| FYP-GUI | `./run.sh` | Launches the GUI (assumes it is already built). |

## Current Status

In terms of NLP...
* For Task 1, the existing SVM is doing fine, and I think is about ready to leave alone. Official scripts give up to ~0.21 which isn't as good as the achievements at ScienceIE but good enough for me, especially when compared with my own evaluation which is a little fairer. Clustering was atempted (using Word2Vec to help calculate distance) but this didn't seem to be very good, usually making just 1 giant cluster which swallowed up single tokens at a time.
* For Task 2, the existing method of using Word2Vec with a simple averaging algorithm seems fairly effective (over 50% of classification on gold data is correct). SVM usage was also attempted inherited from task 1, but proven to be not very good - no surprises there!.
* For Task 3, the existing SVM using Word2Vec is not very good at all. A limited number of words appear in WordNet meaning that may not be a way to work on this task either. A rule engine is expensive to build and probably unachievable, although potentially dynamic searching of Wikipedia/Freebase (resources used by the best solution at SemEval 2017) may be a good solution to at least produce some good output. Also attempted a slightly different SVM using Word2Vec and vector distances/angels, but did not improve.

In terms of making the system into a product...
* A GUI has been constructed, allowing submission and automatic analysis of papers (although currently the paper already has to be on the local system to work, web extraction has not been completed).
* Bootstrap makes it look quite nice!
* Papers can be viewed (with key phrases drawn on) and annotations downloaded. Hyponym/synoym viewing on the webpage isn't shown, as no good solution could be found - although it makes extremely little difference.
* The search page shows papers well, and search is reasonably effective.

## The Dissertation

The dissertation is being completed in the [`report`](report) directory, and the [current PDF version can be viewed here](report/fyp.pdf).

## Results
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

### SVM - as above with phrase TF-IDF filter (threshold = 0.02)
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

### SVM - as above with sanitisation on key phrase creation

Using Google News:

```
Overall statistics (gen): Accuracy: 0.92011127 Precision: 0.89298454 Recall: 0.79202700 F1: 0.83948133
Overall statistics (inc): Accuracy: 0.88610193 Precision: 0.79515990 Recall: 0.68198666 F1: 0.73423783
Overall statistics (str): Accuracy: 0.81752969 Precision: 0.38006839 Recall: 0.30678233 F1: 0.33951560
Overall statistics (rls): Accuracy: 0.80952969 Precision: 0.20231566 Recall: 0.15742058 F1: 0.17706667
Boundary statistics: Accuracy: 0.70283639 Precision: 0.47664850 Recall: 0.52525632 F1: 0.49977329
```

### SVM - as above with stop words noted

Using Google News:

```
Overall statistics (gen): Accuracy: 0.92007046 Precision: 0.88087631 Recall: 0.80584551 F1: 0.84169211
Overall statistics (inc): Accuracy: 0.88540785 Precision: 0.77582899 Recall: 0.70022261 F1: 0.73608944
Overall statistics (str): Accuracy: 0.81625968 Precision: 0.38195691 Recall: 0.33255178 F1: 0.35554627
Overall statistics (rls): Accuracy: 0.80758907 Precision: 0.21262458 Recall: 0.18147448 F1: 0.19581846
Boundary statistics: Accuracy: 0.71908746 Precision: 0.50268300 Recall: 0.56545848 F1: 0.53222606
```

Using Freebase:

```
Overall statistics (gen): Accuracy: 0.91385701 Precision: 0.89001233 Recall: 0.78388358 F1: 0.83358355
Overall statistics (inc): Accuracy: 0.88577008 Precision: 0.81294758 Recall: 0.69953167 F1: 0.75198728
Overall statistics (str): Accuracy: 0.80260332 Precision: 0.34548336 Recall: 0.26715686 F1: 0.30131306
Overall statistics (rls): Accuracy: 0.79427395 Precision: 0.17907574 Recall: 0.13304721 F1: 0.15266758
Boundary statistics: Accuracy: 0.69546586 Precision: 0.46696035 Recall: 0.54798556 F1: 0.50423875
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

#### W2V Classifier

```
Based on average distance
	Correctly Classified Key phrases		Incorrectly Classified Key phrases	
Parameters	GOOGLE_NEWS	FREEBASE_IDS	GOOGLE_NEWS	FREEBASE_IDS
UNKNOWN + false			2052	2052
MATERIAL + false	904	904	1148	1148
MATERIAL + true	1117	904	935	1148
				
Based on closest distance
	Correctly Classified Key phrases		Incorrectly Classified Key phrases	
Parameters	GOOGLE_NEWS	FREEBASE_IDS	GOOGLE_NEWS	FREEBASE_IDS
UNKNOWN + false			2052	2052
MATERIAL + false	904	904	1148	1148
MATERIAL + true	1080	904	972	1148

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

    Process     0.15     0.10     0.12      954
   Material     0.11     0.14     0.12      904
       Task     0.03     0.04     0.03      193

avg / total     0.11     0.11     0.11     2051


tom@tom-redline:~/FYP/testing$ python eval.py gold predicted types
           precision   recall f1-score  support

KEYPHRASE-NOTYPES     0.21     0.20     0.20     2051

avg / total     0.21     0.20     0.20     2051


tom@tom-redline:~/FYP/testing$ python eval.py gold predicted keys
           precision   recall f1-score  support

 Hyponym-of     0.00     0.00     0.00       95
 Synonym-of     0.14     0.02     0.03      112

avg / total     0.09     0.01     0.02      207


tom@tom-redline:~/FYP/testing$ python eval.py gold predicted
           precision   recall f1-score  support

    Process     0.15     0.10     0.12      954
   Material     0.11     0.14     0.12      904
       Task     0.03     0.04     0.03      193
 Synonym-of     0.14     0.02     0.03      112
 Hyponym-of     0.00     0.00     0.00       95

avg / total     0.11     0.10     0.11     2258
```

