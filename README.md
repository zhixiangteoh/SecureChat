# SecureChat

Simple chat server and client application with RSA envelope encryption scheme, with Add128 and Substitution symmetric ciphers. Based off of `ImprovedChatServer` and `ImprovedChatClient` by John Ramirez.

## Deployment

> Note: `keys.txt`, `Add128.java`, `Substitute.java` and `SymCipher.java` are required to run the server and client appropriately.

First compile both `SecureChatServer.java` and `SecureChatClient.java`:

```
javac SecureChatServer.java
javac SecureChatClient.java
```

Start `SecureChatServer`:
```
java SecureChatServer
```

Then start as many `SecureChatClient`s as you want, each in a separate terminal window or tab:
```
java SecureChatClient
```
