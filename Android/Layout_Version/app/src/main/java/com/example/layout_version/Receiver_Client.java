package com.example.layout_version;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Array;
import java.util.Arrays;
import java.util.Base64;
//import javax.imageio.ImageIO;
//import java.awt.image.BufferedImage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;


/**
 * The class Receiver_ client extends async task. This is to connect the video from the server to the app.
 */
public class Receiver_Client extends AsyncTask {
    public static VideoViewOpencv my_videoview;
    static String address = "44.212.17.188";
    static int port = 9999;
    static String ID = "ABCDEFGH";
    //static Socket socket = new Socket(address, port);
    //static DataInputStream input = new DataInputStream(socket.getInputStream());
    //static DataOutputStream output = new DataOutputStream(socket.getOutputStream());

    /**
     *
     * Receiver_ client
     *
     * @return public
     */
    public Receiver_Client(){

        //super.onCreate(savedInstanceState);
        //Client client = new Client( ip address, port);

    }

    @Override

/**
 *
 * Do in background
 *
 * @param objects  the objects.
 * @return Object
 */
    protected Object doInBackground(Object[] objects) {

        custom_run();
        return null;
    }


    /**
     *
     * Custom_run
     *
     */
    public static void custom_run(){

        System.out.println("Start of program");
        // System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        connect(address, port);
    }

    @SuppressLint("NewApi")

/**
 *
 * Connect
 *
 * @param address  the address.
 * @param port  the port.
 */
    public static void connect(String address, int port) {


        try {
            System.out.println("Start of connect method");
            DatagramSocket clientSocket = new DatagramSocket();
            DatagramSocket videoSocket = new DatagramSocket();
            DatagramSocket audioSocket = new DatagramSocket();
            InetAddress IPAddress = InetAddress.getByName(address);
            String sentence = "R" + ID;
            byte[] bytes = sentence.getBytes(StandardCharsets.UTF_8);
            byte[] receivebytes = new byte[5];
            DatagramPacket sendPacket = new DatagramPacket(bytes, bytes.length, IPAddress, port);
            clientSocket.send(sendPacket);
            DatagramPacket receivePacket = new DatagramPacket(receivebytes, receivebytes.length);
            clientSocket.receive(receivePacket);
            receivebytes = receivePacket.getData();
            String decoded = new String(receivebytes, "UTF-8");
            System.out.println("From server: " + decoded);
            int port_num = Integer.parseInt(decoded);
            clientSocket.close();
            byte[] videoInitMessage = "Vid_Init".getBytes();
            byte[] audioInitMessage = "Aud_Init".getBytes();
            DatagramPacket startVideoPacket = new DatagramPacket(videoInitMessage, videoInitMessage.length, IPAddress, port_num);
            DatagramPacket startAudioPacket = new DatagramPacket(audioInitMessage, audioInitMessage.length, IPAddress, port_num + 1);
            videoSocket.send(startVideoPacket);
            // audioSocket.send(startAudioPacket);
            while(true){
                byte[] receivevideobytes = new byte[65536];
                //byte[] receiveaudiobytes = new byte[8];
                DatagramPacket receiveVideoPacket = new DatagramPacket(receivevideobytes, receivevideobytes.length);
                //DatagramPacket receiveAudioPacket = new DatagramPacket(receiveaudiobytes, receiveaudiobytes.length);
                videoSocket.receive(receiveVideoPacket);
                System.out.println(receiveVideoPacket);
                byte[] byte_arr = trim(receivevideobytes);
                //receivevideobytes = receiveAudioPacket.getData();

                ByteArrayInputStream inStreambj = new ByteArrayInputStream(byte_arr);
                ByteArrayInputStream bis = new ByteArrayInputStream(byte_arr);
                Bitmap bp = BitmapFactory.decodeStream(bis); //decode stream to a bitmap image
//                yourImageView.setImageBitmap(bp); //set the JPEG image in your image view
//                BufferedImage bImage = ImageIO.read(inStreambj);
//                ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                ImageIO.write(bImage, "jpg", bos );
//                byte [] data = bos.toByteArray();
//                ByteArrayInputStream bis = new ByteArrayInputStream(data);
//                BufferedImage bImage2 = ImageIO.read(bis);
//                ImageIO.write(bImage2, "jpg", new File("output.jpg") );
                System.out.println("image created");

                //byte[] v_result = trim(receivevideobytes);
                //byte[] a_result = trim(receivevideobytes);
                //audioSocket.receive(receiveAudioPacket);
                //receiveaudiobytes = receiveAudioPacket.getData();
                // System.out.println("Size of video packet: " + a_result.length);

            }
        } catch (UnknownHostException u) {
            System.out.println(u);
        } catch (IOException i) {
            System.out.println(i);
        }
    }
    String line = "";
    /*
            while(!line.equals("Over")){
                try{
                    line = input.readLine();
                    output.writeUTF(line);
                }
                catch(IOException i){
                    System.out.println(i);
                }
            }
            try{
                input.close();
                output.close();
                socket.close();
            }
            catch(IOException i){
                System.out.println(i);
            }
        }*/

    /**
     *
     * Trim
     *
     * @param data  the data.
     * @return byte[]
     */
    public static byte[] trim(byte[] data) {

        byte[] input = data;
        int i = input.length;
        while (i-- > 0 && input[i] == 0) {
        }

        byte[] output = new byte[i + 1];
        System.arraycopy(input, 0, output, 0, i + 1);
        return output;
    }
}
