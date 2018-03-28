package com.movies.explorer;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonSerialize(as = com.movies.explorer.ImmutableMovieSearchResult.class)
@JsonDeserialize(as = com.movies.explorer.ImmutableMovieSearchResult.class)
public interface MovieSearchResult {
    List<MovieData> movies();

    class Builder extends com.movies.explorer.ImmutableMovieSearchResult.Builder {
    }

    static Builder builder() {
        return new Builder();
    }



}
