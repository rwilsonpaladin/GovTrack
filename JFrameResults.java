package govtrack4;

import java.awt.Color;

public class JFrameResults extends JFrame implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5404650993928040266L;
	private JPanel contentPane;
	private JTable tableHeader = new JTable();
	private JTable tableDetail = new JTable();
	private JScrollPane scrollPane_1 = new JScrollPane(); 
	private JLabel lblBillData = new JLabel();
    private JScrollPane scrollPane_2 = new JScrollPane();
    private JTable tableVoteID = new JTable();
    private JLabel lblFound = new JLabel();
    private JLabel lblResults = new JLabel();
	
    String GovMember = "";
    String StateAbrev = "";
    String Created = "";
    String BillInfo = "";
    String sSize = "";
    String VoteId = "";
    String [] PersonId = null;
    String strState = "";
    String strOS = "";
    String strBillLabelTitle = "";
    int CurrentRow = 0;
    int LastRow = 0;
    int LastRow2 = 0;
    Statement stmt = null;
    ResultSet rs = null;
    PreparedStatement pst = null;
    Statement stmt2 = null;
    ResultSet rs2 = null;
    PreparedStatement pst2 = null;
    Connection conn = null;
    Connection conn1 = null;
    Boolean bRet = false;
    DefaultTableModel model;
    DefaultTableModel model2;
    DefaultTableModel model3;
    ArrayList<String> lstPersonId;    
    String strPersonId = null;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
	    final String GovType1 = args[0];  // Government Type ie Senate or House
	    final String strState1 = args[1]; // State abbreviation
	    final String BillInfo1 = args[2]; // Searching for a particular Bill
	    final String sSize = args[3];     // How many representatives selected
	    final String sPersonId = args[4]; // ID of those representatives selected
	        
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JFrameResults frame = new JFrameResults(GovType1,strState1,
	                        BillInfo1,sSize,sPersonId);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	@SuppressWarnings("serial")
	public JFrameResults(String strGovMember1,String strState1,
            String strBillInfo1,String strSize1,String strPersonId1) {
        

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1118, 460);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

	  strOS =  System.getProperty("os.name");
	  GovMember = strGovMember1.toString();
	  strState = strState1.toString();
	  BillInfo = strBillInfo1.toString();
	  sSize = strSize1.toString();
				
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBorder(null);
		scrollPane.setSize(new Dimension(225, 63));
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBorder(null);
		
		scrollPane_2 = new JScrollPane();
		scrollPane_2.setVisible(false);	  
		
		
		// CLOSE Button
		JButton btnClose = new JButton("CLOSE");
		btnClose.setName("btnClose");
		btnClose.setActionCommand("CloseAction");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

		        Boolean bFlag = true;
		        String Action = evt.getActionCommand();
		        String sFrame = null;
		        int i = 0;
		        if (Action.equals("CloseAction"))
		        {
		            Frame [] frame = JFrameMain.getFrames();
		            int c = frame.length;
		            while (bFlag)
		            {
		                if (i >= c)
		                    bFlag = false;
		                else
		                {
		                    sFrame = frame[i].toString();
		                    if (sFrame.contains("JFrameMain"))
		                    {
		                        bFlag = false;
		                        frame[i].setVisible(true);
		                        JFrameResults.this.dispose();  
		                    }     
		                    i++;
		                }
		            }
		        }    
			}
		});
		
		lblFound = new JLabel("New label");
		lblFound.setName("lblFound");
		
		lblResults = new JLabel("New label");
		lblResults.setName("lblResults");

		lblBillData = new JLabel("New label");
		lblBillData.setName("lblBillData");
		JLabel lblInstructions = new JLabel("Double Click on the Row for Detail Results");
				
        // tableHeader data 
        tableHeader = new JTable();
        tableHeader.setFont(new Font("Arial Unicode MS", Font.PLAIN, 15));
        scrollPane.setViewportView(tableHeader);
        lblFound.setLabelFor(tableHeader);
        tableHeader.setName("tableHeader");
		
	    model = new DefaultTableModel() {
	        
        model.addColumn("Date Bill Signed");
        model.addColumn("Vote Result");
        model.addColumn("Bill Title");
        model.setColumnCount(3);

        Dimension dim1;        
        if (GovMember.equals("Senate"))
        {
            if (strOS.equals("Linux"))
            {
                dim1 = new Dimension(1155,5000);
                scrollPane.setSize(1155,5000);            
            }
            else if (strOS.startsWith("Windows", 0))
            {
                dim1 = new Dimension(1050,5000);
                scrollPane.setSize(1050,5000);
            }
            else
            {
                dim1 = new Dimension(1100,5000);
                scrollPane.setSize(1100,5000);                
            }
        }
        else  // House of Representatives
        {
            if (strOS.equals("Linux"))
            {
                dim1 = new Dimension(1135,5000);
                scrollPane.setSize(1135,5000);            
            }
            else if (strOS.startsWith("Windows", 0))
            {
                dim1 = new Dimension(1100,5000);
                scrollPane.setSize(1100,5000);
            }
            else    // Mac
            {
                dim1 = new Dimension(1100,5000);
                scrollPane.setSize(1100,5000);                
            }       
        }

        scrollPane.getViewport().setViewSize(dim1); 
        scrollPane.setPreferredSize(dim1);
        tableHeader.setSize(dim1);
        tableHeader.setPreferredSize(dim1);        
        tableHeader.setModel(model);      
              
        if (GovMember.equals("Senate"))
        {
            if (strOS.equals("Linux"))
            {
                tableHeader.getColumnModel().getColumn(0).setPreferredWidth(185);
                tableHeader.getColumnModel().getColumn(1).setPreferredWidth(235);
                tableHeader.getColumnModel().getColumn(2).setPreferredWidth(735);
            }
            else if (strOS.startsWith("Windows", 0))
            {
                tableHeader.getColumnModel().getColumn(0).setMaxWidth(165);
                tableHeader.getColumnModel().getColumn(1).setMaxWidth(170);
                tableHeader.getColumnModel().getColumn(2).setMaxWidth(710);            	
            }
            else  //Mac
            {
                tableHeader.getColumnModel().getColumn(0).setPreferredWidth(175);
                tableHeader.getColumnModel().getColumn(1).setPreferredWidth(150);
                tableHeader.getColumnModel().getColumn(2).setPreferredWidth(700);
            }
        }
        else if (GovMember.equals("House of Representatives"))
        { 
            if (strOS.equals("Linux"))
            {
                tableHeader.getColumnModel().getColumn(0).setPreferredWidth(165);
                tableHeader.getColumnModel().getColumn(1).setPreferredWidth(130);
                tableHeader.getColumnModel().getColumn(2).setPreferredWidth(775);
            }
            else if (strOS.startsWith("Windows", 0))
            {
                tableHeader.getColumnModel().getColumn(0).setPreferredWidth(165);
                tableHeader.getColumnModel().getColumn(1).setPreferredWidth(160);
                tableHeader.getColumnModel().getColumn(2).setPreferredWidth(775);            
            }
            else  //Mac
            {
                tableHeader.getColumnModel().getColumn(0).setPreferredWidth(175);
                tableHeader.getColumnModel().getColumn(1).setPreferredWidth(125);
                tableHeader.getColumnModel().getColumn(2).setPreferredWidth(750);
            }
        }

        tableHeader.getColumnModel().getColumn(0).setMinWidth(100);
        tableHeader.getColumnModel().getColumn(1).setMinWidth(100);
        tableHeader.getColumnModel().getColumn(2).setMinWidth(400);
        tableHeader.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tableHeader.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        tableHeader.setShowGrid(true);
        tableHeader.setShowHorizontalLines(true);
        tableHeader.setColumnSelectionAllowed(false);
        tableHeader.getTableHeader().setReorderingAllowed(false);    
        
        if (tableHeader.getColumnModel().getColumnCount() > 0) 
        {
            tableHeader.getColumnModel().getColumn(0).setResizable(false);
            tableHeader.getColumnModel().getColumn(1).setResizable(false);
            tableHeader.getColumnModel().getColumn(2).setResizable(false);
        }
                
         tableHeader.addMouseListener(new MouseAdapter()
         {
             @Override
             public void mousePressed(MouseEvent e) 
             {
                 if (e.getClickCount() == 2) 
                 {
                     int row = 0;
                     
                     row = tableHeader.rowAtPoint(new Point(e.getX(),e.getY()));
                     // Get the VoteID of the selected row and get the Created date
                     // and the Bill Name of that selected item from the tableHeader
                     CurrentRow = row;
                     VoteId = tableVoteID.getValueAt(CurrentRow, 0).toString();
                     Created = tableHeader.getValueAt(CurrentRow, 0).toString();
                     strBillLabelTitle = tableHeader.getValueAt(CurrentRow, 2).toString();                 
                     // Populate the tableDetail
                     tableDetail.setVisible(true);
                     tableDetailPopulate();
                 }
             }
         });
		
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(29)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(lblResults, GroupLayout.PREFERRED_SIZE, 416, GroupLayout.PREFERRED_SIZE)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 430, GroupLayout.PREFERRED_SIZE)
									.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
										.addGroup(gl_contentPane.createSequentialGroup()
											.addPreferredGap(ComponentPlacement.RELATED, 540, Short.MAX_VALUE)
											.addComponent(btnClose))
										.addGroup(gl_contentPane.createSequentialGroup()
											.addGap(115)
											.addComponent(scrollPane_2, GroupLayout.PREFERRED_SIZE, 235, GroupLayout.PREFERRED_SIZE))))
								.addComponent(lblFound, GroupLayout.PREFERRED_SIZE, 624, GroupLayout.PREFERRED_SIZE)
								.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 1046, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblInstructions)))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(40)
							.addComponent(lblBillData, GroupLayout.PREFERRED_SIZE, 1010, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblInstructions)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblFound)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblBillData)
					.addPreferredGap(ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblResults)
							.addGap(18)
							.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 98, GroupLayout.PREFERRED_SIZE))
						.addComponent(scrollPane_2, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnClose)
					.addContainerGap())
		);
		     		
		contentPane.setLayout(gl_contentPane);
         
		// tableVoteID data
		 tableVoteID = new JTable();
		 tableVoteID.setVisible(false);
		 scrollPane_2.setViewportView(tableVoteID);
		 tableVoteID.setFillsViewportHeight(true);
		 tableVoteID.setName("tableVoteID");
         
		// This table is not visible and contains the Vote ID of all the Bills
         model2 = new DefaultTableModel() {
        	 boolean[] canEdit = new boolean [] {
		      false
		 };
		
		 @Override
		 public boolean isCellEditable(int rowIndex, int columnIndex) {
		     return canEdit [columnIndex];
		 }};
		
		 model2.addColumn("VoteId");
		 tableVoteID.setModel(model2);      
		 tableVoteID.setSize(150, 4000);
		 tableVoteID.getColumnModel().getColumn(0).setPreferredWidth(40);
		 tableVoteID.getColumnModel().getColumn(0).setMinWidth(20);
		 tableVoteID.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		 tableVoteID.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		 tableVoteID.setShowGrid(true);
		 tableVoteID.setShowHorizontalLines(true);
		 tableVoteID.setColumnSelectionAllowed(false);
		 tableVoteID.getTableHeader().setReorderingAllowed(false);    
 						
		 // tableDetail is only visible after double clicking on an item in
		 // tableHeader
		tableDetail = new JTable();
		tableDetail.setFont(new Font("Arial Unicode MS", Font.PLAIN, 15));
		lblResults.setLabelFor(tableDetail);
		tableDetail.setName("tableDetail");
		scrollPane_1.setViewportView(tableDetail);
			
        model3 = new DefaultTableModel() {
        boolean[] canEdit = new boolean [] {
            false, false, false
        };

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit [columnIndex];
        }};

        model3.addColumn("Full Name");
        model3.addColumn("Party");
        model3.addColumn("Yea / Nay");
        model3.setColumnCount(3);

        
        Dimension dim = new Dimension(400,200);
        scrollPane_1.setSize(400,200);
        scrollPane_1.getViewport().setViewSize(dim); 
        scrollPane_1.setPreferredSize(dim);
        
        tableDetail.setPreferredSize(dim);        

        tableDetail.setModel(model3);      
        tableDetail.getColumnModel().getColumn(0).setPreferredWidth(150);
        tableDetail.getColumnModel().getColumn(1).setPreferredWidth(90);
        tableDetail.getColumnModel().getColumn(2).setPreferredWidth(75);
        tableDetail.getColumnModel().getColumn(0).setMinWidth(10);
        tableDetail.getColumnModel().getColumn(1).setMinWidth(20);
        tableDetail.getColumnModel().getColumn(2).setMinWidth(20);
        tableDetail.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tableDetail.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tableDetail.setShowGrid(true);
        tableDetail.setShowHorizontalLines(true);
        tableDetail.setColumnSelectionAllowed(false);
        tableDetail.getTableHeader().setReorderingAllowed(false);    
	        
       if (tableDetail.getColumnModel().getColumnCount() > 0) 
       {
           tableDetail.getColumnModel().getColumn(0).setResizable(false);
           tableDetail.getColumnModel().getColumn(1).setResizable(false);
           tableDetail.getColumnModel().getColumn(2).setResizable(false);
       }
   
       //Set up Fonts for all of the items on this screen
       tableDetail.getTableHeader().setFont(new Font("Arial Unicode MS", Font.BOLD, 15));        
		   btnClose.setFont(new Font("Arial Unicode MS", Font.BOLD, 15));
       tableHeader.getTableHeader().setFont(new Font("Arial Unicode MS", Font.BOLD, 15));
       lblResults.setFont(new Font("Arial Unicode MS", Font.BOLD, 15));
       lblFound.setFont(new Font("Arial Unicode MS", Font.BOLD, 15));
       lblBillData.setFont(new Font("Arial Unicode MS", Font.BOLD, 15));
       tableVoteID.getTableHeader().setFont(new Font("Arial Unicode MS", Font.BOLD, 15));
       lblInstructions.setFont(new Font("Arial Unicode MS", Font.BOLD, 13));
       lblInstructions.setForeground(Color.RED);
       
	  
	  strPersonId = strPersonId1;
	  lblResults.setText("This is the bill results label");
	  lblBillData.setVisible(false);
	  lblResults.setVisible(false);
	     
	  tableDetail.setVisible(false);
	  String strBillinfo = "BillInfo";
  
    try 
    {
        Class.forName("com.mysql.jdbc.Driver");
         conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/federal",
        		 "root", "#######");
    } 
    catch (ClassNotFoundException e) {
        e.printStackTrace();
    }catch (SQLException ex) 
    {
        System.out.println("SQLException: " + ex.getMessage());
        System.out.println("SQLState: " + ex.getSQLState());
        System.out.println("VendorError: " + ex.getErrorCode());
    }

    
    // Create the Select query according to what the user selected
    String selectEvent = "";      
    selectEvent = "SELECT created, result, question, vote_id FROM ";
    if (GovMember.equals("Senate"))
        selectEvent = selectEvent + "federal.senate_votes_h ";            
    else if (GovMember.equals("House of Representatives"))
        selectEvent = selectEvent + "federal.house_votes_h ";

    if (!BillInfo.equals(strBillinfo)) // check to see if anything to search for
        selectEvent = selectEvent + "WHERE question "+"LIKE '%"+BillInfo+"%' ";
    selectEvent = selectEvent + "ORDER BY created";
                      
    try{
        stmt = conn.createStatement();
        rs = stmt.executeQuery(selectEvent);

        // Retrieve the data from the Select query
        while(rs.next())
        {
            model.addRow(new Object[]{rs.getString("created"),
                    rs.getString("result"),rs.getString("question")});

            // This data is in a jTable that is not visible
            model2.addRow(new Object[]{rs.getString("vote_id")});                    
        }
        LastRow = model.getRowCount();
        model.setRowCount(LastRow);
        model2.setRowCount(LastRow);

        Dimension d = tableHeader.getPreferredSize();                                
        int rowWidth = d.width;
        int rowHeight2 = 0;
        int rowHeight = 0;
        if (LastRow > 10)
        {
            rowHeight2 = tableHeader.getRowHeight()*(10);                    
            rowHeight = tableHeader.getRowHeight()*(LastRow);                    
        }
        else
        {
            rowHeight = tableHeader.getRowHeight()*(LastRow);
            rowHeight2 = tableHeader.getRowHeight()*(LastRow);                    
        }

        Dimension dim80 = new Dimension(rowWidth,rowHeight);
        if (LastRow > 10)
        {
            scrollPane.setMaximumSize(dim80);
            scrollPane_2.setMaximumSize(dim80);
            scrollPane.setSize(rowWidth,rowHeight2);
            scrollPane_2.setSize(rowWidth,rowHeight2);
        }

        if (LastRow == 0)
        	lblFound.setText("No Data Found For <"+BillInfo+">");
        else
        	lblFound.setText("Found "+LastRow+" Items.");
        }
        catch (SQLException e) {
            System.out.println( "Error msg:"+e.getMessage()+"<"+e+">");
        }

         try 
         {
            if (pst != null) {
                pst.close();
            }
            if (conn != null) {
                conn.close();
            }
        } 
         catch (SQLException ex) 
        {
            System.out.println( "Error msg:"+ex.getMessage()+"<"+ex+">");
        }
	}
	
    private void tableCleanUp() {
        int iVar = 0, iDetailRows = 0;
        iDetailRows = tableDetail.getRowCount();
        if (iDetailRows != 0)
        {
            // Remove all the data in both jTables in reverse order
            for(iVar = iDetailRows; iVar <= 0; iVar--)
            {
               model3.removeRow(iVar);
            }
            model3.setRowCount(0);
        }
        
    }
    
	  // Populate the tableDetail with the info from
    // the double click selection from the tableHeader
    private void tableDetailPopulate()
    {        
        int i = 0;
        int iSize = Integer.parseInt(sSize);
        
        String strParse = "";

        String strVoteTotalMinus = "";
        String strVoteTotalOther = "";
        String strVoteTotalPlus = "";
        String strQuestion = "";

        try 
        {
            conn1 =
           DriverManager.getConnection("jdbc:mysql://localhost/" +
                   "federal?" + "user=root&password=#######");
        } catch (SQLException ex) 
        {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }

        // Create the Select query according to what the user selected
        String selectEvent = "";      
        selectEvent = "SELECT a.firstname, a.lastname, a.party, b.option_value, "+
          "b.vote_total_minus, b.vote_total_other, b.vote_total_plus, c.question FROM ";
        if (GovMember.equals("Senate"))
        {
            selectEvent = selectEvent + " federal.senate AS a INNER JOIN "
              + "federal.senate_votes_d AS b  ON a.person_id = "+
                    "b.person_id INNER JOIN federal.senate_votes_h AS c";
        }
        else if (GovMember.equals("House of Representatives"))
        {
            selectEvent = selectEvent + " federal.house AS a INNER JOIN "+
              "federal.house_votes_d AS b ON a.person_id = "+
               "b.person_id INNER JOIN federal.house_votes_h AS c";
        }
        selectEvent = selectEvent + " ON b.vote_id = c.vote_id";

        //  put items from strPersonId into string array
        String delims = "[ \\[,\\]]+";
        String [] tPersonId = strPersonId.split(delims);
        for ( i = 1; i < iSize + 1; i++)
        {
            // Continue creating the Select query accordingly
              if ( i == 1)
                  strParse = "'"+tPersonId[i].toString()+"'";
              else
                  strParse = strParse+" OR a.person_id = '"+
                          tPersonId[i].toString()+"'";
        }
                
        selectEvent = selectEvent +" WHERE (a.person_id = "+strParse+") "
                + "AND b.created = c.created AND b.created =  '"+Created+"' "
                + "AND b.vote_id = '"+VoteId+"' ORDER BY a.lastname";
        
        tableCleanUp();
                    
            try{
                stmt2 = conn1.createStatement();
                rs2 = stmt2.executeQuery(selectEvent);

                int iCount = 0;
                // Retrieve the data from the Select query
                while(rs2.next())
                {
                    model3.addRow(new Object[]{rs2.getString("firstname")+" "+
                            rs2.getString("lastname"),rs2.getString("party"),
                            rs2.getString("option_value")});
                    if ( iCount == 0)
                    {
                        strVoteTotalMinus = rs2.getString("vote_total_minus");
                        strVoteTotalOther = rs2.getString("vote_total_other");
                        strVoteTotalPlus = rs2.getString("vote_total_plus");
                        strQuestion = rs2.getString("question");
                        iCount++; 
                        // I only need to get this data once
                        // so i can put the iCount counter in here
                    }
                }
                LastRow2 = model3.getRowCount();
                model3.setRowCount(LastRow2);

                int iLength = strBillLabelTitle.length();
                if (iLength > 1150)
                {
                    Dimension dimLabelBillTitle = new Dimension(1150,24);
                    lblBillData.setMaximumSize(dimLabelBillTitle);
                }
                String Results = "";
                if (LastRow2 == 0)
                {
                    lblBillData.setText("No Data Found for "+ strBillLabelTitle.toString());
                    Results = "Representative probably not elected for selected year.";               
                }
                else
                {               
                    Results = "Total Nay: "+strVoteTotalMinus+" Total Other: "+
                        strVoteTotalOther+" Total Yea: "+strVoteTotalPlus;
                    lblBillData.setText(strQuestion.toString());
                }
                lblResults.setText(Results.toString());

            Dimension d = tableDetail.getPreferredSize();                                
            int rowWidth = d.width;
            int rowHeight2 = 0;
            int rowHeight = 0;
            if (LastRow2 > 10)
            {
                rowHeight2 = tableDetail.getRowHeight()*(10);                    
                rowHeight = tableDetail.getRowHeight()*(LastRow2);                    
            }
            else
            {
                rowHeight = tableDetail.getRowHeight()*(LastRow2);
                rowHeight2 = tableDetail.getRowHeight()*(LastRow2);                    
            }

            Dimension dim80 = new Dimension(rowWidth,rowHeight);
            tableDetail.setPreferredSize(dim80);
            if (LastRow2 > 10)
            {
                scrollPane_1.setMaximumSize(dim80);
                scrollPane_1.setSize(rowWidth,rowHeight2);
            }
            tableDetail.setLocation(1, 1);
            lblBillData.setVisible(true);
			      lblResults.setVisible(true);
			  
        }
            catch (SQLException e) {
                System.out.println( "Error msg:"+e.getMessage()+"<"+e+">");
            }

             try 
             {
                if (pst2 != null) {
                    pst2.close();
                }
                if (conn1 != null) {
                    conn1.close();
                }
            } 
             catch (SQLException ex) 
            {
                System.out.println( "Error msg:"+ex.getMessage()+"<"+ex+">");
            }
            tableDetail.repaint();            
        
        
    }
}

