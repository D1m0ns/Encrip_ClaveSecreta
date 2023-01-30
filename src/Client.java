/*
/*
*
*---------Autores Alex Cantos y Eduardo Vazquez------------
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.Key;
import java.net.*;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;


    public class Client {
        
        private static String KEY = "clave-secreta-01"; //Clave para cifrar/descifrar

    public static String encripta(String text) throws Exception {
        String aux = "";
        Key aesKey = new SecretKeySpec(KEY.getBytes(), "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);

        byte[] encrypted = cipher.doFinal(text.getBytes());

        aux = Base64.getEncoder().encodeToString(encrypted);
        return aux;
    }

        public static String desencripta(String text) throws Exception {
            byte[] decoded = Base64.getDecoder().decode(text);
            Key aesKey = new SecretKeySpec(KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            return new String(cipher.doFinal(decoded));
        }

        public static void main(String[] args) {
    final Socket socket;
    final BufferedReader in;
    final PrintWriter out;
    final Scanner sc = new Scanner(System.in);

    try {
        socket = new Socket("localhost", 5000);
        out = new PrintWriter(socket.getOutputStream());
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        Thread sender = new Thread(new Runnable() {
            String msg;

            @Override
            public void run() {
                while (true) {
                    msg = sc.nextLine();
                    try {
                        System.out.println("Mensaje Original: " + msg);
                        msg = encripta(msg);
                        System.out.println("Mensaje Encriptado: " + msg + "\n");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    out.println(msg);
                    out.flush();
                }
            }
        });

        Thread receiver = new Thread(new Runnable() {
            String msg;

            @Override
            public void run() {
                try {
                    msg = in.readLine();
                    while (msg != null) {
                        System.out.println("Server: " + msg);
                        msg = desencripta(msg);
                        System.out.println("Mensaje Desencriptado: " + msg+ "\n");
                        msg = in.readLine();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        sender.start();
        receiver.start();
    } catch (Exception e) {
        e.printStackTrace();
    }
   }
}