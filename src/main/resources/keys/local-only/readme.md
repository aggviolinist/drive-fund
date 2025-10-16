## How to create a private key
Private Key `openssl genpkey -algorithm RSA -out private_key.pem -pkeyopt rsa_keygen_bits:2048`

## How to generate public key from private key
Public Key `openssl rsa -pubout -in private_key.pem -out public_key.pem`