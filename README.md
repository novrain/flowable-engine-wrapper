# flowable-engine-wrapper

flowable-engine SpringBoot 包装

1. 包装启动flowale-engine REST接口
2. 实现BpmnRestApiInterceptor

## mvn settings

[./mvn/settings](./mvn/settings)

# flowable

[http://build.chuanyang100.com:8280/iota/flowable-engine/tree/flowable-release-6.4.2](http://build.chuanyang100.com:8280/iota/flowable-engine/tree/flowable-release-6.4.2)

```
mvn deploy -Dmaven.test.skip=true -DaltDeploymentRepository=iota::default::http://build.chuanyang100.com:8180/repository/internal/
```