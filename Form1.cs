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

    public partial class Form1 : Form
    {
        String WhichState = null;
        String WhichGovernment = null;
        String WhichRepresentative = null;
        String WhatBillInfo = null;
        List<string> myPersonId = new List<string>();

        public String ThisState
        {get{return WhichState;}}

        public String ThisGovernment
        {get{return WhichGovernment;}}

        public String ThisRepresentative
        {get{return WhichRepresentative;}}

        public String ThisBillInfo
        { get { return WhatBillInfo; } }


        public List<string> ThisPersonId
        { get { return myPersonId; } }

        // Populate the States Drop Down Box
        public void InitStates()
        {
            State.Items.Clear();
            State.Text = "Please Select State";
            State.Items.Add("Alabama");
            State.Items.Add("Alaska");
            State.Items.Add("Arizona");
            State.Items.Add("Arkansas");
            State.Items.Add("California");
            State.Items.Add("Colorado");
            State.Items.Add("Connecticut");
            State.Items.Add("Delaware");
            State.Items.Add("Florida");
            State.Items.Add("Georgia");
            State.Items.Add("Hawaii");
            State.Items.Add("Idaho");
            State.Items.Add("Illinois");
            State.Items.Add("Indiana");
            State.Items.Add("Iowa");
            State.Items.Add("Kansas");
            State.Items.Add("Kentucky");
            State.Items.Add("Louisiana");
            State.Items.Add("Maine");
            State.Items.Add("Maryland");
            State.Items.Add("Massachusetts");
            State.Items.Add("Michigan");
            State.Items.Add("Minnesota");
            State.Items.Add("Mississippi");
            State.Items.Add("Missouri");
            State.Items.Add("Montana");
            State.Items.Add("Nebraska");
            State.Items.Add("Nevada");
            State.Items.Add("New Hampshire");
            State.Items.Add("New Jersey");
            State.Items.Add("New Mexico");
            State.Items.Add("New York");
            State.Items.Add("North Carolina");
            State.Items.Add("North Dakota");
            State.Items.Add("Ohio");
            State.Items.Add("Oklahoma");
            State.Items.Add("Oregon");
            State.Items.Add("Pennsylvania");
            State.Items.Add("Rhode Island");
            State.Items.Add("South Carolina");
            State.Items.Add("South Dakota");
            State.Items.Add("Tennessee");
            State.Items.Add("Texas");
            State.Items.Add("Utah");
            State.Items.Add("Vermont");
            State.Items.Add("Virginia");
            State.Items.Add("Washington");
            State.Items.Add("West Virginia");
            State.Items.Add("Wisconsin");
            State.Items.Add("Wyoming");
        }

        public Form1()
        {
            InitializeComponent();
            InitStates();

            // group both radio buttons together
            groupBoxCongress.Controls.Add(radioSenate);
            groupBoxCongress.Controls.Add(radioHouseOfRepresentatives);
            // Add the GroupBox to the Form.
            Controls.Add(groupBoxCongress);

            // Set up dummy values in case not used for selection
            WhichGovernment = "- Please Select -";
            WhatBillInfo = "BillInfo";
        }

        // Set up to reference States drop down list to get abbreviation
        private static readonly IDictionary<string, string> stateID = new Dictionary<string, string>
        {
            { "Alabama","AL"},
            { "Alaska","AK" },
            { "Arizona","AZ" },
            { "Arkansas","AR" },
            { "California","CA" },
            { "Colorado","CO" },
            { "Connecticut","CT" },
            { "Delaware","DE" },
            { "Florida","FL" },
            { "Georgia","GA" },
            { "Hawaii","HI" },
            { "Idaho","ID" },
            { "Illinois","IL" },
            { "Indiana","IN" },
            { "Iowa","IA" },
            { "Kansas","KS" },
            { "Kentucky","KY" },
            { "Louisiana","LA" },
            { "Maine","ME" },
            { "Maryland","MD" },
            { "Massachusetts","MA" },
            { "Michigan","MI" },
            { "Minnesota","MN" },
            { "Mississippi","MS" },
            { "Missouri","MO" },
            { "Montana","MT" },
            { "Nebraska","NE" },
            { "Nevada","NV" },
            { "New Hampshire","NH" },
            { "New Jersey","NJ" },
            { "New Mexico","NM" },
            { "New York","NY" },
            { "North Carolina","NC" },
            { "North Dakota","ND" },
            { "Ohio","OH" },
            { "Oklahoma","OK" },
            { "Oregon","OR" },
            { "Pennsylvania","PA" },
            { "Rhode Island","RI" },
            { "South Carolina","SC" },
            { "South Dakota","SD" },
            { "Tennessee","TN" },
            { "Texas","TX" },
            { "Utah","UT" },
            { "Vermont","VT" },
            { "Virginia","VA" },
            { "Washington","WA" },
            { "West Virginia","WV" },
            { "Wisconsin","WI" },
            { "Wyoming","WY" }
        };
     
        public static string StateIDExpand(String stateUS)
        {
            String stateAbrevID;
        //   * error handler is to return an empty string rather than throwing an exception */
            return stateID.TryGetValue(stateUS, out stateAbrevID) ? stateAbrevID : string.Empty;
        }

        // Clears out old data and resets to default initialization
        public void Cleanup()
        {
            REPIDListBox.Items.Clear();
            listView1.Items.Clear();
            listView1.Clear();
            listView1.Refresh();
            SelectAllCheckBox.Checked = false;
        }

        private void State_SelectedIndexChanged(object sender, EventArgs e)
        {
            Cleanup();
            // If radio button not selected it will require one to go further
            if (WhichGovernment.Equals("Please Select One"))
            {
                MessageBox.Show("Please Select which type of Congress Body Search: Senate or House of Representatives",
        "Please Select");
                return;
            }
            // Which State was chosen and lets get the abreviation for it
            WhichState = State.Text;
            String stateAbrev = StateIDExpand(WhichState);

            // Setting up the Columns for the listView which shows
            // the Congressional Members
            listView1.View = View.Details;
            listView1.Columns.Clear();
            listView1.CheckBoxes = true;
            listView1.FullRowSelect = true;
            listView1.GridLines = true;
            listView1.MultiSelect = true;

            if (WhichGovernment == "Senate")
            {
                ColumnHeader columnHeader1 = new ColumnHeader();
                columnHeader1.Text = "Full Name";
                columnHeader1.TextAlign = HorizontalAlignment.Left;
                columnHeader1.Width = 175;

                ColumnHeader columnHeader3 = new ColumnHeader();
                columnHeader3.Text = "Party";
                columnHeader3.TextAlign = HorizontalAlignment.Left;
                columnHeader3.Width = -2;

                this.listView1.Columns.Add(columnHeader1);
                this.listView1.Columns.Add(columnHeader3);
            }
            else
            {
                ColumnHeader columnHeader1 = new ColumnHeader();
                columnHeader1.Text = "Full Name";
                columnHeader1.TextAlign = HorizontalAlignment.Left;
                columnHeader1.Width = 190;

                ColumnHeader columnHeader2 = new ColumnHeader();
                columnHeader2.Text = "District ";
                columnHeader2.TextAlign = HorizontalAlignment.Left;
                columnHeader2.Width = 92;

                ColumnHeader columnHeader3 = new ColumnHeader();
                columnHeader3.Text = "Party";
                columnHeader3.TextAlign = HorizontalAlignment.Left;
                columnHeader3.Width = 90;
                
                this.listView1.Columns.Add(columnHeader1);
                this.listView1.Columns.Add(columnHeader2);
                this.listView1.Columns.Add(columnHeader3);
            }
            listView1.BeginUpdate();

            // Setting up the conncetion to the MySql Database
            String MyConnectionString = "Server=localhost;Database=federal;Uid=root;password=tesa411;Connect Timeout=99999";
            String cmdText;
            MySqlConnection connection = new MySqlConnection(MyConnectionString);
            MySqlDataReader reader = null;
 
            // Clearing out everything before running a connection to
            // the database
            REPIDListBox.Items.Clear();
            listView1.Items.Clear();
            myPersonId.Clear();

            try
            {
                connection = new MySqlConnection(MyConnectionString);
                connection.Open(); //open the connection
                if (WhichGovernment == "Senate")
                {
                    cmdText = "SELECT firstname, lastname, party, person_id FROM ";
                    cmdText = cmdText + "federal.senate WHERE State_id = @myStateId ORDER BY lastname";
                }
                else
                {
                    cmdText = "SELECT firstname, lastname, district, party, person_id FROM ";
                    cmdText = cmdText + "federal.house WHERE State_id = @myStateId ORDER BY lastname";
                }

                MySqlCommand cmd = new MySqlCommand(cmdText, connection);
                cmd.Parameters.AddWithValue("@myStateId", stateAbrev);
                reader = cmd.ExecuteReader(); //execure the reader
                /*The Read() method points to the next record It return false if there are no more records else returns true.*/
                while (reader.Read())
                {
                    var item = new ListViewItem();
                    item.Text = reader.GetString("firstname") + " " + reader.GetString("lastname");
                    if (WhichGovernment != "Senate")
                        item.SubItems.Add("District # "+ reader["district"].ToString());  // 2nd column text
                    item.SubItems.Add(reader["party"].ToString());  // 3nd column text
                    listView1.Items.Add(item);

                    // Add person_id to a nonvisible listview table
                    String RepId = reader.GetString("person_id");
                    REPIDListBox.Items.Add(RepId);
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
        }

        // Search Button Clicked
        private void Search_Click(object sender, EventArgs e)
        {
            bool flgRepChecked = false;
            // Get if any request for selection detail
            WhatBillInfo = BillInfo.Text;
            // Make sure State and Congress were chosen
            if (WhichGovernment.Equals("Please Select One") || (State.Text.Equals("Please Select State")))
            {
                MessageBox.Show("Please Select which type of Congressional Body and for State Search.",
        "Please Select");
                return;
            }
            // Clear out List Array
            myPersonId.Clear();

            int i = 0;
            foreach (ListViewItem item in listView1.Items)
            {
                if (item.Checked == true) // Congress Member Selected
                {   // Add PersonId of Congress Member to List Array
                    myPersonId.Add(REPIDListBox.GetItemText(REPIDListBox.Items[i]));
                    flgRepChecked = true;
                }
                    i = i + 1;
            }
            if (flgRepChecked == true)
            { // Go to BillHeader screen
                BillData BillData = new BillData(this);
                BillData.ShowDialog();
            }
            else
            {
                MessageBox.Show("Please Select which Representative to Query.",
        "Please Select");
                return;
            }
        }

        // Select All Check Box - Select all or disselect all
        private void SelectAllCheckBox_CheckedChanged(object sender, EventArgs e)
        {
            listView1.BeginUpdate();

            listView1.ItemChecked -= listView1_ItemCheck1_1;
            if (SelectAllCheckBox.Checked)
            {
                int i = 0;
                foreach (ListViewItem item in listView1.Items)
                {
                    item.Checked = true;
                    REPIDListBox.SetItemCheckState(i, CheckState.Checked);
                    i = i + 1;
                }
                SelectAllCheckBox.CheckState = CheckState.Checked; 
            }
            else
            {
                int i = 0;
                foreach (ListViewItem item in listView1.Items)
                {
                    item.Checked = false;
                    REPIDListBox.SetItemCheckState(i, CheckState.Unchecked);
                    i = i + 1;
                }
                SelectAllCheckBox.CheckState = CheckState.Unchecked; 
            }
            listView1.ItemChecked += listView1_ItemCheck1_1;
            listView1.EndUpdate();
        }

        private void listView1_ItemCheck1(object sender,
            System.Windows.Forms.ItemCheckEventArgs e)
 	    {
            int z = -10;
            z = e.Index;
            if (SelectAllCheckBox.Checked == true)
            {
                if (e.CurrentValue == CheckState.Checked)
                {
                    int i = 0;
                    foreach (ListViewItem item in listView1.Items)
                    {
                        if (i == z)
                        {
                            SelectAllCheckBox.CheckedChanged -= SelectAllCheckBox_CheckedChanged;
                            if (item.Checked != false)
                                SelectAllCheckBox.Checked = false;
                            else
                                SelectAllCheckBox.Checked = true;
                            SelectAllCheckBox.CheckedChanged += SelectAllCheckBox_CheckedChanged;
                            break;
                        }
                        i = i + 1;
                    }
                }
            }
    }

        private void listView1_ItemCheck1_1(object sender, EventArgs e)
        {
            // needed as a dummy variable to not overload the preceeding 
            // listView1_ItemCheck1
        }

        private void radioSenate_CheckedChanged(object sender, EventArgs e)
        {
            if (radioSenate.Checked)
                radioHouseOfRepresentatives.Checked = false;
            if (WhichGovernment == "House of Representatives")
            {
                InitStates();
                Cleanup();
            }
            WhichGovernment = "Senate";
        }

        private void radioHouseOfRepresentatives_CheckedChanged(object sender, EventArgs e)
        {
            if (radioHouseOfRepresentatives.Checked)
                radioSenate.Checked = false;
            if (WhichGovernment == "Senate")
            {
                InitStates();
                Cleanup();
            }
            WhichGovernment = "House of Representatives";
        }

        private void Form1_FormClosing(object sender, FormClosingEventArgs e)
        {
            if (e.CloseReason == CloseReason.UserClosing)
            {
                DialogResult result = MessageBox.Show("Do you really want to exit?", "Gov Forms", MessageBoxButtons.YesNo);
                if (result == DialogResult.Yes)
                    Environment.Exit(0);
                else
                    e.Cancel = true;
            }
            else
                e.Cancel = true;
        }   

        private void Close_Click(object sender, EventArgs e)
        {
            this.Close();
        }
 
     }
}
