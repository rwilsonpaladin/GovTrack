using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.IO;
using MySql.Data.MySqlClient;

namespace GovForms
{
    public partial class BillData : Form
    {
        String WhichState = null;
        String WhichGovernment = null;
        String WhichRepresentative = null;
        String WhatBillInfo = null;
        List<string> myPerson_Id = new List<string>();
        String CurrentVoterId = null;
        String LastVoterId = null;
        String selectedDate = null;

        public BillData(Form1 otherForm)
        {
            InitializeComponent();

            // Clear out data
            myPerson_Id.Clear();
            VoterID.Items.Clear();
            listView1.Columns.Clear();
            listView2.Columns.Clear();

            ResultLabel.Visible = false;
            BillTitleLabel.Visible = false;

            // Get data from previous screen
            this.otherForm = otherForm;
            GetOtherFormWhichState();
            GetOtherFormWhichGovernment();
            GetOtherFormWhichRepresentative();
            GetOtherFormWhichBillInfo();
            GetOtherFormPerson_Id();

            // Initialize listView1 Columns
            listView1.View = View.Details;
            listView1.FullRowSelect = true;

            ColumnHeader columnHeader1 = new ColumnHeader();
            columnHeader1.Text = "Date Bill Signed";
            columnHeader1.TextAlign = HorizontalAlignment.Left;
            columnHeader1.Width = 150;

            ColumnHeader columnHeader2 = new ColumnHeader();
            columnHeader2.Text = "Vote Result:";
            columnHeader2.TextAlign = HorizontalAlignment.Center;
            columnHeader2.Width = 150;

            ColumnHeader columnHeader3 = new ColumnHeader();
            columnHeader3.Text = "Bill Title";
            columnHeader3.TextAlign = HorizontalAlignment.Left;
            columnHeader3.Width = 700;

            this.listView1.Columns.Add(columnHeader1);
            this.listView1.Columns.Add(columnHeader2);
            this.listView1.Columns.Add(columnHeader3);

            // Initialize Column Data for listView2
            listView2.View = View.Details;

            ColumnHeader columnHeader1B = new ColumnHeader();
            columnHeader1B.Text = "Full Name";
            columnHeader1B.TextAlign = HorizontalAlignment.Left;
            columnHeader1B.Width = 175;

            ColumnHeader columnHeader2B = new ColumnHeader();
            columnHeader2B.Text = "Party";
            columnHeader2B.TextAlign = HorizontalAlignment.Center;
            columnHeader2B.Width = 90;

            ColumnHeader columnHeader3B = new ColumnHeader();
            columnHeader3B.Text = "Yea / Nay";
            columnHeader3B.TextAlign = HorizontalAlignment.Center;
            columnHeader3B.Width = 130;

            this.listView2.Columns.Add(columnHeader1B);
            this.listView2.Columns.Add(columnHeader2B);
            this.listView2.Columns.Add(columnHeader3B);

            listView2.BeginUpdate();

            String MyConnectionString = "Server=localhost;Database=federal;Uid=root;password=tesa411;Connect Timeout=99999";
            String cmdText;
            MySqlConnection connection = new MySqlConnection(MyConnectionString);
            MySqlDataReader reader = null;
            try
            {
                connection = new MySqlConnection(MyConnectionString);
                connection.Open(); //open the connection
                cmdText = "SELECT created, result, question, vote_id FROM ";
                if (WhichGovernment == "Senate")
                    cmdText = cmdText + "federal.senate_votes_h ";
                else
                    cmdText = cmdText + "federal.house_votes_h ";
                // Check to see if any search for Bill Info to take place
                if (!WhatBillInfo.Equals("BillInfo"))
                    cmdText = cmdText + "WHERE question " + "LIKE @myData ";
                cmdText = cmdText + "ORDER BY created";
               
                listView1.BeginUpdate();

                MySqlCommand cmd = new MySqlCommand(cmdText, connection);
                cmd.Parameters.AddWithValue("@myData", "%" + WhatBillInfo + "%");
                reader = cmd.ExecuteReader(); //execure the reader
                /*The Read() method points to the next record It return false if there are no more records else returns true.*/
                while (reader.Read())
                {
                    var item = new ListViewItem();
                    item.Text = reader.GetString("created");
                    item.SubItems.Add(reader["result"].ToString());  // 2nd column text
                    item.SubItems.Add(reader["question"].ToString());  // 3nd column text
                    listView1.Items.Add(item);
                    
                    // Add vote_id to nonvisible VoteID list view table
                    String VoteId = reader.GetString("vote_id");
                    VoterID.Items.Add(VoteId);
                }
            }
            catch (MySqlException err)
            {
                Console.WriteLine("Error: " + err.ToString());
            }
            finally
            {
                if (reader != null)
                    reader.Close();
                connection.Close();
            }
            listView1.EndUpdate();
            this.Controls.Add(this.listView1);

            if (VoterID.Items.Count == 0)
                BillHeaderLabel.Text = "No Data Found for <" + WhatBillInfo + ">";
            else
                BillHeaderLabel.Text = " ";

            Found.Text = "Found  " + VoterID.Items.Count + " Items.";
        }

