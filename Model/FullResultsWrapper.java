package com.projects.nikita.wolframbetty.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class FullResultsWrapper implements Serializable {

    private static final long serialVersionUID = 1L;
    private ArrayList<FullResult> results;

    public FullResultsWrapper(ArrayList<FullResult> allResults) {
        this.results = allResults;
    }

    public ArrayList<FullResult> getResults() {
        return results;
    }

    public int getSize(){
        return results.size();
    }
}
