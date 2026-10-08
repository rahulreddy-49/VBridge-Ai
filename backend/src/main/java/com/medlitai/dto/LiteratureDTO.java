package com.medlitai.dto;

public class LiteratureDTO {
    private Long id;
    private String title;
    private String authors;
    private Integer publicationYear;
    private String journal;
    private String abstractText;
    private String topic;
    private String sourceType;
    private Integer relevanceScore;

    public LiteratureDTO() {
    }

    public LiteratureDTO(Long id, String title, String authors, Integer publicationYear,
                         String journal, String abstractText, String topic, String sourceType,
                         Integer relevanceScore) {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.publicationYear = publicationYear;
        this.journal = journal;
        this.abstractText = abstractText;
        this.topic = topic;
        this.sourceType = sourceType;
        this.relevanceScore = relevanceScore;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    public String getJournal() {
        return journal;
    }

    public void setJournal(String journal) {
        this.journal = journal;
    }

    public String getAbstractText() {
        return abstractText;
    }

    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public Integer getRelevanceScore() {
        return relevanceScore;
    }

    public void setRelevanceScore(Integer relevanceScore) {
        this.relevanceScore = relevanceScore;
    }
}
