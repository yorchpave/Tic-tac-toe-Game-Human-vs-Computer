import java.awt.* ;
import java.awt.event.* ;
import java.io.* ;
import java.net.* ;
import javax.swing.* ;
import javax.swing.border.* ;

public class TicTacToe extends JFrame implements ActionListener {

   JButton b11,b21,b31,
           b12,b22,b32,
           b13,b23,b33 ;
   boolean myturn ;
   BufferedReader br ;
   BufferedWriter bw ;
   Thread connection ;
   Process prologProcess ;
   String prolog ;
   String ttt ;

   public TicTacToe(String prolog, String ttt) {

      this.prolog = prolog ;
      this.ttt = ttt ;
      b11 = new JButton("") ;
      b21 = new JButton("") ;
      b31 = new JButton("") ;
      b12 = new JButton("") ;
      b22 = new JButton("") ;
      b32 = new JButton("") ;
      b13 = new JButton("") ;
      b23 = new JButton("") ;
      b33 = new JButton("") ;
      b11.setActionCommand("(1,1).") ;
      b21.setActionCommand("(2,1).") ;
      b31.setActionCommand("(3,1).") ;
      b12.setActionCommand("(1,2).") ;
      b22.setActionCommand("(2,2).") ;
      b32.setActionCommand("(3,2).") ;
      b13.setActionCommand("(1,3).") ;
      b23.setActionCommand("(2,3).") ;
      b33.setActionCommand("(3,3).") ;
      Font f = new Font("monospaced",Font.PLAIN,64) ;
      b11.setFont(f) ;
      b21.setFont(f) ;
      b31.setFont(f) ;
      b12.setFont(f) ;
      b22.setFont(f) ;
      b32.setFont(f) ;
      b13.setFont(f) ;
      b23.setFont(f) ;
      b33.setFont(f) ;
      b11.addActionListener(this) ;
      b21.addActionListener(this) ;
      b31.addActionListener(this) ;
      b12.addActionListener(this) ;
      b22.addActionListener(this) ;
      b32.addActionListener(this) ;
      b13.addActionListener(this) ;
      b23.addActionListener(this) ;
      b33.addActionListener(this) ;
      JPanel panel = new JPanel() ;
      panel.setLayout(new GridLayout(3,3)) ;
      panel.add(b11) ;
      panel.add(b21) ;
      panel.add(b31) ;
      panel.add(b12) ;
      panel.add(b22) ;
      panel.add(b32) ;
      panel.add(b13) ;
      panel.add(b23) ;
      panel.add(b33) ;
      this.setTitle("Tic Tac Toe") ;
      Border panelborder = BorderFactory.createLoweredBevelBorder() ;
      panel.setBorder(panelborder) ;
      this.getContentPane().add(panel) ;
      this.setSize(300,300) ;
      this.setLocation(900,300) ;
      this.myturn = true ;

      Connector connector = new Connector(8080) ;
      connector.start() ;

      Socket sock ;

      try {

         sock = new Socket("127.0.0.1",8080) ;
         br = new BufferedReader(new InputStreamReader(sock.getInputStream())) ;
         bw = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream())) ;
      }
      catch(Exception x) { 
	System.out.println(x) ;
      }

      connection = new Thread() {
         public void run() {
            while(true) {
               try{
                  String s = br.readLine() ;
                  computer_move(s) ;
               } catch(Exception xx) { System.out.println(xx) ; }
            }
         }
      } ;
      connection.start() ;

      Thread shows = new Thread() {

         public void run() {

            setVisible(true) ;
         }
      } ;

      EventQueue.invokeLater(shows);

      try {

         prologProcess =
           Runtime.getRuntime().exec(prolog + " -f " + ttt) ;
      } 
      catch(Exception xx) 
      	{System.out.println(xx) ; }

      this.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent w) {
            if (prologProcess != null) prologProcess.destroy() ;
            System.exit(0) ;
         }
      }) ;

   }

   public static void main(String[] args) {
      
       // Path to where Prolog is installed --> /usr/local/bin/swipl
       String prolog = "/usr/bin/swipl" ;
       // Path to where the Prolog program currently --> /Desktop/Tic-tac-toe/TicTacToe.pl
       String ttt = "/Desktop/Tic-tac-toe/TicTacToe.pl" ;
       new TicTacToe(prolog,ttt) ;
   }


   void computer_move(String s) {
      String[] c = s.split(",") ;
      int x = Integer.parseInt(c[0].trim()),
          y = Integer.parseInt(c[1].trim()) ;

      if (x == 1) {
         if (y == 1) b11.setText("O") ;
         else if (y == 2) b12.setText("O") ;
         else if (y == 3) b13.setText("O") ;
      }
      else if (x == 2) {
         if (y == 1) b21.setText("O") ;
         else if (y == 2) b22.setText("O") ;
         else if (y == 3) b23.setText("O") ;
      }
      else if (x == 3) {
         if (y == 1) b31.setText("O") ;
         else if (y == 2) b32.setText("O") ;
         else if (y == 3) b33.setText("O") ;
      }
      if (winner()) connection.stop() ;
      else  myturn = true ;
   }

   // Player

   public void actionPerformed(ActionEvent act) {
      if (!myturn) return ;
      String s = ((JButton)act.getSource()).getText() ;
      if (!s.equals("")) return  ;
      ((JButton)(act.getSource())).setText("X") ;
      try {
         bw.write(act.getActionCommand() + "\n") ;
         bw.flush() ;
      } catch(Exception xx) { System.out.println(xx) ; }
      myturn = false ;
      if (winner()) connection.stop() ;
   }

   // Check if we have a winner

   boolean winner() {
      return  line(b11,b21,b31) ||
         line(b12,b22,b32) ||
         line(b13,b23,b33) ||
         line(b11,b12,b13) ||
         line(b21,b22,b23) ||
         line(b31,b32,b33) ||
         line(b11,b22,b33) ||
         line(b13,b22,b31)  ;
   }

   // Check if there is a line of 3 boxes with same input type

   boolean line(JButton b, JButton c, JButton d) {
      if (!b.getText().equals("") &&b.getText().equals(c.getText()) &&
                c.getText().equals(d.getText()))  {
         if (b.getText().equals("O")) {
            b.setBackground(Color.red) ;
            c.setBackground(Color.red) ;
            d.setBackground(Color.red) ;
         }
         else {
            b.setBackground(Color.green) ;
            c.setBackground(Color.green) ;
            d.setBackground(Color.green) ;
         }
         return true ;
      } else return false;
   }

}
