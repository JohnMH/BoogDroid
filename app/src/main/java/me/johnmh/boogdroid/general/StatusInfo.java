package me.johnmh.boogdroid.general;

import java.util.List;

import me.johnmh.boogdroid.bugzilla.ChangeStatusInfo;

/**
 * Wrapper for HashMap
 */
public class StatusInfo {
    boolean open;
    String name;
    private List<ChangeStatusInfo> changeList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    @Override
    public String toString() {
        return getName();
    }

    public void setChangeList(List<ChangeStatusInfo> changeList) {
        this.changeList = changeList;
    }

    public List<ChangeStatusInfo> getChangeList() {
        return changeList;
    }
}
