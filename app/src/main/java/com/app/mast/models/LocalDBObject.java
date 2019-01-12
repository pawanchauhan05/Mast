package com.app.mast.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalDBObject {
    private Map<String,IssueLocal> map = new HashMap<>();

    public Map<String, IssueLocal> getMap() {
        return map;
    }

    public void setMap(Map<String, IssueLocal> map) {
        this.map = map;
    }
}
