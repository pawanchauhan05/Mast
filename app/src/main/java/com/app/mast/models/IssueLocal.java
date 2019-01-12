package com.app.mast.models;

import java.util.ArrayList;
import java.util.List;

public class IssueLocal {
    private List<Issue> issueList = new ArrayList<>();
    private long timestamp;

    public List<Issue> getIssueList() {
        return issueList;
    }

    public void setIssueList(List<Issue> issueList) {
        this.issueList = issueList;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
