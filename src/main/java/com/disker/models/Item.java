package com.disker.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public
class Item {
    private String name;
    @JsonProperty("resource_id")
    private String resourceId;
    private String path;
    private String created;
    private String modified;
    private String type;
    private int size;
    @JsonProperty("mime_type")
    private String mimeType;
    @JsonProperty("media_type")
    private String mediaType;
    private String preview;
    private String sha256;
    private String md5;

    /* fields for JavaScript */
    private String hash;
    private String id;
    private String thumb;

    @JsonProperty("_embedded")
    private EmbeddedResources embeddedResources;
}
