package org.simmi;

import org.simmi.shared.TreeUtil;

import java.util.*;

public class Node {
    String 				name;
    String				id;
    String				meta;
    int					metacount;
    String				imgurl;
    double		h;
    double		h2;
    double		bootstrap;
    String				color;
    List<String> infolist;
    List<Node>			nodes;
    public int			leaves = 0;
    Node		parent;
    public int			comp = 0;
    double		fontsize = -1.0;
    double		framesize = -1.0;
    double		frameoffset = -1.0;

    double		canvasx;
    double		canvasy;

    String		collapsed = null;
    boolean		selected = false;

    public boolean isLeaf() {
        return nodes == null || nodes.size() == 0;
    }

    public List<String> traverse() {
        List<String>	ret = new ArrayList<String>();

        List<Node> nodes = this.getNodes();
        if( nodes != null && nodes.size() > 0 ) {
            for( Node n : nodes ) {
                ret.addAll( n.traverse() );
            }
            if( nodes.size() == 1 ) ret.add( this.getName() );
        } else ret.add( this.getName() );

        return ret;
    }

    public Set<String> getLeaveNames() {
        Set<String>	ret = new HashSet<String>();

        List<Node> nodes = this.getNodes();
        if( nodes != null && nodes.size() > 0 ) {
            for( Node n : nodes ) {
                ret.addAll( n.getLeaveNames() );
            }
            if( nodes.size() == 1 ) ret.add( this.getName() );
        } else ret.add( this.getName() );

        return ret;
    }

    public Node getRoot() {
        Node root = this;

        Node parent = root.getParent();
        while( parent != null ) {
            root = parent;
            parent = root.getParent();
        }

        return root;
    }

    public List<String> getInfoList() {
        return infolist;
    }

    public Node findNode( String id ) {
        if( id.equals( this.id ) ) {
            return this;
        } else {
            for( Node n : this.nodes ) {
                Node ret = n.findNode( id );
                if( ret != null ) {
                    return ret;
                }
            }
        }
        return null;
    }

    public Node getOtherChild( Node child ) {
        if( nodes != null && nodes.size() > 0 ) {
            int i = nodes.indexOf( child );
            return i == 0 ? nodes.get(1) : nodes.get(0);
        }
        return null;
    }

    public Node firstLeaf() {
        Node res = null;
        if( nodes == null || nodes.size() == 0 ) {
            res = this;
        } else {
            for( Node subn : nodes ) {
                res = subn.firstLeaf();
                break;
            }
        }
        return res;
    }

    public Set<String> nodeCalc( List<Set<String>>	ls ) {
        Set<String>	s = new HashSet<String>();
        if( nodes == null || nodes.size() == 0 ) {
            s.add( id );
        } else {
            for( Node subn : nodes ) {
                Set<String> set = subn.nodeCalc( ls );
                s.addAll( set );
            }
            ls.add( s );
        }
        return s;
    }

    public Set<String> nodeCalcMap( Map<Set<String>, NodeSet> ls ) {
        Set<String>	s = new HashSet<String>();
        if( nodes == null || nodes.size() == 0 ) {
            s.add( id == null ? name : id );
        } else {
            for( Node subn : nodes ) {
                Set<String> set = subn.nodeCalcMap( ls );
                s.addAll( set );
            }
            if( nodes.size() == 1 ) s.add( id == null ? name : id );

            NodeSet heights;
            if( ls.containsKey( s ) ) {
                heights = ls.get( s );
                //ls.put( s, ls.get(s)+1 );
            } else {
                heights = new NodeSet( s );
                ls.put( s, heights );
            }
            for( Node subn : nodes ) {
                if( subn.isLeaf() ) {
                    //System.err.println( subn.getName() + "  " + subn.geth() );
                    heights.addLeaveHeight( subn.getName(), subn.geth() );
                }
            }
            heights.addHeight( this.geth() );

            double bt = this.getBootstrap();
            if( bt > 0.0 ) {
					/*if( s.size() == 2 && s.contains("t.scotoductusSA01") ) {
						System.err.println( "bootstrap " + s + "  " + this.getBootstrap() );
					}*/
                heights.addBootstrap( bt );
            } else heights.addBootstrap( 1.0 );
        }
        return s;
    }

