package base.service.frameworks.rpc.common;

import java.util.Map;

public class MessageRequest {
    private String requestId;
    private String module;
    private String action;
    private Map<String, String> parameters;

	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public Map<String, String> getParameters() {
		return parameters;
	}
	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}
	
	@Override
	public String toString(){
		return "MessageRequest[requestId=" +
				requestId +
				", module=" +
				module +
				", action=" +
				action +
				", parameters=" +
				parameters +
				"]";
	}

}