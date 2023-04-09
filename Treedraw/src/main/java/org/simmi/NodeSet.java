package org.simmi;

import java.util.*;

public class NodeSet implements Comparable<NodeSet> {
    public NodeSet( Set<String> nodes ) {
        this.nodes = nodes;
        //this.count = count;
    }

    Set<String>					nodes;
    Map<String, List<Double>> leaveHeightMap = new HashMap<>();
    //Map<String,List<Double>>	leaveHeightMap = new HashMap<String,List<Double>>();
    List<Double> 				count = new ArrayList<Double>();
    List<Double> 				boots = new ArrayList<Double>();

    public int getCount() {
        return count.size();
    }

    public Set<String> getNodes() {
        return nodes;
    }

    public void addLeaveHeight( String name, double h ) {
        List<Double>	leaveHeights;
        if( leaveHeightMap.containsKey(name) ) {
            leaveHeights = leaveHeightMap.get( name );
        } else {
            leaveHeights = new ArrayList<Double>();
            leaveHeightMap.put(name, leaveHeights);
        }
        leaveHeights.add( h );
    }

    public double getAverageLeaveHeight( String name ) {
        if( leaveHeightMap.containsKey( name ) ) {
            List<Double> dlist = leaveHeightMap.get( name );

            double avg = 0.0;

            for( double d : dlist ) {
                avg += d;
            }

            avg /= dlist.size();
            return avg;
        }
        return -1.0;
    }

    public void addHeight( double h ) {
        //if( count == null ) count = new ArrayList<Double>();
        count.add( h );
    }

    public void addBootstrap( double h ) {
        //if( count == null ) count = new ArrayList<Double>();
        boots.add( h );
    }

    public double getAverageHeight() {
        double avg = 0.0;

        for( double d : count ) {
            avg += d;
        }

        avg /= count.size();
        return avg;
    }

    public double getAverageBootstrap() {
        double avg = 0.0;

        for( double d : boots ) {
            avg += d;
        }

        avg /= boots.size();
        return avg;
    }

    @Override
    public int compareTo(NodeSet o) {
        int val = o.count.size() - count.size();
        if( val == 0 ) return o.nodes.size() - nodes.size();
        else return val;
    }
}
