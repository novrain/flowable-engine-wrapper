package cn.iota.flowable.process.api.interceptors;

import java.util.List;

import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.flowable.engine.form.FormData;
import org.flowable.engine.history.HistoricActivityInstanceQuery;
import org.flowable.engine.history.HistoricDetail;
import org.flowable.engine.history.HistoricDetailQuery;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.repository.DeploymentQuery;
import org.flowable.engine.repository.Model;
import org.flowable.engine.repository.ModelQuery;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ExecutionQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceBuilder;
import org.flowable.engine.runtime.ProcessInstanceQuery;
import org.flowable.eventsubscription.api.EventSubscription;
import org.flowable.eventsubscription.api.EventSubscriptionQuery;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.GroupQuery;
import org.flowable.idm.api.User;
import org.flowable.idm.api.UserQuery;
import org.flowable.job.api.DeadLetterJobQuery;
import org.flowable.job.api.Job;
import org.flowable.job.api.JobQuery;
import org.flowable.job.api.SuspendedJobQuery;
import org.flowable.job.api.TimerJobQuery;
import org.flowable.rest.service.api.BpmnRestApiInterceptor;
import org.flowable.rest.service.api.RestResponseFactory;
import org.flowable.rest.service.api.engine.variable.QueryVariable;
import org.flowable.rest.service.api.engine.variable.QueryVariable.QueryVariableOperation;
import org.flowable.rest.service.api.form.SubmitFormRequest;
import org.flowable.rest.service.api.history.HistoricActivityInstanceQueryRequest;
import org.flowable.rest.service.api.history.HistoricDetailQueryRequest;
import org.flowable.rest.service.api.history.HistoricProcessInstanceQueryRequest;
import org.flowable.rest.service.api.history.HistoricTaskInstanceQueryRequest;
import org.flowable.rest.service.api.history.HistoricTaskLogEntryQueryRequest;
import org.flowable.rest.service.api.history.HistoricVariableInstanceQueryRequest;
import org.flowable.rest.service.api.identity.GroupRequest;
import org.flowable.rest.service.api.identity.UserRequest;
import org.flowable.rest.service.api.repository.ModelRequest;
import org.flowable.rest.service.api.runtime.process.ExecutionActionRequest;
import org.flowable.rest.service.api.runtime.process.ExecutionChangeActivityStateRequest;
import org.flowable.rest.service.api.runtime.process.ExecutionQueryRequest;
import org.flowable.rest.service.api.runtime.process.InjectActivityRequest;
import org.flowable.rest.service.api.runtime.process.ProcessInstanceCreateRequest;
import org.flowable.rest.service.api.runtime.process.ProcessInstanceQueryRequest;
import org.flowable.rest.service.api.runtime.process.SignalEventReceivedRequest;
import org.flowable.rest.service.api.runtime.task.TaskActionRequest;
import org.flowable.rest.service.api.runtime.task.TaskQueryRequest;
import org.flowable.rest.service.api.runtime.task.TaskRequest;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.flowable.task.api.history.HistoricTaskLogEntryQuery;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.flowable.variable.api.history.HistoricVariableInstanceQuery;
import org.springframework.beans.factory.annotation.Autowired;

import cn.iota.rest.api.historic.processes.HistoricProcessInstanceQueryRequestExtend;
import cn.iota.rest.api.runtime.processes.ProcessInstanceQueryRequestExtend;
import cn.iota.rest.api.tasks.TaskQueryRequestExtend;

public class FlowableRestApiInterceptor implements BpmnRestApiInterceptor {

    @Autowired
    protected RestResponseFactory restResponseFactory;

