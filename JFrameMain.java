package govtrack4;

import java.awt.Color;
//import javax.swing.SwingConstants;

public class JFrameMain extends JFrame implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4669628829294460334L;
	private JPanel contentPane;
	private JTable tableResults;
	private JTable tableID;
	private JTextField textBillSearch;
//	private JComboBox comboBoxState = new JComboBox();
//    private javax.swing.JComboBox comboBoxState;
	private JCheckBox chckbxSelectAll = new JCheckBox();
	private JScrollPane scrollPane = new JScrollPane();
	private JScrollPane scrollPane_1 = new JScrollPane();
	
    String GovMember = "";
    String StateAbrev = "";    
    String strState = "";
    String BillInfo = "";
    String strOS = "";
    int CurrentRow = 0;
    int LastRow = 0;
    Statement stmt = null;
    ResultSet rs = null;
    PreparedStatement pst = null;
    Connection conn = null;
    Boolean bRet = false;
    DefaultTableModel model;
    DefaultTableModel model2;
    ArrayList<String> lstPersonId;  
    String Please_Select = "- Please Select -";
    ComboBoxModel<?> model4;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new JFrameMain().setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */

	public JFrameMain() {
					
		setTitle("GovTrack");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 870, 366);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		// Populate the Combo Box with the States 
		JLabel lblState = new JLabel("State");
		String[] comboBoxData={"- Please Select -","Alabama","Alaska","Arizona","Arkansas",
        		"California","Colorado","Connecticut","Delaware","Florida","Georgia",
        		"Hawaii","Idaho","Illinois","Indiana","Iowa","Kansas",
        		"Kentucky","Louisiana","Maine","Maryland","Massachusetts","Michigan",
        		"Minnesota","Mississippi","Missouri","Montana","Nebraska","Nevada",
        		"New Hampshire","New Jersey","New Mexico","New York","North Carolina","North Dakota",
        		"Ohio","Oklahoma","Oregon","Pennsylvania","Rhode Island","South Carolina",
        		"South Dakota","Tennessee","Texas","Utah","Vermont","Virginia",
        		"Washington","West Virginia","Wisconsin","Wyoming"};
                		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		final JComboBox comboBoxState = new JComboBox(comboBoxData);
		comboBoxState.setMaximumRowCount(7);
		comboBoxState.setName("comboBoxState");
		lblState.setLabelFor(comboBoxState);
		comboBoxState.setActionCommand("StateSelectAction");
		comboBoxState.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent event) {
        	String Action = event.getActionCommand();
        	
        	if (Action.equals("StateSelectAction"))
        	{
				model4 = comboBoxState.getModel();
            	//
                // Get the source of the component, which is our combo box.
                @SuppressWarnings("rawtypes")
				JComboBox comboBoxState = (JComboBox) event.getSource();
                // If the item selected is the "- Please Select - " option
                // then set strState to nothing
                String selectedState = comboBoxState.getSelectedItem().toString();
                if (selectedState.equals(Please_Select))
                    strState = "";
                else if (!GovMember.isEmpty())
                {  // Getting rid of the first option "- Please Select - "
                	// once something is selected and copy that to strState
                    String comboBoxFirstOption = model4.getElementAt(0).toString();
                    if (comboBoxFirstOption.equals(Please_Select))
                    	comboBoxState.removeItemAt(0);
                    strState = comboBoxState.getSelectedItem().toString();
                }
                   // Another state is selected
                if (!"- Please Select -".equals(comboBoxState.getItemAt(0).toString())) 
                {  // Another change of State Reset Select All Check Box
	                if (chckbxSelectAll.isSelected())
	                	chckbxSelectAll.setSelected(false);
                } 
                 // Selection of Congress radio button occurred 
                if (!GovMember.equals(Please_Select))
                {   // Get the abbreviation of the selected state      
                    StateAbrev SA = new StateAbrev();
                    if (!strState.isEmpty())
                        StateAbrev = SA.StateID(strState);
                    // Once State is selected populate table with representatives
                    if (!StateAbrev.isEmpty())
                    {   // Populate the table 
                        PopulateTable PT = new PopulateTable();
                        PT.SelectEvent();                            
                    } 
                }
                else // If Congress radio button not selected show message box
                {    // to remind the user to do so
                    String[] choices = {"OK"};
                   JOptionPane.showOptionDialog(null 
                   , "Please select which Congressional House before choosing State search. "
                   , "State Information"
                   , JOptionPane.OK_OPTION
                   , JOptionPane.QUESTION_MESSAGE
                   , null
                   , choices
                   , "OK"
                   );       
                   comboBoxState.setSelectedIndex(0);
                   
                }   
            }
            }
          });
     				
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(32, 39));
		scrollPane.setSize(new Dimension(50, 50));
		scrollPane.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		// Select All check box
		final JCheckBox chckbxSelectAll = new JCheckBox("Select All");
		chckbxSelectAll.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
	        int i = 0;

	        // If Select All Check box is checked or unchecked
	        if ("Select All".equals(evt.getActionCommand().toString()))
	        {
	            for (i = 0; i < LastRow; i++)
	            {
	                // Set all check box items in both jTables to true
	                if(chckbxSelectAll.isSelected())
	                {
	                    tableResults.setValueAt(Boolean.TRUE, i, 0); 
	                    tableID.setValueAt(Boolean.TRUE, i, 0);
	                }
	                else // Set all check box items in both jTables to false
	                {
	                    tableResults.setValueAt(Boolean.FALSE, i, 0);                    
	                    tableID.setValueAt(Boolean.FALSE, i, 0);
	                }
	            }
	        }			
		}
		});
		chckbxSelectAll.setName("chckbxSelectAll");
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setPreferredSize(new Dimension(18, 18));
		scrollPane_1.setVisible(false);
		
		JLabel lblBillSearch = new JLabel("Describe which specific Bill to Search for");
		
		textBillSearch = new JTextField();
		lblBillSearch.setLabelFor(textBillSearch);
		textBillSearch.setColumns(10);

		// SEARCH button
		JButton btnSearch = new JButton("SEARCH");
        btnSearch.setActionCommand("SearchAction");
        btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {        	
            
			Boolean flgRep = false;
            int i, iSelected = 0;
            String Action = evt.getActionCommand();
            if (Action.equals("SearchAction"))
            {   
            	// Is there any specific Bill to search for
                BillInfo = textBillSearch.getText().toString();
                // See if any drop down combo box is unselected
                
                if ((Please_Select.equals(GovMember)) 
                        || (Please_Select.equals(comboBoxState.getItemAt(0).toString())))
                {
                    String[] choices = {"OK"};
                   JOptionPane.showOptionDialog(null 
                   , "Please select which type of search from \n Congressional House "
                           + " and State."
                   , "Search Information"
                   , JOptionPane.OK_OPTION
                   , JOptionPane.QUESTION_MESSAGE
                   , null
                   , choices
                   , "OK"
                   );    
                   
                }
                else
                {
                    // Clear the list array for PersonID
                    lstPersonId.clear();
                    
                    for (i = 0; i < LastRow; i++)
                    {
                        if (Boolean.TRUE.equals(model.getValueAt(i, 0)))
                        {
                            // Populate the list array with those selected officials ID
                            lstPersonId.add(iSelected, tableID.getValueAt(i, 1).toString());
                            iSelected++;                   
                            flgRep = true;
                        }
                    }
                    // Conditions were met but no Representative was selected
                    if (!flgRep)
                    {
                        String[] choices = {"OK"};
                       JOptionPane.showOptionDialog(null 
                       , "Please Select which Representative to Query."
                       , "Search Information"
                       , JOptionPane.OK_OPTION
                       , JOptionPane.QUESTION_MESSAGE
                       , null
                       , choices
                       , "OK"
                       );    
                    }
                    else
                    {
                        // If nothing is provided to search fill BillInfo with something
                        if (BillInfo.isEmpty())
                            BillInfo = "BillInfo";
                        
                        // list array being places within a String
                        String sPersonId = lstPersonId.toString();
                        String sSize = Integer.toString(lstPersonId.size());
                        String [] args = 
                        {GovMember.toString(),strState.toString(),
                            BillInfo.toString(),sSize,sPersonId.toString()};
                        // Go to JFrameHeader screen
                        JFrameMain.this.setVisible(false);
                        JFrameResults.main(args);                    

                    }
                }
            }
		}        
    });
       
        // Congress Radio Button "Senate" and "House of Representatives"
		JLabel lblFed = new JLabel("Congress");
		final JRadioButton rdbtnSenate = new JRadioButton("Senate");
		lblFed.setLabelFor(rdbtnSenate);
		final JRadioButton rdbtnHouse = new JRadioButton("House of Representatives");
        		
        rdbtnSenate.setActionCommand("SenateAction");
        rdbtnSenate.addActionListener(new ActionListener() {
		@SuppressWarnings({ "unchecked" })
		public void actionPerformed(ActionEvent evt) {  				
        String Action = evt.getActionCommand();
        if (Action.equals("SenateAction"))
        {				
			if (rdbtnSenate.isSelected() == true)
	        {    
	            if (!GovMember.equals(Please_Select))
	            {  // Congress Radio Button selected
	                if (chckbxSelectAll.isSelected())
	                	chckbxSelectAll.setSelected(false);
	                textBillSearch.setText("");
	                if (!Please_Select.equals(comboBoxState.getItemAt(0).toString()))
	                {
	                	comboBoxState.insertItemAt(Please_Select, 0);
	                	model4.setSelectedItem(Please_Select);
	                }
	                Reset();
	            }
	            rdbtnHouse.setSelected(false);
	            GovMember = "Senate";
	        } 
		}
	}
});
        rdbtnHouse.setActionCommand("HouseAction");
        rdbtnHouse.addActionListener(new ActionListener() {
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent evt) { 
            String Action = evt.getActionCommand();
            if (Action.equals("HouseAction"))
            {				
				if (rdbtnHouse.isSelected() == true)
		        {
		            if (!GovMember.equals(Please_Select))
		            {
		                if (chckbxSelectAll.isSelected())
		                	chckbxSelectAll.setSelected(false);
		                textBillSearch.setText("");
		                if (!Please_Select.equals(comboBoxState.getItemAt(0).toString()))	                	
		                {
		                	comboBoxState.insertItemAt(Please_Select, 0);
			                model4.setSelectedItem(Please_Select);
		                }
			            Reset();
		            }
		            rdbtnSenate.setSelected(false);
		            GovMember = "House of Representatives";
		        } 
			}
		}
	});
        // Put both radio button in a group
        ButtonGroup group = new ButtonGroup();
        group.add(rdbtnSenate);
        group.add(rdbtnHouse);     
		
        // CLOSE button 
		JButton btnClose = new JButton("CLOSE");
		btnClose.setActionCommand("CloseQuitAction");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
		        String Action = evt.getActionCommand();
		        if (Action.equals("CloseQuitAction"))
		        {
		                int  n = JOptionPane.showConfirmDialog(null 
		               , "Do You Wish To Exit GovTrack."
		               , "QUIT Application"
		               , JOptionPane.YES_NO_OPTION
		               , JOptionPane.QUESTION_MESSAGE
		               );    

		               if (n == JOptionPane.YES_OPTION)
		                   System.exit(0);
		           
		        }

			}
		});
		
		JLabel lblInstructions1 = new JLabel("Select which Congress House and State");
		JLabel lblInstructions2 = new JLabel("Select Congress Members for Query");
		
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(12)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblInstructions1)
							.addGap(36)
							.addComponent(lblInstructions2))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(rdbtnHouse)
								.addComponent(lblFed)
								.addComponent(rdbtnSenate)
								.addComponent(comboBoxState, GroupLayout.PREFERRED_SIZE, 175, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblState, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED, 218, Short.MAX_VALUE)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
								.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 435, GroupLayout.PREFERRED_SIZE)
								.addComponent(chckbxSelectAll))))
					.addContainerGap())
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblBillSearch)
							.addContainerGap(655, Short.MAX_VALUE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(textBillSearch, GroupLayout.PREFERRED_SIZE, 236, GroupLayout.PREFERRED_SIZE)
							.addGap(7)
							.addComponent(btnSearch)
							.addPreferredGap(ComponentPlacement.RELATED, 60, Short.MAX_VALUE)
							.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 152, GroupLayout.PREFERRED_SIZE)
							.addGap(170)
							.addComponent(btnClose, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(57))))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(4)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblInstructions1)
						.addComponent(lblInstructions2))
					.addGap(18)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblFed)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(rdbtnSenate)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(rdbtnHouse)
							.addPreferredGap(ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
							.addComponent(lblState)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(comboBoxState, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(chckbxSelectAll, GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
							.addGap(12)
							.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)))
					.addPreferredGap(ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
							.addComponent(lblBillSearch)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
									.addComponent(btnSearch)
									.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE))
								.addComponent(textBillSearch, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addGap(14))
						.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
							.addComponent(btnClose, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(25))))
		);
		contentPane.setLayout(gl_contentPane);
		
		// Set up tableID which is invisible
		tableID = new JTable();
		tableID.setModel(new DefaultTableModel(
			new Object[][] {
				{null, null},
				{null, null},
			},
			new String[] {
				"Select", "VoteID"
			}
		));
		scrollPane_1.setViewportView(tableID);
		tableID.setVisible(false);
		
		// Set up tableResults which show a column for the user to
		// select and the Congressional members full name and
		//  party affiliations 
		tableResults = new JTable();
		tableResults.setFont(new Font("Arial Unicode MS", Font.PLAIN, 15));
		tableResults.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		tableResults.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableResults.setModel(new DefaultTableModel(
			new Object[][] {
				{null, null, null},
				{null, null, null},
				{null, null, null},
			},
			new String[] {
				"Select", "Full Name", "Party"
			}
		));
		
        tableResults.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e) 
            {
                if (e.getClickCount() >= 2) 
                {
                    int row = 0;                    
                    row = tableResults.rowAtPoint(new Point(e.getX(),e.getY()));
                    
                    CurrentRow = row;
                    
                    tableResults.clearSelection();
                    tableResults.repaint();
                }
                else if (e.getClickCount() == 1) // Single click
                {
                    int row = 0, col = 0;
                    
                    row = tableResults.rowAtPoint(new Point(e.getX(),e.getY()));
                    col = tableResults.columnAtPoint(new Point(e.getX(),e.getY()));
                    if (col == 0) // First column is the select check box
                    {
                        if (Boolean.TRUE.equals(model.getValueAt(row, col)))
                        { // if it is currently selected
                            if (chckbxSelectAll.isSelected())
                            {
                                chckbxSelectAll.setSelected(false);
                                tableID.setValueAt(Boolean.FALSE, row, 0);
                            }
                        }
                        else if (Boolean.FALSE.equals(model.getValueAt(row, col)))
                        {
                            if (chckbxSelectAll.isSelected())
                            {
                                tableID.setValueAt(Boolean.TRUE, row, 0);
                            }
                        }
                                        
                    }
                }
            }
        });
		scrollPane.setViewportView(tableResults);

        this.lstPersonId = new ArrayList<>();

        strOS = System.getProperty("os.name");
        GovMember = "- Please Select -";
        
        
        if (strOS.startsWith("Windows"))
        {
	        rdbtnSenate.setFont(new Font("Arial Unicode MS", Font.BOLD, 15));
	        rdbtnHouse.setFont(new Font("Arial Unicode MS", Font.BOLD, 15));
			lblFed.setFont(new Font("Arial Unicode MS", Font.BOLD, 15));
			lblState.setFont(new Font("Arial Unicode MS", Font.BOLD, 15));
			comboBoxState.setFont(new Font("Arial Unicode MS", Font.BOLD, 15));
			tableResults.setFont(new Font("Arial Unicode MS", Font.PLAIN, 15));
			btnClose.setFont(new Font("Arial Unicode MS", Font.BOLD, 15));
			btnSearch.setFont(new Font("Arial Unicode MS", Font.BOLD, 15));
			lblBillSearch.setFont(new Font("Arial Unicode MS", Font.BOLD, 15));
			textBillSearch.setFont(new Font("Arial Unicode MS", Font.BOLD, 15));
			chckbxSelectAll.setFont(new Font("Arial Unicode MS", Font.BOLD, 15));
	        tableResults.getTableHeader().setFont(new Font("Arial Unicode MS", Font.BOLD, 15));    
	        lblInstructions1.setFont(new Font("Arial Unicode MS", Font.BOLD, 13));
	        lblInstructions2.setFont(new Font("Arial Unicode MS", Font.BOLD, 13));
	        lblInstructions1.setForeground(Color.RED);
	        lblInstructions2.setForeground(Color.RED);			
        }
        
	}
	
	// States name with Abbreviation to be used with database query
	public class StateAbrev{
	    String Abrev = null;
	    
	    public String StateID(String ST){
	        
	    String [][] arStates = { {"Alabama", "AL"}, {"Alaska", "AK"}, {"Arizona", "AZ"}, 
	        {"Arkansas", "AR"}, {"California", "CA"}, {"Colorado", "CO"}, {"Connecticut", "CT"}, 
	        {"Delaware", "DE"}, {"Florida", "FL"}, {"Georgia", "GA"}, {"Hawaii", "HI"}, 
	        {"Idaho", "ID"}, {"Illinois", "IL"}, {"Indiana", "IN"}, {"Iowa", "IA"}, 
	        {"Kansas", "KS"}, {"Kentucky", "KY"}, {"Louisiana", "LA"}, {"Maine", "ME"}, 
	        {"Maryland", "MD"}, {"Massachusetts", "MA"}, {"Michigan", "MI"}, {"Minnesota", "MN"}, 
	        {"Mississippi", "MS"}, {"Missouri", "MO"}, {"Montana", "MT"}, {"Nebraska", "NE"}, 
	        {"Nevada", "NV"}, {"New Hampshire", "NH"}, {"New Jersey", "NJ"}, {"New Mexico", "NM"}, 
	        {"New York", "NY"}, {"North Carolina", "NC"}, {"North Dakota", "ND"}, {"Ohio", "OH"},   
	        {"Oklahoma", "OK"}, {"Oregon", "OR"}, {"Pennsylvania", "PA"}, {"Rhode Island", "RI"}, 
	        {"South Carolina", "SC"}, {"South Dakota", "SD"}, {"Tennessee", "TN"}, {"Texas", "TX"}, 
	        {"Utah", "UT"}, {"Vermont", "VT"}, {"Virginia", "VA"}, {"Washington", "WA"},
	        {"West Virginia", "WV"}, {"Wisconsin", "WI"}, {"Wyoming", "WY"}};

	        List<String[]> listStates = Arrays.asList(arStates);
	        int counter = 0;
	        for (Iterator<String[]> StateArray = listStates.iterator(); StateArray.hasNext();) {
	            String[] strState = StateArray.next();
	            if (ST.equals(strState[0].toString()))
	            {
	                Abrev = listStates.get(counter)[1];
	                break;
	            }
	            counter++;
	        }
	        return(Abrev);
	    }
	}


   
