package com.movies.explorer;

import java.util.Set;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@JsonSerialize(as = com.movies.explorer.ImmutableMovieData.class)
@JsonDeserialize(as = com.movies.explorer.ImmutableMovieData.class)
public interface MovieData {

    String title();

    int year();

    Set<String> companies();

    PeopleRoles peopleRoles();

    Set<String> genres();

    Set<String> countries();

    class Builder extends com.movies.explorer.ImmutableMovieData.Builder {}

    static Builder builder() {
        return new Builder();
    }

}
