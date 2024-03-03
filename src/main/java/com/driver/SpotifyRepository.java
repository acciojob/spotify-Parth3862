package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        for(User user:users){
            if(user.getName().equals(name)){
                return null;
            }
        }
        User u = new User();
        u.setName(name);
        u.setMobile(mobile);
        users.add(u);
        return u;
    }

    public Artist createArtist(String name) {
        for(Artist aname:artists){
            if(aname.getName().equals(name)){
                return null;
            }
        }
        Artist a = new Artist(name);
        artists.add(a);
        return a;
    }

    public Album createAlbum(String title, String artistName) {
        for (Album al : albums) {
            if (al.getTitle().equals(title)) {
                return null;
            }
        }

        Artist artist = null;
        for (Artist a : artists) {
            if (a.getName().equals(artistName)) {
                artist = a;
                break;
            }
        }
        if (artist == null) {
            return null;
        }

        Album newAlbum = new Album();
        newAlbum.setTitle(title);
        albums.add(newAlbum);

        return newAlbum;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        for(Song song:songs){
            if(song.getTitle().equals(title)){
                return null;
            }
        }
        Song song = new Song();
        song.setTitle(title);
        song.setLength(length);
        return song;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        User user = null;
        for(User u:users){
            if(u.getMobile().equals(mobile)){
                user=u;
                break;
            }
        }
        if(user==null){
            throw new Exception("User not found");
        }
        Playlist playlist = new Playlist(title);
        for(Song song:songs){
            if(song.getLength()==length){
                playlist.addSong(song,this);
            }
        }
        playlist.setCreator(user);
        user.addPlaylistToRepository(playlist, this);
        return playlist;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        User user = null;
        for(User u : users){
            if(u.getMobile().equals(mobile)){
                user = u;
                break;
            }
        }
        if(user == null){
            throw new Exception("User not found");
        }

        Playlist playlist = new Playlist(title);
        for(String songTitle : songTitles){
            boolean found = false;
            for(Song song : songs){
                if(song.getTitle().equals(songTitle)){
                    playlist.addSong(song, this);
                    found = true;
                    break;
                }
            }
            if(!found){
                throw new Exception("Song not found: " + songTitle);
            }
        }

        playlist.setCreator(user);
        user.addPlaylistToRepository(playlist, this);

        return playlist;
    }


    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        User user = null;
        for(User u:users){
            if(u.getMobile().equals(mobile)){
                user=u;
                break;
            }
        }
        if(user==null){
            throw new Exception("User not found");
        }
        Playlist playlist = null;
        for(Playlist p:playlists){
            if(p.getTitle().equals(playlistTitle)){
                playlist=p;
                break;
            }
        }
        if(playlist==null){
            throw new Exception("Playlist not found"+playlistTitle);
        }
        return playlist;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User user = null;
        for (User u : users) {
            if (u.getMobile().equals(mobile)) {
                user = u;
                break;
            }
        }
        if (user == null) {
            throw new Exception("User not found");
        }

        Song likedSong = null;
        for (Song song : songs) {
            if (song.getTitle().equals(songTitle)) {
                likedSong = song;
                break;
            }
        }
        if (likedSong == null) {
            throw new Exception("Song not found");
        }
        if (user.hasLikedSong(likedSong)) {
            return likedSong;
        }
        List<User> likedByUsers = songLikeMap.getOrDefault(likedSong, new ArrayList<>());
        likedByUsers.add(user);
        songLikeMap.put(likedSong, likedByUsers);

        Artist artist = getArtistOfSong(likedSong);
        if (artist != null) {
            artist.setLikes(artist.getLikes() + 1);
        }

        return likedSong;
    }


    // Helper method to get the artist of a song
    private Artist getArtistOfSong(Song song) {
        for (Map.Entry<Artist, List<Album>> entry : artistAlbumMap.entrySet()) {
            List<Album> albums = entry.getValue();
            for (Album album : albums) {
                List<Song> songs = albumSongMap.get(album);
                if (songs != null && songs.contains(song)) {
                    return entry.getKey(); // Found the artist of the song
                }
            }
        }
        return null; // Artist not found
    }

    public String mostPopularArtist() {
        Map<Artist, Integer> artistLikes = new HashMap<>();
        for (Map.Entry<Song, List<User>> entry : songLikeMap.entrySet()) {
            Song song = entry.getKey();
            Artist artist = ArtistOfSong(song);
            if (artist != null) {
                int likes = entry.getValue().size();
                artistLikes.put(artist, artistLikes.getOrDefault(artist, 0) + likes);
            }
        }
        Artist mostPopularArtist = Collections.max(artistLikes.entrySet(), Map.Entry.comparingByValue()).getKey();
        return mostPopularArtist.getName();
    }
    private Artist ArtistOfSong(Song song) {
        for (Map.Entry<Artist, List<Album>> entry : artistAlbumMap.entrySet()) {
            List<Album> albums = entry.getValue();
            for (Album album : albums) {
                List<Song> songs = albumSongMap.get(album);
                if (songs != null && songs.contains(song)) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    public String mostPopularSong() {
        Song mostPopularSong = Collections.max(songLikeMap.entrySet(), Map.Entry.comparingByValue(Comparator.comparingInt(List::size))).getKey();
        return mostPopularSong.getTitle();
    }
}