// Reset is needed when another state is selected after Population of Table
// or when another Government branch is selected
//@SuppressWarnings("unchecked")
private void Reset()
{

    // Clear out the data in Results Table
    DefaultTableModel modelResults = (DefaultTableModel) tableResults.getModel();
    modelResults.setRowCount(0);
    
    // Clear out the data in the ID Table
    // ID table is never seen but is used to capture the ID from the
    // representative that was selected to pass to the MySQL queries.
    DefaultTableModel modelID = (DefaultTableModel) tableID.getModel();
    modelID.setRowCount(0);    
}

// Populate the tableID and tableResults
public class PopulateTable
{
    @SuppressWarnings("serial")
	public void SelectEvent()
    {
        Statement stmt1 = null;
        ResultSet rs1 = null;
        PreparedStatement pst1 = null;
        Connection conn2 = null;   

        try 
        {
            Class.forName("com.mysql.jdbc.Driver");
             conn2 = DriverManager.getConnection("jdbc:mysql://localhost:3306/federal",
            		 "root", "tesa411");
        } 
	    catch (ClassNotFoundException e) {
	        e.printStackTrace();
	    }catch (SQLException ex) 
        {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        
        // tableID is not visible but data is used to for the MySql queries 
        model2 = new DefaultTableModel() {
        boolean[] canEdit = new boolean [] {
            true, false
        };

        @SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
        public Class getColumnClass(int column) {
            if (column == 0)
                return Boolean.class;
            else
                return (getValueAt(0, column).getClass());
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit [columnIndex];
        }};
        
        model2.addColumn("Select");
        model2.addColumn("Person ID");
        tableID.setModel(model2);      
        scrollPane_1.setSize(300, 300);
        tableID.getColumnModel().getColumn(0).setPreferredWidth(20);
        tableID.getColumnModel().getColumn(1).setPreferredWidth(60);
        tableID.getColumnModel().getColumn(0).setMinWidth(5);
        tableID.getColumnModel().getColumn(1).setMinWidth(20);
        tableID.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tableID.setShowGrid(true);
        tableID.setShowHorizontalLines(true);
        tableID.setColumnSelectionAllowed(false);
        tableID.getTableHeader().setReorderingAllowed(false);            
        
        // tableResults sets up the Congressional members name and 
        // political affiliations 
        if (GovMember.equals("Senate"))   
        {
            model = new DefaultTableModel() {
            boolean[] canEdit = new boolean [] {
                true, false, false
            };

            @SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
            public Class getColumnClass(int column) {
                if (column == 0)
                    return Boolean.class;
                else
                    return (getValueAt(0, column).getClass());
            }
            
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }};
        }
        else if (GovMember.equals("House of Representatives"))
        {
            model = new DefaultTableModel() {
            boolean[] canEdit = new boolean [] {
                true, false, false, false
            };

            @SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
            public Class getColumnClass(int column) {
                if (column == 0)
                    return Boolean.class;
                else
                    return (getValueAt(0, column).getClass());
            }
            
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }};            
        }
                
