package com.tripplannertrip.model;

import lombok.Getter;

@Getter
public enum DateSortType {
    DATE_ASC("date_asc"),
    DATE_DESC("date_desc");

    private final String value;

    DateSortType(String value) {
        this.value = value;
    }

}
