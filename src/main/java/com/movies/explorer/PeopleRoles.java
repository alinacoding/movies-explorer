package com.movies.explorer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonSerialize(as = com.movies.explorer.ImmutablePeopleRoles.class)
@JsonDeserialize(as = com.movies.explorer.ImmutablePeopleRoles.class)
public interface PeopleRoles {

    List<String> directors();

    List<String> screenwriters();

    List<String> actors();

    class Builder extends com.movies.explorer.ImmutablePeopleRoles.Builder {
    }

    static Builder builder() {
        return new Builder();
    }
}
