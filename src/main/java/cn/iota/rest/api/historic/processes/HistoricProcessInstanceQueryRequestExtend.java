package cn.iota.rest.api.historic.processes;

import java.util.List;

import org.flowable.rest.service.api.engine.variable.QueryVariable;
import org.flowable.rest.service.api.history.HistoricProcessInstanceQueryRequest;

public class HistoricProcessInstanceQueryRequestExtend extends HistoricProcessInstanceQueryRequest{

    private List<QueryVariable> orVariables;
    
    private List<List<QueryVariable>> nestOrVariables;
 
    public List<QueryVariable> getOrVariables() {
        return orVariables;
    }

    public void setOrVariables(List<QueryVariable> orVariables) {
        this.orVariables = orVariables;
    }    

    
    public List<List<QueryVariable>> getNestOrVariables() {
        return nestOrVariables;
    }

    public void setNestOrVariables(List<List<QueryVariable>> nestOrVariables) {
        this.nestOrVariables = nestOrVariables;
    }
}
