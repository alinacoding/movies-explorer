package com.movies.explorer;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.io.IOException;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.collect.Sets;

public class WikipediaMovieParser {

    public static MovieData parseMovieData(String movieUrl, String title, int year) throws IOException {
        Document doc = Jsoup.connect(movieUrl).get();

        Elements tableRows = doc.select("table.infobox").first().children().select("tr");

        System.out.println(tableRows.size());
        if (tableRows.size() >= 2) {
            tableRows.remove(0);
            tableRows.remove(0);
        }

        List<Element> filteredTableRows = tableRows.stream()
                .filter(tr -> !tr.children().select("th").text().trim().isEmpty())
                .collect(toList());

        Map<String, Element> categoryToHtmlElement = filteredTableRows
                .stream()
                .collect(Collectors.toMap(
                        tableRow -> tableRow.children().select("th").text().trim(),
                        tableRow -> tableRow));
        Set<String> categoriesOfInterest = Sets.newHashSet("Directed by", "Country",
                "Starring", "Written by", "Screenplay by", "Starring", "Production company",
                "Distributed by");

        Map<String, Element> filteredCategoryToElement = categoryToHtmlElement.entrySet()
                .stream()
                .filter(entry -> categoriesOfInterest.contains(entry.getKey()))
                .collect(Collectors.toMap(entry -> entry.getKey(),
                        entry -> entry.getValue()));

        Set<String> countries = Optional.ofNullable(filteredCategoryToElement.get("Country"))
                .map(WikipediaMovieParser::getCountries)
                .orElse(emptySet());

        Set<String> companies = Sets.union(
                getProductionCompany(filteredCategoryToElement),
                getDistributedBy(filteredCategoryToElement));

        Set<String> directors = Optional.ofNullable(filteredCategoryToElement.get("Directed by"))
                .map(WikipediaMovieParser::getDirectors)
                .orElse(emptySet());

        Set<String> actors = Optional.ofNullable(filteredCategoryToElement.get("Starring"))
                .map(WikipediaMovieParser::getActors)
                // .orElseGet(() -> imdbParser.getActors());
                .orElse(emptySet());

        Set<String> screenwriters = Sets.union(
                getWrittenBy(filteredCategoryToElement),
                getScreenplayBy(filteredCategoryToElement));

        Elements externalLinks = doc.children().select("a[href~=www.imdb.com]");
        if (externalLinks.size() == 0) {
            return MovieData.builder()
                    .title(title)
                    .peopleRoles(PeopleRoles.builder()
                            .actors(actors)
                            .directors(directors)
                            .screenwriters(screenwriters)
                            .build())
                    .year(year)
                    .companies(companies)
                    .countries(countries)
                    .build();
        }

        String imdbMovieId = externalLinks.first().attr("href").split("/")[4];

        Supplier<Connection> connectionSupplier = new ConnectionSupplier();
        ImdbMovieParser imdbParser = new ImdbMovieParser(connectionSupplier, imdbMovieId);
        Set<String> genres = imdbParser.getGenres();

        if (actors.isEmpty()) {
            actors = imdbParser.getActors();
        }

        PeopleRoles peopleRoles = PeopleRoles.builder()
                .actors(actors)
                .directors(directors)
                .screenwriters(screenwriters)
                .directors(directors)
                .build();

        MovieData movieData = MovieData.builder()
                .title(title)
                .year(year)
                .peopleRoles(peopleRoles)
                .genres(genres)
                .companies(companies)
                .countries(countries)
                .build();

        return movieData;
    }

    private static Set<String> getDistributedBy(Map<String, Element> filteredCategoryToElement) {
        return Optional.ofNullable(filteredCategoryToElement.get("Distributed by"))
                .map(aTag -> aTag.select("a")
                        .stream()
                        .map(a -> a.text())
                        .collect(toSet()))
                .orElse(emptySet());
    }

    private static Set<String> getProductionCompany(Map<String, Element> filteredCategoryToElement) {
        return Optional.ofNullable(filteredCategoryToElement.get("Production company"))
                .map(element -> element.select("a")
                        .stream()
                        .map(aTag -> aTag.text())
                        .collect(toSet()))
                .orElse(emptySet());
    }

    private static Set<String> getWrittenBy(Map<String, Element> filteredCategoryToElement) {
        return Optional.ofNullable(filteredCategoryToElement.get("Written by"))
                .map(WikipediaMovieParser::getScreenwriters)
                .orElse(emptySet());
    }

    private static Set<String> getScreenplayBy(Map<String, Element> filteredCategoryToElement) {
        return Optional.ofNullable(filteredCategoryToElement.get("Screenplay by"))
                .map(WikipediaMovieParser::getScreenwriters)
                .orElse(emptySet());
    }

    private static Set<String> getActors(Element element) {
        if (element == null) {
            return Collections.emptySet();
        }
        return element.select("li")
                .stream().map(li -> li.text())
                .collect(toSet());
    }

    private static Set<String> getDirectors(Element element) {
        if (element == null) {
            return Collections.emptySet();
        }
        Set<String> directors = element.children().select("a").stream()
                .map(a -> a.text()).collect(toSet());
        element.getElementsByTag("a").remove();
        return Sets.union(directors, extractItemsFromHtmlElement(element));
    }

    private static Set<String> extractItemsFromHtmlElement(Element element) {
        Set<String> items = new HashSet<>();
        items.addAll(Arrays.stream(
                element.select("td")
                        .html()
                        .split("<[^>]*>"))
                .filter(item -> item != null
                        && item.trim().length() > 0)
                .map(item -> item.trim())
                .collect(toSet()));
        return items;
    }

    private static Set<String> getScreenwriters(Element element) {
        if (element == null) {
            return Collections.emptySet();
        }
        Set<String> screenwriters = element.select("a").stream()
                .map(a -> a.text()).collect(toSet());
        element.getElementsByTag("a").remove();
        return Sets.union(screenwriters, extractItemsFromHtmlElement(element));
    }

    private static Set<String> getCountries(Element element) {
        if (element == null) {
            return Collections.emptySet();
        }
        Set<String> countries = new HashSet<>();
        if (element.select("li").size() == 0) {
            countries = extractItemsFromHtmlElement(element);
        } else {
            countries = element.select("li")
                    .stream()
                    .map(li -> li.text().replaceAll("\\[.*?\\] ?", ""))
                    .collect(toSet());
        }
        return countries;
    }

}
