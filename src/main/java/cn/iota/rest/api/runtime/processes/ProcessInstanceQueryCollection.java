package cn.iota.rest.api.runtime.processes;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.flowable.common.rest.api.DataResponse;
import org.flowable.rest.service.api.runtime.process.BaseProcessInstanceResource;
import org.flowable.rest.service.api.runtime.process.ProcessInstanceResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProcessInstanceQueryCollection extends BaseProcessInstanceResource {
    @PostMapping(value = "/query/process-instances", produces = "application/json")
    public DataResponse<ProcessInstanceResponse> queryProcessInstances(@RequestBody ProcessInstanceQueryRequestExtend queryRequest, @RequestParam Map<String, String> allRequestParams,
            HttpServletRequest request) {
        return getQueryResponse(queryRequest, allRequestParams);
    }
}
