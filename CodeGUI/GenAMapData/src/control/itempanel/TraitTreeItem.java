package control.itempanel;

import control.DataAddRemoveHandler;
import datamodel.Model;
import datamodel.TraitTree;
import datamodel.TraitTreeVal;
import realdata.DataManager;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import javax.swing.JFrame;

/**
 * Imports a tree that has been parsed from a file and puts it into the database.
 * @author rcurtis
 */
public class TraitTreeItem extends ThreadItem
{

    /**
     * The text of the last error
     */
    public String errorText="";
    /**
     * The current status of the import
     */
    public String status="In queue ...";
    /**
     * The name of the traittree being created
     */
    public String name;
    /**
     * The root of the tree that we are going to put into the database
     */
    private TraitTreeVal root;
    /**
     * The id of the traitset that this traittree will belong to. 
     */
    private int tsid;
    /**
     * The form that we call repaint on to update the status visualization
     */
    private JFrame form;
    /**
     * the id of the project that this traitset belongs to
     */
    private int projid;

    /**
     * Creates a new runnable traittree item
     * @param form the form to call repain on to update
     * @param name the name of the new tree
     * @param root the root of the new tree
     * @param tsid the traitset this tree belongs to.
     */
    public TraitTreeItem(JFrame form, String name, TraitTreeVal root, int tsid, int projid)
    {
        this.form = form;
        this.name = name;
        this.root = root;
        this.tsid = tsid;
        this.projid = projid;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getErrorText()
    {
        return errorText;
    }

    @Override
    public String getStatus()
    {
        return status;
    }

    @Override
    public String getSuccessMessage()
    {
        return "Tree uploaded successfully: " + name;
    }

    @Override
    public void start()
    {
        Task t = new Task();
        t.start();
    }

    class Task extends Thread
    {

        protected boolean isError;

        @Override
        public void run()
        {
            status = "Loading tree into database ...";
            setValue(5);
            form.repaint();
            
            String traittreeid; //to store the result of the controller operations if needed
            Queue<TraitTreeVal> q = new LinkedList<TraitTreeVal>();
            /*
             * Knowing that my traitsetid is 47, I would call:
             *  select createTree(47, 'delete'), which returns 3 as the traittreeid.
             * run a select query with "createTree(TRAITSETID, TRAITTREENAME)" as the thing
             * to select.
             * ???
             */

            ArrayList<String> args = new ArrayList<String>();
            args.add(Integer.toString(tsid));
            args.add(name);
            traittreeid = DataManager.runFunction("createTree", args);
            root.setLevel(1);

            /*
             * Now, I can insert the root node:
             *  select insertIntoTree(3, 0, 1), which returns 2 as the traittreevalid.
             * run a select query with "insertIntoTree(TRAITTREEID, LEVEL, 1)" in order to
             * insert the root node and get the id of the new node.
             * ???
             */
            args.clear();
            args.add(traittreeid);
            args.add(Integer.toString(root.getLevel()));
            args.add("1");//because it's the root's parent's id

            String traittreeevalid = DataManager.runFunction("insertIntoTree", args);
            root.setId(Integer.parseInt(traittreeevalid));
            //level order traversal done to:
            //1. set levels
            //2. add nodes to DB
            q.offer(root);
            while (!q.isEmpty())
            {
                TraitTreeVal currentTTE = q.poll();
                if (currentTTE.getChildren() != null)
                {
                    for (TraitTreeVal child : currentTTE.getChildren())
                    {
                        int childLevel = currentTTE.getLevel() + 1;
                        child.setLevel(childLevel);
                        //the parent may not be set in the case that the tree was loaded from the sibling format
                        //however, the parent will obviously have a reference to this child
                        if (child.getParent() == null)
                        {
                            child.setParent(currentTTE);
                        }



                        q.offer(child);

                        if (child.getChildren() != null)
                        {
                            args.clear();
                            args.add(traittreeid);
                            args.add(Integer.toString(childLevel));
                            //parent id
                            args.add(currentTTE.getId() + "");
                            traittreeevalid = DataManager.runFunction("insertIntoTree", args);
                            child.setId(Integer.parseInt(traittreeevalid));
                        }
                    }//end of adding children to queue (for loop)

                }//end of if block
                else if (currentTTE.getChildren() == null)
                {
                    /*
                     *  select insertTraitIntoTree(3, 1, 2, 'YKL163W'); to insert the leaf node ...
                     * you can ignore the return value except for error checking ...
                     * run a select query with "insertTraitIntoTree(TRAITTREEID, LEVEL, PARENTID,
                     * TRAITNAME)" to insert a leaf node.
                     */
                    args.clear();
                    args.add(traittreeid);
                    args.add(Integer.toString(currentTTE.getLevel()));
                    //parent id
                    args.add(currentTTE.getParent().getId() + "");
                    args.add(currentTTE.getName());

                    //because sometimes SQLcontroller.runfunction will return an empty string
                    int tempId = Integer.parseInt(traittreeevalid);
                    traittreeevalid = DataManager.runFunction("insertTraitIntoTree", args);

                    if (traittreeevalid.length() == 0)
                    {
                        tempId++;
                        traittreeevalid = Integer.toString(tempId);
                    }

                    if (!currentTTE.equals(root))
                    {
                        currentTTE.setId(Integer.parseInt(traittreeevalid));
                    }
                }
            }//end of tree traversal and db input (while loop)

            q.clear();
            q.offer(root);
            while (!q.isEmpty())
            {
                TraitTreeVal currentTTE = q.poll();
                if (currentTTE.getChildren() != null)
                {
                    for (TraitTreeVal child : currentTTE.getChildren())
                    {

                        q.offer(child);

                    }//end of adding children to queue (for loop)
                }//end of if block
            }

            args.clear();
            args.add("name=\'" + name + "\'");
            args.add("tsid=" + tsid);
            DataManager.runUpdateQuery("traittree", "loadcmpt", "1", args);
            Model.getInstance().getProject(projid).getTrait(tsid).addTree(
                    new TraitTree(Integer.parseInt(traittreeid),
                    name, Model.getInstance().getProject(projid).getTrait(tsid), false));
            DataAddRemoveHandler.getInstance().refreshDisplay();
            setValue(100);
        }
    }
}
