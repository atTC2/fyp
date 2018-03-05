package xyz.tomclarke.fyp.gui.model;

import java.util.List;

/**
 * Used to present information to the user searching
 * 
 * @author tbc452
 *
 */
public class SearchResultAndDetails {

    private List<SearchResult> results;
    private int resultsFound;
    private long searchTime;

    public List<SearchResult> getResults() {
        return results;
    }

    public void setResults(List<SearchResult> results) {
        this.results = results;
    }

    public int getResultsFound() {
        return resultsFound;
    }

    public void setResultsFound(int resultsFound) {
        this.resultsFound = resultsFound;
    }

    public long getSearchTime() {
        return searchTime;
    }

    public void setSearchTime(long searchTime) {
        this.searchTime = searchTime;
    }

}
