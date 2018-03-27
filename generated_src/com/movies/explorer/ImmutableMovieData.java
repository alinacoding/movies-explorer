package com.movies.explorer;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.List;
import java.util.Objects;
import javax.annotation.CheckReturnValue;
import javax.annotation.Generated;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * Immutable implementation of {@link MovieData}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code new MovieData.Builder()}.
 */
@SuppressWarnings({"all"})
@ParametersAreNonnullByDefault
@Generated({"Immutables.generator", "MovieData"})
@Immutable
@CheckReturnValue
public final class ImmutableMovieData implements MovieData {
  private final String title;
  private final ImmutableList<String> companies;
  private final PeopleRoles peopleRoles;
  private final ImmutableList<String> genres;
  private final ImmutableList<String> countries;

  private ImmutableMovieData(
      String title,
      ImmutableList<String> companies,
      PeopleRoles peopleRoles,
      ImmutableList<String> genres,
      ImmutableList<String> countries) {
    this.title = title;
    this.companies = companies;
    this.peopleRoles = peopleRoles;
    this.genres = genres;
    this.countries = countries;
  }

  /**
   * @return The value of the {@code title} attribute
   */
  @Override
  public String title() {
    return title;
  }

  /**
   * @return The value of the {@code companies} attribute
   */
  @Override
  public ImmutableList<String> companies() {
    return companies;
  }

  /**
   * @return The value of the {@code peopleRoles} attribute
   */
  @Override
  public PeopleRoles peopleRoles() {
    return peopleRoles;
  }

  /**
   * @return The value of the {@code genres} attribute
   */
  @Override
  public ImmutableList<String> genres() {
    return genres;
  }

  /**
   * @return The value of the {@code countries} attribute
   */
  @Override
  public ImmutableList<String> countries() {
    return countries;
  }

  /**
   * Copy the current immutable object by setting a value for the {@link MovieData#title() title} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for title
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableMovieData withTitle(String value) {
    if (this.title.equals(value)) return this;
    String newValue = Objects.requireNonNull(value, "title");
    return new ImmutableMovieData(newValue, this.companies, this.peopleRoles, this.genres, this.countries);
  }

  /**
   * Copy the current immutable object with elements that replace the content of {@link MovieData#companies() companies}.
   * @param elements The elements to set
   * @return A modified copy of {@code this} object
   */
  public final ImmutableMovieData withCompanies(String... elements) {
    ImmutableList<String> newValue = ImmutableList.copyOf(elements);
    return new ImmutableMovieData(this.title, newValue, this.peopleRoles, this.genres, this.countries);
  }

