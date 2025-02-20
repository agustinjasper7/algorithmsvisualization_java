import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.LayoutManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import java.io.PrintStream;
import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JSlider;
import javax.swing.Timer;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Sort extends JFrame{
   private int delay = 10,
               action = 0,
               pass = 0,
               index = 0,
               curr = 0,
               step = 1,
               pause = 0,
               xStart = 0,
               yStart = 0,
               boxWidth = 0,
               boxHeight = 0,
               randomCount = 10, 
               randomStart = 0,
               randomEnd = 100,
               fontSize = 12,
               animSpeedMin = 1,
               animSpeedMax = 20;
   private boolean running = false;

   private JLabel lblAction = new JLabel("Action"),
                  lblInput = new JLabel("Input"),
                  lblSpeedSlow = new JLabel("Slow"),
                  lblSpeedFast = new JLabel("Fast"),
                  lblSpeed = new JLabel("Animation Speed"),
                  lblSizeMinimum = new JLabel("Min"),
                  lblSizeMaximum = new JLabel("Max"),
                  lblFont = new JLabel("Font Size"),
                  lblRandomCount = new JLabel("Count"),
                  lblRandomStart = new JLabel("Start"),
                  lblRandomEnd = new JLabel("End"),
                  lblRandom = new JLabel("Random"),
                  lblData = new JLabel("Data");
   private JTextField txtInput = new JTextField(10),
                      txtRandomCount = new JTextField(Integer.toString(randomCount), 3),
                      txtRandomStart = new JTextField(Integer.toString(randomStart), 3),
                      txtRandomEnd = new JTextField(Integer.toString(randomEnd), 3);
   private JPanel pnlTop = new JPanel(),
                  pnlCenter = new DrawCanvas(null),
                  pnlBottom = new JPanel();
   private JButton btnPlay = new JButton("Start"),
                   btnStop = new JButton("Clear"),
                   btnAdd = new JButton("Add"),
                   btnDelete = new JButton("Delete"),
                   btnGenerate = new JButton("Generate"),
                   btnOptions = new JButton("Options"),
                   btnRecord = new JButton("Record");
   private JSlider sldAnimationSpeed = new JSlider(animSpeedMin, animSpeedMax, animSpeedMin),
                   sldFontSize = new JSlider(50,100,75);
   private JComboBox<String> cmbAlgorithm = new JComboBox<String>(new String[]{"Bubble Sort","Selection Sort","Insertion Sort"});
   private JFrame app = this,
                  frmOptions;
   
   private Color red = new Color(204,0,0), 
                 blue = new Color(0,0,204),
                 yellow = new Color(204,204,0);
   
   private ArrayList<Integer> data;
   private Random r = new Random();
   
   private JLabel[] boxes;
   private JLabel left, right;
   private String message1, message2;
   
   private Timer animator;

   public Sort(){
      data = new ArrayList<Integer>();

      ActionListener dataController = new DataValuesController();
      btnAdd.addActionListener(dataController);
      btnAdd.setEnabled(false);
      btnDelete.addActionListener(dataController);
      btnDelete.setEnabled(false);
      btnGenerate.addActionListener(new GenerateRandomController());
      ActionListener animateController = new AnimationController();
      btnPlay.addActionListener(animateController);
      btnStop.addActionListener(animateController);
      btnOptions.addActionListener(new OptionsController());
      txtInput.getDocument().addDocumentListener(new InputController());
      txtRandomCount.getDocument().addDocumentListener(new RandomInputController());
      txtRandomStart.getDocument().addDocumentListener(new RandomInputController());
      txtRandomEnd.getDocument().addDocumentListener(new RandomInputController());
      sldFontSize.addChangeListener(new FontSizeController());
      btnRecord.addActionListener(new RecordDataController());

      btnPlay.setEnabled(false);
      btnStop.setEnabled(false);
      
      pnlTop.add(lblInput);
      pnlTop.add(txtInput);
      pnlTop.add(btnAdd);
      pnlTop.add(btnDelete);
      pnlTop.add(btnGenerate);
      
      pnlCenter.setBackground(Color.WHITE);
      pnlCenter.setBorder(BorderFactory.createLineBorder(Color.BLACK));
      pnlCenter.addComponentListener(new CenterPanelController());
      
      pnlBottom.add(lblAction);
      pnlBottom.add(cmbAlgorithm);
      pnlBottom.add(btnPlay);
      pnlBottom.add(btnStop);
      pnlBottom.add(btnOptions);
      
      add(pnlTop, BorderLayout.NORTH);
      add(pnlCenter);
      add(pnlBottom, BorderLayout.SOUTH);
   }
   
   public static void main(String[]args){
      JFrame app = new Sort();
      app.setTitle("Sorting Algorithms Simulator");
      app.setSize(600, 300);
      app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      app.setVisible(true);
   }

   private void drawBoxes(){   
      pnlCenter.removeAll();
      
      if(data.size()>0){
         fontSize = (int) (pnlCenter.getHeight()*0.1*sldFontSize.getValue()/100);
         btnPlay.setEnabled(true);
         btnStop.setEnabled(true);
         
         boxes = new JLabel[data.size()];
         boxWidth = pnlCenter.getWidth()/(boxes.length+1);
         boxHeight = pnlCenter.getHeight()/3;
         xStart = boxWidth/2;
         yStart = boxHeight;
         for(int i=0; i<boxes.length; i++){
            JLabel single = boxes[i] = new JLabel(Integer.toString(data.get(i)), JLabel.CENTER);
            single.setBounds(i*boxWidth+xStart, yStart, boxWidth, boxHeight);
            single.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            single.setFont(getFont().deriveFont((float)fontSize).deriveFont(Font.BOLD));
            pnlCenter.add(boxes[i]);
         }
      }

      repaint();
   }
   
   private void finish(){
      message1 = "";
      message2 = ""; 
      running = false;
      btnPlay.setText("Reset");
      btnStop.setText("Clear");
      animator.stop();
   }

   private void reset(){
      cmbAlgorithm.setEnabled(true);
      txtInput.setEnabled(true);
      btnGenerate.setEnabled(true);
      btnPlay.setText("Start");
      animator=null;
   }
   
   private class GenerateRandomController implements ActionListener{
      public void actionPerformed(ActionEvent e){
         data.clear();
         for(int i=0; i<randomCount; ++i)
            data.add(r.nextInt(randomEnd-randomStart+1)+randomStart);
         drawBoxes();
         txtInput.setText("");

         if(data.size()>0)
            btnDelete.setEnabled(true);
         else
            btnDelete.setEnabled(false);
      }
   }

   private class InputController implements DocumentListener{
      public void changedUpdate(DocumentEvent e){
         detectInput();
      }
      public void removeUpdate(DocumentEvent e){
         detectInput();
      }
      public void insertUpdate(DocumentEvent e){
         detectInput();
      }
      private void detectInput(){
         if(txtInput.getText().length()>0)
            btnAdd.setEnabled(true);
         else
            btnAdd.setEnabled(false);
      }
   }

   private class DataValuesController implements ActionListener{
      public void actionPerformed(ActionEvent e){
         if(e.getSource()==btnAdd){
            String input = txtInput.getText(),
                temp = "";
            for(int i=0; i<input.length(); i++){
               char value = input.charAt(i);
               if(Character.isDigit(value) || temp.equals("") && value=='-')
                  temp += value;
               else if(!temp.equals("")){
                  data.add(Integer.parseInt(temp));
                  temp = "";
               }
            }
            if(!temp.equals("")){
               data.add(Integer.parseInt(temp));
               temp = "";
            }
            txtInput.setText("");
         }
         else{
            data.remove(data.size()-1);
         }

         if(data.size()>0)
            btnDelete.setEnabled(true);
         else
            btnDelete.setEnabled(false);

         drawBoxes();
      }
   }

   private class FontSizeController implements ChangeListener{
      public void stateChanged(ChangeEvent e){
         if(boxes!=null){
            fontSize = (int) (sldFontSize.getValue()*pnlCenter.getHeight()*0.1/100);
            for(JLabel single:boxes)
               single.setFont(getFont().deriveFont((float)fontSize).deriveFont(Font.BOLD));
         }  
      }
   }

   private class OptionsController implements ActionListener{
      public void actionPerformed(ActionEvent e){
         if(frmOptions==null){
            JPanel pnlAnimationSpeed = new JPanel(),
                   pnlRandomOptions = new JPanel(),
                   pnlFontSize = new JPanel(),
                   pnlData = new JPanel();

            pnlRandomOptions.add(lblRandomCount);
            pnlRandomOptions.add(txtRandomCount);
            pnlRandomOptions.add(lblRandomStart);
            pnlRandomOptions.add(txtRandomStart);
            pnlRandomOptions.add(lblRandomEnd);
            pnlRandomOptions.add(txtRandomEnd);

            pnlAnimationSpeed.add(lblSpeedSlow);
            pnlAnimationSpeed.add(sldAnimationSpeed);
            pnlAnimationSpeed.add(lblSpeedFast);

            pnlFontSize.add(lblSizeMinimum);
            pnlFontSize.add(sldFontSize);
            pnlFontSize.add(lblSizeMaximum);

            pnlData.add(btnRecord);

            frmOptions = new JFrame("Options");
            frmOptions.setLayout(new GridLayout(8,1));
            frmOptions.add(lblRandom);
            frmOptions.add(pnlRandomOptions);
            frmOptions.add(lblSpeed);
            frmOptions.add(pnlAnimationSpeed);
            frmOptions.add(lblFont);
            frmOptions.add(pnlFontSize);
            frmOptions.add(lblData);
            frmOptions.add(pnlData);
         }
         frmOptions.setSize(300,300);
         frmOptions.setLocation(600,0);
         frmOptions.setResizable(false);
         frmOptions.setVisible(true);
      }
   }
   
   private class RandomInputController implements DocumentListener{
      public void changedUpdate(DocumentEvent e){
         detectInput();
      }
      public void removeUpdate(DocumentEvent e){
         detectInput();
      }
      public void insertUpdate(DocumentEvent e){
         detectInput();
      }
      private void detectInput(){
         try{
            randomCount = Integer.parseInt(txtRandomCount.getText());
         }
         catch(NumberFormatException e){}
         try{
            randomStart = Integer.parseInt(txtRandomStart.getText());
         }
         catch(NumberFormatException e){}
         try{
            randomEnd = Integer.parseInt(txtRandomEnd.getText());
         }
         catch(NumberFormatException e){}
      }
   }

   private String stringTransformArray(Integer[]input){
      String values = "";
      for(int i=0; i<input.length; ++i){
         if(i>0)
            values+=",";
         values+=input[i];   
      }
      return "{"+values+"}";
   }

   private class RecordDataController implements ActionListener{
      public void actionPerformed(ActionEvent e){
         try{
            String location = JOptionPane.showInputDialog(frmOptions, "Enter location", "Record", JOptionPane.QUESTION_MESSAGE);
            if(location!=null){
               Integer[] copy;
               PrintStream out = new PrintStream(location);
               out.println("Input data: "+stringTransformArray(data.toArray(new Integer[data.size()])));
               out.println();
               
               copy = data.toArray(new Integer[data.size()]);
               out.println("Bubble Sort");
               for(int i=0; i<copy.length-1; ++i){
                  out.println("  Pass "+(i+1));
                  for(int j=0; j<copy.length-1-i; ++j){
                     out.print("    "+copy[j]+"<="+copy[j+1]);
                     if(copy[j]>copy[j+1]){
                        int temp = copy[j];
                        copy[j] = copy[j+1];
                        copy[j+1] = temp;
                        out.println(" F swap "+stringTransformArray(copy));
                     }
                     else{
                        out.println(" T ");
                     }
                  }
               }
               out.println("  Sorted Array: "+stringTransformArray(copy));
               out.println();

               copy = data.toArray(new Integer[data.size()]);
               out.println("Selection Sort");
               for(int i=0; i<copy.length-1; ++i){
                  out.println("  Pass "+(i+1));
                  int min = i;
                  for(int j=i+1; j<copy.length; ++j){
                     if(copy[min]>copy[j]){
                        min = j;
                     }
                  }
                  out.print("    Min: "+copy[min]);
                  if(min!=i){
                     String swap = " swap ("+copy[min]+","+copy[i]+") ";
                     int temp = copy[min];
                     copy[min] = copy[i];
                     copy[i] = temp;
                     out.print(swap+stringTransformArray(copy));
                  }
                  out.println();
               }
               out.println("  Sorted Array: "+stringTransformArray(copy));
               out.println();

               copy = data.toArray(new Integer[data.size()]);
               out.println("Insertion Sort");
               for(int i=1; i<copy.length; ++i){
                  int curr = copy[i], j=i-1;
                  out.println("  Pass "+i);
                  out.println("    Curr="+curr);
                  while(j>=0 && curr<copy[j]){
                     out.print("    "+copy[j]+"<="+curr);
                     copy[j+1] = copy[j];
                     --j;
                     out.println(" F move "+stringTransformArray(copy));
                  }
                  if(j>=0)
                     out.println("    "+copy[j]+"<="+curr+" T ");
                  if(curr!=copy[i]){
                     copy[j+1] = curr;
                     out.println("      insert "+stringTransformArray(copy));
                  }
               }
               out.println("  Sorted Array: "+stringTransformArray(copy));
               out.close();
            }
         }
         catch(FileNotFoundException x){}
      }
   }

   private class AnimationController implements ActionListener{
      public void actionPerformed(ActionEvent e){
         if(e.getSource()==btnPlay)
            if(running)
               if(animator!=null && animator.isRunning()){
                  btnPlay.setText("Continue");
                  animator.stop();
               }
               else{
                  btnPlay.setText("Pause");
                  animator.start();
               }
            else
               if(animator!=null){
                  drawBoxes();
                  reset();
                  btnDelete.setEnabled(true);
                  btnPlay.setEnabled(true);
                  btnStop.setEnabled(true);
                  animator=null;
               }
               else{
                  index = 0;
                  pass = 0;
                  action = 0;
                  ActionListener algorithm;
                  switch(cmbAlgorithm.getSelectedIndex()){
                     case 0: algorithm = new BubbleSortController(); break;
                     case 1: algorithm = new SelectionSortController(); break;
                     default: algorithm = new InsertionSortController();
                  }
                  animator = new Timer(delay, algorithm);
                  running = true;
                  btnPlay.setText("Pause");
                  btnAdd.setEnabled(false);
                  btnDelete.setEnabled(false);
                  btnGenerate.setEnabled(false);
                  btnStop.setEnabled(true);
                  btnStop.setText("End");
                  cmbAlgorithm.setEnabled(false);
                  txtInput.setEnabled(false);
                  animator.start();
               }
            
         else if(e.getSource()==btnStop)
            if(running){
               pnlCenter.removeAll();
               
               Integer[] copy = data.toArray(new Integer[data.size()]);
               for(int i=0; i<copy.length-1; i++)
                  for(int j=0; j<copy.length-1-i; j++)
                     if(copy[j]>copy[j+1]){
                        Integer temp = copy[j];
                        copy[j] = copy[j+1];
                        copy[j+1] = temp;
                     }
               
               fontSize = (int) (pnlCenter.getHeight()*0.1*sldFontSize.getValue()/100);
               for(int i=0; i<boxes.length; i++){
                  JLabel single = boxes[i] = new JLabel(copy[i].toString(), JLabel.CENTER);
                  single.setBounds(i*boxWidth+xStart, yStart, boxWidth, boxHeight);
                  single.setOpaque(true);
                  single.setForeground(Color.BLACK);
                  single.setBackground(yellow);
                  single.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                  single.setFont(getFont().deriveFont((float)fontSize).deriveFont(Font.BOLD));
                  pnlCenter.add(single);
               }
               
               repaint();
               finish();
            }
            else{
               pnlCenter.removeAll();
               boxes = null;
               data.clear();
               btnDelete.setEnabled(false);
               btnPlay.setEnabled(false);
               btnStop.setEnabled(false);
               reset();
               repaint();
            }
      }
   }
   
   private class DrawCanvas extends JPanel{
      public DrawCanvas(LayoutManager lm){
         super(lm);
      }
      
      public void paint(Graphics g){
         super.paint(g);
         int xPoint = fontSize/2;
         g.setFont(getFont().deriveFont((float)fontSize).deriveFont(Font.BOLD));
         if(message1!=null){
            g.drawString(message1, xPoint, fontSize+xPoint);
         }
         if(message2!=null){
            g.drawString(message2, xPoint, getHeight()-fontSize);
         }
      }
   } 
   
   private class CenterPanelController implements ComponentListener{
      public void componentResized(ComponentEvent e){
         if(boxes!=null){
            boxWidth = pnlCenter.getWidth()/(boxes.length+1);
            boxHeight = pnlCenter.getHeight()/3;
            xStart = boxWidth/2;
            yStart = boxHeight;
            fontSize = (int) (pnlCenter.getHeight()*0.1*sldFontSize.getValue()/100);
            for(int i=0; i<boxes.length; i++){
               if(boxes[i]!=null){
                  JLabel single = boxes[i];
                  int singleWidth = single.getWidth(),
                      singleHeight = single.getHeight(),
                      newX = (int)(((single.getX()-(i*singleWidth+singleWidth/2))/(double)singleWidth+i)*boxWidth)+xStart,
                      newY = (int)(((single.getY()-singleHeight)/(double)singleHeight+1)*boxHeight);
                  single.setBounds(newX, newY, boxWidth, boxHeight);
                  single.setFont(getFont().deriveFont((float)fontSize).deriveFont(Font.BOLD));
               }
            }
            if(right!=null){
               int rightWidth = right.getWidth(),
                   rightHeight = right.getHeight(),
                   newX = (int)(((right.getX()-(pass*rightWidth+rightWidth/2))/(double)rightWidth+pass)*boxWidth)+xStart,
                   newY = (int)(((right.getY()-rightHeight)/(double)rightHeight+1)*boxHeight);
               right.setBounds(newX, newY, boxWidth, boxHeight);
               right.setFont(getFont().deriveFont((float)fontSize).deriveFont(Font.BOLD));
            }
            repaint();
         }
      }
      
      public void componentMoved(ComponentEvent e){
      }
            
      public void componentShown(ComponentEvent e){
      }
      
      public void componentHidden(ComponentEvent e){
      }
   }
   
   private class BubbleSortController implements ActionListener{
      public void actionPerformed(ActionEvent e){
         step = sldAnimationSpeed.getValue();
         if(pass<boxes.length-1){
            if(index<boxes.length-1-pass){
               left = boxes[index];
               right = boxes[index+1];
               if(action==0){
                  left.setOpaque(true);
                  right.setOpaque(true);
                  left.setBackground(red);
                  right.setBackground(blue);
                  left.setForeground(Color.WHITE);
                  right.setForeground(Color.WHITE);
                  message1 = "Pass "+(pass+1);
                  message2 = "Comparing: "+left.getText()+" <= "+right.getText();
                  pause = (animSpeedMax-step+1)*5;
                  action = 1;
               }
               else if(action==1){
                  if(pause>0){
                     pause--;
                  }
                  else{
                     if(Integer.parseInt(left.getText())>Integer.parseInt(right.getText())){
                        JLabel temp = boxes[index];
                        boxes[index] = boxes[index+1];
                        boxes[index+1] = temp;
                        message2 = "Swapping elements";
                        action = 2;
                     }
                     else{
                        action = 5;
                     }
                  }
               }
               else if(action==2){
                  if(right.getY()-step>yStart-boxHeight/2){
                     left.setLocation(left.getX(), left.getY()+step);
                     right.setLocation(right.getX(), right.getY()-step);
                  }
                  else{
                     left.setLocation(left.getX(), yStart+boxHeight/2);
                     right.setLocation(right.getX(), yStart-boxHeight/2);
                     action = 3;
                  }
               }
               else if(action==3){
                  if(right.getX()+step<(index+1)*boxWidth+xStart){
                     left.setLocation(left.getX()-step, left.getY());
                     right.setLocation(right.getX()+step, right.getY());
                  }
                  else{
                     left.setLocation((index)*boxWidth+xStart, left.getY());
                     right.setLocation((index+1)*boxWidth+xStart, right.getY());
                     action = 4;
                  }
               }
               else if(action==4){
                  if(right.getY()+step<yStart){
                     left.setLocation(left.getX(), left.getY()-step);
                     right.setLocation(right.getX(), right.getY()+step);
                  }
                  else{
                     left.setLocation(left.getX(), yStart);
                     right.setLocation(right.getX(), yStart);
                     action = 5;
                  }
               }
               else{
                  index++;
                  action = 0;
                  left.setOpaque(false);
                  right.setOpaque(false);
                  left.setForeground(Color.BLACK);
                  right.setForeground(Color.BLACK);
               }
            }
            else{
               pass++;
               index = 0;
               right.setOpaque(true);
               right.setBackground(yellow);
            }
         }
         else{
            finish();
            boxes[0].setOpaque(true);
            boxes[0].setBackground(yellow);
         }         
         repaint();
      }
   }
   
   private class SelectionSortController implements ActionListener{
      public void actionPerformed(ActionEvent e){
         step = sldAnimationSpeed.getValue();
         if(pass<boxes.length-1){
            if(action==0){
               message1 = "Pass: "+(pass+1);
               curr = pass;
               left = boxes[curr];
               left.setOpaque(true);
               left.setBackground(red);
               left.setForeground(Color.WHITE);
               if(left.getY()-step>0){
                  message2 = "Setting minimum";
                  left.setLocation(left.getX(), left.getY()-step);
               }
               else{
                  left.setLocation(left.getX(), 0);
                  index = curr + 1;
                  action = 1;
               }
            }
            else if(index<boxes.length){
               if(action==1){
                  right = boxes[index];
                  right.setBackground(blue);
                  right.setOpaque(true);
                  right.setForeground(Color.WHITE);
                  pause = (animSpeedMax-step+1)*5;
                  message2 = "Comparing: "+left.getText()+" <= "+right.getText();
                  action = 2;
               }
               else if(action==2){
                  if(pause>0){
                     pause--;
                  }
                  else{
                     if(Integer.parseInt(left.getText())>Integer.parseInt(right.getText())){
                        curr = index;
                        message2 = "Setting minimum";
                        if(left.getY()+step<yStart){
                           left.setLocation(left.getX(), left.getY()+step);
                           right.setLocation(right.getX(), right.getY()-step);
                        }
                        else{
                           left.setLocation(left.getX(), yStart);
                           right.setLocation(right.getX(), 0);
                           left.setOpaque(false);
                           left.setForeground(Color.BLACK);
                           left = right;
                           left.setBackground(red);
                           action = 3;
                        }
                     }
                     else{
                        right.setOpaque(false);
                        right.setForeground(Color.BLACK);
                        action = 3;
                     }
                  }
               }
               else{
                  index++;
                  action = 1;
               }
            }
            else if(curr==pass){
               message2 = "Returning element";
               if(left.getY()+step<yStart){
                  left.setLocation(left.getX(), left.getY()+step);
               }
               else{
                  left.setLocation(left.getX(), yStart);
                  curr = -1;
                  action = 7;
               }
            }
            else if(action==1){
               right = boxes[pass];
               right.setBackground(blue);
               right.setForeground(Color.WHITE);
               right.setOpaque(true);
               message2 = "Swapping elements";
               action = 4;
            }
            else if(action==4){
               if(right.getY()+step<yStart+boxHeight){
                  right.setLocation(right.getX(), right.getY()+step);
               }
               else{
                  right.setLocation(right.getX(), yStart+boxHeight);
                  action = 5;
               }
            }
            else if(action==5){
               if(left.getX()-step>xStart+pass*boxWidth){
                  left.setLocation(left.getX()-step, left.getY());
                  right.setLocation(right.getX()+step, right.getY());
               }
               else{
                  left.setLocation(xStart+pass*boxWidth, left.getY());
                  right.setLocation(xStart+curr*boxWidth, right.getY());
                  action = 6;
               }
            }
            else if(action==6){
               if(left.getY()+step<yStart){
                  left.setLocation(left.getX(), left.getY()+step);
                  right.setLocation(right.getX(), right.getY()-step);
               }
               else{
                  left.setLocation(left.getX(), yStart);
                  right.setLocation(right.getX(), yStart);
                  JLabel temp = boxes[pass];
                  boxes[pass] = boxes[curr];
                  boxes[curr] = temp;
                  right.setOpaque(false);
                  right.setForeground(Color.BLACK);
                  action = 7;
               }
            }
            else{
               left.setBackground(yellow);
               left.setForeground(Color.BLACK);
               pass++;
               action = 0;
               index = 0;
            }
         }
         else{
            finish();
            boxes[boxes.length-1].setOpaque(true);
            boxes[boxes.length-1].setBackground(yellow);
         }
         repaint();
      }
   }
   
   private class InsertionSortController implements ActionListener{
      public void actionPerformed(ActionEvent e){
         step = sldAnimationSpeed.getValue();
         if(pass<boxes.length-1){
            if(action==0){
               message1 = "Pass: "+(pass+1);
               right = boxes[pass+1];
               right.setOpaque(true);
               right.setBackground(red);
               right.setForeground(Color.WHITE);
               message2 = "Setting current value";
               action = 1;
            }
            else if(action==1){
               if(right.getY()-step>0){
                  right.setLocation(right.getX(), right.getY()-step);
               }
               else{
                  right.setLocation(right.getX(), 0);
                  index = pass;
                  action = 2;
               }
            }
            else if(action==2){
               if(index>=0){
                  left = boxes[index];
                  left.setBackground(blue);
                  left.setOpaque(true);
                  left.setForeground(Color.WHITE);
                  pause = (animSpeedMax-step+1)*5;
                  message2 = "Comparing: "+left.getText()+" <= "+right.getText();
                  action = 3;
               }
               else{
                  action = 4;
               }
            }
            else if(action==3){
               if(pause>0){
                  --pause;
               }
               else{
                  if(Integer.parseInt(left.getText())>Integer.parseInt(right.getText())){
                     message2 = "Moving element";
                     curr = index;
                     if(left.getX()+step<xStart+(index+1)*boxWidth){
                        left.setLocation(left.getX()+step, left.getY());
                     }
                     else{
                        left.setLocation(xStart+(index+1)*boxWidth, left.getY());
                        boxes[index+1] = boxes[index];
                        --index;
                        action = 2;
                        left.setOpaque(false);
                        left.setForeground(Color.BLACK);
                     }
                  }
                  else{
                     action = 4;
                     left.setOpaque(false);
                     left.setForeground(Color.BLACK);
                  }
               }
            }
            else if(action==4){
               if(index==pass){
                  message2 = "Returning element";
                  if(right.getY()+step<yStart){
                     right.setLocation(right.getX(), right.getY()+step);
                  }
                  else{
                     right.setLocation(right.getX(), yStart);
                     action = 6;
                  }
               }
               else{
                  message2 = "Inserting element";
                  if(right.getX()-step>xStart+curr*boxWidth){
                     right.setLocation(right.getX()-step, right.getY());
                  }
                  else{
                     right.setLocation(xStart+curr*boxWidth, right.getY());
                     action = 5;
                  }
               }
            }
            else if(action==5){
               if(right.getY()+step<yStart){
                  right.setLocation(right.getX(), right.getY()+step);
               }
               else{
                  right.setLocation(right.getX(), yStart);
                  boxes[curr] = right;
                  action = 6;
               }
            }
            else{
               right.setOpaque(false);
               right.setForeground(Color.BLACK);
               ++pass;
               action = 0;
               index = 0;
            }
         }
         else{
            finish();
            for(JLabel single: boxes){
               single.setOpaque(true);
               single.setBackground(yellow);
               single.setForeground(Color.BLACK);
            }
         }
         repaint();
      }
   }
}
