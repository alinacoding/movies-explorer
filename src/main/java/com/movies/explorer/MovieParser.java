package com.movies.explorer;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.io.IOException;
import java.util.Arrays;
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

public class MovieParser {

    public static MovieData parseMovieData(String movieUrl, String title) throws IOException {
        Document doc = Jsoup.connect(movieUrl).get();
        Elements externalLinks = doc.select("h2 ~ ul").last().select("li a[href~=https://www.imdb.com]");
        System.out.println(externalLinks);
        String imdbUrl = externalLinks.first().attr("href");
        System.out.println(imdbUrl);
        ImdbParser imdbParser = new ImdbParser(imdbUrl);

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

        Set<String> directors = Optional.ofNullable(filteredCategoryToElement.get("Directed by"))
                .map(MovieParser::getDirectors)
                .orElse(emptySet());

        Set<String> actors = Optional.ofNullable(filteredCategoryToElement.get("Starring"))
                .map(MovieParser::getActors)
                .orElseGet(() -> imdbParser.getActors());

        Set<String> screenwriters = Sets.union(
                getWrittenBy(filteredCategoryToElement),
                getScreenplayBy(filteredCategoryToElement));

        Set<String> countries = Optional.ofNullable(filteredCategoryToElement.get("Country"))
                .map(MovieParser::getCountries)
                .orElse(emptySet());

        Set<String> genres = imdbParser.getGenres();

        PeopleRoles peopleRoles = PeopleRoles.builder()
                .actors(actors)
                .screenwriters(screenwriters)
                .directors(directors)
                .build();

        MovieData movieData = MovieData.builder()
                .title(title)
                .year(2018)
                .peopleRoles(peopleRoles)
                .genres(genres)
                .countries(countries)
                .build();

        return movieData;
    }

    private static Set<String> getWrittenBy(Map<String, Element> filteredCategoryToElement) {
        return Optional.ofNullable(filteredCategoryToElement.get("Written by"))
                .map(MovieParser::getScreenwriters)
                .orElse(emptySet());
    }

    private static Set<String> getScreenplayBy(Map<String, Element> filteredCategoryToElement) {
        return Optional.ofNullable(filteredCategoryToElement.get("Screenplay by"))
                .map(MovieParser::getScreenwriters)
                .orElse(emptySet());
    }

    private static Set<String> getActors(Element element) {
        if (element == null) {
            return null;
        }
        return element.select("li")
                .stream().map(li -> li.text())
                .collect(toSet());
    }

    private static Set<String> getDirectors(Element element) {
        if (element == null) {
            return null;
        }
        Set<String> directors = element.children().select("a").stream()
                .map(a -> a.text()).collect(toSet());
        element.getElementsByTag("a").remove();
        directors.addAll(Arrays.asList(
                element.select("td")
                        .html()
                        .split("<[^>]*>"))
                .stream()
                .filter(director -> director != null
                        && director.trim().length() > 0)
                .map(director -> director.trim())
                .collect(toSet()));
        return directors;
    }

    private static Set<String> getScreenwriters(Element element) {
        if (element == null) {
            return null;
        }
        Set<String> screenwriters = element.select("a").stream()
                .map(a -> a.text()).collect(toSet());
        element.getElementsByTag("a").remove();
        screenwriters.addAll(Arrays.asList(
                element.select("td")
                        .html()
                        .split("<[^>]*>"))
                .stream()
                .filter(screenwriter -> screenwriter != null
                        && screenwriter.trim().length() > 0)
                .collect(toSet()));
        return screenwriters;
    }

    private static Set<String> getCountries(Element element) {
        if (element == null) {
            return null;
        }
        Set<String> countries = new HashSet<>();
        if (element.select("li").size() == 0) {
            countries = Arrays.stream(
                    element.select("td")
                            .html()
                            .split("<[^>]*>"))
                    .filter(country -> country != null
                            && country.trim().length() > 0)
                    .map(country -> country.trim())
                    .collect(toSet());
        } else {
            countries = element.select("li")
                    .stream()
                    .map(li -> li.text().replaceAll("\\[.*?\\] ?", ""))
                    .collect(toSet());
        }
        return countries;
    }

}
