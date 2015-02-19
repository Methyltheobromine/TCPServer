/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pren_tcpserver;

/**
 *
 * @author Severin
 */
public class Pren_TCPServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        server myServer = new server();
        myServer.start();
    }
    
}