    public Set<String> leafIdSet() {
        Set<String> lidSet = new HashSet<String>();

        if( nodes == null || nodes.size() == 0 ) {
            lidSet.add( id );
        } else {
            for( Node subn : nodes ) {
                lidSet.addAll( subn.leafIdSet() );
            }
        }

        return lidSet;
    }

    public String getId() {
        return id;
    }

    public void setSelected( boolean selected ) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public boolean isCollapsed() {
        return collapsed != null;
    }

    public String getCollapsedString() {
        return collapsed;
    }

    public void setCollapsed( String collapsed ) {
        this.collapsed = collapsed;
    }

    public Node() {
        nodes = new ArrayList<>();
        metacount = 0;
    }

    public Node( String name, boolean parse ) {
        this();
        this.setName( name, parse );
			/*this.name = name;
			this.id = name;*/
    }

    public Node( String name ) {
        this();
        this.setName( name );
			/*this.name = name;
			this.id = name;*/
    }

    public void setCanvasLoc( double x, double y ) {
        canvasx = x;
        canvasy = y;
    }

    public double getCanvasX() {
        return canvasx;
    }

    public double getCanvasY() {
        return canvasy;
    }

    public double getBootstrap() {
        return bootstrap;
    }

    public double geth2() {
        return h2;
    }

    public double geth() {
        return h;
    }

    public void setBootstrap( double bootstrap ) {
        this.bootstrap = bootstrap;
    }

    public void seth( double h ) {
        this.h = h;
    }

    public void seth2( double h2 ) {
        this.h2 = h2;
    }

    public String toStringWoLengths() {
        return generateString( false );

			/*String str = "";
			if( nodes.size() > 0 ) {
				str += "(";
				int i = 0;

				/*String n1 = nodes.get(0).toStringSortedWoLengths();
				if( nodes.size() > 1 ) {
					String n2 = nodes.get(1).toStringSortedWoLengths();
					if( n1.compareTo( n2 ) > 0 ) {
						str += n2+","+n1+")";
					} else {
						str += n1+","+n2+")";
					}
				} else {
					str += n1+")";
				}*
				for( i = 0; i < nodes.size()-1; i++ ) {
					str += nodes.get(i).toStringWoLengths()+",";
				}
				str += nodes.get(i).toStringWoLengths()+")";
			}

			if( meta != null && meta.length() > 0 ) {
				if( name != null && name.length() > 0 ) str += "'"+name+";"+meta+"'";
				else str += "'"+meta+"'";
			} else if( name != null && name.length() > 0 ) str += name;

			return str;*/
    }

    public String generateString( boolean wlen ) {
        String str = "";
        if( nodes.size() > 0 ) {
            str += "(";
            int i = 0;
            for( i = 0; i < nodes.size()-1; i++ ) {
                str += nodes.get(i).generateString(wlen)+",";
            }
            str += nodes.get(i).generateString(wlen)+")";
        }

        if( meta != null && meta.length() > 0 ) {
            //System.err.println("muuu " + meta);
            if( name != null && name.length() > 0 ) {
                str += name;
                if( color != null && color.length() > 0 ) str += "["+color+"]";
                if( infolist != null ) {
                    for( String info : infolist ) {
                        str += info;
                    }
                }
                String framestr = this.getFrameString();
                if( framestr != null ) str += "{"+framestr+"}";
                str += ";"+meta; //"'"+name+";"+meta+"'";
            } else {
                if( color != null && color.length() > 0 ) str += "["+color+"]";
                if( infolist != null ) {
                    for( String info : infolist ) {
                        str += info;
                    }
                }
                String framestr = this.getFrameString();
                if( framestr != null ) str += "{"+framestr+"}";
                str += ";"+meta; //"'"+meta+"'";
            }
        } else if( name != null && name.length() > 0 ) {
            str += name;
            if( color != null && color.length() > 0 ) str += "["+color+"]";
            if( infolist != null ) {
                for( String info : infolist ) {
                    str += info;
                }
            }
            String framestr = this.getFrameString();
            if( framestr != null ) str += "{"+framestr+"}";
				/*if( fontsize != -1.0 ) {
					if( framesize == -1.0 ) str += "{"+fontsize+"}";
					else str += "{"+fontsize+" "+framesize+"}";
				}*/
        }

        if( wlen ) str += ":"+h;
        // change: if( color != null && color.length() > 0 ) str += ":"+color;
        //else str += ":0.0";

        return str;
    }

