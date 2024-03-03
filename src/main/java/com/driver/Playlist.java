package com.driver;

import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private String title;
    private User creator;

    public Playlist(){

    }

    public Playlist(String title){
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCreator(User user) {
        this.creator=user;
    }

    public void addSong(Song song, SpotifyRepository repository) {
        List<Song> playlistSongs = repository.playlistSongMap.getOrDefault(this, new ArrayList<>());
        playlistSongs.add(song);
        repository.playlistSongMap.put(this, playlistSongs);
    }

}
