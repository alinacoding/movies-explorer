package com.movies.explorer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WikipediaParser {

    public static void main(String[] args) throws IOException {
        List<MovieData> movies = getMoviesForYear(2018);
        movies.forEach(movie -> {
            System.out.println(movie.title());
            System.out.println(movie.companies());
            System.out.println(movie.peopleRoles().actors());
            System.out.println(movie.peopleRoles().directors());
            System.out.println(movie.peopleRoles().screenwriters());
            System.out.println(movie.countries());
            System.out.println(movie.genres());
        });
    }

    public static List<MovieData> getMoviesForYear(int year) throws IOException {
        List<MovieData> movies = new ArrayList<>();
        //Document doc = Jsoup.connect("https://en.wikipedia.org/wiki/" + year + "_in_film").get();
        File input = new File("temp/" + year + ".html");
        Document doc = Jsoup.parse(input, "UTF-8");
        Elements wikiTables = doc.select("table.wikitable").not(".sortable").not("[style]");
        for (Element wikiTable : wikiTables) {
            Elements tableRows = wikiTable.children().select("tr");
            tableRows.remove(0);
            for (Element tableRow : tableRows) {
                Elements cells = tableRow.children().select("td");
                cells.remove(cells.first());
                cells.remove(cells.last());
                if (cells.size() < 5) {
                    continue;
                }
                String title = cells.get(0).text().replaceAll("\\p{P}", "");
                List<String> companies = parseCellText(cells.get(1).text(), "/");
                PeopleRoles peopleRoles = resolvePeopleRoles(cells.get(2));
                if (peopleRoles == null) {
                    continue;
                }
                List<String> genres = parseCellText(cells.get(3).text(), ",");
                List<String> countries = parseCellText(cells.get(4).text(), ",");

                MovieData movieData = MovieData.builder()
                        .title(title)
                        .companies(companies)
                        .peopleRoles(peopleRoles)
                        .genres(genres)
                        .countries(countries)
                        .build();
                movies.add(movieData);
            }
        }

        return movies;
    }

    private static List<String> parseCellText(String cellText, String separator) {
       return Arrays.asList(cellText.split(separator))
               .stream()
               .map(item -> item.trim())
               .map(item -> item.replaceAll("'", "\'"))
               .collect(Collectors.toList());
    }

    private static PeopleRoles resolvePeopleRoles(Element cell) {
        String cellInfo = cell.text();
        if (cellInfo.charAt(cellInfo.length() - 1) == ';') {
            cellInfo = cellInfo.substring(0, cellInfo.length() - 1);
        }
        int lastSeparator = cellInfo.lastIndexOf(";");
        if (lastSeparator == -1) {
            return null;
        }
        List<String> actors = parseCellText(cellInfo.substring(lastSeparator + 1), ",");

        List<String> directorsAndScreenwriters = parseCellText(cellInfo.substring(0, lastSeparator), "[,;]");

        List<String> directors = new ArrayList<>();
        List<String> screenwriters = new ArrayList<>();

        Pattern regex = Pattern.compile("\\((.*?)\\)");
        List<String> currentRoles = new ArrayList<>();
        int currentPersonIndex = directorsAndScreenwriters.size() - 1;
        while (currentPersonIndex >= 0) {
            String nameAndRole = directorsAndScreenwriters.get(currentPersonIndex);
            String name = nameAndRole.split("\\(")[0].trim();
            Matcher matcher = regex.matcher(nameAndRole);
            if (matcher.find()) {
                String role = matcher.group(1).trim();
                if (role.contains("/")) {
                    currentRoles = Arrays.asList(role.split("/"));
                } else {
                    currentRoles = Arrays.asList(role);
                }
            }
            for (String currentRole : currentRoles) {
                if (currentRole.startsWith("director") || currentRole.endsWith("director")) {
                    directors.add(name);
                }
                if (currentRole.equals("screenplay")) {
                    screenwriters.add(name);
                }
            }
            currentPersonIndex--;
        }

        PeopleRoles peopleRoles = PeopleRoles.builder()
                    .actors(actors)
                    .directors(directors)
                    .screenwriters(screenwriters)
                    .build();

        return peopleRoles;

    }
}
