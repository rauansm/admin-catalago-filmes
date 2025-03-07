package com.codelabs.admin.catalago.domain.video;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Rating {

    ER("ER"),
    L("L"),
    AGE_10("10"),
    AGE_12("12"),
    AGE_14("14"),
    AGE_16("16"),
    AGE_18("18");

    private final String name;

    Rating(final String name) {
        this.name = name;
    }

    public static Rating entryOf(final String name) {
        return Arrays.stream(values())
                .filter(rating -> rating.name.equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(String.format("Rating %s Unknown.", name)));
    }
}
