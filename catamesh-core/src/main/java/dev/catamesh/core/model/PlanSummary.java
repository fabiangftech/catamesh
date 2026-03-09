package dev.catamesh.core.model;

public class PlanSummary {
    private int create;
    private int update;
    private int delete;
    private int replace;
    private int noop;
    private int adopt;

    public PlanSummary() {
        // do nothing
    }

    public int getCreate() {
        return create;
    }

    public int getUpdate() {
        return update;
    }

    public int getDelete() {
        return delete;
    }

    public int getReplace() {
        return replace;
    }

    public int getNoop() {
        return noop;
    }


    public int getAdopt() {
        return adopt;
    }


    public void plusCreate(){
        this.create++;
    }

    public void plusUpdate(){
        this.update++;
    }

    public void plusDelete() {
        this.delete++;
    }

    public void plusNoop(){
        this.noop++;
    }
}