    public String toString() {
        return generateString( true );
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes ) {
        this.nodes = nodes;
    }

    public void addNode( Node node, double h ) {
        if( !nodes.contains( node ) ) {
            nodes.add( node );
            node.h = h;
            node.setParent( this );
        }
    }

    public void removeNode( Node node ) {
        nodes.remove( node );
        node.setParent( null );

        if( nodes.size() == 1 ) {
            Node parent = this.getParent();
            if( parent != null && parent.getNodes().remove( this ) ) {
                Node thenode = nodes.get(0);
                thenode.seth( thenode.geth() + this.geth() );

                String hi = thenode.getName();
                String lo = this.getName();

                if( hi != null && hi.length() > 0 && lo != null && lo.length() > 0 ) {
                    try {
                        double l = Double.parseDouble( lo );
                        double h = Double.parseDouble( hi );

                        if( l > h ) thenode.setName( lo );
                    } catch( Exception e ) {};
                }

                parent.getNodes().add( thenode );
                thenode.setParent( parent );
            }
        }
    }

    public void setName( String newname ) {
        setName( newname, true );
    }

    public void addInfo( String info ) {
        if( infolist == null ) infolist = new ArrayList<String>();
        infolist.add( info );
    }

    public void clearInfo() {
        if( this.infolist != null ) this.infolist.clear();
    }

    public void setName( String newname, boolean parse ) {
        if( parse ) {
            if( newname != null ) {
                int fi = newname.indexOf(';');
                if( fi == -1 ) {
                    int ci = newname.indexOf("[");
                    //int si = newname.indexOf("{");
						/*if( ci == -1 ) {
							if( si == -1 ) {
								this.setName( newname, false );
								this.setFontSize( -1.0 );
							} else {
								this.setName( newname.substring(0,si), false );
								int se = newname.indexOf("}",si+1);
								String mfstr = newname.substring(si+1,se);
								String[] mfsplit = mfstr.split(" ");
								this.setFontSize( Double.parseDouble( mfsplit[0] ) );
								if( mfsplit.length > 1 ) this.setFrameSize( Double.parseDouble( mfsplit[1] ) );
								if( mfsplit.length > 2 ) this.setFrameOffset( Double.parseDouble( mfsplit[2] ) );
							}
							this.setColor( null );
							clearInfo();
						} else {*/
                    if( ci >= 0 ) {
                        this.name = newname.substring(0,ci);
                        int ce = newname.indexOf("]",ci+1);
                        String metastr = newname.substring(ci+1,ce);

                        int coli = metastr.indexOf("#");
                        if( coli >= 0 ) {
                            this.setColor( metastr.substring(coli, coli+7) );
                        }
                        int si = metastr.indexOf("{");
                        if( si == -1 ) {
                            this.setFontSize( -1.0 );

                            ci = newname.indexOf( '[', ce+1 );
                            while( ci != -1 ) {
                                addInfo( newname.substring(ce+1, ci) );
                                ce = newname.indexOf( ']', ci+1 );
                                addInfo( newname.substring(ci, ce+1) );

                                ci = newname.indexOf( '[', ce+1 );
                            }
                            int vi = Math.min(si, fi);
                            if( vi > ce+1 ) addInfo( newname.substring(ce+1, vi) );
                        } else {
                            //this.name = newname.substring(0,Math.min(ci, si));
								/*int se = metastr.indexOf("}",si+1);

								ci = newname.indexOf( '[', ce+1 );
								while( ci != -1 && ci < si ) {
									addInfo( newname.substring(ce+1, ci) );
									ce = newname.indexOf( ']', ci+1 );
									addInfo( newname.substring(ci, ce+1) );

									ci = newname.indexOf( '[', ce+1 );
								}
								int vi = Math.min(si, fi);
								if( vi > ce+1 ) addInfo( newname.substring(ce+1, vi) );

								String mfstr = newname.substring(si+1,se);
								String[] mfsplit = mfstr.split(" ");
								this.setFontSize( Double.parseDouble( mfsplit[0] ) );
								if( mfsplit.length > 1 ) this.setFrameSize( Double.parseDouble( mfsplit[1] ) );
								if( mfsplit.length > 2 ) this.setFrameOffset( Double.parseDouble( mfsplit[2] ) );*/
                        }
                    } else this.name = newname;
                    this.id = this.name;
                    this.setMeta( null );
                } else {
                    this.setName( newname.substring(0,fi) );
                    this.setMeta( newname.substring(fi+1) );
                }
            } else {
                this.name = newname;
                try {
                    double val = Double.parseDouble( newname );
                    this.setBootstrap( val );
                } catch( Exception e ) {

                }
                this.setMeta( null );
                this.setColor( null );
                clearInfo();
            }
        } else {
            this.name = newname;
				/*this.id = newname;
				try {
					double val = Double.parseDouble( newname );
					this.setBootstrap( val );
				} catch( Exception e ) {

				}*/
        }
    }

