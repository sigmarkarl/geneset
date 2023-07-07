package org.simmi.javafasta.shared;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class FXGeneGroup extends GeneGroup {
    transient BooleanProperty selected = new SimpleBooleanProperty();
    public boolean isSelected() {
        return selected.get();
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }
}