  /**
   * Copy the current immutable object with elements that replace the content of {@link MovieData#companies() companies}.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param elements An iterable of companies elements to set
   * @return A modified copy of {@code this} object
   */
  public final ImmutableMovieData withCompanies(Iterable<String> elements) {
    if (this.companies == elements) return this;
    ImmutableList<String> newValue = ImmutableList.copyOf(elements);
    return new ImmutableMovieData(this.title, newValue, this.peopleRoles, this.genres, this.countries);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link MovieData#peopleRoles() peopleRoles} attribute.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for peopleRoles
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableMovieData withPeopleRoles(PeopleRoles value) {
    if (this.peopleRoles == value) return this;
    PeopleRoles newValue = Objects.requireNonNull(value, "peopleRoles");
    return new ImmutableMovieData(this.title, this.companies, newValue, this.genres, this.countries);
  }

  /**
   * Copy the current immutable object with elements that replace the content of {@link MovieData#genres() genres}.
   * @param elements The elements to set
   * @return A modified copy of {@code this} object
   */
  public final ImmutableMovieData withGenres(String... elements) {
    ImmutableList<String> newValue = ImmutableList.copyOf(elements);
    return new ImmutableMovieData(this.title, this.companies, this.peopleRoles, newValue, this.countries);
  }

  /**
   * Copy the current immutable object with elements that replace the content of {@link MovieData#genres() genres}.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param elements An iterable of genres elements to set
   * @return A modified copy of {@code this} object
   */
  public final ImmutableMovieData withGenres(Iterable<String> elements) {
    if (this.genres == elements) return this;
    ImmutableList<String> newValue = ImmutableList.copyOf(elements);
    return new ImmutableMovieData(this.title, this.companies, this.peopleRoles, newValue, this.countries);
  }

  /**
   * Copy the current immutable object with elements that replace the content of {@link MovieData#countries() countries}.
   * @param elements The elements to set
   * @return A modified copy of {@code this} object
   */
  public final ImmutableMovieData withCountries(String... elements) {
    ImmutableList<String> newValue = ImmutableList.copyOf(elements);
    return new ImmutableMovieData(this.title, this.companies, this.peopleRoles, this.genres, newValue);
  }

  /**
   * Copy the current immutable object with elements that replace the content of {@link MovieData#countries() countries}.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param elements An iterable of countries elements to set
   * @return A modified copy of {@code this} object
   */
  public final ImmutableMovieData withCountries(Iterable<String> elements) {
    if (this.countries == elements) return this;
    ImmutableList<String> newValue = ImmutableList.copyOf(elements);
    return new ImmutableMovieData(this.title, this.companies, this.peopleRoles, this.genres, newValue);
  }

  /**
   * This instance is equal to all instances of {@code ImmutableMovieData} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(@Nullable Object another) {
    if (this == another) return true;
    return another instanceof ImmutableMovieData
        && equalTo((ImmutableMovieData) another);
  }

  private boolean equalTo(ImmutableMovieData another) {
    return title.equals(another.title)
        && companies.equals(another.companies)
        && peopleRoles.equals(another.peopleRoles)
        && genres.equals(another.genres)
        && countries.equals(another.countries);
  }

  /**
   * Computes a hash code from attributes: {@code title}, {@code companies}, {@code peopleRoles}, {@code genres}, {@code countries}.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    int h = 5381;
    h += (h << 5) + title.hashCode();
    h += (h << 5) + companies.hashCode();
    h += (h << 5) + peopleRoles.hashCode();
    h += (h << 5) + genres.hashCode();
    h += (h << 5) + countries.hashCode();
    return h;
  }

  /**
   * Prints the immutable value {@code MovieData} with attribute values.
   * @return A string representation of the value
   */
  @Override
  public String toString() {
    return MoreObjects.toStringHelper("MovieData")
        .omitNullValues()
        .add("title", title)
        .add("companies", companies)
        .add("peopleRoles", peopleRoles)
        .add("genres", genres)
        .add("countries", countries)
        .toString();
  }

  /**
   * Creates an immutable copy of a {@link MovieData} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable MovieData instance
   */
  public static ImmutableMovieData copyOf(MovieData instance) {
    if (instance instanceof ImmutableMovieData) {
      return (ImmutableMovieData) instance;
    }
    return new MovieData.Builder()
        .from(instance)
        .build();
  }

  /**
   * Builds instances of type {@link ImmutableMovieData ImmutableMovieData}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  @NotThreadSafe
  public static class Builder {
    private static final long INIT_BIT_TITLE = 0x1L;
    private static final long INIT_BIT_PEOPLE_ROLES = 0x2L;
    private long initBits = 0x3L;

    private @Nullable String title;
    private ImmutableList.Builder<String> companies = ImmutableList.builder();
    private @Nullable PeopleRoles peopleRoles;
    private ImmutableList.Builder<String> genres = ImmutableList.builder();
    private ImmutableList.Builder<String> countries = ImmutableList.builder();

    /**
     * Creates a builder for {@link ImmutableMovieData ImmutableMovieData} instances.
     */
    public Builder() {
      if (!(this instanceof MovieData.Builder)) {
        throw new UnsupportedOperationException("Use: new MovieData.Builder()");
      }
    }

    /**
     * Fill a builder with attribute values from the provided {@code MovieData} instance.
     * Regular attribute values will be replaced with those from the given instance.
     * Absent optional values will not replace present values.
     * Collection elements and entries will be added, not replaced.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final MovieData.Builder from(MovieData instance) {
      Objects.requireNonNull(instance, "instance");
      title(instance.title());
      addAllCompanies(instance.companies());
      peopleRoles(instance.peopleRoles());
      addAllGenres(instance.genres());
      addAllCountries(instance.countries());
      return (MovieData.Builder) this;
    }

    /**
     * Initializes the value for the {@link MovieData#title() title} attribute.
     * @param title The value for title 
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final MovieData.Builder title(String title) {
      this.title = Objects.requireNonNull(title, "title");
      initBits &= ~INIT_BIT_TITLE;
      return (MovieData.Builder) this;
    }

    /**
     * Adds one element to {@link MovieData#companies() companies} list.
     * @param element A companies element
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final MovieData.Builder addCompanies(String element) {
      this.companies.add(element);
      return (MovieData.Builder) this;
    }

    /**
     * Adds elements to {@link MovieData#companies() companies} list.
     * @param elements An array of companies elements
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final MovieData.Builder addCompanies(String... elements) {
      this.companies.add(elements);
      return (MovieData.Builder) this;
    }

    /**
     * Sets or replaces all elements for {@link MovieData#companies() companies} list.
     * @param elements An iterable of companies elements
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final MovieData.Builder companies(Iterable<String> elements) {
      this.companies = ImmutableList.builder();
      return addAllCompanies(elements);
    }

    /**
     * Adds elements to {@link MovieData#companies() companies} list.
     * @param elements An iterable of companies elements
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final MovieData.Builder addAllCompanies(Iterable<String> elements) {
      this.companies.addAll(elements);
      return (MovieData.Builder) this;
    }

    /**
     * Initializes the value for the {@link MovieData#peopleRoles() peopleRoles} attribute.
     * @param peopleRoles The value for peopleRoles 
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final MovieData.Builder peopleRoles(PeopleRoles peopleRoles) {
      this.peopleRoles = Objects.requireNonNull(peopleRoles, "peopleRoles");
      initBits &= ~INIT_BIT_PEOPLE_ROLES;
      return (MovieData.Builder) this;
    }

    /**
     * Adds one element to {@link MovieData#genres() genres} list.
     * @param element A genres element
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final MovieData.Builder addGenres(String element) {
      this.genres.add(element);
      return (MovieData.Builder) this;
    }

    /**
     * Adds elements to {@link MovieData#genres() genres} list.
     * @param elements An array of genres elements
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final MovieData.Builder addGenres(String... elements) {
      this.genres.add(elements);
      return (MovieData.Builder) this;
    }

    /**
     * Sets or replaces all elements for {@link MovieData#genres() genres} list.
     * @param elements An iterable of genres elements
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final MovieData.Builder genres(Iterable<String> elements) {
      this.genres = ImmutableList.builder();
      return addAllGenres(elements);
    }

    /**
     * Adds elements to {@link MovieData#genres() genres} list.
     * @param elements An iterable of genres elements
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final MovieData.Builder addAllGenres(Iterable<String> elements) {
      this.genres.addAll(elements);
      return (MovieData.Builder) this;
    }

    /**
     * Adds one element to {@link MovieData#countries() countries} list.
     * @param element A countries element
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final MovieData.Builder addCountries(String element) {
      this.countries.add(element);
      return (MovieData.Builder) this;
    }

    /**
     * Adds elements to {@link MovieData#countries() countries} list.
     * @param elements An array of countries elements
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final MovieData.Builder addCountries(String... elements) {
      this.countries.add(elements);
      return (MovieData.Builder) this;
    }

    /**
     * Sets or replaces all elements for {@link MovieData#countries() countries} list.
     * @param elements An iterable of countries elements
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final MovieData.Builder countries(Iterable<String> elements) {
      this.countries = ImmutableList.builder();
      return addAllCountries(elements);
    }

    /**
     * Adds elements to {@link MovieData#countries() countries} list.
     * @param elements An iterable of countries elements
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final MovieData.Builder addAllCountries(Iterable<String> elements) {
      this.countries.addAll(elements);
      return (MovieData.Builder) this;
    }

    /**
     * Builds a new {@link ImmutableMovieData ImmutableMovieData}.
     * @return An immutable instance of MovieData
     * @throws java.lang.IllegalStateException if any required attributes are missing
     */
    public ImmutableMovieData build() {
      if (initBits != 0) {
        throw new IllegalStateException(formatRequiredAttributesMessage());
      }
      return new ImmutableMovieData(title, companies.build(), peopleRoles, genres.build(), countries.build());
    }

    private String formatRequiredAttributesMessage() {
      List<String> attributes = Lists.newArrayList();
      if ((initBits & INIT_BIT_TITLE) != 0) attributes.add("title");
      if ((initBits & INIT_BIT_PEOPLE_ROLES) != 0) attributes.add("peopleRoles");
      return "Cannot build MovieData, some of required attributes are not set " + attributes;
    }
  }
}
