
/*
 * AlgorithmView.java
 *
 * Created on Sep 1, 2009, 1:26:02 PM
 */
package algorithm;

import java.util.ArrayList;
import javax.swing.JToggleButton;
import datamodel.Model;
import realdata.DataManager;
import realdata.Data1;
import javax.swing.JPopupMenu;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Timer;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.util.TimerTask;

/**
 * This class will show all of the algorithms as they run. The users can interact
 * with the view in order to stop jobs, pause jobs, restart jobs,
 * and remove them from view.
 *
 * The view keeps track of the running time of jobs, and the user can scroll
 * through the different running views to find jobs that are in error, see if
 * they can interpret the error message and fix it, etc. 
 * @author ross
 */
public class AlgorithmView extends javax.swing.JPanel implements ActionListener
{

    /**
     * The array list containing all algorithm objects that are tracked by
     * the AlgorithmView. These are scanned every 25 seconds to identify changes
     * in status if they have not already completed. If they have newly completed,
     * a model refresh is in order.
     */
    ArrayList<Algorithm> algos = new ArrayList<Algorithm>();
    /**
     * Where in the list the displayed algorithms are (only 6 are shown at a time).
     */
    private int curIdx = -1;
    /**
     * A pop-up menu gives the user options when they right click on the name
     * of an algorithm. They can cancel it, or do some error checking here.
     */
    private JPopupMenu popup = new JPopupMenu();
    /**
     * A pointer to the instance of this class that is used to generate the
     * popup menu items
     */
    private AlgorithmView me;
    private static AlgorithmView instance;
    /**
     * The alarm sounds every 25 seconds; this gives the AlgorithmView a chance
     * to call the update method on each algorithm to identify if it has changed
     * status, percent complete, or finished.
     */
    protected Timer t;
    /**
     * The alarm sounds every 25 seconds; this gives the AlgorithmView a chance
     * to call the update method on each algorithm to identify if it has changed
     * status, percent complete, or finished.
     */
    protected Alarm alarm;
    /**
     * popup menu items
     */
    private final JMenuItem restartMenuItem;
    private final JMenuItem retreatMenuItem;
    private JMenuItem errorMenuItem;

    /**
     * Creates a new AlgorithmView by first reading all the algorithms from
     * the database, creating the popupmenu, adding listeners, and scheduling
     * the alarm to regularly sound. It concludes by selecting the run tab
     * and updating the algorithm display. 
     */
    public AlgorithmView()
    {
        initComponents();
        algos = (ArrayList<Algorithm>) AlgorithmData.getAllAlgos().clone();

        me = this;
        popup.add(me.getMenuItem("Remove", me));
        errorMenuItem = me.getMenuItem("Error Info", me);
        restartMenuItem = me.getMenuItem("Restart", me);
        retreatMenuItem = me.getMenuItem("Retreat and Restart", me);
        this.name1.addMouseListener(ma);
        this.name2.addMouseListener(ma);
        this.name3.addMouseListener(ma);
        this.name4.addMouseListener(ma);
        this.name5.addMouseListener(ma);
        this.name6.addMouseListener(ma);
        alarm = new Alarm(this);
        t = new Timer();
        t.schedule(alarm, 25000, 25000);
        runRadBtn.setSelected(true);
        runRadBtnActionPerformed(null);
        updateAlgoListDisplay(0);
        instance = this;
    }

    /**
     * This class acts like a singleton with a public constructor. A little
     * scary; I'm not sure why it was done this way. 
     * @return
     */
    public static AlgorithmView getInstance()
    {
        return instance;
    }

    /**
     * When the program closes, this method should be called to ensure that
     * the data for the algorithmview is serialized. It serializes finished
     * or errored algorithms that the user wants to ignore. 
     */
    public void acceptClosingMessage()
    {
        AlgorithmData.serialize();
    }

