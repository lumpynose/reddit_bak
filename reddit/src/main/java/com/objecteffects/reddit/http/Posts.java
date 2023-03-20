package com.objecteffects.reddit.http;

import java.util.List;

public class Posts {
    private String kind;
    private ListingData data;

    public String getKind() {
        return this.kind;
    }

    public ListingData getData() {
        return this.data;
    }

    public static class ListingData {
        private int dist;
        private List<Post> children;

        public List<Post> getChildren() {
            return this.children;
        }

        public int getDist() {
            return this.dist;
        }
    }

    public static class Post {
        private String kind;
        private PostData data;

        public String getKind() {
            return this.kind;
        }

        public PostData getPostData() {
            return this.data;
        }
    }

    public static class PostData {
        private String name;
        private String thumbnail;

        public String getName() {
            return this.name;
        }

        public String getThumbnail() {
            return this.thumbnail;
        }

        @Override
        public String toString() {
            return "PostData [name=" + this.name + ", thumbnail="
                    + this.thumbnail + "]";
        }
    }
}
