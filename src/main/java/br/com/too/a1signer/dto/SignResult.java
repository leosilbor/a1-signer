package br.com.too.a1signer.dto;

public class SignResult {
	private String signature;
	private String certChain;
	
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public String getCertChain() {
		return certChain;
	}
	public void setCertChain(String certChain) {
		this.certChain = certChain;
	}
	
	
	
	
}
