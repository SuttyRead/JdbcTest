package com.ua.sutty.jdbc.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Role {

    public static final String ID = "id";
    public static final String NAME = "name";

    private Long id;
    private String name;

}
