package pren_tcpserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Diese Klasse übernimmt den Teil des TCP-Servers und wird auf dem Raspberry Pi
 * gestartet und wartet bis sich ein TCPClient anmeldet. Je nach empfangener
 * Botschaft wird unterschiedliches ausgeführt.
 *
 * Der TCP-Client sendet zuerst und hört dann. Der TCP-Server hört zuerst und
 * sendet dann.
 *
 * @author Severin
 * @see client
 */
public class server extends Thread {

    private final int PORT = 11111;
    private ServerSocket tcpSocket;
    private Socket client;
    private boolean connected;

    public server() {
        connected = false;
    }

    /**
     * Diese Methode startet einen Thrad, der ununterbrochen läuft. Er beginnt
     * mit lesen der erhaltenen Daten. Hat er ein Paket vom Client erhalten,
     * wertet er aus, was ausgeführt werden muss.
     */
    @Override
    public void run() {
        try {
            tcpSocket = new ServerSocket(PORT);
            client = warteAufAnmeldung(tcpSocket);

            System.out.println("anmeldung erfolgt");
            while (!Thread.currentThread().isInterrupted()) {
                String nachricht = leseDaten(client);
                System.out.println("empgangen daten" + nachricht);
                nachricht = datenAuswerten(nachricht);
                System.out.println("schriebe daten" + nachricht);
                if (!Thread.currentThread().isInterrupted()) {
                    currentThread().sleep(50);
                }
                schreibeDaten(client, nachricht);
            }
            client.close();
            tcpSocket.close();

        } catch (IOException e) {
            System.err.println("TCP Server: Error: " + e.getMessage());
        } catch (InterruptedException ex) {
            System.err.println("TCP Server: Error: " + ex.getMessage());
        }
    }

    /**
     * Diese Methode wird vom Thread dann aufgerufen, wenn er Thread Daten
     * erhalten hat die er zuerst auswerten muss.
     *
     * @param nachricht TCP Paket Inhalt
     * @return int Wert der über Weiteres Vorgehen Bescheid gibt
     * @throws InterruptedException
     * @throws IOException
     */
    public String datenAuswerten(String nachricht) throws IOException, InterruptedException {
        String[] splitNachricht = nachricht.split(" ");

        switch (splitNachricht[0]) {
            case "Start": //Starte die autonome Routine
                return "Ende";
            case "Stop": //Stoppt den autonmen Prozess
                return "Ende";
            case "CorDelta": //Koorekturwert für den Unterschied wischen Ultraschall und Bildauswertung
                // splitNachricht[1] ist Wert
                return "Ende";
            case "DEBUG_STEP_Tower":
                // splitNachricht[1] ist Wert
                return "Ende";
            case "DEBUG_STEP_Magazin":
                // splitNachricht[1] ist Wert
                return "Ende";
            case "DEBUG_TakePicture":
                return "Ende";
            case "DEBUG_AnalyzePicture":
                return "Ende";
            case "DEBUG_FREQ_DC":
                // splitNachricht[1] ist Wert
                return "Ende";
            case "Server schliessen":
                return "Ende";
            case "TestServerVerbindung":
                System.out.println("test verbidnung eingegagen");
                return "ServerVerbindungErfolgreich";
            default:
                return "Error";
        }
    }


    /**
     * Da TCP verbindungsorientiert ist, wartet diese Methode, bis sich ein
     * Client angemeldet hat und verankert ihn dann.
     *
     * @param serverSocket welchem der TCP Client eingetragen wird.
     * @return den Socket des Clients
     * @throws IOException
     */
    public Socket warteAufAnmeldung(ServerSocket serverSocket) throws IOException {
        Socket client = serverSocket.accept(); // blockiert, bis sich ein Client angemeldet hat
        return client;
    }

    /**
     * Diese Methode liest den Inhalt des TCP Pakets und gibt ihn als String
     * zurück
     *
     * @param socket um den Inputsteam zu generieren
     * @return String Inhalt des TCP Pakets
     * @throws IOException
     */
    public String leseDaten(Socket socket) throws IOException {
        BufferedReader inStream = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        char[] buffer = new char[1024];
        int anzahlZeichen = inStream.read(buffer, 0, 1024); // blockiert bis Nachricht empfangen
        String nachricht = new String(buffer, 0, anzahlZeichen);
        return nachricht;
    }

    /**
     * Diese Methode schreibt den String in das TCP Paket.
     *
     * @param socket um Outputstream zu generieren
     * @param nachricht String welche in das TCP Paket soll
     * @throws IOException
     */
    public void schreibeDaten(Socket socket, String nachricht) throws IOException {
        PrintWriter outStream = new PrintWriter(
                new OutputStreamWriter(
                        socket.getOutputStream()));
        outStream.print(nachricht);
        outStream.flush();
    }

    /**
     * Methode um den TCP Server zu schliessen
     */
    public void closeTCPServer() {
        try {
            Socket socket = new Socket(InetAddress.getLocalHost().getHostAddress().toString(), 11111);
            String zuSendendeDaten = "Server schliessen";
            schreibeDaten(socket, zuSendendeDaten);
            socket.close();
        } catch (IOException e) {
            System.err.println("TCP Server: Error: " + e.getMessage());
        }
    }

}
