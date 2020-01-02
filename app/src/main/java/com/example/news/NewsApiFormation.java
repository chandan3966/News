package com.example.news;

public class NewsApiFormation {

    private final String URI1 = "https://newsapi.org/v2/top-headlines?country=";
    private final String URI2 = "&apiKey=5a92e7d12a834156a3521b603a95a3af";
    private final String country;

    public NewsApiFormation(String country) {
        this.country = country;
    }

    public String getCountry() {
        return country;
    }

    public String getApi(){
        return URI1+country+URI2;
    }
}
