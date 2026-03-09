package dev.catamesh.core.model;


import java.util.List;

public class Diff {
    private String dataProductName;
    private DiffSummary summary;
    private List<DiffSection> sections;

    public Diff() {
        // do nothing
    }

    public Diff(String dataProductName, DiffSummary summary, List<DiffSection> sections) {
        this.dataProductName = dataProductName;
        this.summary = summary;
        this.sections = List.copyOf(sections);
    }

    public String getDataProductName() {
        return dataProductName;
    }

    public DiffSummary getSummary() {
        return summary;
    }

    public List<DiffSection> getSections() {
        return sections;
    }
}
