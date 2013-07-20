package org.iesapp.framework.dialogs;


 
//file CustomDialog.java  
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import org.iesapp.framework.util.CoreCfg;
 
public class TimeoutDialog extends JDialog implements ActionListener,Runnable{
 
private JButton jButton_Yes = null;
private boolean OK = false;
private Thread thread = null;
private int seconds = 0;
private final int max = 5;//max number of seconds 
 
public TimeoutDialog(Frame frame, String msg){
super(frame,true);//modal
setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
setLocationRelativeTo(null);
 
Box hBox = Box.createHorizontalBox();
hBox.setAlignmentX(Box.CENTER_ALIGNMENT);
 
jButton_Yes = new JButton("Tancar");
jButton_Yes.addActionListener(this);
 
JLabel jLabel = new JLabel();
jLabel.setIcon(new ImageIcon(getClass().getResource("/org/iesapp/framework/icons/construction.gif")));

if(msg==null)
{
    msg = "Degut a tasques de manteniment, el programa\nes troba temporalment fora de servei.\nDisculpin les molèsties.";
}

JTextArea jTextArea = new JTextArea(msg);
jTextArea.setWrapStyleWord(true);
jTextArea.setEditable(false);
jTextArea.setColumns(25);
 
Container cont = getContentPane();
cont.setLayout(new BoxLayout(cont,BoxLayout.Y_AXIS));
JPanel panel = new JPanel();
panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
panel.add(jLabel);
panel.add(jTextArea);
cont.add(panel);

hBox.add(Box.createGlue());
hBox.add(jButton_Yes);
cont.add(hBox);
 
pack();
thread = new Thread(this);
thread.start();
setVisible(true);
}
 
public void actionPerformed(ActionEvent e){
if (e.getSource()==jButton_Yes) {
            OK = true;
        }
setVisible(false);
}
 
public void run(){
while(seconds < max){
seconds++;
setTitle("iesDigital "+CoreCfg.VERSION+" es tancarà en "+(max-seconds)+" s.");
try{
Thread.sleep(1000);
}catch (InterruptedException exc){
};
}
setVisible(false);
}
 
public boolean isOk(){return OK;}
 
public static void main(String[] args){//testing
TimeoutDialog cd = new TimeoutDialog(null,null);
System.out.println(cd.isOk());
cd = null;
System.exit(0);
}
 
}//end
 
