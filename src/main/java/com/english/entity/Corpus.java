package com.english.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author XYC
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Corpus {

    private String en;

    private String enText;

    private String zhText;

    public Corpus(String enText, String zhText) {
        this.enText = enText;
        this.zhText = zhText;
    }

    @Override
    public String toString() {
        return enText + "\n" + zhText;
    }
}
