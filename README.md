# EEET2580 - KunLun - OPWA - BackEnd

### Environment Variables

- Create .env file from the example template

```bash
cp .env.example .env
```

- create a keystore for SSL
```bash
keytool -genkeypair -alias tomcat -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore springboot-keystore.p12 -validity 3650 -storepass your-password
mv springboot-keystore.p12 src/main/resources/
```