    public String getFullname() {
        return "";
    }

    public String getName() {
        return name;
    }

    public double getFontSize() {
        return fontsize;
    }

    public double getFrameSize() {
        return framesize == -1.0 ? fontsize : framesize;
    }

    public double getFrameOffset() {
        return frameoffset;
    }

    public String getFrameString() {
        if( fontsize != -1.0 ) {
            if( framesize != -1.0 ) {
                if( frameoffset != -1.0 ) return fontsize+" " + framesize + " " + frameoffset;
                return fontsize+" "+framesize;
            } else {
                return ""+fontsize;
            }
        }

        return null;
    }

    public void setFontSize( double fs ) {
        this.fontsize = fs;
    }

    public void setFrameSize( double fs ) {
        this.framesize = fs;
    }

    public void setFrameOffset( double fo ) {
        this.frameoffset = fo;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta( String newmeta ) {
        this.meta = newmeta;
    }

    public String getColor() {
        return color;
    }

    public void setColor( String color ) {
        this.color = color;
    }

    public int countSubnodes() {
        int total = 0;
        if( !isCollapsed() && nodes != null && nodes.size() > 0 ) {
            for( Node node : nodes ) {
                total += node.countLeaves();
            }
            total += nodes.size();
        } else total = 1;

        return total;
    }

    public int countLeaves() {
        int total = 0;
        if( !isCollapsed() && nodes != null && nodes.size() > 0 ) {
            for( Node node : nodes ) {
                total += node.countLeaves();
            }
        } else total = 1;
        leaves = total;

        return total;
    }

    public int getLeavesCount() {
        return leaves;
    }

    public int countMaxHeight() {
        int val = 0;
        for( Node node : nodes ) {
            val = Math.max( val, node.countMaxHeight() );
        }
        return val+1;
    }

    public int countParentHeight() {
        int val = 0;
        Node parent = this.getParent();
        while( parent != null ) {
            val++;
            parent = parent.getParent();
        }
        return val;
    }

    public double getHeight() {
        double h = this.geth();
        double d = h + ((parent != null) ? parent.getHeight() : 0.0);
        //console( h + " total " + d );
        return d;
    }

    public double getMaxHeight() {
        double max = 0.0;
        for( Node n : nodes ) {
            double nmax = n.getMaxHeight();
            if( nmax > max ) max = nmax;
        }
        return geth()+max;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent( Node parent ) {
        this.parent = parent;
    }
}
