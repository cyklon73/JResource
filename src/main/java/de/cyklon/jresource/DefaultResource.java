package de.cyklon.jresource;

import java.io.*;
import java.math.BigInteger;
import java.net.URL;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

final class DefaultResource implements Resource {
	private final String name;
	private final String path;
	private final Type type;
	private final UUID uuid;
	private final String fileName;
	private final File file;
	private final URL url;
	private final String hash;
	private final byte[] bytes;


	public DefaultResource(String name, String path, Type type) throws IOException, NoSuchAlgorithmException {
		this(name, path, type, UUID.randomUUID());
	}

	public DefaultResource(String name, String path, Type type, UUID uuid) throws IOException, NoSuchAlgorithmException {
		this.name = name;
		this.path = path;
		this.type = type;
		this.uuid = uuid;
		this.file = new File(path);
		this.fileName = file.getName();
		this.url = type==Type.INTERNAL ? getClass().getClassLoader().getResource(path) : file.toURI().toURL();

		try (InputStream in = type==Type.INTERNAL ? getClass().getClassLoader().getResourceAsStream(path) : new FileInputStream(file)) {
			if (in==null) throw new FileNotFoundException("Resource file from resource " + name + " (" + uuid + ") not found. resource not loaded");
			try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				in.transferTo(out);
				this.bytes = out.toByteArray();
			}
		}

		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(bytes);
		this.hash = new BigInteger(1, md.digest()).toString(16);

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
		return fileName;
	}

	@Override
	public File getFile() {
		return file;
	}

	@Override
	public URL getURL() {
		return url;
	}

	@Override
	public InputStream getInputStream() {
		return new ByteArrayInputStream(bytes);
	}

	@Override
	public UUID getResourceID() {
		return uuid;
	}

	@Override
	public String getHash() {
		return hash;
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
	public byte[] getBytes() {
		return bytes;
	}
}
