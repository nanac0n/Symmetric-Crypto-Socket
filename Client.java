import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

public class Client {
    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port = 12345;

        // 서버-클라이언트 연결
        Socket socket = new Socket(host, port);

        // Server의 공개키를 받음
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        PublicKey serverPublicKey = (PublicKey) in.readObject();

        // AES, IV(초기벡터) 생성
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        SecretKey aesKey = keyGen.generateKey();
        byte[] iv = new byte[16]; // 16 bytes for AES
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);


        // AES key를 Server RSA 공개키로 암호화하여 전송함
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsaCipher.init(Cipher.ENCRYPT_MODE, serverPublicKey);
        byte[] encryptedAESKey = rsaCipher.doFinal(aesKey.getEncoded());
        byte[] encryptedIV = rsaCipher.doFinal(iv);

        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        out.writeObject(encryptedAESKey);
        out.writeObject(encryptedIV);
        out.flush();

        // Server로 부터 받은 공개키, 생성한 AES키, AES 비밀키 출력
        System.out.println("> Received Public Key : " + Base64.getEncoder().encodeToString(serverPublicKey.getEncoded()));
        System.out.println("Creating AES 256 Key ...");
        System.out.println("AES 256 Key: " + Base64.getEncoder().encodeToString(aesKey.getEncoded()));
        System.out.println("Encrypted AES Key : " +Base64.getEncoder().encodeToString(encryptedAESKey));
        System.out.println("");

        // Server로 전송할 메시지 입력
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
        Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/dd/MM HH:mm:ss");
        while (true) {
            // Server로부터 받은 메시지의 암호문과 평문 출력

            byte[] encryptedMessage = (byte[]) in.readObject();
            aesCipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(aesKey.getEncoded(), "AES"), new IvParameterSpec(iv));
            String receivedMessage = new String(aesCipher.doFinal(encryptedMessage));
            System.out.println("Received : \""+receivedMessage+"\" [" + dateFormat.format(new Date()) + "]");
            System.out.println("Encrypted Message : \""+ Base64.getEncoder().encodeToString(encryptedMessage)+"\"");

            // Server로 전송할 메시지 입력
            System.out.print(">");
            String messageToSend = inputReader.readLine();

            // Server로 전송할 메시지를 암호화하여 전송 & 타임스탬프 출력
            aesCipher.init(Cipher.ENCRYPT_MODE, aesKey, new IvParameterSpec(iv));
            byte[] encryptedMessageToSend = aesCipher.doFinal(messageToSend.getBytes());
            out.writeObject(encryptedMessageToSend);
            out.flush();

            System.out.println("[" + dateFormat.format(new Date()) + "] (Encrypted: " + Base64.getEncoder().encodeToString(encryptedMessageToSend) + ")");

            // Client, Server에서 exit 키워드를 입력한 경우, 연결을 종료(socket을 닫음)
            if ("exit".equalsIgnoreCase(messageToSend.trim()) || "exit".equalsIgnoreCase(receivedMessage.trim())) {
                System.out.println("Connection Closed.");
                break;
            }
        }
        socket.close();
    }
}