    /**
     * 这段代码没有办法从flowable复用
     * 
     * @param taskQuery
     * @param variables
     */
    private void addProcessvariables(TaskQuery taskQuery, List<QueryVariable> variables) {
        for (QueryVariable variable : variables) {
            if (variable.getVariableOperation() == null) {
                throw new FlowableIllegalArgumentException("Variable operation is missing for variable: " + variable.getName());
            }
            if (variable.getValue() == null) {
                throw new FlowableIllegalArgumentException("Variable value is missing for variable: " + variable.getName());
            }

            boolean nameLess = variable.getName() == null;

            Object actualValue = restResponseFactory.getVariableValue(variable);

            // A value-only query is only possible using equals-operator
            if (nameLess && variable.getVariableOperation() != QueryVariableOperation.EQUALS) {
                throw new FlowableIllegalArgumentException("Value-only query (without a variable-name) is only supported when using 'equals' operation.");
            }

            switch (variable.getVariableOperation()) {

            case EQUALS:
                if (nameLess) {
                    taskQuery.processVariableValueEquals(actualValue);
                } else {
                    taskQuery.processVariableValueEquals(variable.getName(), actualValue);
                }
                break;

            case EQUALS_IGNORE_CASE:
                if (actualValue instanceof String) {
                    taskQuery.processVariableValueEqualsIgnoreCase(variable.getName(), (String) actualValue);
                } else {
                    throw new FlowableIllegalArgumentException("Only string variable values are supported when ignoring casing, but was: " + actualValue.getClass().getName());
                }
                break;

            case NOT_EQUALS:
                taskQuery.processVariableValueNotEquals(variable.getName(), actualValue);
                break;

            case NOT_EQUALS_IGNORE_CASE:
                if (actualValue instanceof String) {
                    taskQuery.processVariableValueNotEqualsIgnoreCase(variable.getName(), (String) actualValue);
                } else {
                    throw new FlowableIllegalArgumentException("Only string variable values are supported when ignoring casing, but was: " + actualValue.getClass().getName());
                }
                break;

            case GREATER_THAN:
                taskQuery.processVariableValueGreaterThan(variable.getName(), actualValue);
                break;

            case GREATER_THAN_OR_EQUALS:
                taskQuery.processVariableValueGreaterThanOrEqual(variable.getName(), actualValue);
                break;

            case LESS_THAN:
                taskQuery.processVariableValueLessThan(variable.getName(), actualValue);
                break;

            case LESS_THAN_OR_EQUALS:
                taskQuery.processVariableValueLessThanOrEqual(variable.getName(), actualValue);
                break;

            case LIKE:
                if (actualValue instanceof String) {
                    taskQuery.processVariableValueLike(variable.getName(), (String) actualValue);
                } else {
                    throw new FlowableIllegalArgumentException("Only string variable values are supported using like, but was: " + actualValue.getClass().getName());
                }
                break;

            default:
                throw new FlowableIllegalArgumentException("Unsupported variable query operation: " + variable.getVariableOperation());
            }
        }
    }

    @Override
    public void accessTaskInfoWithQuery(TaskQuery taskQuery, TaskQueryRequest request) {
        if (request instanceof TaskQueryRequestExtend) {
            List<QueryVariable> orProcessInstanceVariables = ((TaskQueryRequestExtend) request).getOrProcessInstanceVariables();
            if (orProcessInstanceVariables != null && orProcessInstanceVariables.size() > 0) {
                taskQuery.or();
                addProcessvariables(taskQuery, orProcessInstanceVariables);
                taskQuery.endOr();
            }
        }
    }