        //Populate the List View2 table 
        private void populateDetail()
        {

            String MyConnectionString = "Server=localhost;Database=federal;Uid=root;password=tesa411;Connect Timeout=99999";
            String cmdText = null;
            String cmdBuild = null;
            int idCount = 0;
            int k = 0;
            Boolean flgFirst = true;

            String sMinus = null;
            String sOther = null;
            String sPlus = null;
            String sQuestion = null;

            // get rid of any previous data
            tableCleanUp();

            MySqlConnection connection = new MySqlConnection(MyConnectionString);
            MySqlDataReader reader = null;
            try
            {
                connection = new MySqlConnection(MyConnectionString);
                connection.Open(); //open the connection
                cmdText = "SELECT a.firstname, a.lastname, a.party, b.option_value, "+
                    "b.vote_total_minus, b.vote_total_other, b.vote_total_plus, c.question FROM ";

                if (WhichGovernment == "Senate")
                {
                    cmdText = cmdText + "  federal.senate AS a INNER JOIN "
                      + "federal.senate_votes_d AS b  ON a.person_id = "+
                        "b.person_id INNER JOIN federal.senate_votes_h AS c";
                }
                else
                {
                    cmdText = cmdText + " federal.house AS a INNER JOIN "
                    + "federal.house_votes_d AS b ON a.person_id = " +
                    "b.person_id INNER JOIN federal.house_votes_h AS c";
                }
                cmdText = cmdText + " ON b.vote_id = c.vote_id ";

                // Supply the Person ID for each Selected Member for the query
                idCount = myPerson_Id.Count();
                for (k = 0; k < idCount; k++)
                {
                    if (k == 0)
                        cmdBuild = myPerson_Id[k].ToString();
                    else
                        cmdBuild = cmdBuild + " OR a.person_id = " + myPerson_Id[k].ToString();
                }

                cmdText = cmdText + " WHERE (a.person_id = " + cmdBuild + ") "
                        + "AND b.created = c.created AND b.created = @myCreated "
                        + " AND b.vote_id = @myVoteId ORDER BY a.lastname";

                MySqlCommand cmd = new MySqlCommand(cmdText, connection);
                cmd.Parameters.AddWithValue("@myCreated", selectedDate.ToString());
                cmd.Parameters.AddWithValue("@myVoteId", CurrentVoterId.ToString());
                
                reader = cmd.ExecuteReader(); //execure the reader
                /*The Read() method points to the next record It return false if there are no more records else returns true.*/
                while (reader.Read())
                {
                    var item = new ListViewItem();
                    item.Text = reader.GetString("firstname") + " " + reader.GetString("lastname");
                    item.SubItems.Add(reader["party"].ToString());  // 2nd column text
                    item.SubItems.Add(reader["option_value"].ToString());  // 3nd column text
                    listView2.Items.Add(item);

                    if (flgFirst)
                    {
                        sMinus = reader.GetString("vote_total_minus");
                        sOther = reader.GetString("vote_total_other");
                        sPlus = reader.GetString("vote_total_plus");
                        sQuestion = reader.GetString("question");

                        BillTitleLabel.AutoSize = true;

                        ResultLabel.Text = "Total Nay: " + sMinus.ToString() + " Total Other: " + sOther.ToString() + " Total Yea: " + sPlus.ToString();
                        BillTitleLabel.Text = "Bill's Title:" + sQuestion.ToString();
                        int QuestionLength = sQuestion.Length;
                        flgFirst = false;
                    }
                }
                ResultLabel.Visible = true;
                BillTitleLabel.Visible = true;

            }
            catch (MySqlException err)
            {
                Console.WriteLine("Error: " + err.ToString());
            }
            finally
            {
                if (reader != null)
                    reader.Close();
                connection.Close();
            }
            listView2.EndUpdate();
            this.Controls.Add(this.listView2);

            if (String.IsNullOrEmpty(sQuestion))
            {
                BillTitleLabel.Text = "No Data Found";
                ResultLabel.Text = "Representative probably not elected for selected year.";
            }

        }

        private void tableCleanUp()
        {
            int iVar = 0;
                // Remove all the data in list View2 in reverse order
            foreach (ListViewItem item in listView2.Items)
            {
                listView2.Items[iVar].Remove();
            }
        }

        private void listView1_SelectedIndexChanged_1(object sender, EventArgs e)
        {
           
            if (listView1.SelectedItems.Count > 0)
            {
                int i = 0;
                foreach (ListViewItem item in listView1.Items)
                {
                    if (listView1.Items[i].Selected == true)
                    {
                        listView1.SelectedIndexChanged -= listView1_SelectedIndexChanged_1;
                        CurrentVoterId = VoterID.Items[i].ToString();
                        listView1.Items[i].Selected = false;
                        selectedDate = item.SubItems[0].Text;
                        listView1.Refresh();
                        break;
                    }
                    i = i + 1;
                }

                if (LastVoterId != CurrentVoterId)
                {
                    LastVoterId = CurrentVoterId;
                    populateDetail();
                }
                listView1.SelectedIndexChanged += listView1_SelectedIndexChanged_1;
            }
        }

        private Form1 otherForm;
        private void GetOtherFormWhichState()
        {WhichState = otherForm.ThisState;}

        private void GetOtherFormWhichGovernment()
        {WhichGovernment = otherForm.ThisGovernment;}
        
        private void GetOtherFormWhichRepresentative()
        {WhichRepresentative = otherForm.ThisRepresentative;}

        private void GetOtherFormWhichBillInfo()
        {WhatBillInfo = otherForm.ThisBillInfo;}

        private void GetOtherFormPerson_Id()
        {myPerson_Id = otherForm.ThisPersonId;}

        private void btnClose_Click(object sender, EventArgs e)
        {
            this.Close();
        }

    }

 }
