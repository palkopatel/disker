package com.disker.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmbeddedResources {
    private List<Item> items;
    private int limit;
    private String path;
    private int offset;
    private int total;
}
