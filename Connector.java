import java.io.*;
import java.net.*;
import java.util.* ;
import javax.swing.* ;

public class Connector extends Thread {
   int clientNum ;
   int port ;
   ServerSocket portalSocket ;
   Vector collaborators ;

   public Connector(int port) {
      this.clientNum = 1 ;
      this.port = port ;
      this.collaborators = new Vector() ;
   }

   public void run() {
      try {
         portalSocket = new ServerSocket(port) ;
         while(true) {
            try {
               Socket soc = portalSocket.accept() ;
               BufferedWriter out =
                  new BufferedWriter(new OutputStreamWriter(soc.getOutputStream())) ;
               collaborators.add(out) ;
               System.out.println("Spawning Transducer for " + clientNum) ;
               Transducer b =
                  new Transducer(this,
                               new BufferedReader(new InputStreamReader(soc.getInputStream())),
                               out,
                               clientNum) ;
               b.start() ;
               clientNum++ ;
            }
            catch(Exception e2) {
               JOptionPane.showMessageDialog(null,e2.toString(),"CONNECTOR EXCEPTION #2",JOptionPane.WARNING_MESSAGE) ;
            }
         }
      }
      catch (Exception e1) {
         JOptionPane.showMessageDialog(null,e1.toString(),"CONNECTOR EXCEPTION #1",JOptionPane.WARNING_MESSAGE) ;
      }
   }

   public static void main(String[] args) {
      try {
         int port = Integer.parseInt(args[0]) ;
         Connector prtl = new Connector(port) ;
         prtl.start() ;
      }
      catch(Exception e) {
         System.out.println(e) ;
         System.out.println("usage: java -classpath <> Connector <port>") ;
      }
   }
}

class Transducer extends Thread {
   BufferedReader in ;
   BufferedWriter out ;
   int client ;
   Connector portal ;

   Transducer(Connector p, BufferedReader instream,
                    BufferedWriter outstream, int k) {
      this.portal = p ;
      in = instream ;
      out = outstream ;
      client = k ;
   }

   public void run() {
      while(true) {
         try {

            String s = in.readLine() ;
            if (s == null) {    
               portal.collaborators.remove(out) ;
               break ;
            }
            Iterator it = portal.collaborators.iterator() ;
            while(it.hasNext()) {

               BufferedWriter bw = null ;
               try {
                  bw = (BufferedWriter)(it.next()) ;
                  if (bw != out) {
                     bw.write(s+"\r") ;
                     bw.flush() ;
                  }
               }
               catch (Exception e2) {
                  JOptionPane.showMessageDialog(null,e2.toString(),"TRANSDUCER EXCEPTION #2",JOptionPane.WARNING_MESSAGE) ;
               }
            }
         }
         catch(Exception e1) {
            JOptionPane.showMessageDialog(null,e1.toString(),"TRANSDUCER EXCEPTION #1",JOptionPane.WARNING_MESSAGE) ;
         }
      }
   }
}