    private void addVariables(ProcessInstanceQuery processInstanceQuery, List<QueryVariable> variables) {
        for (QueryVariable variable : variables) {
            if (variable.getVariableOperation() == null) {
                throw new FlowableIllegalArgumentException("Variable operation is missing for variable: " + variable.getName());
            }
            if (variable.getValue() == null) {
                throw new FlowableIllegalArgumentException("Variable value is missing for variable: " + variable.getName());
            }

            boolean nameLess = variable.getName() == null;

            Object actualValue = restResponseFactory.getVariableValue(variable);

            // A value-only query is only possible using equals-operator
            if (nameLess && variable.getVariableOperation() != QueryVariableOperation.EQUALS) {
                throw new FlowableIllegalArgumentException("Value-only query (without a variable-name) is only supported when using 'equals' operation.");
            }

            switch (variable.getVariableOperation()) {

            case EQUALS:
                if (nameLess) {
                    processInstanceQuery.variableValueEquals(actualValue);
                } else {
                    processInstanceQuery.variableValueEquals(variable.getName(), actualValue);
                }
                break;

            case EQUALS_IGNORE_CASE:
                if (actualValue instanceof String) {
                    processInstanceQuery.variableValueEqualsIgnoreCase(variable.getName(), (String) actualValue);
                } else {
                    throw new FlowableIllegalArgumentException("Only string variable values are supported when ignoring casing, but was: " + actualValue.getClass().getName());
                }
                break;

            case NOT_EQUALS:
                processInstanceQuery.variableValueNotEquals(variable.getName(), actualValue);
                break;

            case NOT_EQUALS_IGNORE_CASE:
                if (actualValue instanceof String) {
                    processInstanceQuery.variableValueNotEqualsIgnoreCase(variable.getName(), (String) actualValue);
                } else {
                    throw new FlowableIllegalArgumentException("Only string variable values are supported when ignoring casing, but was: " + actualValue.getClass().getName());
                }
                break;

            case LIKE:
                if (actualValue instanceof String) {
                    processInstanceQuery.variableValueLike(variable.getName(), (String) actualValue);
                } else {
                    throw new FlowableIllegalArgumentException("Only string variable values are supported for like, but was: " + actualValue.getClass().getName());
                }
                break;

            case GREATER_THAN:
                processInstanceQuery.variableValueGreaterThan(variable.getName(), actualValue);
                break;

            case GREATER_THAN_OR_EQUALS:
                processInstanceQuery.variableValueGreaterThanOrEqual(variable.getName(), actualValue);
                break;

            case LESS_THAN:
                processInstanceQuery.variableValueLessThan(variable.getName(), actualValue);
                break;

            case LESS_THAN_OR_EQUALS:
                processInstanceQuery.variableValueLessThanOrEqual(variable.getName(), actualValue);
                break;

            default:
                throw new FlowableIllegalArgumentException("Unsupported variable query operation: " + variable.getVariableOperation());
            }
        }
    }

    @Override
    public void accessProcessInstanceInfoWithQuery(ProcessInstanceQuery processInstanceQuery, ProcessInstanceQueryRequest request) {
        if (request instanceof ProcessInstanceQueryRequestExtend) {
            List<QueryVariable> orVariables = ((ProcessInstanceQueryRequestExtend) request).getOrVariables();
            if (orVariables != null && orVariables.size() > 0) {
                processInstanceQuery.or();
                addVariables(processInstanceQuery, orVariables);
                processInstanceQuery.endOr();
            }
        }
    }

