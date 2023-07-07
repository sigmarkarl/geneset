package org.simmi.javafasta.shared;

import java.util.Set;

public class SimpleFunction {
    private String name;
    private String go;
    private String namespace;
    private int index;
    private String ko;
    private String ec;

    private String kegg;
    private Set<String> isa;
    private String metacyc;

    public SimpleFunction() {}

    public SimpleFunction(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setGo(String go) {
        this.go = go;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getGo() {
        return go;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public String getKo() {
        return ko;
    }

    public void setKo(String ko) {
        this.ko = ko;
    }

    public String getEc() {
        return ec;
    }

    public void setEc(String ec) {
        this.ec = ec;
    }

    public String getKegg() {
        return kegg;
    }

    public void setKegg(String kegg) {
        this.kegg = kegg;
    }

    public Set<String> getIsa() {
        return isa;
    }

    public void setIsa(Set<String> isa) {
        this.isa = isa;
    }

    public String getMetacyc() {
        return metacyc;
    }

    public void setMetacyc(String metacyc) {
        this.metacyc = metacyc;
    }
}
