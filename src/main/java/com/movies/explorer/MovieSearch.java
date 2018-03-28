package com.movies.explorer;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = com.movies.explorer.ImmutableMovieSearch.class)
@JsonDeserialize(as = com.movies.explorer.ImmutableMovieSearch.class)
public interface MovieSearch {

    @JsonProperty("from") int fromYear();
    @JsonProperty("to") int toYear();
    @JsonProperty("company") String company();
    @JsonProperty("genre") String genre();
    @JsonProperty("director") String director();
    @JsonProperty("screenwriter") String screenwriter();
    @JsonProperty("actor") String actor();
    @JsonProperty("country") String country();


    class Builder extends com.movies.explorer.ImmutableMovieSearch.Builder {}

    static MovieSearch.Builder builder() {
        return new Builder();
    }

}