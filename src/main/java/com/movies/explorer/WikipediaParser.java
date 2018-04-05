package com.movies.explorer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WikipediaParser {

    public static void main(String[] args) throws IOException {

        Set<MovieData> movies = getMovies();
        movies.forEach(movie -> {
            System.out.println(movie.title());
            System.out.println(movie.companies());
            System.out.println(movie.peopleRoles().actors());
            System.out.println(movie.peopleRoles().directors());
            System.out.println(movie.peopleRoles().screenwriters());
            System.out.println(movie.countries());
            System.out.println(movie.genres());
        });
        System.out.println(movies.size());
    }

    public static Set<MovieData> getMovies() throws IOException {
        Set<MovieData> movies = new HashSet<>();
        Document doc = Jsoup.connect("https://en.wikipedia.org/wiki/2000_in_film").get();
        Elements wikiTables = doc.select("table.wikitable");
        int numTables = wikiTables.size();
        System.out.println(numTables);
        for (int tableIndex = numTables - 1; tableIndex >= numTables - 4; tableIndex--) {
            Element wikiTable = wikiTables.get(tableIndex);
            Elements tableRows = wikiTable.children().select("tr");
            tableRows.remove(0);
            for (Element tableRow : tableRows) {
                Elements cells = tableRow.children().select("td");
                if ((cells.first().select("[rowspan]").size() > 0) || (cells.first().select("[style]").size() > 0)) {
                    cells.remove(cells.first());
                }
                String endpoint = cells.get(0).select("a").attr("href");
                if (endpoint == null || endpoint.isEmpty()) {
                    continue;
                }
                String title = cells.get(0).text().replaceAll("\\p{P}", "");
                System.out.println(title);
                System.out.println(endpoint);
                String movieUrl = "https://en.wikipedia.org" + endpoint;
                System.out.println(movieUrl);
                MovieData movieData = WikipediaMovieParser.parseMovieData(movieUrl, title);
                movies.add(movieData);
            }
        }
        return movies;

    }
}
