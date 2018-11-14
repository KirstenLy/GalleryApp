package com.kirsten.testapps.meteorapp.another;

public class DellAllPhotoResult {

    private final boolean result;
    private final int successResults;
    private final int failedResults;

    public DellAllPhotoResult(boolean result, int sucsessResults, int failedResults) {
        this.result = result;
        this.successResults = sucsessResults;
        this.failedResults = failedResults;
    }

    public boolean isResult() {
        return result;
    }

    public int getSuccessResults() {
        return successResults;
    }

    public int getFailedResults() {
        return failedResults;
    }
}