    private void addVariables(HistoricProcessInstanceQuery processInstanceQuery, List<QueryVariable> variables) {
        for (QueryVariable variable : variables) {
            if (variable.getVariableOperation() == null) {
                throw new FlowableIllegalArgumentException("Variable operation is missing for variable: " + variable.getName());
            }
            if (variable.getValue() == null) {
                throw new FlowableIllegalArgumentException("Variable value is missing for variable: " + variable.getName());
            }

            boolean nameLess = variable.getName() == null;

            Object actualValue = restResponseFactory.getVariableValue(variable);

            // A value-only query is only possible using equals-operator
            if (nameLess && variable.getVariableOperation() != QueryVariableOperation.EQUALS) {
                throw new FlowableIllegalArgumentException("Value-only query (without a variable-name) is only supported when using 'equals' operation.");
            }

            switch (variable.getVariableOperation()) {

            case EQUALS:
                if (nameLess) {
                    processInstanceQuery.variableValueEquals(actualValue);
                } else {
                    processInstanceQuery.variableValueEquals(variable.getName(), actualValue);
                }
                break;

            case EQUALS_IGNORE_CASE:
                if (actualValue instanceof String) {
                    processInstanceQuery.variableValueEqualsIgnoreCase(variable.getName(), (String) actualValue);
                } else {
                    throw new FlowableIllegalArgumentException("Only string variable values are supported when ignoring casing, but was: " + actualValue.getClass().getName());
                }
                break;

            case NOT_EQUALS:
                processInstanceQuery.variableValueNotEquals(variable.getName(), actualValue);
                break;

            case LIKE:
                if (actualValue instanceof String) {
                    processInstanceQuery.variableValueLike(variable.getName(), (String) actualValue);
                } else {
                    throw new FlowableIllegalArgumentException("Only string variable values are supported for like, but was: " + actualValue.getClass().getName());
                }
                break;

            case LIKE_IGNORE_CASE:
                if (actualValue instanceof String) {
                    processInstanceQuery.variableValueLikeIgnoreCase(variable.getName(), (String) actualValue);
                } else {
                    throw new FlowableIllegalArgumentException("Only string variable values are supported for like, but was: " + actualValue.getClass().getName());
                }
                break;

            case GREATER_THAN:
                processInstanceQuery.variableValueGreaterThan(variable.getName(), actualValue);
                break;

            case GREATER_THAN_OR_EQUALS:
                processInstanceQuery.variableValueGreaterThanOrEqual(variable.getName(), actualValue);
                break;

            case LESS_THAN:
                processInstanceQuery.variableValueLessThan(variable.getName(), actualValue);
                break;

            case LESS_THAN_OR_EQUALS:
                processInstanceQuery.variableValueLessThanOrEqual(variable.getName(), actualValue);
                break;

            default:
                throw new FlowableIllegalArgumentException("Unsupported variable query operation: " + variable.getVariableOperation());
            }
        }
    }

    @Override
    public void accessHistoryProcessInfoWithQuery(HistoricProcessInstanceQuery historicProcessInstanceQuery, HistoricProcessInstanceQueryRequest request) {
        if (request instanceof HistoricProcessInstanceQueryRequestExtend) {
            List<QueryVariable> orVariables = ((HistoricProcessInstanceQueryRequestExtend) request).getOrVariables();
            if (orVariables != null && orVariables.size() > 0) {
                historicProcessInstanceQuery.or();
                addVariables(historicProcessInstanceQuery, orVariables);
                historicProcessInstanceQuery.endOr();
            }
        }
    }

    @Override
    public void accessTaskInfoById(Task task) {

    }

    @Override
    public void createTask(Task task, TaskRequest request) {

    }

    @Override
    public void updateTask(Task task, TaskRequest request) {

    }

    @Override
    public void deleteTask(Task task) {

    }

    @Override
    public void executeTaskAction(Task task, TaskActionRequest actionRequest) {

    }

    @Override
    public void accessExecutionInfoById(Execution execution) {

    }

    @Override
    public void accessExecutionInfoWithQuery(ExecutionQuery executionQuery, ExecutionQueryRequest request) {

    }

    @Override
    public void doExecutionActionRequest(ExecutionActionRequest executionActionRequest) {

    }

    @Override
    public void accessProcessInstanceInfoById(ProcessInstance processInstance) {

    }

    @Override
    public void createProcessInstance(ProcessInstanceBuilder processInstanceBuilder, ProcessInstanceCreateRequest request) {

    }

    @Override
    public void deleteProcessInstance(ProcessInstance processInstance) {

    }

    @Override
    public void sendSignal(SignalEventReceivedRequest signalEventReceivedRequest) {

    }

    @Override
    public void changeActivityState(ExecutionChangeActivityStateRequest changeActivityStateRequest) {

    }

    @Override
    public void migrateProcessInstance(String processInstanceId, String migrationDocument) {

    }

    @Override
    public void injectActivity(InjectActivityRequest injectActivityRequest) {

    }

    @Override
    public void accessEventSubscriptionById(EventSubscription eventSubscription) {

    }

    @Override
    public void accessEventSubscriptionInfoWithQuery(EventSubscriptionQuery eventSubscriptionQuery) {

    }

