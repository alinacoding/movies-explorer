package com.movies.explorer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.annotation.concurrent.Immutable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WikipediaParser {


    public static void main(String[] args) throws IOException {

        Document doc = Jsoup.connect("https://en.wikipedia.org/wiki/2018_in_film").get();
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
                //System.out.println(cells);
                String title = cells.get(0).text();
                String company = cells.get(1).text();
                resolvePeopleRoles(cells.get(2));

//                MovieData movieData = MovieData.builder()
//                    .build();

            }
        }
    }

    private static void resolvePeopleRoles(Element cell) {
        String cellInfo = cell.text();
        int lastSeparator = cellInfo.lastIndexOf(";");
        if (lastSeparator == -1) {
            return;
        }
        List<String> actors = Arrays.asList(cellInfo.substring(lastSeparator+1).split(","));
        String[] directorsAndScreenPlayers = cellInfo.substring(0, lastSeparator).split("[,;]");
        List<String> directors = new ArrayList<>();
        List<String> screenwriters = new ArrayList<>();

        Pattern regex = Pattern.compile("\\((.*?)\\)");
        List<String> currentRoles = new ArrayList<>();
        int currentPersonIndex = directorsAndScreenPlayers.length - 1;
        while (currentPersonIndex >= 0) {
            String nameAndRole = directorsAndScreenPlayers[currentPersonIndex];
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
                if (currentRole.startsWith("director") || currentRole.equals("co-director")) {
                    directors.add(name);
                }
                if (currentRole.equals("screenplay")) {
                    screenwriters.add(name);
                }
            }
            currentPersonIndex--;
        }

        System.out.println("Directors: ");
        System.out.println(directors);
        System.out.println("Screenwriters");
        System.out.println(screenwriters);

    }
}
