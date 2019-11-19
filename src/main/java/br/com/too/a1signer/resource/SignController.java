package br.com.too.a1signer.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.too.a1signer.dto.Response;
import br.com.too.a1signer.dto.SignResult;
import br.com.too.a1signer.service.SignService;

@RestController
@RequestMapping("/sign-a1/api/v1")
public class SignController {
	@Autowired
	private SignService service;
	
	@GetMapping("/sign/{hash}")
    public Response<SignResult> signMd5(@PathVariable("hash") String hash) {
        return sign("MD5", hash);
    }
	
	@GetMapping("/sign/{algo}/{hash}")
    public Response<SignResult> sign(@PathVariable("algo") String algo, @PathVariable("hash") String hash) {
        try {
			SignResult result = service.sign(algo, hash);
			return Response.success(result);
		} catch (Exception e) {
			e.printStackTrace();
			return Response.error(e.getMessage());
		}
    }
	
	@GetMapping("/algos")
    public Response<String[]> listAlgos() {
        try {
			String[] algos = service.listAlgos();
			return Response.success(algos);
		} catch (Exception e) {
			e.printStackTrace();
			return Response.error(e.getMessage());
		}
    }
	
}
