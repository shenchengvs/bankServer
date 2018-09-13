package holley_server.lysw.service_http;

import holley_server.lysw.model.RequestPackage;

import holley_server.lysw.model.ResponsePackage;

public interface BankWithholdServiceHttp {


	ResponsePackage requestRegisterUser(RequestPackage rp);

	ResponsePackage cancelRegister(RequestPackage createRequest);

}
