package br.com.too.a1signer.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPrivateKey;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import br.com.too.a1signer.dto.SignResult;
import sun.security.rsa.RSACore;
import sun.security.rsa.RSAKeyFactory;
import sun.security.rsa.RSAPadding;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.AlgorithmId;

@Service
public class SignService {
	
	private RSAPadding padding;
	private RSAPrivateKey rsaKey;
	private PrivateKey pvtKey;
	private Map<String, ObjectIdentifier> ids;
	
	private String encodedCertChain;
	
	@Value("${CERT_PIN}")
	private String pin;
	@Value("${CERT_URL}")
	private String certUrl;
	
	private String[] algos = new String[] {"MD2", "MD5", "SHA", "SHA224", "SHA256", "SHA384", "SHA512"};
	private Set<String> setAlgos = new HashSet<String>(Arrays.asList(algos));
	
	@PostConstruct
	public void init() throws Exception {
		KeyStore ks = KeyStore.getInstance("PKCS12");
		InputStream is = getCertA1();
		ks.load(is, pin.toCharArray());
		String alias = ks.aliases().nextElement();
		this.pvtKey = (PrivateKey) ks.getKey(alias, pin.toCharArray());
		Certificate[] certsChain = ks.getCertificateChain(alias);
		this.encodedCertChain = encodeCertChain(certsChain);
		this.rsaKey = (RSAPrivateKey) RSAKeyFactory.toRSAKey(pvtKey);
		int keySize = RSACore.getByteLength(rsaKey);
		this.padding = RSAPadding.getInstance(RSAPadding.PAD_BLOCKTYPE_1, keySize, null);
		this.ids = loadIds();
	}
	
	private Map<String, ObjectIdentifier> loadIds() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		Map<String, ObjectIdentifier> ids = new HashMap<String, ObjectIdentifier>();
		for (String alg: algos) {
			ids.put(alg, (ObjectIdentifier) AlgorithmId.class.getDeclaredField(alg+"_oid").get(null));
		}
		return ids;
	}
	
	public SignResult sign (String algo, String hash) throws Exception {
		if ( !setAlgos.contains(algo) ) {
			throw new Exception("Invalid '"+algo+"' algorithm");
		}
		byte[] digest = convertHashToBytes(hash);
		byte[] encoded = encodeSignature(ids.get(algo), digest);
		byte[] padded = padding.pad(encoded);
		byte[] signature = RSACore.rsa(padded, rsaKey, true);
		SignResult res = new SignResult();
		res.setSignature( Base64Utils.encodeToString(signature) );
		res.setCertChain( encodedCertChain );
		return res;
	}
	
	public String[] listAlgos() {
		return this.algos;
	}
	
	private byte[] encodeSignature(ObjectIdentifier oid, byte[] digest) throws IOException {
        DerOutputStream out = new DerOutputStream();
        new AlgorithmId(oid).encode(out);
        out.putOctetString(digest);
        DerValue result = new DerValue(DerValue.tag_Sequence, out.toByteArray());
        return result.toByteArray();
    }
	
	private byte[] convertHashToBytes(String hash) {
		String s2;
		byte[] b = new byte[hash.length() / 2];
		int i;
		for (i = 0; i < hash.length() / 2; i++){
			s2 = hash.substring(i * 2, i * 2 + 2);
			b[i] = (byte) (Integer.parseInt(s2, 16) & 0xff);
		}
		return b;
	}
	
	private InputStream getCertA1() throws IOException {
		return new URL(certUrl).openStream();
	}

	private String encodeCertChain(Certificate[] certChain) throws Exception {
		List<Certificate> certList = Arrays.asList(certChain);
		CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
		CertPath certPath = certFactory.generateCertPath(certList);
		byte[] encodedCertChain = certPath.getEncoded("PkiPath");
		return Base64Utils.encodeToString(encodedCertChain);
	}
}
