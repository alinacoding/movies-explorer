package com.movies.explorer;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.collect.Sets;

public class WikipediaMovieParser {

    public static MovieData parseMovieData(String movieUrl, String title) throws IOException {
        Document doc = Jsoup.connect(movieUrl).get();
        Elements externalLinks = doc.select("h2 ~ ul").last().select("li a[href~=https://www.imdb.com]");
        System.out.println(externalLinks);
        String imdbUrl = externalLinks.first().attr("href");
        System.out.println(imdbUrl);
        ImdbMovieParser imdbParser = new ImdbMovieParser(imdbUrl);

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
        List<String> categoriesOfInterest = Arrays.asList("Directed by", "Country",
                "Starring", "Written by", "Screenplay by", "Starring");
        Map<String, Element> filteredCategoryToElement = categoryToHtmlElement.entrySet()
                .stream()
                .filter(entry -> categoriesOfInterest.contains(entry.getKey()))
                .collect(Collectors.toMap(entry -> entry.getKey(),
                        entry -> entry.getValue()));
        Set<String> countries = Optional.ofNullable(filteredCategoryToElement.get("Country"))
                .map(WikipediaMovieParser::getCountries)
                .orElse(emptySet());

        Set<String> genres = imdbParser.getGenres();

        PeopleRoles peopleRoles = resolvePeopleRoles(filteredCategoryToElement, imdbParser);

        MovieData movieData = MovieData.builder()
                .title(title)
                .year(2018)
                .peopleRoles(peopleRoles)
                .genres(genres)
                .countries(countries)
                .build();

        return movieData;
    }

    private static PeopleRoles resolvePeopleRoles(Map<String, Element> filteredCategoryToElement,
            ImdbMovieParser imdbParser) {

        Set<String> directors = Optional.ofNullable(filteredCategoryToElement.get("Directed by"))
                .map(WikipediaMovieParser::getDirectors)
                .orElse(emptySet());

        Set<String> actors = Optional.ofNullable(filteredCategoryToElement.get("Starring"))
                .map(WikipediaMovieParser::getActors)
                .orElseGet(() -> imdbParser.getActors());

        Set<String> screenwriters = Sets.union(
                getWrittenBy(filteredCategoryToElement),
                getScreenplayBy(filteredCategoryToElement));

        PeopleRoles peopleRoles = PeopleRoles.builder()
                .actors(actors)
                .screenwriters(screenwriters)
                .directors(directors)
                .build();

        return peopleRoles;

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
                .filter(person -> person != null
                        && person.trim().length() > 0)
                .map(person -> person.trim())
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
