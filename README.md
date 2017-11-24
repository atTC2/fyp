# tbc452's FYP: Extracting Key Phrases and Relations from Scientific Publications

Taking on the [ScienceIE](https://scienceie.github.io/) task.

## Set up and run
### Prerequisites
* Java 8+ (64-bit)
* Maven (if building)

### What to run
* To evaluate the current SVM performance, run test class `xyz.tomclarke.fyp.nlp.svm.TestSVMProcessor`
* To evaluate current clustering performance with Word2Vec, run test class `xyz.tomclarke.fyp.nlp.cluster.TestW2VCluster`

## Results
The current SVM performance evaluated by this system is:
```
Overall statistics (gen): Accuracy: 0.92101271 Precision: 0.87625899 Recall: 0.78614458 F1: 0.82875936
Overall statistics (inc): Accuracy: 0.88143886 Precision: 0.74390244 Recall: 0.64126150 F1: 0.68877911
Overall statistics (str): Accuracy: 0.81331160 Precision: 0.26991614 Recall: 0.21080639 F1: 0.23672719
```

