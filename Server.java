import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

public class Server {
    public static void main(String[] args) throws Exception {
        int port = 12345;

        // server socket생성&연결
        ServerSocket serverSocket = new ServerSocket(port);
        Socket socket = serverSocket.accept();

        // RSA 공개키, 개인키 쌍(2048bit) 생성
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        // 클라이언트에 공개키 전송
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        out.writeObject(publicKey);
        out.flush();

        // 암호화된 AES키, IV를 클라이언트로부터 받음
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        byte[] encryptedAESKey = (byte[]) in.readObject();
        byte[] encryptedIV = (byte[]) in.readObject();

        // 암호화된 AES 비밀키를 개인키로 복호화 하는 과정 (패딩은 PKCS7 사용)
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] aesKey = rsaCipher.doFinal(encryptedAESKey);
        byte[] iv = rsaCipher.doFinal(encryptedIV);

        // 생성한 RSA 공개키/개인키 쌍 출력
        System.out.println("> Creating RSA Key Pair... ");
        System.out.println("Private Key: " + Base64.getEncoder().encodeToString(privateKey.getEncoded()));
        System.out.println("Public Key: " + Base64.getEncoder().encodeToString(publicKey.getEncoded()));
        System.out.println("");
        // Client로 부터 받은 AES 암호화된 비밀키& 비밀키 출력
        System.out.println("> Received AES KEY : " + Base64.getEncoder().encodeToString(encryptedAESKey));
        System.out.println("Decrypted AES Key : " + Base64.getEncoder().encodeToString(aesKey));
        System.out.println("");

        // Client로 전송할 메시지 입력
        // Client에서 전송된 메시지 & 타임스템프 & 암호화된 메시지 출력
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
        Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/dd/MM HH:mm:ss");
        while (true) {
            // Client로 전송할 메시지 입력
            System.out.print(">");
            String messageToSend = inputReader.readLine();

            // Client로 전송할 메시지를 암호화한 후, Client로 전송
            aesCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(aesKey, "AES"), new IvParameterSpec(iv));
            byte[] encryptedMessageToSend = aesCipher.doFinal(messageToSend.getBytes());
            out.writeObject(encryptedMessageToSend);
            out.flush();

            // Client로부터 전송된 메시지 출력 & 암호화된 메시지 출력
            byte[] encryptedMessage = (byte[]) in.readObject();
            aesCipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(aesKey, "AES"), new IvParameterSpec(iv));
            String receivedMessage = new String(aesCipher.doFinal(encryptedMessage));
            System.out.println("Received : \""+receivedMessage+"\""+"[" + dateFormat.format(new Date()) + "] ");
            System.out.println("Encrypted Message : \"" + Base64.getEncoder().encodeToString(encryptedMessage) + "\" ");

            // Client, Server에서 exit 키워드를 입력한 경우, 연결을 종료(socket을 닫음)
            if ("exit".equalsIgnoreCase(messageToSend.trim()) || "exit".equalsIgnoreCase(receivedMessage.trim())) {
                System.out.println("Connection Closed.");
                break;
            }
        }
        socket.close();
        serverSocket.close();
    }
}
