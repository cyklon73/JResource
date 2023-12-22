![[Java CI]](https://github.com/Cyklon73/JResource/actions/workflows/check.yml/badge.svg)
![[Latest Version]](https://maven.mineking.dev/api/badge/latest/releases/de/cyklon/JResource?prefix=v&name=Latest%20Version&color=0374b5)

# Installation

JResource is hosted on a custom repository at [https://maven.mineking.dev](https://maven.mineking.dev/#/releases/de/cyklon/JResource). Replace VERSION with the lastest version (without the `v` prefix).
Alternatively, you can download the artifacts from jitpack (not recommended).

### Gradle

```groovy
repositories {
  maven { url "https://maven.mineking.dev/releases" }
}

dependencies {
  implementation "de.cyklon:JResource:VERSION"
}
```

### Maven

```xml
<repositories>
  <repository>
    <id>mineking</id>
    <url>https://maven.mineking.dev/releases</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>de.cyklon</groupId>
    <artifactId>JResource</artifactId>
    <version>VERSION</version>
  </dependency>
</dependencies>
```