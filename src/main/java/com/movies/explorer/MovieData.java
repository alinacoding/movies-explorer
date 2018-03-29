package com.movies.explorer;

import java.util.List;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@JsonSerialize(as = com.movies.explorer.ImmutableMovieData.class)
@JsonDeserialize(as = com.movies.explorer.ImmutableMovieData.class)
public interface MovieData {

    String title();

    int year();

    List<String> companies();

    PeopleRoles peopleRoles();

    List<String> genres();

    List<String> countries();

    class Builder extends com.movies.explorer.ImmutableMovieData.Builder {}

    static Builder builder() {
        return new Builder();
    }

}
