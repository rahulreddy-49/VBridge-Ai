package com.medlitai.model;

import jakarta.persistence.*;

@Entity
@Table(name = "literature")
public class Literature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(nullable = false, length = 500)
    private String authors;

    @Column(name = "publication_year", nullable = false)
    private Integer publicationYear;

    @Column(nullable = false, length = 255)
    private String journal;

    @Column(name = "abstract_text", nullable = false, columnDefinition = "TEXT")
    private String abstractText;

    @Column(nullable = false, length = 100)
    private String topic;

    @Column(name = "source_type", nullable = false, length = 100)
    private String sourceType = "Demo Literature Record";

    public Literature() {
    }

    public Literature(String title, String authors, Integer publicationYear, String journal,
                      String abstractText, String topic, String sourceType) {
        this.title = title;
        this.authors = authors;
        this.publicationYear = publicationYear;
        this.journal = journal;
        this.abstractText = abstractText;
        this.topic = topic;
        this.sourceType = sourceType != null ? sourceType : "Demo Literature Record";
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
}