    /**
     * Adds an algorithm to the server's db so that it can be run. This method
     * is called for algorithms that don't have parameters. 
     * @param type the type of algorithm that will be added to the db - this is the
     * 3 letter code that becomes the name of the algorithm.
     * @param jobID The jobtype id that will index into the steps that this algorithm
     * will follow.
     * @param projID The project that this algorithm belongs to.
     * @param tID The traitset that this algorithm belongs to (can be null).
     * @param mID The markerset that this algorithm belongs to (can be null). 
     */
    public void addAlgorithm(String type, int jobID,
            int projID, int tID, int mID)
    {
        addAlgorithm(type, jobID, projID, tID, mID, null);
    }

    /**
     * Adds an algorithm to the server's db so that it can be run.
     * @param type the type of algorithm that will be added to the db - this is the
     * 3 letter code that becomes the name of the algorithm.
     * @param jobID The jobtype id that will index into the steps that this algorithm
     * will follow.
     * @param projID The project that this algorithm belongs to.
     * @param tID The traitset that this algorithm belongs to (can be null).
     * @param mID The markerset that this algorithm belongs to (can be null).
     * @param parms The parameter object used to pass in the name and network id for
     * association algorithms. 
     */
    public void addAlgorithm(String type, int jobID,
            int projID, int tID, int mID, ParameterObject parms)
    {
        String algoname = null;

        algoname = type + getCurrentAlgoCode();

        try
        {
            AlgorithmData.getAllAlgos().add(new Algorithm(algoname, parms, jobID, projID, tID, mID));

            Algorithm a = AlgorithmData.getAllAlgos().get(AlgorithmData.getAllAlgos().size() - 1);
            if (this.allRadBtn.isSelected() || (this.runRadBtn.isSelected() && a.getStatus().equals("running"))
                    || (this.errRadBtn.isSelected() && a.getStatus().equals("error")))
            {
                algos.add(a);
                updateAlgoListDisplay(curIdx);
            }
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(this,
                    "This algorithm cannot be run.\n" + e.getMessage(),
                    "Algorithm failed.", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method calls the AlgoIdx class to finish generating the code.
     * This method will communicate with the database in order to determine
     * the usergroup id and the algorithm index that will be run. 
     * @return
     */
    private String getCurrentAlgoCode()
    {
        int max = getIdx();
        String s = Integer.toHexString(max + 1);
        while (s.length() < 3)
        {
            s = "0" + s;
        }
        ArrayList<String> where = new ArrayList<String>();
        where.add("teamid = team.id");
        where.add("uid=\"" + Data1.getInstance().mysqlusername + "\"");
        String key = (String)DataManager.runSelectQuery("keycode", "team,user",
                true, where, null).get(0);
        return key+s;
    }

    /**
     * Queries the database to find out what the current index for this algorithm
     * should be in the database. Also, it will increase the counter. 
     * @return
     */
    private int getIdx()
    {
        ArrayList<String> where = new ArrayList<String>();
        where.add("uid = \"" +Data1.getInstance().mysqlusername + "\"");
        where.add("teamid = team.id");
        int toRet = Integer.parseInt(
                (String)DataManager.runSelectQuery("algoidx", "user,team", true, where, null)
                .get(0));

        where.clear();
        where.add("id=" + Model.getInstance().getTeamId());
        DataManager.runUpdateQuery("team", "algoidx", ""+(toRet+1), where);

        return toRet;
    }

    /**
     * Goes through each of the 6 displayed visualizations of the running
     * algorithms and synchronizes the dispalay with the model values. 
     * @param start
     */
    private void updateAlgoListDisplay(int start)
    {
        if (curIdx != start)
        {
            /*if (curIdx != -1)
            {
            removeObservers();
            }*/
            curIdx = start;
        }
        this.scrollBar.setVisible(algos.size() > 6);
        this.scrollBar.setVisibleAmount(6);
        this.scrollBar.setMinimum(0);
        this.scrollBar.setMaximum(algos.size());
        this.scrollBar.setValue(start);

        for (int i = start; i < start + 6; i++)
        {
            if (i == start)
            {
                setUpLabel(i, name1, prog1, time1, status1, btn1);
            }
            if (i == start + 1)
            {
                setUpLabel(i, name2, prog2, time2, status2, btn2);
            }
            if (i == start + 2)
            {
                setUpLabel(i, name3, prog3, time3, status3, btn3);
            }
            if (i == start + 3)
            {
                setUpLabel(i, name4, prog4, time4, status4, btn4);
            }
            if (i == start + 4)
            {
                setUpLabel(i, name5, prog5, time5, status5, btn5);
            }
            if (i == start + 5)
            {
                setUpLabel(i, name6, prog6, time6, status6, btn6);
            }

        }

    }

    /**
     * Sets up a particular algorithm visualization to match an assigned algorithm.
     * @param i
     * @param name
     * @param prog
     * @param time
     * @param status
     * @param btn
     */
    private void setUpLabel(int i, javax.swing.JLabel name, javax.swing.JProgressBar prog,
            javax.swing.JLabel time, javax.swing.JLabel status, javax.swing.JToggleButton btn)
    {
        try
        {
            if (i < algos.size())
            {
                name.setVisible(true);
                name.setText(algos.get(i).getName());
                prog.setVisible(true);
                prog.setValue((int) algos.get(i).getPercentComplete());
                time.setVisible(true);
                time.setText(algos.get(i).getRunningTime());
                status.setVisible(true);
                status.setText(algos.get(i).getStatus());
                btn.setVisible(true);
                btn.setEnabled(status.getText().equals("paused") || status.getText().equals("running"));
                btn.setSelected(status.getText().equals("paused"));
            }
            else
            {
                name.setVisible(false);
                prog.setVisible(false);
                time.setVisible(false);
                status.setVisible(false);
                btn.setVisible(false);
            }
        }
        catch (Exception e)
        {
            //ignore because we've probably changed tabs during an update.
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        prog1 = new javax.swing.JProgressBar();
        name1 = new javax.swing.JLabel();
        name2 = new javax.swing.JLabel();
        name3 = new javax.swing.JLabel();
        name6 = new javax.swing.JLabel();
        name4 = new javax.swing.JLabel();
        name5 = new javax.swing.JLabel();
        btn1 = new javax.swing.JToggleButton();
        prog2 = new javax.swing.JProgressBar();
        prog3 = new javax.swing.JProgressBar();
        prog5 = new javax.swing.JProgressBar();
        prog4 = new javax.swing.JProgressBar();
        prog6 = new javax.swing.JProgressBar();
        status1 = new javax.swing.JLabel();
        status3 = new javax.swing.JLabel();
        status2 = new javax.swing.JLabel();
        status4 = new javax.swing.JLabel();
        status6 = new javax.swing.JLabel();
        status5 = new javax.swing.JLabel();
        btn2 = new javax.swing.JToggleButton();
        btn3 = new javax.swing.JToggleButton();
        btn5 = new javax.swing.JToggleButton();
        btn4 = new javax.swing.JToggleButton();
        btn6 = new javax.swing.JToggleButton();
        jLabel13 = new javax.swing.JLabel();
        allRadBtn = new javax.swing.JRadioButton();
        runRadBtn = new javax.swing.JRadioButton();
        cmpRadBtn = new javax.swing.JRadioButton();
        scrollBar = new javax.swing.JScrollBar();
        errRadBtn = new javax.swing.JRadioButton();
        time2 = new javax.swing.JLabel();
        time1 = new javax.swing.JLabel();
        time3 = new javax.swing.JLabel();
        time4 = new javax.swing.JLabel();
        time5 = new javax.swing.JLabel();
        time6 = new javax.swing.JLabel();

        setBackground(java.awt.SystemColor.controlLtHighlight);
        setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, java.awt.Color.white, java.awt.Color.blue, null, null));
        setPreferredSize(new java.awt.Dimension(296, 218));

        prog1.setStringPainted(true);

        name1.setFont(new java.awt.Font("Tahoma", 0, 9));
        name1.setText("jLabel1");

        name2.setFont(new java.awt.Font("Tahoma", 0, 9));
        name2.setText("jLabel1");

        name3.setFont(new java.awt.Font("Tahoma", 0, 9));
        name3.setText("jLabel1");

        name6.setFont(new java.awt.Font("Tahoma", 0, 9));
        name6.setText("jLabel1");

        name4.setFont(new java.awt.Font("Tahoma", 0, 9));
        name4.setText("jLabel1");

        name5.setFont(new java.awt.Font("Tahoma", 0, 9));
        name5.setText("jLabel1");

        btn1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/algorithm/pause.gif"))); // NOI18N
        btn1.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/algorithm/play.gif"))); // NOI18N
        btn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn1ActionPerformed(evt);
            }
        });

