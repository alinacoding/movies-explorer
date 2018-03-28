package com.movies.explorer;

import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
public interface MovieData {

    String title();
    List<String> companies();
    PeopleRoles peopleRoles();
    List<String> genres();
    List<String> countries();

    class Builder extends com.movies.explorer.ImmutableMovieData.Builder {}

    static Builder builder() {
        return new Builder();
    }

}
