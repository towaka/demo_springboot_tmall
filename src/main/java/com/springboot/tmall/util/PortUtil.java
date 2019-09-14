package com.springboot.tmall.util;

import java.io.IOException;
import java.net.ServerSocket;
 
import javax.swing.JOptionPane;
 
public class PortUtil {

    /**
     * 测试端口
     * 如果端口能够占用了就证明未启动redis，返回false
     * 否则返回true
     * @param port
     * @return
     */
    public static boolean testPort(int port) {
        try {
            ServerSocket ss = new ServerSocket(port);
            ss.close();
            return false;
        } catch (java.net.BindException e) {
            return true;
        } catch (IOException e) {
            return true;
        }
    }
 
    public static void checkPort(int port, String server, boolean shutdown) {
        if(!testPort(port)) {
            if(shutdown) {
                String message =String.format("在端口 %d 未检查得到 %s 启动%n",port,server);
                JOptionPane.showMessageDialog(null, message);
                System.exit(1);
            }
            else {
                String message =String.format("在端口 %d 未检查得到 %s 启动%n,是否继续?",port,server);
                if(JOptionPane.OK_OPTION !=     JOptionPane.showConfirmDialog(null, message))
                    System.exit(1);
                 
            }
        }
    }
 
}