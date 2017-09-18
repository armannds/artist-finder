package com.armannds.artistfinder.service;

import com.armannds.artistfinder.data.Artist;

import java.util.concurrent.ExecutionException;

public interface ArtistService {

	Artist getArtistById(String id) throws ExecutionException, InterruptedException;
}
