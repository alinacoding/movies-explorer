package com.movies.explorer;

import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
public interface MovieData {

    String title();
    List<String> companies();
    List<String> directors();
    List<String> screenwriters();
    List<String> actors();
    List<String> genres();
    List<String> countries();

    class Builder extends ImmutableMovieData.Builder {}

    static Builder builder() {
        return new Builder();
    }


}
