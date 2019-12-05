package cn.iota.rest.api.tasks;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.flowable.common.rest.api.DataResponse;
import org.flowable.rest.service.api.runtime.task.TaskBaseResource;
import org.flowable.rest.service.api.runtime.task.TaskResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaskQueryCollection extends TaskBaseResource {

    @PostMapping(value = "/query/tasks", produces = "application/json")
    public DataResponse<TaskResponse> getQueryResult(@RequestBody TaskQueryRequestExtend request, @RequestParam Map<String, String> requestParams, HttpServletRequest httpRequest) {
        return getTasksFromQueryRequest(request, requestParams);
    }
}
