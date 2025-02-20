import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Rectangle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import java.io.FileNotFoundException;
import java.io.PrintStream;

import java.util.Random;
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.Timer;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class TreeTraversal extends JFrame{
   private int action = 0, 
               delay = 2500,
               randomLevel = 5, 
               randomStart = 0,
               randomEnd = 100,
               randomChance = 50,
               fontSize = 12,
               traverseSpeedMin = 1,
               traverseSpeedMax = 50,
               trverseSpeedMid = (traverseSpeedMin+traverseSpeedMax)/2,
               nodeSize = 50;
   private JPanel pnlNorth = new JPanel(new GridLayout(2,1)),
                  pnlTreeActions = new JPanel(),
                  pnlTraverseActions = new JPanel();
   private JButton btnAdd = new JButton("Add"),
                   btnUpdate = new JButton("Update"),
                   btnDelete = new JButton("Delete"),
                   btnGenerate = new JButton("Random"),
                   btnTraverse = new JButton("Traverse"),
                   btnOptions = new JButton("Options"),
                   btnSave = new JButton("Save"),
                   btnLoad = new JButton("Load");
   private JLabel lblTreeActions = new JLabel("Node"),
                  lblTraverseActions = new JLabel("Algorithm "),
                  lblStatus = new JLabel("Draw a Tree"),
                  lblSpeedSlow = new JLabel("Slow"),
                  lblSpeedFast = new JLabel("Fast"),
                  lblSpeed = new JLabel("Animation Speed"),
                  lblFontSizeMinimum = new JLabel("Min"),
                  lblFontSizeMaximum = new JLabel("Max"),
                  lblFont = new JLabel("Font Size"),
                  lblNodeSizeMinimum = new JLabel("Min"),
                  lblNodeSizeMaximum = new JLabel("Max"),
                  lblNode = new JLabel("Node Size"),
                  lblRandomLevel = new JLabel("Level"),
                  lblRandomStart = new JLabel("Start"),
                  lblRandomEnd = new JLabel("End"),
                  lblRandomChance = new JLabel("Chance"),
                  lblRandom = new JLabel("Random"),
                  lblData = new JLabel("Data");
   private JTextField txtInput = new JTextField(3),
                      txtRandomLevel = new JTextField(Integer.toString(randomLevel)),
                      txtRandomStart = new JTextField(Integer.toString(randomStart)),
                      txtRandomEnd = new JTextField(Integer.toString(randomEnd)),
                      txtRandomChance = new JTextField(Integer.toString(randomChance));
   private JSlider sldTraverseSpeed = new JSlider(traverseSpeedMin, traverseSpeedMax, trverseSpeedMid),
                   sldFontSize = new JSlider(10,20,12),
                   sldNodeSize = new JSlider(50,100,nodeSize);
   private JComboBox<String> cmbAlgorithm = new JComboBox<String>(new String[]{"Pre-Order","In-Order","Post-Order"});
   private JFrame app = this,
                  frmOptions;
   
   private JPanel board = new DrawCanvas(null); 
   private MouseController mouseTask = new MouseController();
   private JLabel selector;
   private Timer runner;
   private Stack<Node> list;
   private Node curr;
   
   private Tree data;
   
   private Color red = new Color(200, 0, 0);
   private Color blue = new Color(0, 0, 200);
   private Color yellow = new Color(200, 200, 0);

   private String value;
   
   private Random r = new Random();
   
   public TreeTraversal(){
      ActionListener treeDataController = new TreeDataController();
      btnAdd.addActionListener(treeDataController);
      btnAdd.setEnabled(false);
      btnUpdate.setEnabled(false);
      btnDelete.addActionListener(treeDataController);
      btnUpdate.addActionListener(treeDataController);
      btnGenerate.addActionListener(treeDataController);
      btnTraverse.addActionListener(new TraverseAlgorithmController());
      btnOptions.addActionListener(new OptionsController());
      txtInput.getDocument().addDocumentListener(new InputController());
      DocumentListener randomInputController = new RandomInputController();
      txtRandomLevel.getDocument().addDocumentListener(randomInputController);
      txtRandomStart.getDocument().addDocumentListener(randomInputController);
      txtRandomEnd.getDocument().addDocumentListener(randomInputController);
      txtRandomChance.getDocument().addDocumentListener(randomInputController);
      sldFontSize.addChangeListener(new FontSizeController());
      sldNodeSize.addChangeListener(new NodeSizeController());
      sldTraverseSpeed.addChangeListener(new TraverseSpeedController());
      ActionListener dataController = new DataController();
      btnSave.addActionListener(dataController);
      btnLoad.addActionListener(dataController);

      selector = new JLabel("");
      selector.setBorder(BorderFactory.createLineBorder(Color.BLACK));

      board.setBackground(Color.WHITE);
      board.setBorder(BorderFactory.createLineBorder(Color.BLACK));

      pnlTreeActions.add(lblTreeActions);
      pnlTreeActions.add(txtInput);
      pnlTreeActions.add(btnAdd);
      pnlTreeActions.add(btnUpdate);
      pnlTreeActions.add(btnDelete);
      pnlTreeActions.add(btnGenerate);
      pnlTraverseActions.add(lblTraverseActions);      
      pnlTraverseActions.add(cmbAlgorithm);
      pnlTraverseActions.add(btnTraverse);
      pnlTraverseActions.add(btnOptions);
      pnlNorth.add(pnlTreeActions);
      pnlNorth.add(pnlTraverseActions);
      add(pnlNorth, BorderLayout.NORTH);
      add(board);
      add(lblStatus, BorderLayout.SOUTH);
   }
   
   class Tree{
      private Node root;
      
      public Tree(Node node){
         root = node;
      }
      
      public void setRootNode(Node root){
         this.root = root;
      }
      
      public Node getRootNode(){
         return root;
      }
      
      public int getTreeSize(){
         return getNodeCount(root);
      }
      
      public int getNodeCount(Node node){
         if(node==null){
            return 0;
         }
         return 1+getNodeCount(node.getLeftNode())+getNodeCount(node.getRightNode());
      }
      
      public int getTreeHeight(){
         return getNodeLevel(root)-1;
      }
      
      public int getNodeLevel(Node node){
         if(node==null){
            return 0;
         }
         int left = getNodeLevel(node.getLeftNode()),
             right = getNodeLevel(node.getRightNode()),
             max = 0;
         if(left>right){
            max = left;
         }
         else{
            max = right;
         }
         return 1 + max;
      }
   }
      
   class Node extends JLabel{
      private String value;
      private Node parent, left, right;
      private Color fill;
      
      public Node(String value, Node parent, Color fill, Color text){
         super(value, JLabel.CENTER);
         this.value = value;
         this.parent = parent;
         this.fill = fill;
         setForeground(text);
         setFont(getFont().deriveFont((float) fontSize).deriveFont(Font.BOLD));
         addMouseListener(mouseTask);
         addMouseMotionListener(mouseTask);
      }
      
      public void paintComponent(Graphics g){
         double dblSide = getWidth();
         if(dblSide>getHeight()){
            dblSide = getHeight();
         }
         dblSide*=(nodeSize/100.0);
         int intSide = (int) dblSide;
         int x = (getWidth()-intSide)/2,
             y = (getHeight()-intSide)/2;
         if(fill!=null){
            g.setColor(fill);
            g.fillOval(x, y, intSide, intSide);
         }
         g.setColor(Color.BLACK);
         g.drawOval(x, y, intSide, intSide);
         super.paintComponent(g);
      }
      
      public void setParentNode(Node parent){
         this.parent = parent;
      }
      
      public void setLeftNode(Node left){
         this.left = left;
      }
      
      public void setRightNode(Node right){
         this.right = right;
      }
      
      public void setValue(String value){
         this.value = value;
         setText(value);
      }
      
      public void setFillColor(Color fill){
         this.fill = fill;
      }
      
      public Node getParentNode(){
         return parent;
      }
      
      public Node getLeftNode(){
         return left;
      }
      
      public Node getRightNode(){
         return right;
      }
      
      public String toString(){
         return value;
      }
      
      public Color getFillColor(){
         return fill;
      }
   }

   class OptionsController implements ActionListener{
      public void actionPerformed(ActionEvent e){
         if(frmOptions==null){
            JPanel pnlAnimationSpeed = new JPanel(),
                   pnlRandomOptions = new JPanel(new GridLayout(2,4,10,0)),
                   pnlFontSize = new JPanel(),
                   pnlNodeSize = new JPanel(),
                   pnlData = new JPanel();

            pnlRandomOptions.add(lblRandomLevel);
            pnlRandomOptions.add(txtRandomLevel);
            pnlRandomOptions.add(lblRandomChance);
            pnlRandomOptions.add(txtRandomChance);
            pnlRandomOptions.add(lblRandomStart);
            pnlRandomOptions.add(txtRandomStart);
            pnlRandomOptions.add(lblRandomEnd);
            pnlRandomOptions.add(txtRandomEnd);

            pnlAnimationSpeed.add(lblSpeedFast);
            pnlAnimationSpeed.add(sldTraverseSpeed);
            pnlAnimationSpeed.add(lblSpeedSlow);

            pnlFontSize.add(lblFontSizeMinimum);
            pnlFontSize.add(sldFontSize);
            pnlFontSize.add(lblFontSizeMaximum);

            pnlNodeSize.add(lblNodeSizeMinimum);
            pnlNodeSize.add(sldNodeSize);
            pnlNodeSize.add(lblNodeSizeMaximum);

            pnlData.add(btnSave);
            pnlData.add(btnLoad);

            frmOptions = new JFrame("Options");
            frmOptions.setLayout(new GridLayout(10,1));
            frmOptions.add(lblRandom);
            frmOptions.add(pnlRandomOptions);
            frmOptions.add(lblSpeed);
            frmOptions.add(pnlAnimationSpeed);
            frmOptions.add(lblFont);
            frmOptions.add(pnlFontSize);
            frmOptions.add(lblNode);
            frmOptions.add(pnlNodeSize);
            frmOptions.add(lblData);
            frmOptions.add(pnlData);
         }
         frmOptions.setSize(300,400);
         frmOptions.setResizable(false);
         frmOptions.setVisible(true);
      }
   }

   class DataController implements ActionListener{
      public void actionPerformed(ActionEvent e){
         if(e.getSource()==btnSave){
            if(data!=null){
               String location = JOptionPane.showInputDialog(frmOptions, "Enter location", "Save", JOptionPane.QUESTION_MESSAGE);
               if(location!=null){
                  PrintStream out;
                  try{ 
                     out = new PrintStream(location);
                     out.println("List: ("+transformList(data.getRootNode())+")");
                     out.println("Pre-Order:"+performTraversal(data.getRootNode(),0));
                     out.println("In-Order:"+performTraversal(data.getRootNode(),1));
                     out.println("Post-Order:"+performTraversal(data.getRootNode(),2));
                     out.close();
                  }
                  catch(FileNotFoundException x){ }
               }
            }
            else
               JOptionPane.showMessageDialog(frmOptions, "Draw a Tree First!");
         }
         else{
            String contents = JOptionPane.showInputDialog(frmOptions, "Enter list representation", "Load", JOptionPane.QUESTION_MESSAGE).trim();
            if(contents!=null){
               Tree loadData = null;
               boolean error = false;
               if(contents.charAt(0)=='(' && contents.charAt(contents.length()-1)==')'){
                  Stack<Node> previousList = new Stack<Node>();
                  String text = contents.substring(1, contents.length()-1);
                  boolean rightNode=false;
                  Node temp = null;
                  String token = "";
                  for(int i=0; i<text.length(); i++){
                     char currSymbol = text.charAt(i);
                     if(currSymbol=='('||currSymbol==')'||currSymbol==','){
                        if(!token.equals("")){
                           if(!token.equalsIgnoreCase("null")){
                              temp = new Node(token, null, Color.WHITE, Color.BLACK);
                              if(loadData==null)
                                 loadData = new Tree(temp);
                              else if(previousList.size()>0){
                                 Node previous = previousList.peek();
                                 temp.setParentNode(previous);
                                 if(rightNode)
                                    if(previous.getRightNode()!=null)
                                       error = true;
                                    else{
                                       previous.setRightNode(temp);
                                       rightNode = false;
                                    }
                                 else
                                    if(previous.getLeftNode()!=null)
                                       error = true;
                                    else
                                       previous.setLeftNode(temp);
                              }
                              else
                                 error = true;
                           }
                           token = "";
                        }
                        if(currSymbol=='(' && temp!=null){
                           previousList.push(temp);
                           temp = null;
                        }
                        else if(currSymbol==')')
                           if(previousList.size()>0)
                              previousList.pop();
                           else error=true;
                        else if(currSymbol==',')
                           if(!rightNode)
                              rightNode = true;
                           else
                              error = true;
                     }
                     else
                        token+=currSymbol;
                  }
                  if(!token.equals(""))
                     if(loadData==null)
                        loadData = new Tree(new Node(token, null, Color.WHITE, Color.BLACK));
                     else
                        error = true;
                  if(previousList.size()>0)
                     error = true;
               }
               else 
                  error = true;
               if(error)
                  JOptionPane.showMessageDialog(frmOptions, "Invalid List Representation!");
               else{
                  data = loadData;
                  repaint();
               }
            }
         }
      }
   }

   private String performTraversal(Node root, int algorithm){ 
      // 0 (Pre), 1 (In), 2 (Post)
      if(root==null) return "";
      String result,
             left = performTraversal(root.getLeftNode(), algorithm),
             right = performTraversal(root.getRightNode(), algorithm);
      switch(algorithm){
         case 0: result = " "+root+left+right; break;
         case 1: result = left+" "+root+right; break;
         default: result = left+right+" "+root;
      }
      return result;
   }

   private String transformList(Node root){
      if(root==null) return "";
      String result="", left = "", right = "";
      result += root;
      left = transformList(root.getLeftNode());
      right = transformList(root.getRightNode());
      if(!left.equals("") || !right.equals("")){
         if(left.equals(""))
            left = "NULL";
         if(!right.equals(""))
            right = ","+right;
         result += "("+left+right+")";
      }
      return result;
   }

   class InputController implements DocumentListener{
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
         if(txtInput.getText().length()>0){
            btnAdd.setEnabled(true);
            btnUpdate.setEnabled(true);
         }
         else{
            btnAdd.setEnabled(false);
            btnUpdate.setEnabled(false);
         }
      }
   }

   class RandomInputController implements DocumentListener{
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
            randomLevel = Integer.parseInt(txtRandomLevel.getText());
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
         try{
            randomChance = Integer.parseInt(txtRandomChance.getText());
         }
         catch(NumberFormatException e){}
      }
   }

   class FontSizeController implements ChangeListener{
      public void stateChanged(ChangeEvent e){
         fontSize = sldFontSize.getValue();
         if(data!=null)
            changeFontSize(data.getRootNode(), (float) fontSize);
      }
   }

   class NodeSizeController implements ChangeListener{
      public void stateChanged(ChangeEvent e){
         nodeSize = sldNodeSize.getValue();
         repaint();
      }
   }

   class TraverseSpeedController implements ChangeListener{
      public void stateChanged(ChangeEvent e){
         delay = sldTraverseSpeed.getValue()*100;
         if(runner!=null)
            runner.setDelay(delay);
      }
   }

   private void changeFontSize(Node curr, float size){
      curr.setFont(getFont().deriveFont(size).deriveFont(Font.BOLD));
      if(curr.getLeftNode()!=null)
         changeFontSize(curr.getLeftNode(), size);
      if(curr.getRightNode()!=null)
         changeFontSize(curr.getRightNode(), size);
   }

   class TreeDataController implements ActionListener{
      public void actionPerformed(ActionEvent e){
         if(e.getSource().equals(btnAdd))
            if(action==0){
               value = txtInput.getText();
               if(data==null){
                  data = new Tree(new Node(value, null, Color.WHITE, Color.BLACK));
                  lblStatus.setText("Tree Created");
                  txtInput.setText("");
                  repaint();
               }
               else{
                  disableControls();
                  btnAdd.setText("Cancel");
                  btnAdd.setEnabled(true);
                  action = 1;
               }
            }
            else{
               lblStatus.setText("Action Cancelled");
               reset();
            }
         else if(e.getSource().equals(btnUpdate))
            if(data!=null)
               if(action==0){
                  action = 2;
                  disableControls();
                  btnUpdate.setEnabled(true);
                  btnUpdate.setText("Cancel");
               }
               else{
                  lblStatus.setText("Action Cancelled");
                  reset();
               }
            else
               JOptionPane.showMessageDialog(app, "No nodes yet");
         else if(e.getSource().equals(btnDelete))
            if(data!=null)
               if(action==0){
                  action = 3;
                  disableControls();
                  btnDelete.setEnabled(true);
                  btnDelete.setText("Cancel");
               }
               else{
                  lblStatus.setText("Action Cancelled");
                  reset();
               }
            else
               JOptionPane.showMessageDialog(app, "No nodes yet");
         else{
            Node node = generateRandomNode(randomLevel, randomStart, randomEnd, randomChance, null);
            if(node!=null){
               data = new Tree(node);
               lblStatus.setText("Random Tree Generated");
            }
            repaint();
         }
      }
   }
   
   private Node generateRandomNode(int level, int start, int end, int chance, Node parent){
      Node node = null;
      if(level>=0 && r.nextInt(100)<chance){
         node = new Node(Integer.toString(r.nextInt(end-start)+start), parent, Color.WHITE, Color.BLACK);
         node.setLeftNode(generateRandomNode(level-1, start, end, chance, node));
         node.setRightNode(generateRandomNode(level-1, start, end, chance, node));
      }
      return node;
   }
   
   private void drawNode(Graphics g, Node node, int x, int y, int width, int height){
      if(node!=null){
         Node parent = node.getParentNode();
         if(parent!=null){
            g.drawLine(parent.getX()+width, parent.getY()+height/2, x+width/2, y+height/2);
         }
         node.setBounds(x, y, width, height);
         board.add(node);
         int centerX = x+width/2,
             centerY = y+height/2;
         drawNode(g, node.getLeftNode(), x, y+height, width/2, height);
         drawNode(g, node.getRightNode(), x+width/2, y+height, width/2, height);
      }
   }
   
   private void showSelector(Rectangle loc, Color fill){
      selector.setBounds(loc);
      selector.setOpaque(true);
      selector.setBackground(fill);
      selector.setVisible(true);
   }
   
   class MouseController implements MouseListener, MouseMotionListener{
      public void mouseMoved(MouseEvent e){
         Node node = (Node) e.getComponent();
         if(action==1){
            int boxWidth = node.getWidth()/2;
            if(e.getX()<boxWidth){
               showSelector(new Rectangle(node.getX(), node.getY(), boxWidth, node.getHeight()), blue);
               lblStatus.setText("Add Node "+value+" as Left Child of Node "+node.toString());
            }
            else{
               showSelector(new Rectangle(node.getX()+boxWidth, node.getY(), boxWidth, node.getHeight()), blue);
               lblStatus.setText("Add Node "+value+" as Right Child of Node "+node.toString());
            }
         }
         else if(action==2){
            showSelector(node.getBounds(), yellow);
            lblStatus.setText("Update Node "+node.toString());
         }
         else if(action==3){
            showSelector(node.getBounds(), red);
            lblStatus.setText("Delete Node "+node.toString());
         }
         else if(action==0){
            lblStatus.setText("Node "+node.toString());
         }
         repaint();
      }
      
      public void mouseDragged(MouseEvent e){
      }
      
      public void mouseEntered(MouseEvent e){
      }
      
      public void mouseExited(MouseEvent e){
         if(action!=4 && action!=5){
            selector.setVisible(false);
            lblStatus.setText("Tree data     Height: "+data.getTreeHeight()+"     Size: "+data.getTreeSize());
            repaint();
         }
      }
      
      public void mousePressed(MouseEvent e){
      }
      
      public void mouseReleased(MouseEvent e){
      }
      
      public void mouseClicked(MouseEvent e){
         Node node = (Node) e.getComponent();
         if(action==1){
            if(e.getX()<node.getWidth()/2){
               if(node.getLeftNode()==null){
                  node.setLeftNode(new Node(value, node, Color.WHITE, Color.BLACK));
                  lblStatus.setText("Node Added");
                  txtInput.setText("");
                  reset();
               }
               else{
                  JOptionPane.showMessageDialog(app, "Left Child Node Existing");
               }
            }
            else{
               if(node.getRightNode()==null){
                  node.setRightNode(new Node(value, node, Color.WHITE, Color.BLACK));
                  lblStatus.setText("Node Added");
                  txtInput.setText("");
                  reset();
               }
               else{
                  JOptionPane.showMessageDialog(app, "Right Child Node Existing");
               }
            }
         }
         else if(action==2){
            node.setValue(txtInput.getText());
            reset();
            txtInput.setText("");
            lblStatus.setText("Node Updated");
         }
         else if(action==3){
            if(node.getLeftNode()==null && node.getRightNode()==null){
               Node parent = node.getParentNode();
               if(parent!=null){
                  if(parent.getLeftNode()==node){
                     parent.setLeftNode(null);
                  }
                  else{
                     parent.setRightNode(null);
                  }
                  lblStatus.setText("Node Deleted");
               }
               else{
                  data = null;
               }
               lblStatus.setText("Tree Deleted");    
               reset();
            }
            else{
               JOptionPane.showMessageDialog(app, "Cannot Delete Node. Child Nodes Existing");
            }
         }
      }
   }
   
   private void reset(){
      selector.setVisible(false);
      action = 0;
      value = "";
      btnAdd.setText("Add");
      btnUpdate.setText("Update");
      btnDelete.setText("Delete");
      btnTraverse.setText("Traverse");
      cmbAlgorithm.setEnabled(true);
      btnDelete.setEnabled(true);
      btnGenerate.setEnabled(true);
      btnTraverse.setEnabled(true);
      txtInput.setEnabled(true);
      lblStatus.setText(" ");
      if(!txtInput.getText().equals("")){
         btnAdd.setEnabled(true);
         btnUpdate.setEnabled(true);
      }
      if(data!=null)
         resetNodeColor(data.getRootNode(), Color.WHITE, Color.BLACK); 
      repaint();
   }
   
   private void disableControls(){
      txtInput.setEnabled(false);
      btnAdd.setEnabled(false);
      btnUpdate.setEnabled(false);
      btnDelete.setEnabled(false);
      btnGenerate.setEnabled(false);
      cmbAlgorithm.setEnabled(false);
   }
   
   class DrawCanvas extends JPanel{
      public DrawCanvas(LayoutManager m){
         super(m);
      }
      
      public void paintComponent(Graphics g){
         super.paintComponent(g);
         board.removeAll();
         if(data!=null){
            int level = data.getNodeLevel(data.getRootNode()),
                height = board.getHeight()/level;
            drawNode(g, data.getRootNode(), 0, 0, board.getWidth(), height);
         }
         board.add(selector);
      }
   }
   
   private void resetNodeColor(Node node, Color fill, Color text){
      if(node!=null){
         node.setFillColor(fill);
         node.setForeground(text);
         resetNodeColor(node.getLeftNode(), fill, text);
         resetNodeColor(node.getRightNode(), fill, text);
      }
   }
   
   class TraverseAlgorithmController implements ActionListener{
      public void actionPerformed(ActionEvent e){
         if(action==0){
            disableControls();
            if(data!=null){
               curr = data.getRootNode();
               resetNodeColor(curr, blue, Color.WHITE);
               repaint();
            }
            list = new Stack<Node>(); 
            ActionListener algorithm;
            switch(cmbAlgorithm.getSelectedIndex()){
               case 0: algorithm = new PreOrderTraverseTask(); break;
               case 1: algorithm = new InOrderTraverseTask(); break;
               default: algorithm = new PostOrderTraverseTask();
            }
            runner = new Timer(delay, algorithm);
            lblStatus.setText("Traversal:");
            btnTraverse.setText("Pause");
            action = 4;
            runner.start();
         }
         else if(action==4){
            if(runner.isRunning()){
               runner.stop();
               btnTraverse.setText("Continue");
            }
            else{
               runner.start();
               btnTraverse.setText("Pause");
            }
         }
         else{
            if(data!=null){
               resetNodeColor(data.getRootNode(), blue, Color.WHITE);
            }
            reset();
         }
      }
   }
   
   class PreOrderTraverseTask implements ActionListener{
      public void actionPerformed(ActionEvent e){
         if(curr==null && list.empty()){
            btnTraverse.setText("Reset");
            list = null;
            runner.stop();
            action = 5;
            JOptionPane.showMessageDialog(app, "Preorder Traversal Done\nOutput: "+lblStatus.getText().substring(lblStatus.getText().indexOf(":")+1));
         }
         else if(curr==null){
            curr = list.pop().getRightNode();
         }
         else{
            curr.setFillColor(red);
            lblStatus.setText(lblStatus.getText()+" "+curr.toString());
            list.push(curr);
            curr = curr.getLeftNode();
         }
         repaint();
      }
   }
   
   class InOrderTraverseTask implements ActionListener{
      public void actionPerformed(ActionEvent e){
         if(curr==null && list.empty()){
            btnTraverse.setText("Reset");
            list = null;
            runner.stop();
            action = 5;
            JOptionPane.showMessageDialog(app, "Inorder Traversal Done\nOutput: "+lblStatus.getText().substring(lblStatus.getText().indexOf(":")+1));
         }
         else if(curr==null){
            curr = list.pop();
            lblStatus.setText(lblStatus.getText()+" "+curr.toString());
            curr.setFillColor(red);
            curr.setForeground(Color.WHITE);
            curr = curr.getRightNode();
         }
         else{
            curr.setFillColor(yellow);
            curr.setForeground(Color.BLACK);
            list.push(curr);
            curr = curr.getLeftNode();
         }
         repaint();
      }
   }
   
   class PostOrderTraverseTask implements ActionListener{
      public void actionPerformed(ActionEvent e){
         if(curr==null && list.empty()){
            btnTraverse.setText("Reset");
            list = null;
            runner.stop();
            action = 5;
            JOptionPane.showMessageDialog(app, "Postorder Traversal Done\nOutput: "+lblStatus.getText().substring(lblStatus.getText().indexOf(":")+1));
         }
         else if(curr==null){
            curr = list.peek().getRightNode();
            if(curr==null || curr.getFillColor()==red){
               curr = list.pop();
               lblStatus.setText(lblStatus.getText()+" "+curr.toString());
               curr.setFillColor(red);
               curr.setForeground(Color.WHITE);
               curr = null;
            }
         }
         else{
            curr.setFillColor(yellow);
            curr.setForeground(Color.BLACK);
            list.push(curr);
            curr = curr.getLeftNode();
         }
         repaint();
      }
   }
   
   public static void main(String[]args){
      JFrame app = new TreeTraversal();
      app.setTitle("Tree Traversal Algorithms Simulator");
      app.setSize(500,500);
      app.setVisible(true);
      app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   }
}
