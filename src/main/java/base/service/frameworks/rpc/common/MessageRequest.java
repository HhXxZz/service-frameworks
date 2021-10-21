package base.service.frameworks.rpc.common;

import base.service.frameworks.misc.Parameters;

import java.util.Map;

public class MessageRequest {
    private String requestId;
    private String serviceName;
	private String api;
	private Parameters params;

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getApi() {
		return api;
	}

	public void setApi(String api) {
		this.api = api;
	}

	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}


	public Parameters getParams() {
		return params;
	}

	public void setParams(Parameters params) {
		this.params = params;
	}

	@Override
	public String toString(){
		return "MessageRequest[requestId=" +
				this.requestId +
				", serviceName=" +
				serviceName +
				", api=" +
				api +
				", parameters=" +
				params.toString() +
				"]";
	}

}