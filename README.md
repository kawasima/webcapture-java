webcapture-java
===============

A bridge to PhantomJS for capturing web pages. 

![architecture](https://farm6.staticflickr.com/5581/14215424188_057d66dcc8_z.jpg)

## Prerequisite

You need to install PhantomJS 1.9 or higher.

## Usage

Initialize WebCaptureService.

```java
final WebCaptureService service = new WebCaptureService(5);
service.setCaptureDirectory(new File("target/capture"));
```

To capture web pages, call the capture method.

```java
service.capture("http://localhost/");
```

And you must call the `shutdown` method to terminate phantomjs's prosesses.

```java
service.shutdown();
```
