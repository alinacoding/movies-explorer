package com.movies.explorer;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import java.util.Optional;

@Value.Immutable
@JsonSerialize(as = com.movies.explorer.ImmutableMovieSearch.class)
@JsonDeserialize(as = com.movies.explorer.ImmutableMovieSearch.class)
public interface MovieSearch {

    Optional<Integer> fromYear();

    @JsonProperty("toYear")
    Optional<Integer> toYear();

    @JsonProperty("title")
    Optional<String> title();

    @JsonProperty("company")
    Optional<String> company();

    @JsonProperty("genre")
    Optional<String> genre();

    @JsonProperty("director")
    Optional<String> director();

    @JsonProperty("screenwriter")
    Optional<String> screenwriter();

    @JsonProperty("actor")
    Optional<String> actor();

    @JsonProperty("country")
    Optional<String> country();

    class Builder extends com.movies.explorer.ImmutableMovieSearch.Builder {
    }

    static MovieSearch.Builder builder() {
        return new Builder();
    }

}