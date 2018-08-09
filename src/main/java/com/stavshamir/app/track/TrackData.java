package com.stavshamir.app.track;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class TrackData {

    @Id
    private String uri;

    private String name;
    private String[] artists;
    private String albumName;
    private String albumImageUrl;

    public TrackData() {
    }

    public TrackData(String uri, String name, String[] artists, String albumName, String albumImageUrl) {
        this.uri = uri;
        this.name = name;
        this.artists = artists;
        this.albumName = albumName;
        this.albumImageUrl = albumImageUrl;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getArtists() {
        return artists;
    }

    public void setArtists(String[] artists) {
        this.artists = artists;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getAlbumImageUrl() {
        return albumImageUrl;
    }

    public void setAlbumImageUrl(String albumImageUrl) {
        this.albumImageUrl = albumImageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrackData trackData = (TrackData) o;
        return Objects.equals(uri, trackData.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri);
    }

    public static TrackDataBuilder builder() {
        return new TrackDataBuilder();
    }

    static class TrackDataBuilder {
        private String uri;
        private String name;
        private String[] artists;
        private String albumName;
        private String albumImageUrl;

        public TrackData build() {
            return new TrackData(uri, name, artists, albumName, albumImageUrl);
        }

        public TrackDataBuilder withUri(String uri) {
            this.uri = uri;
            return this;
        }

        public TrackDataBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public TrackDataBuilder withArtists(String[] artists) {
            this.artists = artists;
            return this;
        }

        public TrackDataBuilder withAlbumName(String albumName) {
            this.albumName = albumName;
            return this;
        }

        public TrackDataBuilder withAlbumImageUrl(String albumImageUrl) {
            this.albumImageUrl = albumImageUrl;
            return this;
        }
    }

}
