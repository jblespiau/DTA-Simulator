package graphics;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GUI extends JFrame {
	  public GUI(){
	    this.setTitle("Ma première fenêtre Java");
	    this.setSize(400, 500);
	    this.setLocationRelativeTo(null);
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);             
	    this.setVisible(true);
	    
	    //Instanciation d'un objet JPanel
	    JPanel pan = new JPanel();
	    //Définition de sa couleur de fond
	    pan.setBackground(Color.ORANGE);        
	    //On prévient notre JFrame que notre JPanel sera son content pane
	    this.setContentPane(pan);               
	    this.setVisible(true);
	  }
	}
