package org.simmi.javafasta.shared;

import java.io.Serializable;

public class SimpleAnnotation implements Serializable {

    String			name;
    String			note;
    String			id;
    String			tag;
    String	        designation;
    public double 			eval;
    public int				start;
    public int				stop;
    int				ori;

    public SimpleAnnotation() {
        name = "";
        note = "";
        id = "";
        tag = "";
    }

    public SimpleAnnotation( String name, String note, String id, String tag ) {
    	this.name = name;
    	this.note = note;
    	this.id = id;
    	this.tag = tag;
    }

    public String toString() {
    	return name + " " + note + " " + id + " " + tag;
    }

    public String getName() {
    	return name;
    }

    public String getNote() {
    	return note;
    }

    public String getId() {
    	return id;
    }

    public String getTag() {
    	return tag;
    }

    public void setName( String name ) {
    	this.name = name;
    }

    public void setNote( String note ) {
    	this.note = note;
    }

    public void setId( String id ) {
    	this.id = id;
    }

    public void setTag( String tag ) {
    	this.tag = tag;
    }

    public void setDesignation( String designation ) {
    	this.designation = designation;
    }

    public String getDesignation() {
    	return designation;
    }

    public void setEval( double eval ) {
    	this.eval = eval;
    }

    public double getEval() {
    	return eval;
    }

    public void setStart( int start ) {
    	this.start = start;
    }

    public int getStart() {
    	return start;
    }

    public void setStop( int stop ) {
    	this.stop = stop;
    }

    public int getStop() {
    	return stop;
    }

    public void setOri( int ori ) {
    	this.ori = ori;
    }

    public int getOri() {
    	return ori;
    }
}
