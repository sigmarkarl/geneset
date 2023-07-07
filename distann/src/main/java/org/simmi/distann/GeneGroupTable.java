package org.simmi.distann;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import org.simmi.javafasta.shared.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GeneGroupTable extends TableView<FXGeneGroup> {
    SortedList<FXGeneGroup> sortedData;
    FilteredList<FXGeneGroup> filteredData;
    Label label;
    boolean tableisselecting = false;
    FunctionTable   ftable;
    GeneTable       gtable;
    final Set<Integer> filterset = new HashSet<>();
    GeneSetHead geneSetHead;

    public GeneGroupTable(GeneSetHead geneSetHead, FunctionTable ftable, GeneTable gtable, Label label) {
        this.geneSetHead = geneSetHead;
        this.label = label;
        this.ftable = ftable;
        this.gtable = gtable;
    }

    public void popuplate(List<GeneGroup> allgenegroups) {
        ObservableList<FXGeneGroup> ogenegroup = FXCollections.observableList( (List<FXGeneGroup>)(List<? extends GeneGroup>)allgenegroups );
        filteredData = new FilteredList<>(ogenegroup, p -> true);
        sortedData = new SortedList<>( filteredData );
        setItems( sortedData );
        sortedData.comparatorProperty().bind(comparatorProperty());
        setItems(sortedData);
    }

    public void init() {
        var table = this;

        TableColumn<FXGeneGroup, String> namedesccol = new TableColumn<>("Desc");
        namedesccol.setCellValueFactory( new PropertyValueFactory<>("name"));
        namedesccol.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> sortedData.comparatorProperty().bind(table.comparatorProperty()));
        table.getColumns().add( namedesccol );
        TableColumn<FXGeneGroup, String> connectionscol = new TableColumn<>("Connections");
        connectionscol.setCellValueFactory( new PropertyValueFactory<>("Connections"));
        table.getColumns().add( connectionscol );
        TableColumn<FXGeneGroup, String> ggislandidcol = new TableColumn<>("IslandID");
        ggislandidcol.setCellValueFactory( new PropertyValueFactory<>("IslandId"));
        table.getColumns().add( ggislandidcol );
        TableColumn<FXGeneGroup, String> ggislandsizecol = new TableColumn<>("IslandSize");
        ggislandsizecol.setCellValueFactory( new PropertyValueFactory<>("IslandSize"));
        table.getColumns().add( ggislandsizecol );
        TableColumn<FXGeneGroup, String> trianglecol = new TableColumn<>("SingleInsert");
        trianglecol.setCellValueFactory( new PropertyValueFactory<>("Triangle"));
        table.getColumns().add( trianglecol );
        TableColumn<FXGeneGroup, String> labelcol = new TableColumn<>("Label");
        labelcol.setCellValueFactory( new PropertyValueFactory<>("Label"));
        table.getColumns().add( labelcol );
        TableColumn<FXGeneGroup, String> pagerankcol = new TableColumn<>("PageRank");
        pagerankcol.setCellValueFactory( new PropertyValueFactory<>("PageRank"));
        table.getColumns().add( pagerankcol );
        TableColumn<FXGeneGroup, String> connectedcol = new TableColumn<>("Connected");
        connectedcol.setCellValueFactory( new PropertyValueFactory<>("Connected"));
        table.getColumns().add( connectedcol );
        TableColumn<FXGeneGroup, String> origincol = new TableColumn<>("Origin");
        origincol.setCellValueFactory( new PropertyValueFactory<>("origin"));
        table.getColumns().add( origincol );
        TableColumn<FXGeneGroup, String> geneidcol = new TableColumn<>("Genid");
        geneidcol.setCellValueFactory( new PropertyValueFactory<>("genid"));
        table.getColumns().add( geneidcol );
        TableColumn<FXGeneGroup, String> refidcol = new TableColumn<>("Refid");
        refidcol.setCellValueFactory( new PropertyValueFactory<>("refid"));
        table.getColumns().add( refidcol );
        TableColumn<FXGeneGroup, String> unidcol = new TableColumn<>("Unid");
        unidcol.setCellValueFactory( new PropertyValueFactory<>("unid"));
        table.getColumns().add( unidcol );
        TableColumn<FXGeneGroup, String> descol = new TableColumn<>("Designation");
        descol.setCellValueFactory( new PropertyValueFactory<>("designation"));
        table.getColumns().add( descol );
        TableColumn<FXGeneGroup, String> goidcol = new TableColumn<>("Goid");
        goidcol.setCellValueFactory( new PropertyValueFactory<>("goid"));
        table.getColumns().add( goidcol );
        TableColumn<FXGeneGroup, String> keggpathcol = new TableColumn<>("Kegg pathway");
        keggpathcol.setCellValueFactory( new PropertyValueFactory<>("keggPathway"));
        table.getColumns().add( keggpathcol );
        TableColumn<FXGeneGroup, String> kocol = new TableColumn<>("KO");
        kocol.setCellValueFactory( new PropertyValueFactory<>("ko"));
        table.getColumns().add( kocol );
        TableColumn<FXGeneGroup, String> cazy = new TableColumn<>("Cazy");
        cazy.setCellValueFactory( new PropertyValueFactory<>("cazy"));
        table.getColumns().add( cazy );

        TableColumn<FXGeneGroup, String> dbcan = new TableColumn<>("Dbcan");
        dbcan.setCellValueFactory( new PropertyValueFactory<>("dbcan"));
        table.getColumns().add( dbcan );

        TableColumn<FXGeneGroup, String> phaster = new TableColumn<>("Phrog");
        phaster.setCellValueFactory( new PropertyValueFactory<>("phrog"));
        table.getColumns().add( phaster );

        TableColumn<FXGeneGroup, String> hhpred = new TableColumn<>("HHPred");
        hhpred.setCellValueFactory( new PropertyValueFactory<>("hhblits"));
        table.getColumns().add( hhpred );

        TableColumn<FXGeneGroup, String> hhpreduni = new TableColumn<>("HHPredUni");
        hhpreduni.setCellValueFactory( new PropertyValueFactory<>("hhblitsuni"));
        table.getColumns().add( hhpreduni );

        TableColumn<FXGeneGroup, String> symbcol = new TableColumn<>("Symbol");
        symbcol.setCellValueFactory( new PropertyValueFactory<>("symbol"));
        table.getColumns().add( symbcol );
        TableColumn<FXGeneGroup, String> konamecol = new TableColumn<>("KO name");
        konamecol.setCellValueFactory( new PropertyValueFactory<>("koname"));
        table.getColumns().add( konamecol );
        TableColumn<FXGeneGroup, String> pbidcol = new TableColumn<>("Pfam");
        pbidcol.setCellValueFactory( new PropertyValueFactory<>("Pfam"));
        table.getColumns().add( pbidcol );
        TableColumn<FXGeneGroup, String> eccol = new TableColumn<>("Ec");
        eccol.setCellValueFactory( new PropertyValueFactory<>("ec"));
        table.getColumns().add( eccol );
        TableColumn<FXGeneGroup, String> cognamecol = new TableColumn<>("Cog name");
        cognamecol.setCellValueFactory( new PropertyValueFactory<>("cogname"));
        table.getColumns().add( cognamecol );
        TableColumn<FXGeneGroup, String> cogcol = new TableColumn<>("Cog");
        cogcol.setCellValueFactory( new PropertyValueFactory<>("cog"));
        table.getColumns().add( cogcol );
        TableColumn<FXGeneGroup, String> cogannocol = new TableColumn<>("Cog annotation");
        cogannocol.setCellValueFactory( new PropertyValueFactory<>("coganno"));
        table.getColumns().add( cogannocol );
        TableColumn<FXGeneGroup, String> cogsymbcol = new TableColumn<>("Cog symbol");
        cogsymbcol.setCellValueFactory( new PropertyValueFactory<>("cogsymbol"));
        table.getColumns().add( cogsymbcol );

        TableColumn<FXGeneGroup, String> oldsymbcol = new TableColumn<>("Original cogsymbol");
        oldsymbcol.setCellValueFactory( new PropertyValueFactory<>("oldsymbol"));
        table.getColumns().add( oldsymbcol );

        TableColumn<FXGeneGroup, String> cazyaa = new TableColumn<>("Cazy_AA");
        cazyaa.setCellValueFactory( new PropertyValueFactory<>("cazyAA"));
        table.getColumns().add( cazyaa );
        TableColumn<FXGeneGroup, String> cazyce = new TableColumn<>("Cazy_CE");
        cazyce.setCellValueFactory( new PropertyValueFactory<>("cazyCE"));
        table.getColumns().add( cazyce );
        TableColumn<FXGeneGroup, String> cazygh = new TableColumn<>("Cazy_GH");
        cazygh.setCellValueFactory( new PropertyValueFactory<>("cazyGH"));
        table.getColumns().add( cazygh );
        TableColumn<FXGeneGroup, String> cazygt = new TableColumn<>("Cazy_GT");
        cazygt.setCellValueFactory( new PropertyValueFactory<>("cazyGT"));
        table.getColumns().add( cazygt );
        TableColumn<FXGeneGroup, String> cazypl = new TableColumn<>("Cazy_PL");
        cazypl.setCellValueFactory( new PropertyValueFactory<>("cazyPL"));
        table.getColumns().add( cazypl );
        TableColumn<FXGeneGroup, String> prescol = new TableColumn<>("Present in");
        prescol.setCellValueFactory( new PropertyValueFactory<>("presentin"));
        table.getColumns().add( prescol );

        TableColumn<FXGeneGroup, Integer> groupindcol = new TableColumn<>("Group index");
        groupindcol.setCellValueFactory( new PropertyValueFactory<FXGeneGroup,Integer>("groupIndex"));
        table.getColumns().add( groupindcol );
        TableColumn<FXGeneGroup, Integer> groupcovcol = new TableColumn<>("Group coverage");
        groupcovcol.setCellValueFactory( new PropertyValueFactory<FXGeneGroup,Integer>("groupCoverage"));
        table.getColumns().add( groupcovcol );
        TableColumn<FXGeneGroup, Integer> groupsizecol = new TableColumn<>("Group size");
        groupsizecol.setCellValueFactory( new PropertyValueFactory<FXGeneGroup,Integer>("groupGeneCount"));
        table.getColumns().add( groupsizecol );

        TableColumn<FXGeneGroup, String> locprefcol = new TableColumn<>("Loc pref");
        locprefcol.setCellValueFactory( new PropertyValueFactory<>("locpref"));
        table.getColumns().add( locprefcol );
        TableColumn<FXGeneGroup, String> avgcpcol = new TableColumn<>("Avg GC%");
        avgcpcol.setCellValueFactory( new PropertyValueFactory<>("avggcp"));
        table.getColumns().add( avgcpcol );
        TableColumn<FXGeneGroup, String> maxlencol = new TableColumn<>("Max length");
        maxlencol.setCellValueFactory( new PropertyValueFactory<>("maxLength"));
        table.getColumns().add( maxlencol );
        TableColumn<FXGeneGroup, String> numloccol = new TableColumn<>("#Loc");
        numloccol.setCellValueFactory( new PropertyValueFactory<>("numloc"));
        table.getColumns().add( numloccol );
        TableColumn<FXGeneGroup, String> numlocgroupcol = new TableColumn<>("#Loc group");
        numlocgroupcol.setCellValueFactory( new PropertyValueFactory<>("numlocgroup"));
        table.getColumns().add( numlocgroupcol );

        TableColumn<FXGeneGroup, ShareNum> sharenumcol = new TableColumn<>("Sharing number");
        sharenumcol.setCellValueFactory( new PropertyValueFactory<>("sharingNumber"));
        table.getColumns().add( sharenumcol );
        TableColumn<FXGeneGroup, String> maxcyccol = new TableColumn<>("Max cyc");
        maxcyccol.setCellValueFactory( new PropertyValueFactory<>("maxCyc"));
        table.getColumns().add( maxcyccol );

        TableColumn<FXGeneGroup, Boolean> selectedcol = new TableColumn<>("Selected");
        selectedcol.setCellValueFactory(param -> param.getValue().selectedProperty());
        selectedcol.setCellFactory(CheckBoxTableCell.forTableColumn(selectedcol));
        selectedcol.setEditable(true);
        table.getColumns().add( selectedcol );

        table.getSelectionModel().selectedItemProperty().addListener( e -> {
            if(geneSetHead.isTableSelectListenerEnabled) {
                label.setText(table.getItems().size() + "/" + table.getSelectionModel().getSelectedItems().size());
                // table.clearSelection();
                tableisselecting = true;
                if (!ftable.ftableisselecting && filterset.isEmpty()) {
                    //ftable.removeRowSelectionInterval(0, ftable.getRowCount() - 1);
                    if (!geneSetHead.isGeneview()) {
                        for (GeneGroup gg : table.getSelectionModel().getSelectedItems()) {
                            for (var f : gg.getFunctions()) {
                                try {
                                    ftable.getSelectionModel().select((Function)f);
                                    //int rf = ftable.convertRowIndexToView(f.index);
                                    //if( rf >= 0 && rf < ftable.getRowCount() ) ftable.addRowSelectionInterval(rf, rf);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    } else {
                        for (Gene g : gtable.getSelectionModel().getSelectedItems()) {
                            if (g.funcentries != null) {
                                for (var f : g.funcentries) {
                                    //Function f = funcmap.get(go);
                                    try {
                                        ftable.getSelectionModel().select((Function)f);
                                        //int rf = ftable.convertRowIndexToView(f.index);
                                        //if( rf >= 0 && rf < ftable.getRowCount() ) ftable.addRowSelectionInterval(rf, rf);
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
                tableisselecting = false;
            }
        });

        table.setOnKeyPressed( ke -> {
            if (ke.getCode() == KeyCode.ESCAPE) {
                FXGeneGroup selgg = table.getSelectionModel().getSelectedItem();

                List<FXGeneGroup> sel = new ArrayList<>( filteredData );
                filteredData.setPredicate(null);
                int[] rows = sel.stream().mapToInt( gg -> sortedData.indexOf(gg) ).toArray();
                if( rows.length > 0 ) table.getSelectionModel().selectIndices(rows[0], rows);
                if (label != null)
                    label.setText(table.getItems().size() + "/" + table.getSelectionModel().getSelectedIndices().size());

                table.scrollTo( selgg );
                //genefilterset.clear();
                //updateFilter(table, genefilter, label);
                //geneset.scrollToSelection( table );
            }
        });

        table.setOnMousePressed( e -> {
            tableisselecting = true;
            if (!ftable.ftableisselecting && e.getClickCount() == 2) {
                /*
                 * int[] rr = ftable.getSelectedRows(); int minr =
                 * ftable.getRowCount(); int maxr = 0; for( int r : rr ) {
                 * if( r < minr ) minr = r; if( r > maxr ) maxr = r; }
                 * ftable.removeRowSelectionInterval(minr, maxr);
                 */
                // ftable.removeRowSelectionInterval(0, filterset.isEmpty()
                // ? ftable.getRowCount()-1 : filterset.size()-1 );

                var fset = new HashSet<SimpleFunction>();
                filterset.clear();
                if( !geneSetHead.isGeneview() ) {
                    for (GeneGroup gg : table.getSelectionModel().getSelectedItems()) {
                        fset.addAll( gg.getFunctions() );
                    }
                } else {
                    for (Gene g : gtable.getSelectionModel().getSelectedItems()) {
                        if (g.funcentries != null) {
                            for( var f : g.funcentries ) {
                                //Function f = funcmap.get(go);
                                // ftable.getRowSorter().convertRowIndexToView(index)
                                // int rf = ftable.convertRowIndexToView(
                                // f.index );
                                filterset.add(f.getIndex());
                                // ftable.addRowSelectionInterval(rf, rf);
                            }
                        }
                    }
                }
                ftable.ffilteredData.setPredicate(fset::contains);
            }
            tableisselecting = false;
        });

        table.setOnMouseClicked(event -> {
            var y = event.getY();
            if (y<27) sortedData.comparatorProperty().bind(comparatorProperty());
        });

        table.getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
		/*table.getSelectionModel().selectedItemProperty().addListener( e -> {
			label.setText(table.getItems().size() + "/" + table.getSelectionModel().getSelectedItems().size());
		});*/
    }
}
