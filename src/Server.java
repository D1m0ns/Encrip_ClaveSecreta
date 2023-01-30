/*
*
*---------Autores Alex Cantos y Eduardo Vazquez------------
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import java.net.*;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;


public class Server {
    private static String KEY = "clave-secreta-01"; //Clave para cifrar/descifrar
    
    public static String encripta(String text) throws Exception{
        String aux = "";
        Key aesKey = new SecretKeySpec(KEY.getBytes(), "AES");
    
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
    
        byte[] encrypted = cipher.doFinal(text.getBytes());
            
        aux = Base64.getEncoder().encodeToString(encrypted);
        return aux;
    }
    
    public static String desencripta(String text) throws Exception{
        byte[] decoded = Base64.getDecoder().decode(text);
        Key aesKey = new SecretKeySpec(KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, aesKey);
        return new String(cipher.doFinal(decoded));
    }
    
    public static void main(String[] args){
   
         final ServerSocket serverSocket;
         final Socket clientSocket;
         final BufferedReader in;
         final PrintWriter out;
         final Scanner sc= new Scanner (System.in);
         
            try {
                serverSocket= new ServerSocket(5000);//se pone la ip para establecer la conexion
                clientSocket = serverSocket.accept();
                out= new PrintWriter(clientSocket.getOutputStream());
                in = new BufferedReader (new InputStreamReader
            (clientSocket.getInputStream()));
                
                Thread sender= new Thread (new Runnable(){
                String msg;
                @Override
                public void run(){
                    while (true){
                    msg=sc.nextLine();//lee datos del teclado
                    try {
                        System.out.println("Mensaje Original: " + msg);
                        msg = encripta(msg);
                        System.out.println("Mensaje Encriptado: " + msg+ "\n");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    out.println(msg);//escribir datos almacenados en el cliente
                    out.flush();//forzar el envio de datos
                
                    }
                }
                
                });
                
                    sender.start();
                    Thread recive = new Thread(new Runnable(){
                    String msg;
                    @Override 
                    public void run (){
                        try {
                            msg=in.readLine();
                            //siempre el usuario este conectado
                            while (msg!=null){
                                System.out.println("Client:"+msg);
                                msg = desencripta(msg);
                                System.out.println("Mensaje Desencriptado: " + msg+ "\n");
                                msg=in.readLine();
                            }
                            System.out.println("Client disconnected");
                            clientSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                            recive.start();
            } catch (IOException e) {
                e.printStackTrace();
                }
    }
}