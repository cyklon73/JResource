package de.cyklon.jresource;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

final class DefaultResourceManager implements ResourceManager {

	private final Map<String, Resource> resourceMap;

	public DefaultResourceManager() {
		this.resourceMap = new HashMap<>();
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
		return resourceMap.get(name);
	}

	@Override
	public Resource unloadResource(String name) {
		return resourceMap.remove(name);
	}

	@Override
	public Resource reloadResource(String name) {
		Resource resource = unloadResource(name);
		if (resource==null) return null;
		return resource.getType()== Resource.Type.INTERNAL ? loadResource(name, resource.getPath()) : loadExternalResource(name, resource.getFile());
	}

	private Resource initResource(String name, String path, Resource.Type type) {
		Resource r = new DefaultResource(this, name, path, type);
		resourceMap.put(name, r);
		return r;
	}
}
