package org.simmi.javafasta.shared;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class BaseGeneGroup implements Serializable {
    long id;
    String groupName;
    Set<SimpleAnnotation> genes;

    static long globalId = 0;

    public BaseGeneGroup() {
        id = ++globalId;
        genes = new HashSet<>();
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String newname) {
        groupName = newname;
    }

    public long getId() {
        return id;
    }

    public void setId(long newid) {
        id = newid;
    }

    public int getGeneCount() {
        return genes.size();
    }
}
