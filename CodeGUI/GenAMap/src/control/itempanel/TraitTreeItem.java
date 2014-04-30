package control.itempanel;

import control.DataAddRemoveHandler;
import control.TableParser;
import datamodel.Model;
import datamodel.TraitTree;
import datamodel.TraitTreeVal;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import realdata.DataManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.util.HashSet;

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
     * The file name from which we will load
     */
    private String fileName;
    /**
     * The id of the traitset that this traittree will belong to. 
     */
    private int tsid;
    /**
     * The format of the file specified
     */
    private boolean isSibling;
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
    public TraitTreeItem(JFrame form, String name, String fileName, int tsid, int projid, boolean isSibling)
    {
        this.form = form;
        this.name = name;
        this.fileName = fileName;
        this.tsid = tsid;
        this.projid = projid;
        this.isSibling = isSibling;
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
    
    /**
    * Called from reading the siblings format. Puts the tree together. 
    * @param s
    * @param parent
    * @param from
    * @param to
    * @return
    */
    private TraitTreeVal buildTree(String s, TraitTreeVal parent, int from, int to)
    {
        if (s.charAt(from) != '(')
        {
            parent.setName(s.substring(from, to));
            return parent;
        }

        int b = 0;//bracket counter
        int x = from; //position marker

        for (int i = from; i < to; i++)
        {
            char c = s.charAt(i);

            if (c == '(')
            {
                b++;
            }
            else if (c == ')')
            {
                b--;
            }

            if (b == 0 || (b == 1 && c == ','))
            {
                String name = Integer.toString(x + 1) + "to" + i;
                TraitTreeVal child = new TraitTreeVal(name);
                child.setParent(parent);
                parent.addChild(buildTree(s, child , x + 1, i));
                x = i;
            }
        }

        return parent;
    }

    private String parseSiblingFile(ArrayList<TraitTreeVal> rootDummyList)
    {
        String line = "";
        String lineCache;
        int lineCounter = 0;
        TraitTreeVal root = null;

        try
        {
            FileInputStream fstream = new FileInputStream(fileName);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            while ((lineCache = br.readLine()) != null)
            {
                if(!lineCache.equals(""))
                {
                    line = lineCache;
                    lineCounter++;
                }
            }
        }
        catch(Exception e)
        {
            return e.getMessage();
        }

        if(lineCounter > 1)
            return "File contains more than 1 line. It should only contain one line.";

        if(lineCounter < 1)
            return "Supplied file is empty";

        //check and clean the line
        StringBuilder builder = new StringBuilder();
        int numOpen = 0;
        int numClose = 0;
        String entry = "";
        boolean wasClosed = false;
        ArrayList<String> entries = new ArrayList();

        for(int i=0;i<line.length();i++)
        {
            char c = line.charAt(i);
            if(c == ' ')
                continue;
            builder.append(c);
            if(c == '(')
            {
                if(!entry.equals("") || wasClosed)
                    return "Formatting problem detected at position " + i + ".\nOpening brace should be preceded by another opening brace or a comma.";
                numOpen++;
            }
            else if (c == ')')
            {
                if(!wasClosed)
                {
                    if(entry.equals(""))
                        return "Formatting problem detected at position " + i + ".\nClosing brace should not follow an opening brace or comma.";
                    entries.add(entry);
                    entry = "";
                    wasClosed = true;
                } 
                numClose++;
            }
            else if (c == ',')
            {
                if(!wasClosed)
                {
                    if(entry.equals(""))
                        return "Formatting problem detected at position " + i + ".\nComma should not follow an opening brace or another comma.";
                    entries.add(entry);
                    entry = "";
                }
                else
                    wasClosed = false;
            }
            else
                entry = entry + c;
            if(i+1==line.length())
            {
                if(numOpen != numClose)
                    return "The number of opening and closing braces is not balanced.\nThere are " + numOpen + " opening braces and " + numClose + "closing braces";
            }
            else
            {
                if(numOpen <= numClose)
                    return "The first opening brace was closed at position " + i;
            }
        }

        //check the entries are unique
        HashMap<String, Integer> entryMap = new HashMap();
        for(int i=0;i<entries.size();i++)
        {
            if(entryMap.containsKey(entries.get(i)))
                return "Found duplicate traits. The " + i + "'th trait and the " + entryMap.get(entries.get(i)) + "'th trait are equal.";
            entryMap.put(entries.get(i), i);
        }
        entries = null;

        //check that all traits are entries         
        ArrayList<String> whereArgs = new ArrayList();
        whereArgs.add("traitsetid=" + tsid);
        ArrayList<String> traitlist = DataManager.runSelectQuery("name", "trait", true, whereArgs, null);
        for(int i=0;i<traitlist.size();i++)
        {
            if(!entryMap.containsKey(traitlist.get(i)))
                return "trait " + traitlist.get(i) + " is not contained in the file";
        }

        //check that all entries are traits
        if(entryMap.size() != traitlist.size())
            return "some of the entries in the file are not valid traits";

        //build the actual tree
        line = line.substring(0, line.length() - 1) + ",)";
        TraitTreeVal rootNode = buildTree(line, new TraitTreeVal("root"), 0, line.length()-1);
        rootNode.setParent(new TraitTreeVal("rootparent"));
        rootDummyList.add(rootNode);
        return "";       
    }

    private int getTotalNodes(HashMap<String, ArrayList<String> > parentChildMap, String node)         
    {
        if(parentChildMap.containsKey(node))
        {
            int total = 0;
            ArrayList<String> children = parentChildMap.get(node);
            for(int i=0;i<children.size();i++)
                total += getTotalNodes(parentChildMap, children.get(i));
            total++;
            return total;
        }
        else
            return 1;            
    }                

    private String parseTabDelimFile(ArrayList<TraitTreeVal> rootDummyList)
    {
        HashMap<String, String> childParentMap = new HashMap();
        HashMap<String, ArrayList<String> > parentChildMap = new HashMap();

        ArrayList<String> whereArgs = new ArrayList();
        whereArgs.add("traitsetid=" + tsid);
        ArrayList<String> traitlist = DataManager.runSelectQuery("name", "trait", true, whereArgs, null);
        int numTraits = traitlist.size();

        TableParser tparser = new TableParser();

        tparser.colTypes.add("String");
        tparser.colTypes.add("String");
        tparser.delimiter = "Tab";
        ArrayList<Integer> nodeKey = new ArrayList();
        nodeKey.add(0);
        tparser.keys.add(nodeKey);
        tparser.setup(fileName);
        ArrayList<String> line;

        while(true)
        {
            line = tparser.readline();
            if(line.size() == 1)
                return line.get(0);
            if(line.get(1) == null)
                break;             
            String parentName = line.get(2);
            String childName = line.get(1);
            childParentMap.put(childName, parentName);
            if(!parentChildMap.containsKey(parentName))
                parentChildMap.put(parentName, new ArrayList());
            parentChildMap.get(parentName).add(childName);     
        }

        tparser.restart();

        //check whether there is root
        if(!parentChildMap.containsKey("root"))
            return "\"root\" never appears in the specified tree.";
        if(childParentMap.containsKey("root"))
            return "\"root\" must not appear as a child in the specified tree.";

        //check whether all traits appear as children and not as parents
        for(int i=0;i<numTraits;i++)
        {
            if(!childParentMap.containsKey(traitlist.get(i)))
                return "Not all traits in the trait set are contains in the tree specified. " + traitlist.get(i) + " is missing.";
            if(parentChildMap.containsKey(traitlist.get(i)))
                return "Trait " + traitlist.get(i) + " appears as a parent in the specified tree.";
        }

        //check whether all traits are reachable from the root
        if(getTotalNodes(parentChildMap, "root") != childParentMap.size()+1)
            return "Not all child nodes are connected to the root in the tree specified.";

        //check that there are no non-trait leaf nodes
        if(parentChildMap.size() + traitlist.size() != childParentMap.size()+1)
            return "Some leafs in the tree aren't traits";

        //build the actual tree 
        HashMap<String, TraitTreeVal> nodeMap = new HashMap(); 
        TraitTreeVal rootNode = new TraitTreeVal("root");
        for(String child : childParentMap.keySet())
        {
            TraitTreeVal node = new TraitTreeVal();
            node.setName(child);
            nodeMap.put(child, node);
        }
        nodeMap.put("root", rootNode);
        for(String child : childParentMap.keySet())
        {
            nodeMap.get(child).setParent(nodeMap.get(childParentMap.get(child)));
        }
        rootNode.setParent(new TraitTreeVal("rootparent"));
        childParentMap = null;
        for(String parent : parentChildMap.keySet())
        {
            for(String child : parentChildMap.get(parent))
            {
                nodeMap.get(parent).addChild(nodeMap.get(child));
            }
        }
        rootDummyList.add(rootNode);
        return "";                
    }


    class Task extends Thread
    {

        protected boolean isError;
                
        @Override
        public void run()
        {
            status = "Checking data file ...";
            setValue(1);
            form.repaint();
            
            HashMap<String, String> childParentMap = new HashMap();
            HashMap<String, ArrayList<String> > parentChildMap = new HashMap();
            
            String check = "";
            ArrayList<TraitTreeVal> rootDummyList = new ArrayList();
            
            if(isSibling)
            {
                check = parseSiblingFile(rootDummyList);
            }
            else
            {
                check = parseTabDelimFile(rootDummyList);
            }
                        
            if(!check.equals(""))
            {
                JOptionPane.showMessageDialog(null, "Error while checking the data file.\n This is likely caused by in incorrectly formatted input file.\n Error message was:\n" + check);
                errorText = "Error while checking data file";
                setIsError(true);
                form.repaint();
                return;
            }
            
            TraitTreeVal root = rootDummyList.get(0);
            
            status = "Loading tree into database ...";
            setValue(5);
            form.repaint();
            
            String traittreeid = null; //to store the result of the controller operations if needed
            
            try
            {
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
            catch(Exception e)
            {
                if(traittreeid != null)
                {
                    ArrayList<String> whereArgs = new ArrayList();
                    whereArgs.add("ttid=" + traittreeid);
                    DataManager.deleteQuery("traittreeval", whereArgs);
                    whereArgs.clear();
                    whereArgs.add("id=" + traittreeid);
                    DataManager.deleteQuery("traittree", whereArgs); 
                }
                
                JOptionPane.showMessageDialog(null, "Error while uploading the data to the database.\n This is a bug. Please contact the developers.\n Error message was:\n" + check);
                errorText = "Error while uploading data";
                setIsError(true);
                form.repaint();
                return;                
            }
        }
    }
}