    @Override
    public void accessProcessDefinitionById(ProcessDefinition processDefinition) {

    }

    @Override
    public void accessProcessDefinitionsWithQuery(ProcessDefinitionQuery processDefinitionQuery) {

    }

    @Override
    public void accessDeploymentById(Deployment deployment) {

    }

    @Override
    public void accessDeploymentsWithQuery(DeploymentQuery deploymentQuery) {

    }

    @Override
    public void executeNewDeploymentForTenantId(String tenantId) {

    }

    @Override
    public void enhanceDeployment(DeploymentBuilder deploymentBuilder) {

    }

    @Override
    public void deleteDeployment(Deployment deployment) {

    }

    @Override
    public void accessModelInfoById(Model model) {

    }

    @Override
    public void accessModelInfoWithQuery(ModelQuery modelQuery) {

    }

    @Override
    public void createModel(Model model, ModelRequest request) {

    }

    @Override
    public void accessJobInfoById(Job job) {

    }

    @Override
    public void accessJobInfoWithQuery(JobQuery jobQuery) {

    }

    @Override
    public void accessTimerJobInfoWithQuery(TimerJobQuery jobQuery) {

    }

    @Override
    public void accessSuspendedJobInfoWithQuery(SuspendedJobQuery jobQuery) {

    }

    @Override
    public void accessDeadLetterJobInfoWithQuery(DeadLetterJobQuery jobQuery) {

    }

    @Override
    public void deleteJob(Job job) {

    }

    @Override
    public void accessManagementInfo() {

    }

    @Override
    public void accessTableInfo() {

    }

    @Override
    public void accessHistoryTaskInfoById(HistoricTaskInstance historicTaskInstance) {

    }

    @Override
    public void accessHistoryTaskInfoWithQuery(HistoricTaskInstanceQuery historicTaskInstanceQuery, HistoricTaskInstanceQueryRequest request) {

    }

    @Override
    public void deleteHistoricTask(HistoricTaskInstance historicTaskInstance) {

    }

    @Override
    public void accessHistoryProcessInfoById(HistoricProcessInstance historicProcessInstance) {

    }

    @Override
    public void deleteHistoricProcess(HistoricProcessInstance historicProcessInstance) {

    }

    @Override
    public void accessHistoryActivityInfoWithQuery(HistoricActivityInstanceQuery historicActivityInstanceQuery, HistoricActivityInstanceQueryRequest request) {

    }

    @Override
    public void accessHistoryDetailById(HistoricDetail historicDetail) {

    }

    @Override
    public void accessHistoryDetailInfoWithQuery(HistoricDetailQuery historicDetailQuery, HistoricDetailQueryRequest request) {

    }

    @Override
    public void accessHistoryVariableInfoById(HistoricVariableInstance historicVariableInstance) {

    }

    @Override
    public void accessHistoryVariableInfoWithQuery(HistoricVariableInstanceQuery historicVariableInstanceQuery, HistoricVariableInstanceQueryRequest request) {

    }

    @Override
    public void accessHistoricTaskLogWithQuery(HistoricTaskLogEntryQuery historicTaskLogEntryQuery, HistoricTaskLogEntryQueryRequest request) {

    }

    @Override
    public void accessGroupInfoById(Group group) {

    }

    @Override
    public void accessGroupInfoWithQuery(GroupQuery groupQuery) {

    }

    @Override
    public void createGroup(GroupRequest groupRequest) {

    }

    @Override
    public void deleteGroup(Group group) {

    }

    @Override
    public void accessUserInfoById(User user) {

    }

    @Override
    public void accessUserInfoWithQuery(UserQuery userQuery) {

    }

    @Override
    public void createUser(UserRequest userRequest) {

    }

    @Override
    public void deleteUser(User user) {

    }

    @Override
    public void accessFormData(FormData formData) {

    }

    @Override
    public void submitFormData(SubmitFormRequest formRequest) {

    }

}
