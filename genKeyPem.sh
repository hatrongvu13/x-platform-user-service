# ECDSA P-256 (khuyến nghị 2025)
openssl ecparam -genkey -name prime256v1 -noout -out private_ec256.pem
openssl ec -in private_ec256.pem -pubout -out public_ec256.pem