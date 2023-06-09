package org.simmi.javafasta.shared;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class GeneGroup extends Cassette implements Serializable {
	String							label;
	double							pageRank;
	String							connected;
	transient GenomeSet				geneset;
	transient public Map<String, Teginfo>  	species = new TreeMap<>();
	public int                	 	groupIndex;
	public boolean					triangle = false;
	int                 			groupCount = -1;
	public int						index;
	Map<Set<String>, ShareNum> 		specset;
	//int			groupGeneCount;
	Map<String,String> 				ko2name;
	Map<String,Cog>					cogmap;
	Map<String,Cog>					pfammap;
	Map<String,Set<String>>			biosystemsmap;

	/*Map<String,String>				cazyaamap;
	Map<String,String>				cazycemap;
	Map<String,String>				cazyghmap;
	Map<String,String>				cazygtmap;
	Map<String,String>				cazyplmap;

	public void setCazyAAMap( Map<String,String> cazyaamap ) {
		this.cazyaamap = cazyaamap;
	}

	public void setCazyCEMap( Map<String,String> cazycemap ) {
		this.cazycemap = cazycemap;
	}

	public void setCazyGHMap( Map<String,String> cazyghmap ) {
		this.cazyghmap = cazyghmap;
	}

	public void setCazyGTMap( Map<String,String> cazygtmap ) {
		this.cazygtmap = cazygtmap;
	}

	public void setCazyPLMap( Map<String,String> cazyplmap ) {
		this.cazyplmap = cazyplmap;
	}*/

	public Set<GeneGroup> getGeneGroups() {
		return Set.of(this);
	}

	public String getConnections() {
		return getFront() + " " + getBack();
	}

	public String toString() {
		return this.getName() + " " + genes.size() + "  " + this.getMaxLength();
	}

	public void setSpecSet( Map<Set<String>,ShareNum> specset ) {
		this.specset = specset;
	}

	public Boolean getTriangle() {
		return triangle;
	}

	public Set<GeneGroup> getNext() {
		var ret = new HashMap<GeneGroup,Integer>();
		var bret = new HashMap<GeneGroup,Integer>();
		for (Annotation a : getGenes()) {
			var an = a.getNext();
			if (an!=null && an.getContig() == a.getContig() && an.getGeneGroup()!=null) {
				ret.merge(an.getGeneGroup(), 1, Integer::sum);
			} else {
				var ap = a.getPrevious();
				if (ap!=null && ap.getContig() == a.getContig() && ap.getGeneGroup()!=null) {
					//ap.getContig().setReverse(true);
					bret.merge(ap.getGeneGroup(), 1, Integer::sum);
				}
			}
		}

		var tot = new HashMap<>(ret);
		for (var entry : bret.entrySet()) {
			tot.merge(entry.getKey(), entry.getValue(), Integer::sum);
		}

		if (tot.size() < ret.size()+bret.size()) {
			var gg = ret.entrySet().stream().sorted(Map.Entry.comparingByValue()).map(Map.Entry::getKey).findFirst().get();
		}

		return ret.keySet();
	}

	public Set<GeneGroup> getPrevious() {
		var ret = new HashMap<GeneGroup,Integer>();
		var bret = new HashMap<GeneGroup,Integer>();
		for (Annotation a : getGenes()) {
			var ap = a.getPrevious();
			if (ap!=null && ap.getContig() == a.getContig() && ap.getGeneGroup()!=null) {
				ret.merge(ap.getGeneGroup(), 1, Integer::sum);
			} else {
				var an = a.getNext();
				if (an!=null && an.getContig() == a.getContig() && an.getGeneGroup()!=null) {
					//an.getContig().setReverse(true);
					bret.merge(an.getGeneGroup(), 1, Integer::sum);
				}
			}
		}
		return ret.keySet();
	}

	public Map<Set<String>,ShareNum> getSpecSet() {
		return specset;
	}

	public Set<Annotation> getGenes() {
		var ann = (Set<? extends SimpleAnnotation>)genes;
		return (Set<Annotation>)ann;
	}

	public boolean containsDirty() {
		for( Annotation a : getGenes() ) {
			if( a.isDirty() ) return true;
		}
		return false;
	}

	public String getFasta( boolean id ) throws IOException {
		StringWriter sb = new StringWriter();
		for( Annotation a : getGenes() ) {
			a.getGene().getFasta( sb, id );
		}
		return sb.toString();
	}

	public void getFasta( Writer w, boolean id ) throws IOException {
		for( Annotation a : getGenes() ) {
			var g = a.getGene();
			if (g != null) g.getFasta( w, id );
		}
	}

	public void getAlignedFasta( Writer w, boolean id ) throws IOException {
		int prev = -1;
		for( Annotation a : getGenes() ) {
			var alseq = a.getAlignedSequence();
			if (alseq!=null) {
				int len = alseq.writeSequence(w, id ? a.getId() : a.getName());
				if (prev != -1 && prev != len) {
					System.err.println();
				}
				prev = len;
			} else {
				var ps = a.getProteinSequence();
				if (ps!=null) ps.writeSequence(w, id ? a.getId() : a.getName());
			}
		}
	}

	public int getMaxCyc() {
		int max = -1;
		for( Annotation a : getGenes() ) {
			Gene g = a.getGene();
			if( g != null && g.getMaxCyc() > max ) max = g.getMaxCyc();
		}
		return max;
	}

	public int getMaxLength() {
		int max = -1;
		for( Annotation a : getGenes() ) {
			if( a.getProteinLength() > max ) max = a.getProteinLength();
		}
		return max;
	}

	public Annotation getLongestSequence() {
		int max = 0;
		Annotation seltv = null;
		for( Annotation a : getGenes() ) {
			int unalen = a.getAlignedSequence().getUnalignedLength();
			if( unalen > max ) {
				seltv = a;
				max = unalen;
			}
		}
		return seltv;
	}

	public Teginfo getTes( String spec ) {
		return species.get( spec );
	}

	public List<Annotation> getTegevals( Set<String> sortspecies ) {
		List<Annotation>	ltv = new ArrayList<>();

		for( String sp : sortspecies )
		/*for( Gene g : getGenes() ) {
			Teginfo stv = g.species.get(sp);
			if( stv == null ) {
				//System.err.println( sp );
			} else {
				for (Tegeval tv : stv.tset) {
					ltv.add( tv );
				}
			}
		}*/
			ltv.addAll( getTegevals( sp ) );

		return ltv;
	}

	public List<Annotation> getTegevals( String specs ) {
		List<Annotation>	ltv = new ArrayList();

		Teginfo genes = species.get( specs );
		if( genes != null ) ltv.addAll(genes.tset);

		return ltv;
	}

	@Override
	public Islinfo getInfo(String spec) {
		if (getSpecies().contains(spec)) {
			return new Islinfo(spec);
		} else {
			return new Islinfo("");
		}
	}

	public List<Annotation> getTegevals() {
		return new ArrayList<>(getGenes());
	}

	public void setIndex( int i ) {
		this.index = i;
	}

	public int getIndex() {
		return index;
	}

	public double getAvgGCPerc() {
		double gc = 0.0;
		int count = 0;
		for( Annotation a : getGenes() ) {
			gc += a.getGCPerc();
			count++;
		}
		return gc/count;
	}

	public double getAvggcp() {
		return getAvgGCPerc();
	}

	public double getStddevGCPerc( double avggc ) {
		double gc = 0.0;
		int count = 0;
		for( Annotation a : getGenes() ) {
			double val = a.getGCPerc()-avggc;
			gc += val*val;
			count++;
		}
		return Math.sqrt(gc/count);
	}

	public String getDesignation() {
		StringBuilder ret = new StringBuilder();
		var set = getGenes().stream().map(a -> a.designation).filter(d -> d!=null && d.length()>0).collect(Collectors.toSet());
		return set.size() > 0 ? set.toString() : "";
	}

	public Set<SimpleFunction> getFunctions() {
		Set<SimpleFunction>	funcset = new HashSet();
		for( Annotation a : getGenes() ) {
			Gene g = a.getGene();
			if( g != null && g.funcentries != null && g.funcentries.size() > 0 ) {
				//Function f = funcmap.get( go );
				funcset.addAll(g.funcentries);
			}
		}
		return funcset;
	}

	public String getCommonGO( boolean breakb, boolean withinfo, Set<SimpleFunction> allowedFunctions ) {
		String ret = "";
		Set<String> already = new HashSet<>();
		for( Annotation a : getGenes() ) {
			Gene g = a.getGene();
			if( g.funcentries != null && g.funcentries.size() > 0 ) {
				for( SimpleFunction f : g.funcentries ) {
					//Function f = funcmap.get( go );

					if( allowedFunctions == null || allowedFunctions.contains(f) ) {
						String name = f.getGo(); //getName().replace('/', '-').replace(",", "");
						if( withinfo && f.getName() != null ) name += "-"+f.getName().replace(",", "");

						//System.err.println( g.getName() + "  " + go );
						if( ret.length() == 0 ) ret = name;
						else if( !already.contains(name) ) ret += ","+name;

						already.add( name );
					}
				}
				if( breakb ) break;
			}
		}
		return ret;
	}

	public String getCommonFunction( boolean breakb, Set<SimpleFunction> allowedFunctions ) {
		StringBuilder ret = new StringBuilder();
		for( Annotation a : getGenes() ) {
			Gene g = a.getGene();
			if( g.funcentries != null && g.funcentries.size() > 0 ) {
				for( SimpleFunction f : g.funcentries ) {
					//Function f = funcmap.get( go );

					if( allowedFunctions == null || allowedFunctions.contains(f) ) {
						String name = f.getName().replace('/', '-').replace(",", "");

						//System.err.println( g.getName() + "  " + go );
						if( ret.length() == 0 ) ret.append(name);
						else ret.append(",").append(name);
					}
				}
				if( breakb ) break;
			}
		}
		return ret.toString();
	}

	public boolean isOnAnyPlasmid() {
		for( Annotation a : getGenes() ) {
			Contig ctg = a.getContshort();
			if( ctg!=null && ctg.isPlasmid() ) return true;
		}

		return false;
	}

	public boolean isInAnyPhage() {
		for( Annotation a : getGenes() ) {
			if( a.isPhage() ) return true;
		}

		return false;
	}

	public String getCommonNamespace() {
		StringBuilder ret = new StringBuilder();
		Set<String>	included = new HashSet<>();
		for( Annotation a : getGenes() ) {
			Gene g = a.getGene();
			if( g.funcentries != null ) for( SimpleFunction f : g.funcentries ) {
				//Function f = funcmap.get( go );
				String namespace = f.getNamespace();
				//System.err.println( g.getName() + "  " + go );
				if( !included.contains(namespace) ) {
					if( ret.length() == 0 ) ret.append(namespace);
					else ret.append(",").append(namespace);
					included.add(namespace);
				}
			}
		}
		return ret.toString();
	}

	public String getOrigin() {
		String ret = null;
		for( Annotation a : getGenes() ) {
			Gene g = a.getGene();
			if(g != null) {
				ret = a.getGene().getSpecies();
				break;
			}
		}

		return ret;
	}

	public String getCommonTag() {
		for( Annotation a : getGenes() ) {
			Gene g = a.getGene();
			if(g != null) {
				String tag = g.getTag();
				if (tag != null) return tag;
			}
		}
		return null;
	}

	public String getCommonId() {
		String ret = null;
		for( Annotation a : getGenes() ) {
			String id = a.getId();
			if( ret == null ) ret = id;
			else {
				boolean jsome = (ret.startsWith("J") || ret.startsWith("A") || ret.startsWith("L") || ret.startsWith("B")) && ret.charAt(4) == '0';
				boolean isome = id != null && (id.startsWith("J") || id.startsWith("A") || id.startsWith("L") || id.startsWith("B")) && id.charAt(4) == '0';
				if( ((jsome || ret.contains("contig") || ret.contains("scaffold") || ret.contains("uid")) && !ret.contains(":")) ||
						(id != null && !(isome || id.contains("contig") || id.contains("scaffold") || id.contains("uid") || id.contains("unnamed") || id.contains("hypot")) )) ret = id;
			}
		}
		return ret;
	}

	public static boolean plainCompare = true;

	@Override
	public boolean equals(Object ogg) {
		if (!plainCompare && ogg instanceof GeneGroup) {
			if (this != ogg) {
				var gg = (GeneGroup) ogg;
				var ggname = gg.getName();
				var ggnameLow = ggname.toLowerCase();
				var ggci = ggname.indexOf(',');
				var ggfixName = ggci == -1 ? ggnameLow : ggnameLow.substring(0, ggci).trim();

				var name = getName();
				var nameLow = name.toLowerCase();
				var ci = name.indexOf(',');
				var fixName = ci == -1 ? nameLow : nameLow.substring(0, ci).trim();

				/*if (ggfixName.startsWith("holin")) {
					System.err.println();
				}*/
				if (!nameLow.contains("hypo") && (fixName.startsWith("holin") || fixName.startsWith("rna poly") || fixName.contains("rad52") || fixName.contains("cbbq")) && (fixName.startsWith(ggfixName) || ggfixName.startsWith(fixName))) {
					return true;
				}
				return false;
			}
			return true;
		}
		return super.equals(ogg);
	}

	@Override
	public boolean contains(GeneGroup gg) {
		return gg == this;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public double getPageRank() {
		return pageRank;
	}

	public void setPageRank(double pageRank) {
		this.pageRank = pageRank;
	}

	public String getConnected() {
		return connected;
	}

	public void setConnected(String connected) {
		this.connected = connected;
	}

	@Override
	public String getName() {
		var retname = super.getGroupName();
		if (retname==null) {
			String ret = "";
			if (genes.size() > 0) {
				for (Annotation a : getGenes()) {
					String name = a.getName();
					if (name != null) {
						if (ret.length() == 0) ret = name;
						else {
							boolean jsome = (ret.startsWith("J") || ret.startsWith("A") || ret.startsWith("L") || ret.startsWith("B")) && (ret.length() > 4 && ret.charAt(4) == '0');
							boolean nsome = (name.startsWith("J") || name.startsWith("A") || name.startsWith("L") || name.startsWith("B")) && (name.length() > 4 && name.charAt(4) == '0');

							if ((
									(jsome || ret.startsWith("Consensus") || ret.contains("plasmid") || ret.contains("chromosome") || ret.contains("contig") || ret.contains("scaffold ") || ret.contains("uid") || (ret.startsWith("hypot") && !name.contains("contig"))) /*&& !ret.contains(":")*/
							) ||
									!(nsome || name.contains("Consensus") || name.contains("plasmid") || name.contains("chromosome") || name.contains("contig") || name.contains("scaffold ") || name.contains("uid") || name.contains("unnamed") || (!ret.startsWith("Consensus") && name.contains("hypot"))))
								ret = name;
						}
					}
				}
				int k = ret.lastIndexOf('(');
				if (k != -1) {
					ret = ret.substring(0, k);
				}

				String genename = ret;
				if (genename.contains("CRISPR")) {
					k = genename.indexOf('(');
					if (k == -1) k = genename.length();
					genename = genename.substring(0, k);
					genename = genename.replace("CRISPR-associated", "");
					genename = genename.replace("CRISPR", "");
					genename = genename.replace("helicase", "");
					genename = genename.replace("endonuclease", "");
					genename = genename.replace("Cas3-HD", "");
					genename = genename.replace("/", "");
					genename = genename.replace(",", "");
					genename = genename.replace("type I-E", "");
					genename = genename.replace("ECOLI-associated", "");
					genename = genename.replace("family", "");
					genename = genename.replace("protein", "");
					genename = genename.replace("RAMP", "");
					genename = genename.trim();
					ret = genename;
				}

			/*if( ret == null || ret.length() == 0 ) {
				System.err.println();

				for( Gene g : getGenes() ) {
					String name = g.getName();
					if( ret == null ) ret = name;
					else if( (ret.contains("contig") || ret.contains("scaffold")) || !(name.contains("contig") || name.contains("scaffold") || name.contains("unnamed") || name.contains("hypot")) ) ret = name;
				}
			}*/
			} else {
				ret = getTegevals().stream().map(Annotation::getName).collect(Collectors.joining(","));
			}

			setGroupName(ret);
			if (ret.startsWith("Phage protein") || ret.contains("hypoth") || ret.contains("-contig0")) {
				for (Annotation a : getGenes()) {
					var g = a.getGene();
					if(g!=null&&g.hhblits!=null&&g.hhblits.length()>0) {
						var bil = g.hhblits.indexOf(' ');
						var tab = g.hhblits.indexOf('\t');
						var eix = g.hhblits.indexOf("E-value=",tab+1);
						if (eix>0) {
							try {
								var evl = Double.parseDouble(g.hhblits.substring(eix + 8).trim());
								if (evl < 1.0) setGroupName(g.hhblits.substring(bil + 1, tab));
							} catch(NumberFormatException ne) {

							}
						}
						break;
					}
				}
			}
			retname = ret;
		}
		return retname;
	}

	public Cog getCog( Map<String,Cog> cogmap ) {
		for( Annotation a : getGenes() ) {
			if( cogmap.containsKey( a.getId() ) ) {
				return cogmap.get( a.getId() );
			}
		}
		for( Annotation a : getGenes() ) {
			Gene g = a.getGene();
			if( g != null && g.cog != null ) return g.cog;
		}
		return null;
	}

	public String getCogname() {
		Cog cog = getCog( cogmap );
		return cog != null ? cog.name : null;
	}

	public String getCog() {
		Cog cog = getCog( cogmap );
		return cog != null ? cog.id : null;
	}

	public String getCoganno() {
		Cog cog = getCog( cogmap );
		return cog != null ? cog.annotation : null;
	}

	public String getCogsymbol() {
		Cog cog = getCog( cogmap );
		return cog != null ? cog.genesymbol : null;
	}

	public String getOldsymbol() {
		Cog cog = getCog( cogmap );
		return cog != null ? cog.cogsymbol : null;
	}

	public Cog getPfamId( Map<String,Cog> pfammap ) {
		for( Annotation a : getGenes() ) {
			if( pfammap.containsKey( a.getId() ) ) return pfammap.get( a.getId() );
		}
		for( Annotation a : getGenes() ) {
			Gene g = a.getGene();
			if( g != null & g.cog != null ) return g.cog;
		}
		return null;
	}

	public String getPfamname() {
		Cog pfam = getPfamId( pfammap );
		return pfam != null ? pfam.name : null;
	}

	public String getPfamId() {
		Cog pfam = getCog( pfammap );
		return pfam != null ? pfam.id : null;
	}

	public String getPfamanno() {
		Cog pfam = getCog( pfammap );
		return pfam != null ? pfam.annotation : null;
	}

	public String getPfamsymbol() {
		Cog pfam = getPfamId( pfammap );
		return pfam != null ? pfam.genesymbol : null;
	}

	public int getPresentin() {
		return getSpecies().size();
	}

	public String getCommonCazy( Map<String,String> cazymap ) {
		for( Annotation a : getGenes() ) {
			if( cazymap.containsKey( a.getId() ) ) return cazymap.get( a.getId() );
		}
		return null;
	}

	public String getCazy() {
		for( Annotation a : getGenes() ) {
			Gene g = a.getGene();
			if( g != null && g.cazy != null && g.cazy.length() > 0 ) return g.cazy;
		}
		return null;
	}

	public String getPhaster() {
		for( Annotation a : getGenes() ) {
			Gene g = a.getGene();
			if( g != null && g.phaster != null && g.phaster.length() > 0 ) return g.phaster;
		}
		return null;
	}

	public String getPhrog() {
		for( Annotation a : getGenes() ) {
			Gene g = a.getGene();
			if( g != null && g.getPhrog() != null && g.getPhrog().length() > 0 ) return g.getPhrog();
		}
		return null;
	}

	public String getHhblits() {
		for( Annotation a : getGenes() ) {
			Gene g = a.getGene();
			if( g != null && g.hhblits != null && g.hhblits.length() > 0 ) return g.hhblits;
		}
		return null;
	}

	public String getHhblitsuni() {
		for( Annotation a : getGenes() ) {
			Gene g = a.getGene();
			if( g != null && g.hhblitsuni != null && g.hhblitsuni.length() > 0 ) return g.hhblitsuni;
		}
		return null;
	}

	public String getDbcan() {
		for( Annotation a : getGenes() ) {
			Gene g = a.getGene();
			if( g != null && g.dbcan != null && g.dbcan.length() > 0 ) return g.dbcan;
		}
		return null;
	}

	public String getCazyAA() {
		Map<String,String> cazyaamap = geneset.getCazyAAMap();
		for( Annotation a : getGenes() ) {
			if( cazyaamap.containsKey( a.getId() ) ) return cazyaamap.get( a.getId() );
		}
		return null;
	}

	public String getCazyCE() {
		Map<String,String> cazycemap = geneset.getCazyCEMap();
		for( Annotation a : getGenes() ) {
			if( cazycemap.containsKey( a.getId() ) ) return cazycemap.get( a.getId() );
		}
		return null;
	}

	public String getCazyGH() {
		Map<String,String> cazyghmap = geneset.getCazyGHMap();
		for( Annotation a : getGenes() ) {
			if( cazyghmap.containsKey( a.getId() ) ) return cazyghmap.get( a.getId() );
		}
		return null;
	}

	public String getCazyGT() {
		Map<String,String> cazygtmap = geneset.getCazyGTMap();
		for( Annotation a : getGenes() ) {
			if( cazygtmap.containsKey( a.getId() ) ) return cazygtmap.get( a.getId() );
		}
		return null;
	}

	public String getCazyPL() {
		Map<String,String> cazyplmap = geneset.getCazyPLMap();
		for( Annotation a : getGenes() ) {
			if( cazyplmap.containsKey( a.getId() ) ) return cazyplmap.get( a.getId() );
		}
		return null;
	}

	public String getKo() {
		for( Annotation a : getGenes() ) {
			Gene g = a.getGene();
			if( g != null && g.koid != null && g.koid.length() > 0 ) return g.koid;
		}
		return null;
	}

	public String getRefid() {
		var ret = getGenes().stream().map(Annotation::getGene).filter(Objects::nonNull).map(Gene::getRefid).filter(refid->refid != null && refid.length() > 0 && !refid.contains("scaffold") && !refid.contains("contig")).collect(Collectors.joining(","));
		return ret;
	}

	public String getUnid() {
		return getGenes().stream().map(Annotation::getGene).filter(Objects::nonNull).map(g->g.uniid).filter(p->p!=null&&p.length()>0).collect(Collectors.joining(","));
	}

	public String getGenid() {
		return getGenes().stream().map(Annotation::getGene).filter(Objects::nonNull).map(g->g.genid).filter(p->p!=null&&p.length()>0).collect(Collectors.joining(","));
	}

	public String getSymbol() {
		Set<String> s = new HashSet<>();
		for( Annotation a : getGenes() ) {
			Gene g = a.getGene();
			if( g != null && g.symbol != null ) s.add( g.symbol );
		}
		if( s.isEmpty() ) {
			return null;
		} else {
			String remstr = "";
			while( remstr != null ) {
				remstr = null;
				if( s.size() > 1 ) {
					for( String str : s ) {
						if( str.length() >= 7 ) {
							remstr = str;
							break;
						}
					}
					s.remove( remstr );
				}
			}
			String ret = s.toString();
			return ret.substring(1, ret.length()-1 );
		}
	}

	public String getKsymbol() {
		Set<String> s = new HashSet<>();
		for( Annotation a : getGenes() ) {
			Gene g = a.getGene();
			//if( g.koname != null && g.koname.length() > 0 && g.koname.length() < 7 ) {
					//if( sel == null || (g.koname != null && g.koname.length() > 0 && g.koname.length() < 7 && (sel.length() >= 7 || g.koname.length() > sel.length())) ) {
					//if( sel != null && sel.contains("dnaA") ) System.err.println( sel + "   " + g.koname );
				//sel += ", " + g.koname;
			//}
			if( g != null && g.koname != null ) s.add( g.koname );
		}
		if( s.isEmpty() ) return null;
		else {
			String remstr = "";
			while( remstr != null ) {
				remstr = null;
				if( s.size() > 1 ) {
					for( String str : s ) {
						if( str.length() >= 7 ) {
							remstr = str;
							break;
						}
					}
					s.remove( remstr );
				}
			}
			String ret = s.toString();
			return ret.substring(1, ret.length()-1 );
		}
	}

	public String getKoname() {
		String ko = this.getKo();
		if( ko != null ) {
			if( ko2name != null && ko2name.containsKey( ko ) ) {
				return ko2name.get(ko);
			}
		}
		return this.getSymbol();
	}

	public String getEc() {
		for( Annotation a : getGenes() ) {
			Gene g = a.getGene();
			if( g != null && g.ecid != null && g.ecid.length() > 0 ) return g.ecid;
		}
		return null;
	}

	public String getPfam() {
		for( Annotation a : getGenes() ) {
			Gene g = a.getGene();
			if( g != null && g.pfamid != null && g.pfamid.length() > 0 ) return g.pfamid;
		}
		return null;
	}

	public String getCommonSignalP() {
		for( Annotation a : getGenes() ) {
			Gene g = a.getGene();
			if( g != null && g.signalp ) return "Y";
		}
		return null;
	}

	public String getCommonTransM() {
		for( Annotation a : getGenes() ) {
			Gene g = a.getGene();
			if( g != null && g.transm ) return "Y";
		}
		return null;
	}

	public String getKeggid() {
		StringBuilder ret = new StringBuilder();
		for( Annotation a : getGenes() ) {
			Gene g = a.getGene();
			if( g != null && g.keggid != null ) {
				if( ret.length() == 0 ) ret.append(g.keggid);
				else ret.append(" ").append(g.keggid);
			}
		}
		return ret.toString();
	}

	public String getGoid() {
		StringBuilder ret = new StringBuilder();
		for( Annotation a : getGenes() ) {
			Gene g = a.getGene();
			if( g != null && g.goid != null ) {
				if( ret.length() == 0 ) ret.append(g.goid);
				else ret.append(" ").append(g.goid);
			}
		}
		return ret.toString();
	}

	public String getKeggPathway() {
		StringBuilder ret = new StringBuilder();
		for( Annotation a : getGenes() ) {
			Gene g = a.getGene();
			if( g != null && g.keggpathway != null ) {
				if( ret.length() == 0 ) ret.append(g.keggpathway);
				else ret.append(" ").append(g.keggpathway);
			}
		}
		//if( ret == null && biosystemsmap != null ) return genes.stream().filter( g -> g.genid != null && biosystemsmap.containsKey(g.genid) ).flatMap( g -> biosystemsmap.get(g.genid).stream() ).collect(Collectors.joining(";"));
		return ret.toString();
	}

	@Override
	public int getSize() {
		return 1;
	}

	public int size() {
		return genes.size();
	}

	public Set<String> getSpecies() {
		return species.keySet();
	}

	public boolean isSingluar() {
		return this.getGroupCount() == this.getGroupCoverage();
	}

    public Teginfo getGenes( String spec ) {
        return species.get( spec );
    }

	public void addGenes(Collection<Annotation> genes) {
		for (Annotation gene : getGenes()) {
			addGene(gene);
		}
	}

	public void mergeAnnotations(Collection<Annotation> genes) {
		for (Annotation gene : getGenes()) {
			mergeAnnotation(gene);
		}
	}

	public void mergeAnnotation(Annotation gene) {
		if( gene.getGeneGroup() != this ) {
			var specstr = gene.getSpecies();
			if (specstr != null) {
				if (species.containsKey(specstr)) {
					var anno = species.get(specstr).best;
					anno.start = Math.min(anno.start,gene.start);
					anno.stop = Math.max(anno.stop,gene.stop);
				} else {
					species.put(specstr, gene.getGeneGroup().getTes(specstr));
				}
			}
			gene.setGeneGroup( this );
		}
	}

	public void addGene( Annotation gene ) {
		if( gene.getGeneGroup() != this ) gene.setGeneGroup( this );
		else {
			if( genes.add( gene ) ) {
				String specstr = gene.getSpecies();
				if (specstr != null) {
					Teginfo tigenes;
					if (species.containsKey(specstr)) {
						tigenes = species.get(specstr);
					} else {
						tigenes = new Teginfo();
						species.put(specstr, tigenes);
					}
					tigenes.add(gene);
				}
			}
        }
	}

	public void setCogMap( Map<String,Cog> cogmap ) {
		this.cogmap = cogmap;
	}

	public Map<String,Cog> getCogMap() {
		return cogmap;
	}

	public void setKonameMap( Map<String,String> konamemap ) {
		this.ko2name = konamemap;
	}

	public Map<String,String> getKonameMap() {
		return this.ko2name;
	}

	public void setBiosystemsmap( Map<String,Set<String>> biosystems ) {
		this.biosystemsmap = biosystems;
	}

	public Map<String,Set<String>> getBiosystemsmap() {
		return this.biosystemsmap;
	}

	/*public void addSpecies( String species ) {
		this.species.add( species );
	}

	public void addSpecies( Set<String> species ) {
		this.species.addAll( species );
	}*/

	public GeneGroup( GenomeSet geneset, int i, Map<Set<String>,ShareNum> specset, Map<String,Cog> cogmap, Map<String,Cog> pfammap, Map<String,String> konamemap, Map<String,Set<String>> biosystemsmap ) {
		super();
		this.groupIndex = i;
		this.specset = specset;
		this.cogmap = cogmap;
		this.ko2name = konamemap;
		this.biosystemsmap = biosystemsmap;
		this.geneset = geneset;
		//geneGroups.add(this);
	}

	public GeneGroup() {
		this( null,-1, null, null, null, null, null );
	}

	public int getGroupIndex() {
		return this.groupIndex;
	}

	public Integer getGroupCoverage() {
		return this.species.size();
	}

	public void setGroupCount( int count ) {
		this.groupCount = count;
	}

	public int getGroupCount() {
		if( groupCount == -1 ) {
			this.groupCount = genes.size();
		}
		return this.groupCount;
	}

	public int getGroupGeneCount() {
		return this.genes.size();//this.groupGeneCount;
	}

	public ShareNum getSharingNumber() {
		return specset.get( getSpecies() );
	}
};
