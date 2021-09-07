package base.service.frameworks.rpc.common;

public class MessageResponse {
    private String requestId;
    private int errCode;
    private String errMsg;
    private String data;

	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public int getErrCode() {
		return errCode;
	}

	public void setErrCode(int errCode) {
		this.errCode = errCode;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	
	public boolean isSuccess(){
		return this.errCode == 0;
	}

	@Override
	public String toString(){
		return "MessageRequest[requestId=" +
				requestId +
				", errCode=" +
				errCode +
				", errMsg=" +
				errMsg +
				", data=" +
				data +
				"]";
	}
}
