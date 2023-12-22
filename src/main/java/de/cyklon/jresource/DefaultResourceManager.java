package de.cyklon.jresource;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

final class DefaultResourceManager implements ResourceManager {

	static final ResourceManager MANAGER = new DefaultResourceManager();

	private final Map<String, Resource> resourceMap;

	public DefaultResourceManager() {
		this.resourceMap = new HashMap<>();
	}

	private String getKey(UUID uuid) {
		return resourceMap
				.entrySet()
				.stream()
				.filter(e -> e.getValue().getResourceID().equals(uuid))
				.map(Map.Entry::getKey)
				.findAny()
				.orElse(null);
	}

	@Override
	public Resource loadResource(String path) {
		return loadResource(path, path);
	}

	@Override
	public Resource loadResource(String name, String path) {
		return initResource(name, path, Resource.Type.INTERNAL);
	}

	@Override
	public Resource loadExternalResource(String name, File resource) {
		return initResource(name, resource.getPath(), Resource.Type.EXTERNAL);
	}

	@Override
	public Resource getResource(String name) {
		return Optional.ofNullable(resourceMap.get(name)).orElseThrow();
	}

	@Override
	public Resource getResource(UUID uuid) {
		return resourceMap
				.values()
				.stream()
				.filter(r -> r.getResourceID().equals(uuid))
				.findFirst()
				.orElseThrow();
	}

	@Override
	public Resource unloadResource(String name) {
		return resourceMap.remove(name);
	}

	@Override
	public Resource unloadResource(UUID uuid) {
		String key = getKey(uuid);
		if (key==null) return null;
		return resourceMap.remove(key);
	}

	@Override
	public Resource reloadResource(String name) {
		Resource resource = unloadResource(name);
		if (resource==null) return null;
		return resource.getType()== Resource.Type.INTERNAL ? loadResource(name, resource.getPath()) : loadExternalResource(name, resource.getFile());
	}

	@Override
	public Resource reloadResource(UUID uuid) {
		String key = getKey(uuid);
		if (key==null) return null;
		return reloadResource(key);
	}

	private Resource initResource(String name, String path, Resource.Type type) {
		try {
			Resource resource = new DefaultResource(name, path, type);
			resourceMap.put(name, resource);
			return resource;
		} catch (IOException | NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
}
