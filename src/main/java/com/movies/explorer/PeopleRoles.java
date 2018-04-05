package com.movies.explorer;

import java.util.Set;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@JsonSerialize(as = com.movies.explorer.ImmutablePeopleRoles.class)
@JsonDeserialize(as = com.movies.explorer.ImmutablePeopleRoles.class)
public interface PeopleRoles {

    Set<String> directors();

    Set<String> screenwriters();

    Set<String> actors();

    class Builder extends com.movies.explorer.ImmutablePeopleRoles.Builder {}

    static Builder builder() {
        return new Builder();
    }
}
