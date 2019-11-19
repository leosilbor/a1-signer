package br.com.too.a1signer.dto;

import java.util.Arrays;
import java.util.List;

public class Response<T> {
	private String status;
	private Integer code;
	private List<String> messages;
	private T data;
	
	public static <T> Response<T> success(T data) {
		return new Response<T>("ok", 200, null, data);
	}
	
	public static <T> Response<T> error(String mensagem) {
		return new Response<T>("error", 500, Arrays.asList(mensagem), null);
	}
	
	public Response () {
		
	}
	
	public Response(String status, Integer code, List<String> messages, T data) {
		super();
		this.status = status;
		this.code = code;
		this.messages = messages;
		this.data = data;
	}



	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public List<String> getMessages() {
		return messages;
	}
	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	
	
}
