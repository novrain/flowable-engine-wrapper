package cn.iota.rest.api.historic.processes;

import java.util.List;

import org.flowable.rest.service.api.engine.variable.QueryVariable;
import org.flowable.rest.service.api.history.HistoricProcessInstanceQueryRequest;

public class HistoricProcessInstanceQueryRequestExtend extends HistoricProcessInstanceQueryRequest{

    private List<QueryVariable> orVariables;

    public List<QueryVariable> getOrVariables() {
        return orVariables;
    }

    public void setOrVariables(List<QueryVariable> orVariables) {
        this.orVariables = orVariables;
    }
}
