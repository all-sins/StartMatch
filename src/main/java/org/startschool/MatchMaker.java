package org.startschool;

import java.util.List;

public class MatchMaker {

    private List<String> mmUsers;
    private Boolean mmInProgress = false;

    private MatchMaker() {
    }

    private static class Holder {
        private static final MatchMaker INSTANCE = new MatchMaker();
    }

    public static MatchMaker getInstance() {
        return Holder.INSTANCE;
    }

    public List<String> getMmUsers() {
        return mmUsers;
    }

    public void setMmUsers(List<String> mmUsers) {
        this.mmUsers = mmUsers;
    }

    public Boolean getMmInProgress() {
        return mmInProgress;
    }

    public void setMmInProgress(Boolean mmInProgress) {
        this.mmInProgress = mmInProgress;
    }
}
