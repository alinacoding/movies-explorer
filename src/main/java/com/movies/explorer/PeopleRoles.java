package com.movies.explorer;

import org.immutables.value.Value;
import java.util.List;

@Value.Immutable
public interface PeopleRoles {

    List<String> directors();
    List<String> screenwriters();
    List<String> actors();

    class Builder extends com.movies.explorer.ImmutablePeopleRoles.Builder {}

    static Builder builder() {
        return new Builder();
    }


}
