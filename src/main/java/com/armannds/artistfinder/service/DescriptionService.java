package com.armannds.artistfinder.service;

import com.armannds.artistfinder.data.Relation;
import com.armannds.artistfinder.errorhandling.DescriptionException;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface DescriptionService {

    CompletableFuture<String> getDescriptionFromRelations(Set<Relation> relations) throws DescriptionException;
}