        if (GovMember.equals("Senate"))
        {
            model.addColumn("Select");
            model.addColumn("Full Name");
            model.addColumn("Party");
        }
        else if (GovMember.equals("House of Representatives"))
        {
            model.addColumn("Select");
            model.addColumn("Full Name");
            model.addColumn("District");
            model.addColumn("Party");
        } 
        
        tableResults.getTableHeader().setFont(new Font("Arial Unicode MS", Font.BOLD, 15));

        if (GovMember.equals("Senate"))
        {
            Dimension dim1 = new Dimension(400,100);
            scrollPane.setSize(300,100);
    		scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));

            tableResults.setPreferredSize(dim1);        
            tableResults.setModel(model);      

            tableResults.getColumnModel().getColumn(0).setPreferredWidth(75);
            tableResults.getColumnModel().getColumn(1).setPreferredWidth(175);
            tableResults.getColumnModel().getColumn(2).setPreferredWidth(150);
            tableResults.getColumnModel().getColumn(0).setMinWidth(5);
            tableResults.getColumnModel().getColumn(1).setMinWidth(40);
            tableResults.getColumnModel().getColumn(2).setMinWidth(25);
        }
        else if (GovMember.equals("House of Representatives"))
        {
            Dimension dim1;
            if (strOS.equals("Linux"))
            {
                dim1 = new Dimension(500,325);
                scrollPane.setSize(500,325);            
            }
            else
            {
                dim1 = new Dimension(425,325);
                scrollPane.setSize(425,325);
            }
            scrollPane.getViewport().setViewSize(dim1); 
            scrollPane.setPreferredSize(dim1);

            tableResults.setPreferredSize(dim1);   
            tableResults.setModel(model);      

            scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            if (strOS.equals("Linux"))
            {
                tableResults.getColumnModel().getColumn(0).setPreferredWidth(70);
                tableResults.getColumnModel().getColumn(1).setPreferredWidth(175);
                tableResults.getColumnModel().getColumn(2).setPreferredWidth(70);
                tableResults.getColumnModel().getColumn(3).setPreferredWidth(125);
            }
            else
            {
                tableResults.getColumnModel().getColumn(0).setPreferredWidth(60);
                tableResults.getColumnModel().getColumn(1).setPreferredWidth(175);
                tableResults.getColumnModel().getColumn(2).setPreferredWidth(60);
                tableResults.getColumnModel().getColumn(3).setPreferredWidth(125);
            }
            
            tableResults.getColumnModel().getColumn(0).setMinWidth(5);
            tableResults.getColumnModel().getColumn(1).setMinWidth(40);
            tableResults.getColumnModel().getColumn(2).setMinWidth(30);
            tableResults.getColumnModel().getColumn(3).setMinWidth(70);            
        }
  
        tableResults.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tableResults.setShowGrid(true);
        tableResults.setShowHorizontalLines(true);
        tableResults.setColumnSelectionAllowed(false);
        tableResults.getTableHeader().setReorderingAllowed(false);    
 
       if (tableResults.getColumnModel().getColumnCount() > 0) 
       {
            tableID.getColumnModel().getColumn(0).setResizable(false);
            tableID.getColumnModel().getColumn(1).setResizable(false);

            tableResults.getColumnModel().getColumn(0).setResizable(false);
            tableResults.getColumnModel().getColumn(1).setResizable(false);
            tableResults.getColumnModel().getColumn(2).setResizable(false);
            if (GovMember.equals("House of Representatives"))
                tableResults.getColumnModel().getColumn(3).setResizable(false);
        }
              
        tableResults.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e) 
            {
                if (e.getClickCount() >= 2) 
                {
                    int row = 0;
                    
                    row = tableResults.rowAtPoint(new Point(e.getX(),e.getY()));                    
                    CurrentRow = row;
                    
                    tableResults.clearSelection();
                    tableResults.repaint();
                }
                else if (e.getClickCount() == 1)
                {
                    int row = 0, col = 0;
                    
                    row = tableResults.rowAtPoint(new Point(e.getX(),e.getY()));
                    col = tableResults.columnAtPoint(new Point(e.getX(),e.getY()));
                    if (col == 0)
                    {
                        if (Boolean.TRUE.equals(model.getValueAt(row, col)))
                        {
                            if (chckbxSelectAll.isSelected())
                            {
                                chckbxSelectAll.setSelected(false);
                                tableID.setValueAt(Boolean.FALSE, row, 0);
                            }
                        }
                        else if (Boolean.FALSE.equals(model.getValueAt(row, col)))
                        {
                            if (chckbxSelectAll.isSelected())
                            {
                                tableID.setValueAt(Boolean.TRUE, row, 0);
                            }
                        }
                                        
                    }
                }
            }
        });
        // Clear out any data and any selection before putting data in
        tableID.clearSelection();
        model2.setRowCount(0);

        tableResults.clearSelection();
        model.setRowCount(0);
                
        // Create the Select query according to what the user selected
        String selectEvent = null;      
        if (GovMember.equals("Senate"))
            selectEvent = "SELECT firstname, lastname, party, person_id FROM "+
                      "federal.senate WHERE State_id = '"+StateAbrev+"' ORDER BY lastname";
        else if (GovMember.equals("House of Representatives"))
             selectEvent = "SELECT firstname, lastname, district, party, person_id FROM "+
                      "federal.house WHERE State_id = '"+StateAbrev+"' ORDER BY lastname";
                          
        try{
            stmt1 = conn2.createStatement();
            rs1 = stmt1.executeQuery(selectEvent);

            while(rs1.next())
            {
                // Retrieve the data from the Select query
                // 1st variable is a check box that is unchecked
                if (GovMember.equals("Senate"))
                    model.addRow(new Object[]{false,rs1.getString("firstname")+" "+
                        rs1.getString("lastname"),rs1.getString("party")});
                else if (GovMember.equals("House of Representatives"))
                    model.addRow(new Object[]{false,rs1.getString("firstname")+" "+
                        rs1.getString("lastname"),rs1.getString("district"),
                        rs1.getString("party")});
                
                // This is a hidden jTable that contains a check box
                // and the person_id 
                model2.addRow(new Object[]{false,rs1.getString("person_id")});                    
            }
            LastRow = model.getRowCount();
            model.setRowCount(LastRow);
            model2.setRowCount(LastRow);

            Dimension d = tableResults.getPreferredSize();                                
            int rowWidth = d.width;
            int rowHeight = 0;
            if (LastRow > 5)
                rowHeight = tableResults.getRowHeight()*(LastRow);                    
            else
                rowHeight = tableResults.getRowHeight()*(LastRow);                    

            Dimension dim81 = new Dimension(rowWidth,rowHeight);
            Dimension dim80 = new Dimension(rowWidth,100);
            tableResults.setPreferredSize(dim81);
            if (LastRow > 5)
            {
                scrollPane.setMaximumSize(dim80);
                scrollPane.setSize(rowWidth,100);
            }
            tableResults.setLocation(1, 1);
        }
        catch (SQLException e) {
            System.out.println( "Error msg:"+e.getMessage()+"<"+e+">");
        }

        // closing up connections, etc
         try 
         {
            if (pst1 != null) {
                pst1.close();
            }
            if (conn2 != null) {
                conn2.close();
            }
        } 
         catch (SQLException ex) 
        {
            System.out.println( "Error msg:"+ex.getMessage()+"<"+ex+">");
        }
                     
        tableID.repaint();            
        tableResults.repaint();            
    }
	
	}
}
