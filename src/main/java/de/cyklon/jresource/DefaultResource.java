package de.cyklon.jresource;

import java.io.*;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

final class DefaultResource implements Resource {

	private final ResourceManager manager;
	private final String name;
	private final String path;
	private final Type type;
	private final UUID uuid;


	public DefaultResource(ResourceManager manager, String name, String path, Type type) {
		this(manager, name, path, type, UUID.randomUUID());
	}

	public DefaultResource(ResourceManager manager, String name, String path, Type type, UUID uuid) {
		this.manager = manager;
		this.name = name;
		this.path = path;
		this.type = type;
		this.uuid = uuid;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getFileName() {
		return getFile().getName();
	}

	@Override
	public File getFile() {
		return new File(getPath());
	}

	@Override
	public URL getURL() throws MalformedURLException {
		return getType()==Type.INTERNAL ? getClass().getClassLoader().getResource(getPath()) : getFile().toURI().toURL();
	}

	@Override
	public InputStream getInputStream() throws FileNotFoundException {
		InputStream in = getType()==Type.INTERNAL ? getClass().getClassLoader().getResourceAsStream(getPath()) : new FileInputStream(getFile());
		if (in==null) {
			manager.unloadResource(getName());
			throw new FileNotFoundException("Resource file from resource " + getName() + " (" + getResourceID() + ") not found. this resource is unloaded");
		}
		return in;
	}

	@Override
	public UUID getResourceID() {
		return uuid;
	}

	@Override
	public String getHash() throws IOException {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(getBytes());
			return String.format("%032x", new BigInteger(1, md.digest()));
		} catch (NoSuchAlgorithmException ignored) {}
		return null;
	}

	@Override
	public void export(File file) throws IOException {
		export(file, false);
	}

	@Override
	public void export(File file, boolean replace) throws IOException {
		if (!replace && file.exists()) return;
		try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(file.toPath()))) {
			out.write(getBytes());
		}
	}

	@Override
	public byte[] getBytes() throws IOException {
		byte[] bytes;
		try (InputStream in = getInputStream(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			byte[] buffer = new byte[4096];
			int length;
			while ((length = in.read(buffer)) >= 0) out.write(buffer, 0, length);
			bytes = out.toByteArray();
		}
		return bytes;
	}
}
