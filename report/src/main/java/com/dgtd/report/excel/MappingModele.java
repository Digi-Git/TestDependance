package com.dgtd.report.excel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MappingModele {

    private File file;
    private String tag;
    private String titre;
    private List<MappingExportChamp> mappings;
    private boolean subLevel;

    public MappingModele() {

        this.mappings = new ArrayList<>();
        subLevel = false;
    }

    public MappingModele(File file, String tag, String titre, boolean subLevel) {

        this.file = file;
        this.tag = tag;
        this.titre = titre;
        this.subLevel = subLevel;

        this.mappings = new ArrayList<>();
    }



    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public List<MappingExportChamp> getMappings() {
        return mappings;
    }

    public void setMappings(List<MappingExportChamp> mappings) {
        this.mappings = mappings;
    }

    public boolean isSubLevel() {
        return subLevel;
    }

    public void setSubLevel(boolean subLevel) {
        this.subLevel = subLevel;
    }

}
