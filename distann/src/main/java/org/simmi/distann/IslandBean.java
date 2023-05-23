package org.simmi.distann;

public class IslandBean {
    long id;
    int size;
    String name;

    public IslandBean() {

    }

    public IslandBean(long id, int size, String name) {
        this.id = id;
    	this.size = size;
        this.name = name;
    }

    public long getId() {
    	return id;
    }

    public void setId( long id ) {
    	this.id = id;
    }

    public int getSize() {
    	return size;
    }

    public void setSize( int size ) {
    	this.size = size;
    }

    public String getName() {
    	return name;
    }

    public void setName( String name ) {
    	this.name = name;
    }
}
