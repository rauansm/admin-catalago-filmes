package com.codelabs.admin.catalago.infrastructure.storing.service;

import com.codelabs.admin.catalago.domain.video.Resource;

import java.util.List;
import java.util.Optional;

public interface StorageService {

    void store(final String key, final Resource resource);

    Optional<Resource> get(String id);

    List<String> list(String prefix);

    void deleteAll(final List<String> ids);
}
