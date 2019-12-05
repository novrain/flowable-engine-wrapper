package cn.iota.rest.api.tasks;

import java.util.List;

import org.flowable.rest.service.api.engine.variable.QueryVariable;
import org.flowable.rest.service.api.runtime.task.TaskQueryRequest;

public class TaskQueryRequestExtend extends TaskQueryRequest {

    private List<QueryVariable> orProcessInstanceVariables;

    public List<QueryVariable> getOrProcessInstanceVariables() {
        return orProcessInstanceVariables;
    }

    public void setOrProcessInstanceVariables(List<QueryVariable> orProcessInstanceVariables) {
        this.orProcessInstanceVariables = orProcessInstanceVariables;
    }
}
