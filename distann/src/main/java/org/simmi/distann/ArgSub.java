package org.simmi.distann;

import java.util.Set;

public class ArgSub {
    long id;
    String name;
    int geneCount;
    long parentId;

    static long globalId = 0;

    public ArgSub(long id, String name, int geneCount, long parentId) {
        this.id = id;
        this.name = name;
        this.geneCount = geneCount;
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String newname) {
        name = newname;
    }

    public long getId() {
        return id;
    }

    public void setId(long newid) {
        id = newid;
    }

    public int getGeneCount() {
        return geneCount;
    }

    public void setGeneCount(int i) {
        geneCount = i;
    }

    public long getParentId() {
    	return parentId;
    }

    public void setParentId( long parentId ) {
    	this.parentId = parentId;
    }
}
