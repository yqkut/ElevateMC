package com.elevatemc.potpvp.util;

import java.util.LinkedList;
import java.util.List;

public class ListWrapper<T> {
 
    private List<T> backingList;

    public List<T> ensure() {
        return isPresent() ? backingList : (backingList = new LinkedList<>());
    }

    public boolean isPresent() {
        return backingList != null;
    }
}
