package com.driver;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String name;
    private String mobile;
    private List<Song> likedSongs;


    public User() {}

    public User(String name, String mobile) {
        this.name = name;
        this.mobile = mobile;
        likedSongs = new ArrayList<>();
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getMobile() {

        return mobile;
    }

    public void setMobile(String mobile) {

        this.mobile = mobile;
    }

    public void addPlaylistToRepository(Playlist playlist, SpotifyRepository repository) {
        List<Playlist> userPlaylists = repository.userPlaylistMap.getOrDefault(this, new ArrayList<>());
        userPlaylists.add(playlist);
        repository.userPlaylistMap.put(this, userPlaylists);
    }
    public boolean hasLikedSong(Song song) {
        for (Song likedSong : likedSongs) {
            if (likedSong.equals(song)) {
                return true;
            }
        }
        return false;
    }
}
