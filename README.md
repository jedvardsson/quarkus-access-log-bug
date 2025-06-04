# quarkus-access-log-bug

This project reproduces a bug with Quarkus access log not logging requests to the management interface
when it's running on a separate host and port, i.e. when using `quarkus.management.enabled=true`.

Run the tests like so:
```shell script
./mvnw test
```

### Description

Here is request to a regular application resource

```
# curl http://localhost:8080/users/77
2025-06-03 09:09:13,983 INFO  [io.qua.htt.access-log] (vert.x-eventloop-thread-2) 127.0.0.1 - - [03/Jun/2025:09:09:13 +0200] "GET /users/77 HTTP/1.1" 404 501
```

And here is a request to the health check api.

```
# curl http://localhost:8080/q/health
2025-06-03 09:10:57,506 INFO  [io.qua.htt.access-log] (vert.x-eventloop-thread-1) 127.0.0.1 - - [03/Jun/2025:09:10:57 +0200] "GET /q/health HTTP/1.1" 200 2692
```

Now enable management interface on a separate port using `quarkus.management.enabled=true`. Requests to application paths are still logged.

```
# curl http://localhost:8080/users/77
2025-06-03 09:11:59,319 INFO  [io.qua.htt.access-log] (vert.x-eventloop-thread-2) 127.0.0.1 - - [03/Jun/2025:09:11:59 +0200] "GET /users/77 HTTP/1.1" 404 672
```

But not requests to the management interface (now on separate port).

```
# note the new port number
# curl http://localhost:9000/q/health
# nothing is logged
```

**Steps to reproduce**
1. Create a hello world Quarkus REST app
2. Enable logging (should be enabled by default)
3. Enable access log: `quarkus.http.access-log.enabled=true`
4. Do a health check: `curl http://localhost:8080/q/health`
5. Note that access log entry is logged
6. Enable management interface on separate port: `quarkus.management.enabled=true`
7. Do a health check: `curl http://localhost:9000/q/health`

**expected results**
An access log entry should be logged like so:
```
2025-06-03 09:10:57,506 INFO  [io.qua.htt.access-log] (vert.x-eventloop-thread-1) 127.0.0.1 - - [03/Jun/2025:09:10:57 +0200] "GET /q/health HTTP/1.1" 200 2692
```

**Actual results**
Nothing is logged for the health check request.

**Workaround**
Don't run management interface on separate port.

**Suggested fix**
Access log should be enabled on all routers. Possibly configurable for each host:port.
