/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imageuploadtest_v2;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.*;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;
import java.util.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;




/**
 *
 * @author Administrator
 */
public class ImageUploadTest_v2 {
    
    
    

    public static String ConvertByteArrayToString(byte[] data, int size)
    {
        String result = "";

        for (int i = 0; i < size; i++)
        {
            result += (char)data[i];
        }

        return result;
    }

    public static String GetFileContents(String filename)
    {
        String result = "";

        try
        {
            
           String path = System.getProperty("user.dir") + "\\src\\imageuploadtest_v2\\" + filename;
           RandomAccessFile f = new RandomAccessFile(path, "rw");

           while (true)
           {
               byte[] data = new byte[1024];
               int size = f.read(data);


               if (size == -1) 
               {
                   break;
               }

               result += ConvertByteArrayToString(data, size);

           }

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return result;
    }
    
    public static void SaveToFile(String filename, String string)
    {
        try
        {
            
           String path = System.getProperty("user.dir") + "\\" + filename;
           
           
           
           
           File fl = new File(path);
           if (fl.exists())
           {
               fl.delete();
           }
           
           
          RandomAccessFile f = new RandomAccessFile(path, "rw");
           byte[] imageByteArray = Base64.getMimeDecoder().decode(string);
           
           
          f.write(imageByteArray);
          
           
           f.close();
           
           
           
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    
    
    public static void StartServer()
    {
        try
        {
                
                                
		ServerSocket server = new ServerSocket(80);
                
                
                while (true)
                {
                    
                    Socket socket = server.accept();
                                        
                    new Thread(() -> 
                    {
                        boolean image_data = false;
                        String string = "";
                        
                        while (true)
                        {

                            try
                            {
                                
                                
                                if (socket.isClosed() == true || socket.isConnected() == false) 
                                {                   
                                    
                                    break;
                                } 
                                
                                

                                BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
                               // ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                                BufferedOutputStream dataOut = new BufferedOutputStream(socket.getOutputStream());
                                
                                PrintWriter out = new PrintWriter(socket.getOutputStream());

                            //    System.out.println(ois.available());

                                String input = "";

                                while (true)
                                {
                                    byte[] data = new byte[1024];
                                    int size = bis.read(data);
                                    if (size == -1) break;
                                    
                                    for (int i = 0; i < size; i++)
                                    {
                                        char c = (char)data[i];
                                        
                                        
                                        
                                        if (c != '\n')
                                        {                                            
                                            input += c;
                                        }
                                        else
                                        {
                                            
                                              
                                              if (image_data == false)
                                              {
                                                  
                                                if (input.contains("Content-Disposition: form-data; name=\"" + "0_afile\""))
                                                {
                                                    image_data = true;
                                                    System.out.println("upload started: " + Calendar.getInstance().getTime());
                                                }
                                                
                                              }
                                              else
                                              {
                                                  
                                                  if (input.contains("-----"))
                                                  {
                                                     System.out.println("upload complete: " + string.length() + " " + Calendar.getInstance().getTime());
                                                      
                                                      //image_data = false;
                                                  }
                                                  else
                                                  {
                                                      if (input.length() > 1)
                                                      {
                                                          
                                                          if (string.length() == 0)
                                                          {
                                                              
                                                              for (int i2 = 0; i2 < input.length(); i2++)
                                                              {
                                                                  if (input.charAt(i2) == ',')
                                                                  {
                                                                      input = input.substring(i2 + 1, input.length());
                                                                      break;
                                                                  }
                                                              }
                                                              
                                                              string += input;
                                                              
                                                          }
                                                          else
                                                          {
                                                                string += input;
                                                          }
                                                          
                                                      }
                                                      
                                                  }
                                              }
                                              
                                            
                                            
                                            if (input.contains("GET / HTTP/1.1"))
                                            {
                                                
                                                String response = GetFileContents("index.html");

                                                int responseLength = response.length();
                                                
                                              //  System.out.println(responseLength);

                                                // send HTTP Headers
                                                out.println("HTTP/1.1 200 OK");
                                                out.println("Server: RBTrading_version4");
                                                out.println("Date: " + new Date());
                                                out.println("Content-type: " + "text/html");
                                                out.println("Content-length: " + responseLength);
                                                out.println(); 
                                                out.flush(); 

                                                dataOut.write(response.getBytes(), 0, responseLength);
                                                dataOut.flush();
                                            }
                                            
                                            input = "";
                                            
                                        }
                                        
                                    }
                                    
                                }

                                

                            }
                            catch (Exception ex)
                            {
                                
                                if (ex.getMessage() != null && ex.getMessage().contains("Connection reset"))
                                {
                                    break;
                                }
                                
                            }

                        }
                        
                        
                        
                        
                    }).start();

                    
                }
                
                
        }
        catch (Exception ex)
        {
            System.out.println("ex 2");
            ex.printStackTrace();
        }
    }

    public static String total_string = "";
    
    public static void StartServer2()
    {
        try
        {
                
                                
		ServerSocket server = new ServerSocket(80);
                
                
                while (true)
                {
                    
                    Socket socket = server.accept();
                                        
                    new Thread(() -> 
                    {
                        int segment_id = -1;
                        boolean image_data = false;
                        boolean end_of_file = false;
                        String string = "";
                        
                        while (true)
                        {

                            try
                            {
                                
                                
                                if (socket.isClosed() == true || socket.isConnected() == false) 
                                {                   
                                    
                                    break;
                                } 
                                
                                

                                BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
                               // ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                                BufferedOutputStream dataOut = new BufferedOutputStream(socket.getOutputStream());
                                
                                PrintWriter out = new PrintWriter(socket.getOutputStream());

                            //    System.out.println(ois.available());

                                String input = "";

                                while (true)
                                {
                                    byte[] data = new byte[1024];
                                    int size = bis.read(data);
                                    if (size == -1) break;
                                    
                                    for (int i = 0; i < size; i++)
                                    {
                                        char c = (char)data[i];
                                        
                                        
                                        
                                        if (c != '\n')
                                        {                                            
                                            input += c;
                                        }
                                        else
                                        {
                                            
                                              
                                              if (image_data == false)
                                              {
                                                  if (input.contains("Content-Disposition: form-data; name=\"eof\""))
                                                  {
                                                      end_of_file = true;
                                                      image_data = true;                                                              
                                                      System.out.println("upload started :" + "eof" + " " + Calendar.getInstance().getTime());
                                               
                                                      
                                                  }
                                                  
                                                  if (input.contains("_afile"))
                                                  {
                                                    int sid = 0;
                                                    while (true)
                                                    {
                                                         if (input.contains("Content-Disposition: form-data; name=\"" + sid + "_afile\""))
                                                          {
                                                              segment_id = sid;
                                                              image_data = true;
//                                                              System.out.println("upload started :" + String.valueOf(sid) + " " + Calendar.getInstance().getTime());
                                                              break;
                                                          }
                                                         sid++;
                                                    }
                                                  }
                                                  
                                                  /*
                                                if (input.contains("Content-Disposition: form-data; name=\"" + "0_afile\""))
                                                {
                                                    segment_id = 0;
                                                    image_data = true;
                                                    System.out.println("upload started 0: " + Calendar.getInstance().getTime());
                                                }
                                                if (input.contains("Content-Disposition: form-data; name=\"" + "1_afile\""))
                                                {
                                                    segment_id = 1;
                                                    image_data = true;
                                                    System.out.println("upload started 1: " + Calendar.getInstance().getTime());
                                                }*/
                                                
                                              }
                                              else
                                              {
                                                  
                                                  if (input.contains("-----"))
                                                  {
                                                      if (end_of_file == true)
                                                      {
//                                                          System.out.println("upload complete " + "eof" + ": " + string.length() + " " + Calendar.getInstance().getTime());
                                                            
                                                          total_string += string;
                                                          
                                                          for (int i2 = 0; i2 < total_string.length(); i2++)
                                                              {
                                                                  if (total_string.charAt(i2) == ',')
                                                                  {
                                                                      total_string = total_string.substring(i2 + 1, total_string.length());
                                                                      break;
                                                                  }
                                                              }
                                                          
                                                          
                                                          
                                                          
                                                            SaveToFile("mydata.jpg", total_string);
                                                                dataOut.write("all done".getBytes());
                                                                dataOut.close();
                                                                
                                                                 image_data = false;
                                                          break;
                                                      }
                                                      else
                                                      {
                                                        int sid = 0;
                                                        while (true)
                                                        {
                                                          if (segment_id == sid)
                                                          {
//                                                                System.out.println("upload complete " + sid + ": " + string.length() + " " + Calendar.getInstance().getTime());

                                                                total_string += string;
                                                                
                                                                dataOut.write("done".getBytes());
                                                                dataOut.close();
                                                                 image_data = false;
                                                                 break;
                                                          }
                                                          sid++;
                                                        }
                                                      }
                                                      
                                                      /*
                                                      if (segment_id == 0)
                                                      {
                                                            System.out.println("upload complete 0: " + string.length() + " " + Calendar.getInstance().getTime());

                                                            dataOut.write("done".getBytes());
                                                            dataOut.close();
                                                             image_data = false;
                                                      }
                                                      if (segment_id == 1)
                                                      {
                                                            System.out.println("upload complete 1: " + string.length() + " " + Calendar.getInstance().getTime());

                                                            dataOut.write("all done".getBytes());
                                                            dataOut.close();
                                                             image_data = false;
                                                      }
                                                      */
                                                  }
                                                  else
                                                  {
                                                      if (input.length() > 1)
                                                      {
                                                          string += input;
//                                                          if (end_of_file == true)
//                                                          {
//                                                          if (string.length() == 0)
//                                                          {
//                                                              
//                                                              for (int i2 = 0; i2 < input.length(); i2++)
//                                                              {
//                                                                  if (input.charAt(i2) == ',')
//                                                                  {
//                                                                      input = input.substring(i2 + 1, input.length());
//                                                                      break;
//                                                                  }
//                                                              }
//                                                              
//                                                              string += input;
//                                                              
//                                                          }
//                                                          else
//                                                          {
//                                                                string += input;
//                                                          }
//                                                          }
//                                                          else
//                                                          {
//                                                              
//                                                          }
                                                          
                                                      }
                                                      
                                                  }
                                              }
                                              
                                            
                                            
                                            if (input.contains("GET / HTTP/1.1"))
                                            {
                                                
                                                String response = GetFileContents("index.html");

                                                int responseLength = response.length();
                                                
                                              //  System.out.println(responseLength);

                                                // send HTTP Headers
                                                out.println("HTTP/1.1 200 OK");
                                                out.println("Server: RBTrading_version4");
                                                out.println("Date: " + new Date());
                                                out.println("Content-type: " + "text/html");
                                                out.println("Content-length: " + responseLength);
                                                out.println(); 
                                                out.flush(); 

                                                dataOut.write(response.getBytes(), 0, responseLength);
                                                dataOut.flush();
                                            }
                                            
                                            input = "";
                                            
                                        }
                                        
                                    }
                                    
                                }

                                

                            }
                            catch (Exception ex)
                            {
                                
                                if (ex.getMessage() != null && ex.getMessage().contains("Connection reset"))
                                {
                                    break;
                                }
                                
                            }

                        }
                        
                        
                        
                        
                    }).start();

                    
                }
                
                
        }
        catch (Exception ex)
        {
            System.out.println("ex 2");
            ex.printStackTrace();
        }
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        // **** remember to compile before running..
        
        StartServer2();
        
        
    }
    
    
    
    
    
}

