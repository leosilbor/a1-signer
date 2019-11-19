# a1-signer

A1-signer is a Restfull service that can be used to sign documents using A1 digital certificates.

# Configuration

* CERT_PIN = certificate PIN 
* CERT_URL = URL pointing to the certificate location (http:// or file:///)
    
# Usage

`http://localhost:8080/sign-a1/api/v1/sign/MD5/575d55152906463bb64a89a4b9689ef4`

Returns the base64 of the signature and certificate chain.

* The first parameter is the hash algorithm used to genarete tha hash of the document to be signed
* The second parameter is the hexadecimal form of the hash of the document to be signed

