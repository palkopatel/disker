package com.disker.controller;

import com.disker.config.DiskRestClient;
import com.disker.models.Item;
import com.disker.service.ItemService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@Log4j2
@CrossOrigin(origins = "http://localhost:8080")
@RequestMapping("disk")
@RestController
public class ResourcesController {

    @Autowired
    @Lazy
    private DiskRestClient restClient;
    @Autowired
    private ItemService itemService;

    @GetMapping(path = "{id}")
    public @ResponseBody byte[] getItem(@PathVariable("id") String id, HttpServletResponse response) throws IOException {
        log.info("getItem {}", id);
        itemService.loading(id);
        response.setHeader(HttpHeaders.CACHE_CONTROL,
                CacheControl.maxAge(1, TimeUnit.DAYS)
                        .cachePublic()
                        .getHeaderValue());
        try (FileInputStream in = new FileInputStream("cache/" + id)) {
            return in.readAllBytes();
        }
    }


    @PostMapping(path = "path", consumes = { APPLICATION_JSON_VALUE, TEXT_PLAIN_VALUE})
    public Item postResources(@RequestBody String path) {
        log.info("Load resources by path : {}", path);
        Item resourcesList = restClient.getResources(path);
        log.trace("Response : {}", resourcesList);
        List<Item> items = resourcesList.getEmbeddedResources().getItems();
        items.stream()
                .filter(item -> "dir".equals(item.getType()))
                .forEach(item -> item.setType("folder"));
        items.forEach(item -> {
                    item.setHash(item.getResourceId());
                    item.setId(item.getPath());
                    if (item.getPreview() != null && !item.getPreview().isEmpty()) {
                        item.setThumb("disk/" + item.getSha256());
                    }
                    itemService.put(item);
                });
        return resourcesList;
    }


}