        prog2.setStringPainted(true);

        prog3.setStringPainted(true);

        prog5.setStringPainted(true);

        prog4.setStringPainted(true);

        prog6.setStringPainted(true);

        status1.setFont(new java.awt.Font("DejaVu Sans", 2, 11));
        status1.setText("running");

        status3.setFont(new java.awt.Font("DejaVu Sans", 2, 11));
        status3.setText("paused");

        status2.setFont(new java.awt.Font("DejaVu Sans", 2, 11));
        status2.setText("error");

        status4.setFont(new java.awt.Font("DejaVu Sans", 2, 11));
        status4.setText("complete");

        status6.setFont(new java.awt.Font("DejaVu Sans", 2, 11));
        status6.setText("jLabel7");

        status5.setFont(new java.awt.Font("DejaVu Sans", 2, 11));
        status5.setText("jLabel7");

        btn2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/algorithm/pause.gif"))); // NOI18N
        btn2.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/algorithm/play.gif"))); // NOI18N
        btn2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn2ActionPerformed(evt);
            }
        });

        btn3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/algorithm/pause.gif"))); // NOI18N
        btn3.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/algorithm/play.gif"))); // NOI18N
        btn3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn3ActionPerformed(evt);
            }
        });

        btn5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/algorithm/pause.gif"))); // NOI18N
        btn5.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/algorithm/play.gif"))); // NOI18N
        btn5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn5ActionPerformed(evt);
            }
        });

        btn4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/algorithm/pause.gif"))); // NOI18N
        btn4.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/algorithm/play.gif"))); // NOI18N
        btn4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn4ActionPerformed(evt);
            }
        });

        btn6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/algorithm/pause.gif"))); // NOI18N
        btn6.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/algorithm/play.gif"))); // NOI18N
        btn6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn6ActionPerformed(evt);
            }
        });

        jLabel13.setText("Show:");

        allRadBtn.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup1.add(allRadBtn);
        allRadBtn.setText("all");
        allRadBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allRadBtnActionPerformed(evt);
            }
        });

        runRadBtn.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup1.add(runRadBtn);
        runRadBtn.setText("running");
        runRadBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runRadBtnActionPerformed(evt);
            }
        });

        cmpRadBtn.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup1.add(cmpRadBtn);
        cmpRadBtn.setText("complete");
        cmpRadBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmpRadBtnActionPerformed(evt);
            }
        });

        scrollBar.addAdjustmentListener(new java.awt.event.AdjustmentListener() {
            public void adjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {
                scrollBarAdjustmentValueChanged(evt);
            }
        });

        errRadBtn.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup1.add(errRadBtn);
        errRadBtn.setText("error");
        errRadBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                errRadBtnActionPerformed(evt);
            }
        });

        time2.setFont(new java.awt.Font("DejaVu Sans", 1, 13));
        time2.setText("10 12:17:41");

        time1.setFont(new java.awt.Font("DejaVu Sans", 1, 13));
        time1.setText("10 12:17:41");

        time3.setFont(new java.awt.Font("DejaVu Sans", 1, 13));
        time3.setText("10 12:17:41");

        time4.setFont(new java.awt.Font("DejaVu Sans", 1, 13));
        time4.setText("10 12:17:41");

        time5.setFont(new java.awt.Font("DejaVu Sans", 1, 13));
        time5.setText("10 12:17:41");

        time6.setFont(new java.awt.Font("DejaVu Sans", 1, 13));
        time6.setText("10 12:17:41");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(name3)
                                .addComponent(name4)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(name6)
                                    .addComponent(name5)))
                            .addComponent(name2)
                            .addComponent(name1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(prog1, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(prog3, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(prog4, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(prog2, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(prog5, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(prog6, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(7, 7, 7)
                                .addComponent(status6))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(status4)
                                    .addComponent(status5)
                                    .addComponent(status3)
                                    .addComponent(status2)
                                    .addComponent(status1))))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(time2)
                            .addComponent(time1)
                            .addComponent(time3)
                            .addComponent(time4)
                            .addComponent(time5)
                            .addComponent(time6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btn6, 0, 0, Short.MAX_VALUE)
                            .addComponent(btn5, 0, 0, Short.MAX_VALUE)
                            .addComponent(btn4, 0, 0, Short.MAX_VALUE)
                            .addComponent(btn3, 0, 0, Short.MAX_VALUE)
                            .addComponent(btn2, 0, 0, Short.MAX_VALUE)
                            .addComponent(btn1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(runRadBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(errRadBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmpRadBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(allRadBtn)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addComponent(scrollBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollBar, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btn1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addComponent(btn2, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn3, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn4, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn5, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn6, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(prog1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(status1)
                            .addComponent(time1)
                            .addComponent(name1))
                        .addGap(7, 7, 7)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(prog2, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(status2)
                            .addComponent(time2)
                            .addComponent(name2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(prog3, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(status3)
                            .addComponent(time3)
                            .addComponent(name3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(prog4, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(status4)
                            .addComponent(time4)
                            .addComponent(name4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(prog5, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(status5)
                            .addComponent(time5)
                            .addComponent(name5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(prog6, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(status6)
                            .addComponent(time6)
                            .addComponent(name6))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmpRadBtn)
                    .addComponent(jLabel13)
                    .addComponent(runRadBtn)
                    .addComponent(errRadBtn)
                    .addComponent(allRadBtn))
                .addGap(30, 30, 30))
        );
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Allows the user to scroll through the list - the same visualization
     * pieces are assigned to new algorithms and update accordingly.
     * @param evt
     */
    private void scrollBarAdjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {//GEN-FIRST:event_scrollBarAdjustmentValueChanged
        //System.out.println(evt.getValue());
        this.updateAlgoListDisplay(evt.getValue());
    }//GEN-LAST:event_scrollBarAdjustmentValueChanged

    /**
     * The user can choose which algorithms to examine - they do this by
     * switching between radio buttons. The algorithm visualizations update
     * accordingly
     * @param evt
     */
    private void allRadBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allRadBtnActionPerformed
        //removeObservers();
        curIdx = -1;
        this.algos = (ArrayList<Algorithm>) AlgorithmData.getAllAlgos().clone();
        this.updateAlgoListDisplay(0);
    }//GEN-LAST:event_allRadBtnActionPerformed

    /**
     * The user can choose which algorithms to examine - they do this by
     * switching between radio buttons. The algorithm visualizations update
     * accordingly
     * @param evt
     */
    private void runRadBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runRadBtnActionPerformed
        //removeObservers();
        curIdx = -1;
        this.algos = new ArrayList<Algorithm>();
        ArrayList<Algorithm> allAlgos = AlgorithmData.getAllAlgos();
        for (int i = 0; i < allAlgos.size(); i++)
        {
            if (allAlgos.get(i).getStatus().equals("running")
                    || allAlgos.get(i).getStatus().equals("paused")
                    || allAlgos.get(i).getStatus().equals("pausing")
                    || allAlgos.get(i).getStatus().equals("init..."))
            {
                algos.add(allAlgos.get(i));
            }
        }
        this.updateAlgoListDisplay(0);
    }//GEN-LAST:event_runRadBtnActionPerformed

    /**
     * The user can choose which algorithms to examine - they do this by
     * switching between radio buttons. The algorithm visualizations update
     * accordingly
     * @param evt
     */
    private void cmpRadBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmpRadBtnActionPerformed
        //removeObservers();
        curIdx = -1;
        this.algos = new ArrayList<Algorithm>();
        ArrayList<Algorithm> allAlgos = AlgorithmData.getAllAlgos();
        for (int i = 0; i < allAlgos.size(); i++)
        {
            if (allAlgos.get(i).getStatus().equals("complete"))
            {
                algos.add(allAlgos.get(i));
            }
        }
        this.updateAlgoListDisplay(0);
    }//GEN-LAST:event_cmpRadBtnActionPerformed

    /**
     * The user can choose which algorithms to examine - they do this by
     * switching between radio buttons. The algorithm visualizations update
     * accordingly
     * @param evt
     */
    private void errRadBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_errRadBtnActionPerformed
        //removeObservers();
        curIdx = -1;
        this.algos = new ArrayList<Algorithm>();
        ArrayList<Algorithm> allAlgos = AlgorithmData.getAllAlgos();
        for (int i = 0; i < allAlgos.size(); i++)
        {
            if (allAlgos.get(i).getStatus().equals("error"))
            {
                algos.add(allAlgos.get(i));
            }
        }
        this.updateAlgoListDisplay(0);
    }//GEN-LAST:event_errRadBtnActionPerformed

    /**
     * This method is called to to find out whioh algorithm should be paused
     * or unpaused on one of these types of events. 
     * @param name
     * @return
     */
    private Algorithm getAlgorithmAtVisual(String name)
    {
        for (int i = 0; i < AlgorithmData.getAllAlgos().size(); i++)
        {
            if (AlgorithmData.getAllAlgos().get(i).getName().equals(name))
            {
                return AlgorithmData.getAllAlgos().get(i);
            }
        }
        return null;
    }

    /**
     * Users can choose to stop the progression of an algorithm (doesn't cancel
     * condor jobs, just doesn't spawn any new ones). This happens through
     * the control of these buttons.
     * @param evt
     */
    private void btn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn1ActionPerformed
        Algorithm a = getAlgorithmAtVisual(name1.getText());
        handlePauseEvent(a, btn1);
    }//GEN-LAST:event_btn1ActionPerformed

    /**
     * Users can choose to stop the progression of an algorithm (doesn't cancel
     * condor jobs, just doesn't spawn any new ones). This happens through
     * the control of these buttons.
     * @param evt
     */
    private void btn2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn2ActionPerformed
        Algorithm a = getAlgorithmAtVisual(name2.getText());
        handlePauseEvent(a, btn2);
    }//GEN-LAST:event_btn2ActionPerformed

    /**
     * Users can choose to stop the progression of an algorithm (doesn't cancel
     * condor jobs, just doesn't spawn any new ones). This happens through
     * the control of these buttons.
     * @param evt
     */
    private void btn3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn3ActionPerformed
        Algorithm a = getAlgorithmAtVisual(name3.getText());
        handlePauseEvent(a, btn3);
    }//GEN-LAST:event_btn3ActionPerformed

    /**
     * Users can choose to stop the progression of an algorithm (doesn't cancel
     * condor jobs, just doesn't spawn any new ones). This happens through
     * the control of these buttons.
     * @param evt
     */
    private void btn4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn4ActionPerformed
        Algorithm a = getAlgorithmAtVisual(name4.getText());
        handlePauseEvent(a, btn4);
    }//GEN-LAST:event_btn4ActionPerformed

    /**
     * Users can choose to stop the progression of an algorithm (doesn't cancel
     * condor jobs, just doesn't spawn any new ones). This happens through
     * the control of these buttons.
     * @param evt
     */
    private void btn5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn5ActionPerformed
        Algorithm a = getAlgorithmAtVisual(name5.getText());
        handlePauseEvent(a, btn5);
    }//GEN-LAST:event_btn5ActionPerformed

    /**
     * Users can choose to stop the progression of an algorithm (doesn't cancel
     * condor jobs, just doesn't spawn any new ones). This happens through
     * the control of these buttons.
     * @param evt
     */
    private void btn6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn6ActionPerformed
        Algorithm a = getAlgorithmAtVisual(name6.getText());
        handlePauseEvent(a, btn6);
    }//GEN-LAST:event_btn6ActionPerformed

    /**
     * Called when a user selects an algorithm to puase. This writes a
     * message to the database that will cause the running algorithm to pause. 
     * @param a
     * @param btn
     */
    private void handlePauseEvent(Algorithm a, JToggleButton btn)
    {
        if (btn.isSelected())
        {
            if (a.pauseAlgorithm())
            {
                this.updateAlgoListDisplay(curIdx);
            }
            else
            {
                btn.setSelected(true);
                JOptionPane.showMessageDialog(this,
                        "This algorithm cannot be paused.",
                        "Pause failed.", JOptionPane.ERROR_MESSAGE);
            }
        }
        else
        {
            a.unpauseAlgorithm();
        }
    }
    private String popupName = "";
    /**
     * The controls that manage the popups and then trigger the appropriate events.
     * The user can restart and algorithm, pause it, or remove it from the list
     * (which stops the algorithm if it was running). 
     */
    private MouseListener ma = new MouseAdapter()
    {

        private void checkForPopup(MouseEvent e)
        {
            boolean toAdd = false;

            if (e.isPopupTrigger())
            {
                popup.remove(errorMenuItem);
                popup.remove(restartMenuItem);
                popup.remove(retreatMenuItem);
                popupName = ((JLabel) e.getComponent()).getText();
                if (name1.getText().equals(popupName))
                {
                    if (status1.getText().equals("error"))
                    {
                        toAdd = true;
                    }
                }
                else
                {
                    if (name2.getText().equals(popupName))
                    {
                        if (status2.getText().equals("error"))
                        {
                            toAdd = true;
                        }
                    }
                    else
                    {
                        if (name3.getText().equals(popupName))
                        {
                            if (status3.getText().equals("error"))
                            {
                                toAdd = true;
                            }
                        }
                        else
                        {
                            if (name4.getText().equals(popupName))
                            {
                                if (status4.getText().equals("error"))
                                {
                                    toAdd = true;
                                }
                            }
                            else
                            {
                                if (name5.getText().equals(popupName))
                                {
                                    if (status5.getText().equals("error"))
                                    {
                                        toAdd = true;
                                    }
                                }
                                else
                                {
                                    if (name6.getText().equals(popupName))
                                    {
                                        if (status6.getText().equals("error"))
                                        {
                                            toAdd = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (toAdd)
                {
                    popup.add(errorMenuItem);
                    popup.add(restartMenuItem);
                    popup.add(retreatMenuItem);
                }

                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }

        public
        @Override
        void mousePressed(MouseEvent e)
        {
            checkForPopup(e);
        }

        public
        @Override
        void mouseReleased(MouseEvent e)
        {
            checkForPopup(e);
        }

        public
        @Override
        void mouseClicked(MouseEvent e)
        {
            checkForPopup(e);
        }
    };

    /**
     * Generates a menu item that will call the appropriate methods when
     * selected. Should be use to create each menu item in the popup handler.
     * @param s The text of the item
     * @param al Who is going to listen to the item. 
     * @return
     */
    private JMenuItem getMenuItem(String s, ActionListener al)
    {
        JMenuItem menuItem = new JMenuItem(s);
        menuItem.setActionCommand(s.toUpperCase());
        menuItem.addActionListener(al);
        return menuItem;
    }

    /**
     * This is the action listener that will listen to the popup menu for
     * a selection of any of the menu items. It will then call the appropriate
     * methods to remove, restart, display error info for the algorithm, etc. 
     * @param e
     */
    public void actionPerformed(ActionEvent e)
    {
        String ac = e.getActionCommand();
        ArrayList<Algorithm> allAlgos = AlgorithmData.getAllAlgos();
        if (ac.equals("REMOVE"))
        {
            for (int i = 0; i < allAlgos.size(); i++)
            {
                if (allAlgos.get(i).getName().equals(popupName))
                {
                    allAlgos.remove(i);
                }
            }
            for (int i = 0; i < this.algos.size(); i++)
            {
                if (algos.get(i).getName().equals(popupName))
                {
                    AlgorithmData.ignoreAlgorithm(algos.get(i));
                    //algos.get(i).deleteObserver(this);
                    algos.get(i).stopAlgorithm();
                    algos.remove(i);
                }
            }
            //removeObservers();
            this.updateAlgoListDisplay(curIdx);
        }
        if (ac.equals("ERROR INFO"))
        {
            Algorithm a = null;
            for (int i = 0; i < allAlgos.size(); i++)
            {
                if (allAlgos.get(i).getName().equals(popupName))
                {
                    a = allAlgos.get(i);
                }
            }
            JOptionPane.showMessageDialog(this,
                    a.getErrorMessage(),
                    "Error Message for " + a.getName(), JOptionPane.ERROR_MESSAGE);

        }
        if (ac.equals("RETREAT AND RESTART"))
        {
            Algorithm a = null;
            for (int i = 0; i < allAlgos.size(); i++)
            {
                if (allAlgos.get(i).getName().equals(popupName))
                {
                    a = allAlgos.get(i);
                }
            }
            a.restart(true);
        }
        if (ac.equals("RESTART"))
        {
            Algorithm a = null;
            for (int i = 0; i < allAlgos.size(); i++)
            {
                if (allAlgos.get(i).getName().equals(popupName))
                {
                    a = allAlgos.get(i);
                }
            }
            a.restart(false);
        }


    }

    /**
     * This is the timer class that goes off every 25 seconds. It goes through
     * each of the algorithms currently being watched and updates them with
     * the database. 
     */
    public class Alarm extends TimerTask
    {

        private AlgorithmView owner;

        public Alarm(AlgorithmView owner)
        {
            this.owner = owner;
        }

        public void run()
        {
            for (int i = 0; i < algos.size(); i++)
            {

                Algorithm current = algos.get(i);

                current.algoUpdate();

            }
            owner.updateAlgoListDisplay(curIdx);
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton allRadBtn;
    private javax.swing.JToggleButton btn1;
    private javax.swing.JToggleButton btn2;
    private javax.swing.JToggleButton btn3;
    private javax.swing.JToggleButton btn4;
    private javax.swing.JToggleButton btn5;
    private javax.swing.JToggleButton btn6;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JRadioButton cmpRadBtn;
    private javax.swing.JRadioButton errRadBtn;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel name1;
    private javax.swing.JLabel name2;
    private javax.swing.JLabel name3;
    private javax.swing.JLabel name4;
    private javax.swing.JLabel name5;
    private javax.swing.JLabel name6;
    private javax.swing.JProgressBar prog1;
    private javax.swing.JProgressBar prog2;
    private javax.swing.JProgressBar prog3;
    private javax.swing.JProgressBar prog4;
    private javax.swing.JProgressBar prog5;
    private javax.swing.JProgressBar prog6;
    private javax.swing.JRadioButton runRadBtn;
    private javax.swing.JScrollBar scrollBar;
    private javax.swing.JLabel status1;
    private javax.swing.JLabel status2;
    private javax.swing.JLabel status3;
    private javax.swing.JLabel status4;
    private javax.swing.JLabel status5;
    private javax.swing.JLabel status6;
    private javax.swing.JLabel time1;
    private javax.swing.JLabel time2;
    private javax.swing.JLabel time3;
    private javax.swing.JLabel time4;
    private javax.swing.JLabel time5;
    private javax.swing.JLabel time6;
    // End of variables declaration//GEN-END:variables
}
