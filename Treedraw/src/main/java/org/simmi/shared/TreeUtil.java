package org.simmi.shared;

import org.simmi.Node;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TreeUtil {
	private Node currentNode = null;
	String treelabel = null;

	public Node removeRoot( Node n ) {
		Node ret;
		List<Node> ln = n.getNodes();
		Node n1 = ln.get( 0 );
		Node n2 = ln.get( 1 );
		if( n1.getNodes() != null & n1.getNodes().size() > 0 ) {
			n1.addNode( n2, n1.geth()+n2.geth() );
			n1.setParent( null );
			ret = n1;
		} else {
			n2.addNode( n1, n2.geth()+n2.geth() );
			n2.setParent( null );
			ret = n2 ;
		}
		ret.countLeaves();

		return ret;
	}

	public Node getParent( Node root, Set<String> leaveNames ) {
		Set<String> currentLeaveNames = root.getLeaveNames();
		if( currentLeaveNames.size() >= leaveNames.size() ) {
			//System.err.println( currentLeaveNames );
			if( currentLeaveNames.equals( leaveNames ) ) return root;
			else {
				for( Node n : root.getNodes() ) {
					Node par = getParent(n, leaveNames);
					if( par != null ) return par;
				}
			}
		}

		return null;
	}

	public String getSelectString( Node n, boolean meta ) {
		String ret = "";
		if( n.isLeaf() ) {
			if( n.isSelected() ) ret += n.toStringWoLengths(); //meta ? (n.getMeta() != null ? n.getMeta() : n.getName()) : n.getName();
		} else for( Node nn : n.getNodes() ) {
			String selstr = getSelectString( nn, meta );
			if( selstr.length() > 0 ) {
				if( ret.length() == 0 ) ret += getSelectString( nn, meta );
				else ret += ","+getSelectString( nn, meta );
			}
		}
		return ret;
	}

	public void reduceParentSize( Node n ) {
		List<Node> nodes = n.getNodes();
		if( nodes != null && nodes.size() > 0 ) {
			for( Node node : nodes) {
				reduceParentSize( node );
			}
			if( n.getFontSize() != -1.0 && n.getFontSize() != 0.0 ) n.setFontSize( n.getFontSize()*0.8 );
			else n.setFontSize( 0.8 );
		}
	}

	public void propogateSelection( Set<String> selset, Node node ) {
		List<Node> nodes = node.getNodes();
		if( nodes != null ) {
			for( Node n : nodes ) {
				propogateSelection(selset, n);
			}
		}
		if( node.isLeaf() && (selset.contains( node.toStringWoLengths() ) || selset.contains( node.getName() )) ) node.setSelected( true );
		//else node.setSelected( false );
	}

	public void invertSelectionRecursive( Node root ) {
		root.setSelected( !root.isSelected() );
		if( root.getNodes() != null ) for( Node n : root.getNodes() ) {
			invertSelectionRecursive( n );
		}
	}

	public boolean isChildSelected( Node n ) {
		if( n.isSelected() ) return true;

		List<Node> nodes = n.getNodes();
		if( nodes != null ) {
			for( Node node : nodes ) {
				if( isChildSelected(node) ) return true;
			}
		}

		return false;
	}

	public boolean retainSelection( Node n ) {
		if( isChildSelected( n ) ) {
			List<Node> nodes = n.getNodes();
			if( nodes != null ) {
				Node rem = null;
				List<Node> copy = new ArrayList<Node>(nodes);
				for( Node node : copy ) {
					if( retainSelection( node ) ) {
						rem = node;
					}
				}
				if( rem != null ) {
					rem.getParent().removeNode( rem );
				}
			}
			return false;
		} else {
			return true;
		}
	}

	public void setTreeLabel( String label ) {
		this.treelabel = label;
	}

	public String getTreeLabel() {
		return this.treelabel;
	}

	public boolean isRooted() {
		return currentNode.getNodes().size() == 2;
	}

	public void propogateCompare( Node n ) {
		if( n.getNodes().size() > 0 ) {
			n.comp++;
			for( Node nn : n.getNodes() ) {
				propogateCompare( nn );
			}
		}
	}

	public void appendCompare( Node n ) {
		if( n.getNodes().size() > 0 ) {
			n.setName(""+n.comp);
			for( Node nn : n.getNodes() ) {
				appendCompare( nn );
			}
		}
	}

	public Node findNode( Node root, String subtree ) {
		Node ret = null;

		String rn = root.toStringWoLengths();
		if( rn.equals(subtree) ) ret = root;
		else if( rn.length() > subtree.length() ) {
			for( Node n : root.getNodes() ) {
				Node nn = findNode( n, subtree );
				if( nn != null ) {
					ret = nn;
					break;
				}
			}
		}

		return ret;
	}

	public void compareTrees( String ns1, Node n1, Node n2 ) {
		if( n2.getNodes().size() > 1 ) {
			String ns2 = n2.toStringWoLengths();

			if( ns1.contains(ns2) ) {
				Node n = findNode( n1, ns2 );
				propogateCompare( n );
			} else {
				for( Node n : n2.getNodes() ) {
					compareTrees( ns1, n1, n );
				}
			}
		}
	}

	public void arrange( Node root, Comparator<Node> comparator ) {
		List<Node> nodes = root.getNodes();
		if( nodes != null ) {
			for( Node n : nodes ) {
				arrange( n, comparator );
			}
			Collections.sort( nodes, comparator );
		}
	}

	public double[][] lms( double[] distmat, List<String> corrInd, Node toptree ) {
		int len = corrInd.size();
		List<Node> nodes = this.getLeaves( toptree );
		int c = 0;
		for( String s : corrInd ) {
			int i = c;
			while( !s.equals( nodes.get(i).getName() ) ) i++;

			Node tnode = nodes.get(c);
			nodes.set( c, nodes.get(i) );
			nodes.set( i, tnode );

			c++;
		}

		List<Double> lad = new ArrayList<Double>();
		for( int y = 0; y < corrInd.size()-1; y++ ) {
			for( int x = y+1; x < corrInd.size(); x++ ) {
				lad.add( distmat[y*corrInd.size()+x] );
			}
		}
		double[] d = new double[ lad.size() ];
		int count = 0;
		for( double dval : lad ) {
			d[count++] = dval;
		}

		int nodecount = toptree.countSubnodes();
		double[][] X = new double[ lad.size() ][ nodecount ];
		for( int k = 0; k < nodecount; k++ ) {
			for( int i = 0; i < lad.size(); i++ ) {

			}
		}

		return X;
	}

	public Node neighborJoin( double[] corrarr, List<String> corrInd, Node guideTree, boolean rootTree, boolean parseName ) {
		Node retnode = new Node();
		try {
			List<Node> nodes;
			int len = corrInd.size();
			if( guideTree != null ) {
				nodes = this.getLeaves( guideTree );
				int c = 0;
				for( String s : corrInd ) {
					int i = c;
					while( !s.equals( nodes.get(i).getName() ) ) i++;

					Node tnode = nodes.get(c);
					nodes.set( c, nodes.get(i) );
					nodes.set( i, tnode );

					c++;
				}
			} else {
				nodes = new ArrayList<Node>();
				for( String name : corrInd ) {
					Node n = new Node( name, parseName );
					nodes.add( n );
				}
			}

			double[] dmat = corrarr; //new double[len*len];
			double[] u = new double[len];
			//System.arraycopy(corrarr, 0, dmat, 0, len*len);
			while( len > 2 ) {
				//System.err.println( "trying " + len + " size is " + nodes.size() );
				for ( int i = 0; i < len; i++ ) {
					u[i] = 0;
					for ( int j = 0; j < len; j++ ) {
						if( i != j ) {
							double dval = dmat[i*len+j];
							if( Double.isNaN( dval ) ) {
								System.err.println("erm");
							}
							u[i] += dval;
						}
					}
					u[i] /= len-2;
				}

				int imin = 0;
				int jmin = 1;
				double dmin = Double.MAX_VALUE;

				if( guideTree == null ) {
					for ( int i = 0; i < len-1; i++ ) {
						for ( int j = i+1; j < len; j++ ) {
							//if( i != j ) {
							double val = dmat[i*len+j] - u[i] - u[j];
							//if( dmat[i*len+j] < 50 ) System.err.println("euff " + val + " " + i + " " + j + "  " + dmat[i*len+j] );
							if( val < dmin ) {
								dmin = val;
								imin = i;
								jmin = j;
							}
							//}
						}
					}
				} else {
					for ( int i = 0; i < len-1; i++ ) {
						for ( int j = i+1; j < len; j++ ) {
							Node iparent = nodes.get( i ).getParent();
							Node jparent = nodes.get( j ).getParent();
							if( iparent == jparent ) {
								double val = dmat[i*len+j] - u[i] - u[j];
								//if( dmat[i*len+j] < 50 ) System.err.println("euff " + val + " " + i + " " + j + "  " + dmat[i*len+j] );
								if( val < dmin ) {
									dmin = val;
									imin = i;
									jmin = j;
								}
							}
						}
					}
				}

				//System.err.println( dmat[imin*len+jmin] );
				double vi = (dmat[imin*len+jmin]+u[imin]-u[jmin])/2.0;
				double vj = (dmat[imin*len+jmin]+u[jmin]-u[imin])/2.0;

				Node parnode;
				Node nodi = nodes.get( imin );
				Node nodj = nodes.get( jmin );
				if( guideTree == null ) {
					parnode = new Node();
					parnode.addNode( nodi, vi );
					parnode.addNode( nodj, vj );
				} else {
					parnode = nodi.getParent();
					nodi.seth( vi );
					nodj.seth( vj );
				}

				if( imin > jmin ) {
					nodes.remove(imin);
					nodes.remove(jmin);
				} else {
					nodes.remove(jmin);
					nodes.remove(imin);
				}
				nodes.add( parnode );

				double[] dmatmp = new double[(len-1)*(len-1)];
				int k = 0;
				//boolean done = false;
				for( int i = 0; i < len; i++ ) {
					if( i != imin && i != jmin ) {
						for( int j = 0; j < len; j++ ) {
							if( j != imin && j != jmin ) {
                                /*if( k >= dmatmp.length ) {
									System.err.println();
								}*/
                                /*if( k >= dmatmp.length ) {
									System.err.println("ok");
								}*/
								dmatmp[k] = dmat[i*len+j];
								k++;
							}
						}

						k++;

						//done = true;
					}
				}
				k = 0;
				for( int i = 0; i < len; i++ ) {
					if( i != imin && i != jmin ) {
						dmatmp[((k++) + 1)*(len-1)-1] = (dmat[imin*len+i] + dmat[jmin*len+i] - dmat[imin*len+jmin])/2.0;
					}
				}
				k = 0;
				for( int i = 0; i < len; i++ ) {
					if( i != imin && i != jmin ) {
						dmatmp[(len-2)*(len-1)+(k++)] = (dmat[i*len+imin] + dmat[i*len+jmin] - dmat[jmin*len+imin])/2.0;
					}
				}
				len--;
				dmat = dmatmp;

				//System.err.println( "size is " + nodes.size() );
			}

			if( rootTree ) {
				retnode.addNode( nodes.get(0), dmat[1] );
				retnode.addNode( nodes.get(1), dmat[2] );
			} else {
				retnode = nodes.get(0);
				retnode.seth(0);
				retnode.setParent( null );
				retnode.addNode( nodes.get(1), dmat[1]+dmat[2] );
			}
			nodes.clear();
		} catch( Exception e ) {
			e.printStackTrace();
			//console( e.getMessage() );
		}

		retnode.countLeaves();
		return retnode;
	}

	public Node getNode() {
		return currentNode;
	}

	public void setNode( Node node ) {
		currentNode = node;
	}

	public void grisj( Node startNode ) {
		List<Node> lnodes = startNode.getNodes();
		if( lnodes != null ) {
			List<Node> kvislist = new ArrayList<Node>();
			for( Node n : lnodes ) {
				if( !n.isLeaf() ) {
					kvislist.add( n );
				}
			}

			if( kvislist.size() == 0 ) {
				Node longestNode = null;
				double h = -1.0;
				for( Node n : lnodes ) {
					if( n.geth() > h ) {
						h = n.geth();
						longestNode = n;
					}
				}
				if( longestNode != null ) startNode.removeNode( longestNode );
			} else {
				for( Node n : kvislist ) {
					grisj( n );
				}
			}
		}
	}

	public Node getValidNode(Set<String> s, Node n ) {
		List<Node> subn = n.getNodes();
		if( subn != null ) {
			for( Node sn : subn ) {
				Set<String> ln = sn.getLeaveNames();
				if( ln.containsAll( s ) ) {
					return getValidNode( s, sn );
				}
			}
		}
		return n;
	}

	public boolean isValidSet( Set<String> s, Node n ) {
		if( n.countLeaves() > s.size() ) {
			List<Node> subn = n.getNodes();
			if( subn != null ) {
				for( Node sn : subn ) {
					Set<String> lns = sn.getLeaveNames();
					int cntcnt = 0;
					for( String ln : lns ) {
						if( s.contains(ln) ) cntcnt++;
					}
					if( !(cntcnt == 0 || cntcnt == lns.size()) ) {
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}

	public Node getConnectingParent( Node leaf1, Node leaf2 ) {
		Set<Node> ns = new HashSet<Node>();
		Node parent1 = leaf1.getParent();
		while( parent1 != null ) {
			ns.add( parent1 );
			parent1 = parent1.getParent();
		}

		Node parent2 = leaf2.getParent();
		while( parent2 != null ) {
			if( ns.contains( parent2 ) ) break;
			parent2 = parent2.getParent();
		}

		return parent2;
	}

	public double[] getDistanceMatrix( List<Node> leaves ) {
		double[] ret = new double[ leaves.size() * leaves.size() ];

		for( int i = 0; i < leaves.size(); i++ ) {
			ret[i+i*leaves.size()] = 0.0;
		}

		for( int i = 0; i < leaves.size(); i++ ) {
			for( int k = i+1; k < leaves.size(); k++ ) {
				Node leaf1 = leaves.get(i);
				Node leaf2 = leaves.get(k);
				Node parent = getConnectingParent(leaf1, leaf2);
				double val = 0.0;

				Node par = leaf1.getParent();
				while( par != parent ) {
					val += leaf1.geth();
					leaf1 = par;
					par = leaf1.getParent();
				}
				val += leaf1.geth();

				par = leaf2.getParent();
				while( par != parent ) {
					val += leaf2.geth();
					leaf2 = par;
					par = leaf2.getParent();
				}
				val += leaf2.geth();

				ret[i+k*leaves.size()] = val;
				ret[k+i*leaves.size()] = val;
			}
		}

		return ret;
	}

	public Set<String> getLeaveNames( Node node ) {
		Set<String> ret = new HashSet<String>();

		List<Node> nodes = node.getNodes();
		if( nodes != null && nodes.size() > 0 ) {
			for( Node n : nodes ) {
				ret.addAll( getLeaveNames( n ) );
			}
		} else ret.add( node.getName() );

		return ret;
	}

	public List<Node> getLeaves( Node node ) {
		List<Node> ret = new ArrayList<Node>();

		List<Node> nodes = node.getNodes();
		if( nodes != null && nodes.size() > 0 ) {
			for( Node n : nodes ) {
				ret.addAll( getLeaves( n ) );
			}
		} else ret.add( node );

		return ret;
	}

	public List<Node> getSubNodes( Node node ) {
		List<Node> ret = new ArrayList<Node>();

		List<Node> nodes = node.getNodes();
		if( nodes != null ) {
			for( Node n : nodes ) {
				ret.add( n );
				ret.addAll( getSubNodes( n ) );
			}
		}

		return ret;
	}

	public Node findNode( Node old, Node node ) {
		for( Node n : old.getNodes() ) {
			if( n == node ) return old;
			else {
				Node ret = findNode( n, node );
				if( ret != null ) return n;
			}
		}
		return null;
	}

	public Node findNode( Node old, double val ) {
		for( Node n : old.getNodes() ) {
			if( n.geth2() == val ) return n;
			else {
				Node ret = findNode( n, val );
				//if( ret != null )
				return ret;
			}
		}
		return null;
	}

	public void getlevel( Map<Integer,Set<Node>> map, Node n, int l ) {
		Set<Node> set;
		if( map.containsKey( l ) ) {
			set = map.get( l );
		} else {
			set = new HashSet<Node>();
			map.put( l, set );
		}
		set.addAll( n.getNodes() );
		for( Node node : n.getNodes() ) {
			getlevel( map, node, l+1 );
		}
	}

	public void propnull( Node n ) {
		for( Node node : n.getNodes() ) {
			propnull( node );
		}
		if( n.geth() < 0.002 ) n.seth(0.6);
	}

	public void extractMetaRecursive( Node node, Map<String,Map<String,String>> mapmap, Set<String> collapset, boolean collapse ) {
		if( node.getName() != null ) extractMeta( node, mapmap );

		List<Node> checklist = node.getNodes();
		for( Node subnode : checklist ) {
			extractMetaRecursive(subnode, mapmap, collapset, collapse);
		}

		if( mapmap != null && mapmap.size() > 0 && checklist.size() > 0 ) {
			String metacheck = null;
			boolean dual = true;
			String partial = "";
			for( Node n : checklist ) {
				if( n.getMeta() != null ) {
					String nmeta = null;
					if( n.getName() != null && n.getName().length() > 0 ) {
						nmeta = n.getName().substring(7).trim();

                        /*if( n.name.startsWith("T.ign") ) {
							System.err.println();
						}*/
					}

					if( n.getMeta().contains(";") || (n.getNodes() != null && n.getNodes().size() > 0) ) {
						String[] split = n.getMeta().split(";");
						if( split.length > 2 ) {
							String[] msp = split[split.length-1].split(":");
							String val = null;
							if( msp.length > 1 ) {
								val = (msp[1].contains("awai") || msp[1].contains("ibet") || msp[1].contains("ellow")) ? msp[1].split(" ")[0] : msp[0];
							} else {
								val = msp[0];
							}

							if( nmeta == null ) nmeta = val;
							else nmeta += "-"+val;
						} else if( nmeta == null ) nmeta = split[0];

                        /*String[] lsp = nmeta.split("-");
						if( lsp.length > 1 ) {
							String[] msp = lsp[1].split(":");
							if( msp.length > 1 ) {
								nmeta = lsp[0] + "-" + ((msp.length > 1 && (nmeta.contains("awai") || nmeta.contains("ibet") || nmeta.contains("ellow"))) ? msp[1].split(" ")[0] : msp[0]);
							}
						} else {
							String[] msp = nmeta.split(":");
							if( msp.length > 1 ) {
								nmeta = (msp.length > 1 && (nmeta.contains("awai") || nmeta.contains("ibet") || nmeta.contains("ellow"))) ? msp[1].split(" ")[0] : msp[0];
							}
						}*/
						//}
					}

					if( nmeta != null ) {
						//if( nmeta.contains("oshimai") ) System.err.println( nmeta + "  " + metacheck );

						if( metacheck == null ) {
							metacheck = nmeta;
						} else if( nmeta.length() == 0 || metacheck.length() == 0 ) {
							//System.err.println( "buuuu " + nmeta + "  " + metacheck);
							dual = false;
						} else {
							if( !collapse ) {
								if( (!nmeta.contains(metacheck) && !metacheck.contains(nmeta)) ) {
									String[] split1 = nmeta.split("-");
									String[] split2 = metacheck.split("-");

									String cont = null;
									if( split1.length > 1 || split2.length > 1 ) {
										Set<String> s1 = new HashSet<String>( Arrays.asList(split1) );
										Set<String> s2 = new HashSet<String>( Arrays.asList(split2) );

										for( String str : s1 ) {
											if( s2.contains( str ) ) {
												cont = str+"-";
												break;
											}
										}
									}

									if( cont != null ) {
										metacheck = cont;
										partial = cont;
									} else dual = false;
								} else {
									if( nmeta.length() > metacheck.length() ) {
										metacheck = collapset.contains(metacheck) ? nmeta : metacheck;
									} else {
										metacheck = collapset.contains(nmeta) ? metacheck : nmeta;
									}
									partial = metacheck;
								}
							} else {
								if( (!nmeta.contains(metacheck) || !metacheck.contains(nmeta)) ) {
									dual = false;
								}
							}
						}
					}
				}
			}

			if( dual ) {
				//if( metacheck.contains("oshimai") ) System.err.println("dual "+metacheck);
				for( Node n : checklist ) {
					if( n.getNodes() != null && n.getNodes().size() > 0 ) {
						//if(n.meta != null) System.err.println("delete meta" + n.meta);
						if( partial.length() > 0 ) {
							if( n.getMeta() != null && partial.length() >= n.getMeta().length() ) {
								n.setMeta(null);
							}
							//System.err.println( "meta " + n.meta );
							//n.meta = n.meta.replace(partial, "");
							//n.meta = n.meta.replace("-", "")	;
						} else {
							n.setMeta(null);
						}
					}
				}
				//String[] msp = metacheck.split(":");
				//node.meta = (msp.length > 1 && (metacheck.contains("awai") || metacheck.contains("ibet") || metacheck.contains("ellow"))) ? msp[1].split(" ")[0] : msp[0];
				node.setMeta(metacheck);
			} else node.setMeta(partial);
		}
	}

	public Set<Node> includeNodes( Node n, Set<String> include ) {
		Set<Node> ret = null;
		if( include.contains( n.getName() ) ) {
			ret = new HashSet<Node>();
			ret.add( n );
		}
		for( Node sn : n.getNodes() ) {
			Set<Node> ns = includeNodes( sn, include );
			if( ns != null ) {
				if( ret == null ) ret = ns;
				else ret.addAll( ns );
			}
		}

		return ret;
	}

	public void includeAlready( Node n, Set<Node> include ) {
		if( n.getParent() != null && !include.contains(n.getParent()) ) {
            /*if( n.parent.name != null || n.parent.name.length() > 0 ) {
				System.err.println( "erm " + n.parent.name );
			}*/
			include.add( n.getParent() );
			includeAlready( n.getParent(), include );
		}
	}

	public void deleteNotContaining( Node n, Set<Node> ns ) {
		n.getNodes().retainAll( ns );
		for( Node sn : n.getNodes() ) {
			deleteNotContaining(sn, ns);
		}
		if( n.getNodes().size() == 1 ) {
			Node nn = n.getNodes().get(0);
			//if( nn.nodes.size() > 0 ) {
			n.setName(nn.getName());
			n.setMeta(nn.getMeta());
			n.seth(n.geth() + nn.geth());
			n.setNodes(nn.getNodes());
			//}
            /*else if( nn.name == null || nn.name.length() == 0 ) {
				n.nodes.clear();
				n.nodes = null;
			}*/
		}
	}

	public void markColor( Node node, Map<String,String> colormap ) {
		if( colormap.containsKey(node.getMeta()) ) node.setColor(colormap.get(node.getMeta()));
		for( Node n : node.getNodes() ) {
			markColor(n, colormap);
		}
	}

	public TreeUtil() {
		super();
	}

	public void recursiveAdd( String[] list, Node root, int i ) {
		Node father = new Node( list[i] );
		Node mother = new Node( list[i+1] );
		root.addNode(father, 1.0);
		root.addNode(mother, 1.0);

		if( i*2+1 < list.length ) recursiveAdd( list, father, i*2 );
		if( (i+1)*2+1 < list.length ) recursiveAdd( list, mother, (i+1)*2 );
	}

	public String parseNodeList( String nodeStr ) {
		String[] split = nodeStr.split(",");
		Node n = new Node( split[1] );
		recursiveAdd( split, n, 2 );
		this.setNode( n );
		return n.toString();
	}

	public void clearParentNames( Node node ) {
		if( node.getNodes() != null && node.getNodes().size() > 0 ) {
			node.setName("");
			for( Node n : node.getNodes() ) {
				clearParentNames( n );
			}
		}
	}

	public void setLoc( int newloc ) {
		this.loc = newloc;
	}

	public void init( String str, boolean inverse, Set<String> include, Map<String,Map<String,String>> mapmap, boolean collapse, Set<String> collapset, Map<String,String> colormap, boolean clearParentNodes ) {
		//super();
		loc = 0;
		//System.err.println( str );
		if( str != null && str.length() > 0 ) {
			Node resultnode = parseTreeRecursive( str, inverse );

			if( clearParentNodes ) {
				clearParentNames( resultnode );
			}

			if( include == null ) {
				include = new HashSet<String>();
				String inc = str.substring( loc+1 ).trim();
				if( inc.length() > 0 && !inc.startsWith("(") ) {
					String[] split = inc.split(",");
					for( String sp : split ) {
						include.add( sp.trim() );
					}
				}
			}

			if( include.size() > 0 ) {
				Set<Node> sn = includeNodes( resultnode, include );
				Set<Node> cloneset = new HashSet<Node>( sn );
				for( Node n : sn ) {
					includeAlready( n, cloneset );
				}

				deleteNotContaining( resultnode, cloneset );
				resultnode.seth(0.0);

                /*for( Node n : cloneset ) {
					if( n.name != null && n.name.trim().length() > 0 ) System.err.println( "nnnnnnnn " + n.name );
				}*/
			}

			extractMetaRecursive( resultnode, mapmap, collapset, collapse );
			if( colormap != null ) {
				markColor( resultnode, colormap );
			}
			if( collapse ) {
				collapseTree( resultnode, collapset, false );
			}

			this.setNode( resultnode );
		} /*else {
			System.err.println( str );
		}*/
	}

	public void collapseTreeAdvanced( Node node, Collection<String> collapset, boolean simple ) {
		if( node.getNodes() != null && node.getNodes().size() > 0 ) {
			if( node.getNodes().size() == 1 ) {
				Node parent = node.getParent();
				if( parent.getNodes().remove( node ) ) {
					Node thenode = node.getNodes().get(0);
					thenode.seth( thenode.geth() + node.geth() );
					parent.getNodes().add( thenode );
				}
			}

			for( Node n : node.getNodes() ) {
				collapseTreeAdvanced( n, collapset, simple );
			}

			String test = null;
			int count = 0;

			boolean collapse = node.getNodes().size() > 1;
			if( collapse ) {
				for( Node n : node.getNodes() ) {
					String nname = n.getName() != null ? n.getName() : "";
					if( collapset == null || collapset.isEmpty() ) {
						if( test == null ) {
							test = nname;
						} else if( test.length() == 0 || nname.length() == 0 || !nname.equals(test) ) { //!(nname.contains(test) || test.contains(nname)) ) {
							test = test.length() > nname.length() ? test : nname;
							collapse = false;
							break;
						}
					} else {
						if( test == null ) {
							for( String s : collapset ) {
								if( nname.contains(s) ) {
									test = s;
									break;
								}
							}

							if( test == null ) {
								test = "";
							}
						} else if( !nname.contains(test) ) {
							collapse = false;
							break;
						}
					}

					String meta = n.getMeta();
					try {
						if( meta != null && meta.length() > 0 ) {
							int mi = Integer.parseInt( meta );
							count += mi;
						} else count++;
					} catch( Exception e ) {
						count++;
					}
				}
			}

			if( collapse && (collapset == null || collapset.contains(test)) ) {
				node.getNodes().clear();
				//node.nodes = null;
				//node.setMeta( Integer.toString(count) );
				node.setName( test+";"+Integer.toString(count) );
			}
		}
	}

	public void collapseTreeSimple( Node node, Set<String> collapset ) {
		if( node.getNodes() != null && node.getNodes().size() > 0 ) {
			boolean check = false;
			for( String s : collapset ) {
				if( node.getMeta() != null && node.getMeta().contains(s) ) {
					check = true;
					break;
				}
			}
			if( check ) {
				node.setName(node.getMeta());
				node.setMeta(Integer.toString( node.countLeaves()));
				node.getNodes().clear();
				//node.nodes = null;
			} else {
				for( Node n : node.getNodes() ) {
					collapseTreeSimple( n, collapset );
				}
			}
		}
	}

	public void nameParentNodes( Node node ) {
		if( node.getNodes() != null && node.getNodes().size() > 0 ) {
			for( Node n : node.getNodes() ) {
				nameParentNodes( n );
			}
			boolean check = true;
			String sel = null;
			String col = null;
			for( Node n : node.getNodes() ) {
				if( n.getName() != null && n.getName().length() > 0 ) {
					if( sel == null ) {
						sel = n.getName();
						col = n.getColor();
					} else {
						if( !sel.equals( n.getName() ) ) {
							check = false;
							break;
						}
					}
				} else check = false;
			}
			if( check ) {
				for( Node n : node.getNodes() ) {
					if( n.getNodes() != null && n.getNodes().size() > 0 ) {
						n.setName( null );
					}
				}
				String name = (col == null || col.length() == 0) ? sel : sel+"["+col+"]{1.0 3.0 1.00}";
				node.setName( name );
			}
		}
	}

	public void nameParentNodesMeta( Node node ) {
		if( node.getNodes() != null && node.getNodes().size() > 0 ) {
			for( Node n : node.getNodes() ) {
				nameParentNodesMeta( n );
			}
			boolean check = true;
			String sel = null;
			//String col = null;
			for( Node n : node.getNodes() ) {
				int c1 = n.countMaxHeight();
                /*if( c1 > 4 && n.getMeta() != null && n.getMeta().contains("aquat") ) {
					System.err.println();
				}*/
				if( n.getMeta() != null && n.getMeta().length() > 0 ) {
					if( sel == null ) {
						sel = n.getMeta();
						int i1 = sel.indexOf('[');
						if( i1 == -1 ) i1 = sel.length();
						int i2 = sel.indexOf('{');
						if( i2 == -1 ) i2 = sel.length();
						int i = Math.min(i1, i2);
						sel = sel.substring(0, i);
						//col = n.getColor();
					} else {
						String nmeta = n.getMeta();
						int i1 = nmeta.indexOf('[');
						if( i1 == -1 ) i1 = nmeta.length();
						int i2 = nmeta.indexOf('{');
						if( i2 == -1 ) i2 = nmeta.length();
						int i = Math.min(i1, i2);
						String str = nmeta.substring(0, i);

						if( !sel.equals( str ) ) {
							check = false;
							break;
						}
					}
				} else check = false;
			}
			if( check ) {
				for( Node n : node.getNodes() ) {
					if( n.getNodes() != null && n.getNodes().size() > 0 ) {
						n.setMeta( "" );
					}
				}
				String meta = sel+"{1.5 2.0 1.10}"; //(col == null || col.length() == 0) ? sel : sel+"["+col+"]";
				node.setMeta( meta );
			}
		}
	}

	public boolean collapseTree( Node node, Set<String> collapset, boolean delete ) {
		boolean ret = false;

		if( node.getNodes() != null && node.getNodes().size() > 0 ) {
			//Set<Node>	delset = null;
			//if( delete ) delset = new HashSet<Node>();

			boolean any = false;
			for( Node n : node.getNodes() ) {
				if( collapseTree( n, collapset, delete ) ) any = true;
				//else if( delset != null ) delset.add( n );
			}

			//if( delset != null ) node.getNodes().removeAll( delset );

			if( any ) ret = true;
			else {
				if( node.getMeta() != null && node.getMeta().length() > 0 ) {
					node.setName(node.getMeta());
					node.setMeta(Integer.toString( node.countLeaves()));
					node.getNodes().clear();
					ret = true;
				}
			}
		}

		return ret;
	}

	public double rerootRecur( Node oldnode, Node newnode ) {
		for( Node res : oldnode.getNodes() ) {
			double b;
			if( res == newnode ) b = res.geth();
			else b = rerootRecur( res, newnode );

			if( b != -1 ) {
				res.getNodes().add( oldnode );
				oldnode.setParent(res);

				double tmph = oldnode.geth();
				//res.h = oldnode.h;
				oldnode.seth(b);
				oldnode.getNodes().remove( res );

				setNode( newnode );
				currentNode.countLeaves();

				return tmph;
			}
		}

		return -1;
	}

	public void recursiveReroot() {

	}

	public void reroot( Node newnode ) {
		rerootRecur(currentNode, newnode);
		setNode( newnode );
		currentNode.countLeaves();

        /*double h = newnode.h;

		Node formerparent = newnode.getParent();
		if( formerparent != null ) {
			Node nextparent = formerparent.getParent();

			formerparent.getNodes().remove( newnode );
			Node newroot = new Node();
			newroot.addNode( newnode, h/2.0 );

			Node child = formerparent;
			Node parent = nextparent;

			if( parent == null ) {
				for( Node nn : child.getNodes() ) {
					if( nn != child ) {
				//Node erm = child.getNodes().get(0) == newnode ? child.getNodes().get(1) : child.getNodes().get(0);
						newroot.addNode(nn, newnode.h+nn.h);
					}
				}
			} else {
				newroot.addNode( formerparent, h/2.0 );
			}

			while( parent != null ) {
				parent.getNodes().remove( child );

				Node nparent = parent.getParent();
				if( nparent != null ) {
					child.addNode(parent, child.h);
				} else {
					//child.addNode(parent, child.h);

					for( Node nn : parent.getNodes() ) {
						if( nn != child ) {
						//Node erm = parent.getNodes().get(0) == child ? parent.getNodes().get(1) : parent.getNodes().get(0);
							child.addNode( nn, child.h+nn.h );
						}
					}
					break;
				}

				child = parent;
				parent = nparent;
			}

			//newparent.addNode( formerparent, h/2.0 );
			//newnode.setParent( newparent );

			currentNode = newroot;
			//console( currentNode.getNodes().size() );
			currentNode.countLeaves();
		}*/
	}

	public double getminh2() {
		return minh2;
	}

	public double getmaxh2() {
		return maxh2;
	}

	public double getminh() {
		return minh;
	}

	public double getmaxh() {
		return maxh;
	}

	public double getdiff() {
		return maxh-minh;
	}

	public double getdiff2() {
		return maxh2-minh2;
	}

	public static void main(String[] args) {
		try {
			maintree( args );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void maintree( String[] args ) throws IOException {
		byte[] bb = Files.readAllBytes( Paths.get("/Users/sigmar/777.tre") ); //"c:/sample.tree") ); //"c:/influenza.tree") );
		String str = new String( bb );
		String treestr = null;;

		if( str.startsWith("#") ) {
			int i = str.lastIndexOf("begin trees");
			if( i != -1 ) {
				i = str.indexOf('(', i);
				int l = str.indexOf(';', i+1);

				treestr = str.substring(i, l).replaceAll("[\r\n]+", "");
			}
		} else treestr = str.replaceAll("[\r\n]+", "");

		if( treestr != null ) {
			//System.err.println( treestr.substring( treestr.length()-10 ) );

			TreeUtil treeutil = new TreeUtil();
			treeutil.init( treestr, false, null, null, false, null, null, false );
			System.err.println( treeutil.getNode().toString().length() );
			treeutil.grisj( treeutil.getNode() );
			System.err.println( treeutil.getNode().toString().length() );
			treeutil.grisj( treeutil.getNode() );
			System.err.println( treeutil.getNode().toString().length() );
			treeutil.grisj( treeutil.getNode() );
			System.err.println( treeutil.getNode().toString().length() );
			treeutil.grisj( treeutil.getNode() );
			System.err.println( treeutil.getNode().toString().length() );
			treeutil.grisj( treeutil.getNode() );
			System.err.println( treeutil.getNode().toString().length() );
			treeutil.grisj( treeutil.getNode() );
			System.err.println( treeutil.getNode().toString().length() );
			treeutil.grisj( treeutil.getNode() );
			System.err.println( treeutil.getNode().toString().length() );
			treeutil.grisj( treeutil.getNode() );
			System.err.println( treeutil.getNode().toString().length() );
			treeutil.grisj( treeutil.getNode() );
			System.err.println( treeutil.getNode().toString().length() );
			treeutil.grisj( treeutil.getNode() );
			System.err.println( treeutil.getNode().toString().length() );
			System.err.println( treeutil.getNode() );

		}
	}

	public void softReplaceNames( Node node, Map<String,String> namesMap ) {
		List<Node> nodes = node.getNodes();
		for( String key : namesMap.keySet() ) {
			//if( node.getName() != null && node.getName().length() > 0 )
			//	System.err.println( "blehehe " + node.getName() );
			if( node.getName() != null && node.getName().contains( key ) ) {
				node.setName(namesMap.get( key ));
			}
		}
		//if( namesMap.containsKey( node.getName() ) ) node.setName( namesMap.get(node.getName()) );
		for( Node n : nodes ) {
			softReplaceNames(n, namesMap);
		}
	}

	public void replaceNames( Node node, Map<String,String> namesMap ) {
		List<Node> nodes = node.getNodes();
		if( nodes == null || nodes.size() == 0 ) {
			if( namesMap.containsKey( node.getName() ) ) node.setName( namesMap.get(node.getName()) );
		} else {
			for( Node n : nodes ) {
				replaceNames(n, namesMap);
			}
		}
	}

	public void swapNamesMeta( Node node ) {
		List<Node> nodes = node.getNodes();
		if( nodes != null ) {
			for( Node n : nodes ) {
				swapNamesMeta( n );
			}
		}
		String meta = node.getMeta();
		String name = node.getName() == null ? "" : node.getName();
		name = node.getColor() == null ? name : (name + "["+node.getColor()+"]");
		if( node.getInfoList() != null ) {
			for( String info : node.getInfoList() ) name += info;
		}
		name = node.getFrameString() == null ? name : name + "{" + node.getFrameString() + "}";
		if( meta != null && meta.length() > 0 ) {
			if( name != null && name.length() > 0 ) {
				node.setName( meta+";"+name );
			} else {
				node.setName( meta );
			}
		} else {
			node.setName( ";"+name );
		}
	}

	public void replaceNamesMeta( Node node ) {
		List<Node> nodes = node.getNodes();
		if( nodes != null ) {
			for( Node n : nodes ) {
				replaceNamesMeta( n );
			}
		}
		String meta = node.getMeta();
		String name = node.getName() == null ? "" : node.getName();
		name = node.getColor() == null || node.getColor().length() == 0 ? name : (name + "["+node.getColor()+"]");
		if( node.getInfoList() != null ) {
			for( String info : node.getInfoList() ) name += info;
		}
		name = node.getFrameString() == null || node.getFrameString().length() == 0 ? name : name + "{" + node.getFrameString() + "}";
		if( meta != null && meta.length() > 0 ) {
			if( name != null && name.length() > 0 ) {
				node.setName( meta+";"+name );
			} else {
				node.setName( meta );
			}
		} else {
			node.setName( name );
		}
	}

	int metacount = 0;

	public void extractMeta( Node node, Map<String,Map<String,String>> mapmap ) {
		node.setName(node.getName().replaceAll("'", ""));

		int ki = node.getName().indexOf(';');
		if( ki != -1 ) {
			//String[] metasplit = node.name.split(";");
			node.setMeta(node.getName().substring(ki+1).trim());
			node.setName(node.getName().substring(0,ki).trim());

            /*int ct = 1;
			String meta = metasplit[ ct ].trim();
			while( !meta.contains(":") && ct < metasplit.length-1 ) {
				meta = metasplit[ ++ct ];
			}

			String[] msplit = meta.split(":");
			node.meta = meta.contains("awai") || meta.contains("ellow") ? msplit[1].split(" ")[0].trim() : msplit[0].trim();
			metacount++;

			/*for( String meta : metasplit ) {
				if( meta.contains("name:") ) {
					node.name = meta.substring(5).trim();
				} else if( meta.contains("country:") ) {
					String[] msplit = meta.substring(8).trim().split(":");
					node.meta = meta.contains("awai") || meta.contains("ellow") ? msplit[1].trim() : msplit[0].trim();
					metacount++;
				}
			}*/
		}

		if( mapmap != null ) {
			String mapname = node.getName();
            /*int ik = mapname.indexOf('.');
			if( ik != -1 ) {
				mapname = mapname.substring(0, ik);
			}*/

			if( mapmap.containsKey( mapname ) ) {
				Map<String,String> keyval = mapmap.get( mapname );

				for( String key : keyval.keySet() ) {
					String meta = keyval.get(key);

					if( key.equals("name") ) {
						node.setName(meta.trim());
					} else if( node.getMeta() == null || node.getMeta().length() == 0 ) {
						node.setMeta(meta);
					} else {
						node.setMeta( node.getMeta() + ";" + meta );
						//node.meta += meta;
					}
				}
                /*if( keyval.containsKey("country") ) {
					String meta = keyval.get("country");
					//int i = meta.indexOf(':');
					//if( i != -1 ) meta = meta.substring(0, i);
					node.meta = meta;
				}

				if( keyval.containsKey("full_name") ) {
					String tax = keyval.get("full_name");
					int i = tax.indexOf(':');
					if( i != -1 ) tax = tax.substring(0, i);
					node.name = tax;
				}*/
			}
		}
	}

	double minh = Double.MAX_VALUE;
	double maxh = 0.0;
	double minh2 = Double.MAX_VALUE;
	double maxh2 = 0.0;
	int loc;

	public Node parseTreeRecursive( String str, boolean inverse ) {
		Node ret = new Node();
		Node node = null;
		while( loc < str.length()-1 && str.charAt(loc) != ')' ) {
			loc++;
			char c = str.charAt(loc);
			if( c == '(' ) {
				node = parseTreeRecursive(str, inverse);
				//if( node.getNodes().size() == 1573 ) System.err.println( node );
				if( inverse ) {
					node.getNodes().add( ret );
					ret.setParent(node);
					node.leaves++;
				} else {
					ret.getNodes().add( node );
					node.setParent(ret);
					//if( ret.name != null && ret.name.length() > 0 ) System.err.println("fokk you too");
					ret.leaves += node.leaves;
				}
			} else {
				node = new Node();
				int end = loc+1;
				char n = str.charAt(end);

				int si = 0;
                /*if( c == '\'' ) {
					while( end < str.length()-1 && n != '\'' ) {
						n = str.charAt(++end);
					}
					si = end-loc-1;
					//String code = str.substring( loc, end );
					//node.name = code.replaceAll("'", "");
					//loc = end+1;
				}*/

				boolean outsvig = true;
				boolean brakk = n == '[';
				//while( end < str.length()-1 && n != ',' && n != ')' ) {
				while( end < str.length()-1 && ( brakk || (n != ',' && n != ')' || !outsvig) ) ) {
					n = str.charAt(++end);
					if( n == '[' ) {
						brakk = true;
						//n = str.charAt(++end);
					} else if( n == ']' ) {
						brakk = false;
						//n = str.charAt(++end);
					} else if( outsvig && n == '(' ) {
						outsvig = false;
						n = str.charAt(++end);
					} else if( !outsvig && n == ')' ) {
						outsvig = true;
						n = str.charAt(++end);
					}

					//end++;
				}

				String code = str.substring( loc, end );
				int ci = code.indexOf(":", si);
				if( ci != -1 ) {
					String[] split;
					//int i = code.lastIndexOf("'");
					String name;
                    /*if( i > 0 ) {
						split = code.substring(i, code.length()).split(":");
						name = code.substring(0, i+1);
					} else {
						split = code.split(":");
						name = split[0];
					}*/

					split = code.split(":");
					name = split[0];

                    /*int coli = name.indexOf("[#");
					if( coli != -1 ) {
						int ecoli = name.indexOf("]", coli+2);
						node.color = name.substring(coli+1,ecoli);
						name = name.substring(0, coli);
					}

					int idx = name.indexOf(';');
					if( idx == -1 ) {
						node.name = name;
					} else {
						node.name = name.substring(0,idx);
						node.meta = name.substring(idx+1);
					}
					node.id = node.name;*/
					node.setName( name );
					//extractMeta( node, mapmap );

                    /*if( split.length > 2 ) {
						String color = split[2].substring(0);
						if( color.contains("rgb") ) {
							try {
								int co = color.indexOf('(');
								int ce = color.indexOf(')', co+1);
								String[] csplit = color.substring(co+1, ce).split(",");
								int r = Integer.parseInt( csplit[0].trim() );
								int g = Integer.parseInt( csplit[1].trim() );
								int b = Integer.parseInt( csplit[2].trim() );
								node.color = "rgb("+r+","+g+","+b+")"; //new Color( r,g,b );
							} catch( Exception e ) {

							}
						} else {
							try {
								int r = Integer.parseInt( color.substring(0, 2), 16 );
								int g = Integer.parseInt( color.substring(2, 4), 16 );
								int b = Integer.parseInt( color.substring(4, 6), 16 );
								node.color = "rgb("+r+","+g+","+b+")"; //new Color( r,g,b );
							} catch( Exception e ) {

							}
						}
					}// else node.color = null;*/

					String dstr = split[1].trim();
                    /*String dstr2 = "";
					if( dstr.contains("[") ) {
						int start = dstr.indexOf('[');
						int stop = dstr.indexOf(']');
						dstr2 = dstr.substring( start+1, stop );
						dstr = dstr.substring( 0, start );
					}*/

					try {
						node.seth(Double.parseDouble( dstr ));
                        /*if( dstr2.length() > 0 ) {
							node.h2 = Double.parseDouble( dstr2 );
							if( node.name == null || node.name.length() == 0 ) {
								node.setName( dstr2 );
								/*node.name = dstr2;
								node.id = node.name;*
							}
						}*/
					} catch( Exception e ) {
						System.err.println();
					}

					if( node.geth() < minh ) minh = node.geth();
					if( node.geth() > maxh ) maxh = node.geth();

					if( node.geth2() < minh2 ) minh2 = node.geth2();
					if( node.geth2() > maxh2 ) maxh2 = node.geth2();
				} else {
					node.setName( code );
                    /*int idx = code.indexOf(';');
					if( idx == -1 ) {
						node.name = code;
					} else {
						node.name = code.substring(0,idx);
						node.meta = code.substring(idx+1);
					}
					//node.name = code; //code.replaceAll("'", "");
					node.id = node.name;*/
				}
				loc = end;

				if( inverse ) {
					node.getNodes().add( ret );
					ret.setParent(node);
					node.leaves++;
				} else {
					ret.getNodes().add( node );
					//if( ret.name != null && ret.name.length() > 0 ) System.err.println("fokk");
					node.setParent(ret);
					ret.leaves++;
				}
			}
		}

		Node use = inverse ? node : ret;


        /*List<Node> checklist = use.getNodes();
		String metacheck = null;
		boolean dual = true;
		for( Node n : checklist ) {
			if( n.meta != null ) {
				if( metacheck == null ) metacheck = n.meta;
				else if( !n.meta.equals(metacheck) ) dual = false;
			}
		}

		if( dual ) {
			for( Node n : checklist ) {
				if( n.getNodes() != null && n.getNodes().size() > 0 ) n.meta = null;
			}
			use.meta = metacheck;
		} else use.meta = "";*/

		//System.err.println("setting: "+metacheck + use.getNodes());

		if( loc < str.length()-1 ) {
			loc++;
			int end = loc;
			char n = str.charAt(end);

			int si = 0;
            /*if( n == '\'' ) {
				n = str.charAt(++end);
				while( end < str.length()-1 && n != '\'' ) {
					n = str.charAt(++end);
				}
				si = end-loc-1;
				//String code = str.substring( loc, end );
				//node.name = code.replaceAll("'", "");
				//loc = end+1;
			}*/

			boolean brakk = n == '[';
			while( end < str.length()-1 && ( brakk || (n != ',' && n != ')') ) ) {
				n = str.charAt(++end);
				if( n == '[' ) {
					brakk = true;
					//n = str.charAt(++end);
				} else if( n == ']' ) {
					brakk = false;
					//n = str.charAt(++end);
				}
			}

			String code;
			if( n == ']' ) {
				code = str.substring( loc, end+1 );
			} else code = str.substring( loc, end );
			int ci = code.indexOf(":", si);
			if( ci != -1 ) {
				String[] split;
				int i = code.lastIndexOf("'");
				if( i > 0 ) {
					split = code.substring(i, code.length()).split(":");
					ret.setName( code.substring(0, i+1) );
				} else {
					split = code.split(":");
					ret.setName( split.length > 0 ? split[0] : "" );
				}

				//String[] split = code.split(":");
				if( split.length > 2 ) {
					String color = split[2].substring(0);
					try {
						int r = Integer.parseInt( color.substring(0, 2), 16 );
						int g = Integer.parseInt( color.substring(2, 4), 16 );
						int b = Integer.parseInt( color.substring(4, 6), 16 );
						ret.setColor("rgb("+r+","+g+","+b+")"); //new Color( r,g,b );
					} catch( Exception e ) {

					}
				}// else ret.color = null;
				String dstr = split.length > 1 ? split[1].trim() : "0";
                /*String dstr2 = "";
				if( dstr.contains("[") ) {
					int start = split[1].indexOf('[');
					int stop = split[1].indexOf(']');
					dstr2 = dstr.substring( start+1, stop );
					dstr = dstr.substring( 0, start );
				}*/
				try {
					ret.seth(Double.parseDouble( dstr ));
                    /*if( dstr2.length() > 0 ) {
						ret.h2 = Double.parseDouble( dstr2 );
						if( ret.name == null || ret.name.length() == 0 ) {
							ret.setName( dstr2 );
						}
					}*/
				} catch( Exception e ) {}
				if( ret.geth() < minh ) minh = ret.geth();
				if( ret.geth() > maxh ) maxh = ret.geth();
				if( ret.geth2() < minh2 ) minh2 = ret.geth2();
				if( ret.geth2() > maxh2 ) maxh2 = ret.geth2();
			} else {
				ret.setName( code.replaceAll("'", "") );
			}
			loc = end;
		}

        /*if( use.leaves == 1573 ) {
			try {
				FileWriter fw = new FileWriter("/home/sigmar/tree"+(cnt++)+".ntree");
				fw.write( use.toString() );
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
		return use;
	}

	int cnt = 0;

	public double nDistance(Node node1, Node node2) {
		double ret = 0.0;

		List<Set<String>> nlist1 = new ArrayList<Set<String>>();
		node1.nodeCalc( nlist1 );

		List<Set<String>> nlist2 = new ArrayList<Set<String>>();
		node2.nodeCalc( nlist2 );

		for( Set<String> s1 : nlist1 ) {
			boolean found = false;
			for( Set<String> s2 : nlist2 ) {
				if( s1.size() == s2.size() && s1.containsAll( s2 ) ) {
					found = true;
					break;
				}
			}
			if( !found ) ret += 1.0;
		}

		for( Set<String> s2 : nlist2 ) {
			boolean found = false;
			for( Set<String> s1 : nlist1 ) {
				if( s1.size() == s2.size() && s1.containsAll( s2 ) ) {
					found = true;
					break;
				}
			}
			if( !found ) ret += 1.0;
		}

		return ret;
	}
}
