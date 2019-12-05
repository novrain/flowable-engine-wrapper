package cn.iota.rest.api.runtime.processes;

import java.util.List;

import org.flowable.rest.service.api.engine.variable.QueryVariable;
import org.flowable.rest.service.api.runtime.process.ProcessInstanceQueryRequest;

public class ProcessInstanceQueryRequestExtend extends ProcessInstanceQueryRequest {
    private List<QueryVariable> orVariables;

    public List<QueryVariable> getOrVariables() {
        return orVariables;
    }

    public void setOrVariables(List<QueryVariable> orVariables) {
        this.orVariables = orVariables;
    }
}
