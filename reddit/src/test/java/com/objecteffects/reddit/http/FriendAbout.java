package com.objecteffects.reddit.http;

import com.google.gson.annotations.SerializedName;

public class FriendAbout {
    private String kind;
    private FriendData data;

    public String getKind() {
        return this.kind;
    }

    public FriendData getData() {
        return this.data;
    }

    public static class FriendData {
        @SerializedName("is_suspended")
        private String isSuspended;
        @SerializedName("total_karma")
        private int totalKarma;

        public String getIsSuspended() {
            return this.isSuspended;
        }

        public int getTotalKarma() {
            return this.totalKarma;
        }
    }
}
