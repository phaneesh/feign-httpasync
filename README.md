# Feign HttpAsyncClient [![Travis build status](https://travis-ci.org/phaneesh/feign-httpasync.svg?branch=master)](https://travis-ci.org/phaneesh/dropwizard-maxmind-bundle)

This bundle adds MaxMind GeoIP2 support for dropwizard.
This bundle compiles only on Java 8.

## Dependencies
* OpenFeign 9.5.1
* Apache HTTP Async Client

## Usage
Just pass the default CloseableHttpAsyncClient or pass a custom created client to FeignBuilder and you are done 

### Build instructions
  - Clone the source:

        git clone github.com/phaneesh/feign-httpasync

  - Build

        mvn install

### Maven Dependency
Use the following repository:
```xml
<repository>
    <id>clojars</id>
    <name>Clojars repository</name>
    <url>https://clojars.org/repo</url>
</repository>
```
Use the following maven dependency:
```xml
<dependency>
    <groupId>io.github.openfeign</groupId>
    <artifactId>feign-httpasync</artifactId>
    <version>0.0.1</version>
</dependency>
```

### Using Feign HttpAsync

```java
    GitHub github = Feign.builder()
                         .client(new CloseableHttpAsyncClient())
                         .target(GitHub.class, "https://api.github.com");
```

LICENSE
-------

Copyright 2016 Phaneesh Nagaraja <phaneesh.n@gmail.com>.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
