package com.english.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @author XYC
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Dictionary {

    private String en;

    private String zh;

    private List<Corpus> corpusList;

    public Dictionary(String en, String zh) {
        this.en = en;
        this.zh = zh;
    }

    public void addCorpus(Corpus corpus) {
        corpusList.add(corpus);
    }
}
