package com.movies.explorer;

import static java.util.stream.Collectors.toSet;

import java.io.IOException;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ImdbParser {

    private final Document imdbDoc;

    public ImdbParser(String imdbUrl) {
        this.imdbDoc = getDocument(imdbUrl);
    }

    public Set<String> getActors() {
        return imdbDoc.select("span[itemprop~=actors]")
                .stream()
                .map(tag -> tag.text().replace(",", ""))
                .collect(toSet());
    }

    public Set<String> getGenres() {
        return imdbDoc.select("span.ghost ~ a[href*=/genre/]")
                .stream()
                .map(tag -> tag.text())
                .collect(toSet());
    }

    private Document getDocument(String imdbUrl) {
        try {
            return Jsoup.connect(imdbUrl).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
