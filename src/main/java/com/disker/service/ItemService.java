package com.disker.service;

import com.disker.config.DiskRestClient;
import com.disker.models.Item;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
@Service
public class ItemService {
    @Autowired
    @Lazy
    private DiskRestClient restClient;
    private Map<String, Item> itemsCache = new ConcurrentHashMap<>();

    @PostConstruct
    private void init() {
        log.info("Init cache directory: {}", new File("cache").mkdir());
    }

    public void put(Item item) {
        itemsCache.put(item.getResourceId(), item);
    }

    public void loading(String id) {
        Item item = itemsCache.entrySet().stream()
                .filter(e -> id.equals(e.getValue().getSha256()))
                .map(e -> e.getValue())
                .findFirst()
                .orElse(null);
        if (item == null || item.getPreview() == null || item.getPreview().isEmpty()) {
            return;
        }
        log.debug("Item {} preview URI={}", id, item.getPreview());
        File localFile = new File("cache/" + id);
        if (localFile.exists()) {
            return;
        }
        File newFile = restClient.downloadFile(item.getPreview(), localFile);
        log.info("Item {} downloaded={}", id, newFile.length());
    }

}
