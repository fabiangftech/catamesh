package dev.catamesh.core.model;


import java.util.List;

public class DiffResult {
    private final String dataProductName;
    private final DiffSummary summary;
    private final List<DiffSection> sections;

    public DiffResult(String dataProductName, DiffSummary summary, List<DiffSection> sections) {
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
