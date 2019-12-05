package cn.iota.rest.api.historic.processes;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.flowable.common.rest.api.DataResponse;
import org.flowable.rest.service.api.history.HistoricProcessInstanceBaseResource;
import org.flowable.rest.service.api.history.HistoricProcessInstanceResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HistoricProcessInstanceQueryCollection extends HistoricProcessInstanceBaseResource {

    @PostMapping(value = "/query/historic-process-instances", produces = "application/json")
    public DataResponse<HistoricProcessInstanceResponse> queryProcessInstances(@RequestBody HistoricProcessInstanceQueryRequestExtend queryRequest,
            @RequestParam Map<String, String> allRequestParams, HttpServletRequest request) {
        return getQueryResponse(queryRequest, allRequestParams);
    }
}
