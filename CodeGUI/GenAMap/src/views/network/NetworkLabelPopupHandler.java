package views.network;

import datamodel.Trait;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractGraphMousePlugin;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import java.io.*;
import java.net.*;
import javax.swing.JPanel;
import javax.swing.JTable;
import realdata.BareBonesBrowserLaunch;

/**
 * This class interfaces with the labels of the traits and the outside world
 * of database links. When a user clicks, we have to determine if they have
 * clicked on a label, and then query UniProt for information. We then direct
 * them to that information. 
 * @author mzuromskis
 */
public class NetworkLabelPopupHandler extends AbstractGraphMousePlugin implements MouseListener
{
    /**
     * The popup menu that is used to link the user to outside database
     * information.
     */
    private JPopupMenu popup;
    /**
     * The urls that are linked to in the popup menu
     */
    private ArrayList<String> urls;
    /**
     * The names of the genes that are found from querying uniprot
     */
    private ArrayList<String> names;
    /**
     * The names of the species that are found from querying uniprot
     */
    private ArrayList<String> species;
    /**
     * TODO
     */
    private int curNum;

    /**
     *
     * constructor
     */
    public NetworkLabelPopupHandler()
    {
        super(0);
        popup = new JPopupMenu();
        urls = new ArrayList<String>();
        names = new ArrayList<String>();
        species = new ArrayList<String>();
        curNum = 0;
    }

    /**
     * performs action based on triggering action.
     * Take the user out to the external database!
     * @param e
     */
    @SuppressWarnings(
    {
        "unchecked", "serial", "serial"
    })
    public boolean handlePopup(MouseEvent e, final String name, JPanel jp)
    {
        boolean ret = false;
        ret = true;
        //A label has been clicked.  Start web reader

        popup.removeAll();

        try
        {
            final URL url = new URL("http://www.uniprot.org/uniprot/?query=" + name + "&sort=score");
            jp.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));    
            URLConnection uc = url.openConnection();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                    uc.getInputStream()));


            String inputLine = "";
            String line;
            while ((line = in.readLine()) != null)
            {
                inputLine = inputLine + line;

            }
            in.close();
            final String masterLine = inputLine;

            while (inputLine.contains("uniprot/"))
            {
                int index = inputLine.indexOf("uniprot/");
                String sub = inputLine.substring(index + 8, index + 9);
                if (!(sub.equals("?") || sub.equals("\"") || inputLine.substring(index + 8, index + 11).equals("by/") ))
                {
                    urls.add(("http://www.uniprot.org/" + inputLine.substring(index, index + 14) + ".txt"));
                }
                inputLine = inputLine.substring(index + 20);
            }

            inputLine = masterLine;
            int index;

            while (inputLine.contains("\"short\"><strong>"))
            {
                index = inputLine.indexOf("\"short\"><strong>");
                inputLine = inputLine.substring(index + 15);
                index = inputLine.indexOf("<");
                names.add(inputLine.substring(1, index));
            }
            
            for(int i=urls.size();i<names.size();i++)
                urls.add("http://www.uniprot.org/uniprot/?query=" + name + "&sort=score");

            String inputLine3 = masterLine;
            index = 0;

            while (inputLine3.contains("<br /></td><td style=\"\">"))
            {
                index = inputLine3.indexOf("<br /></td><td style=\"\">");
                inputLine3 = inputLine3.substring(index + 24);
                index = inputLine3.indexOf("<");
                species.add(inputLine3.substring(0, index));
            }

            in.close();
            jp.setCursor(Cursor.getDefaultCursor());
        }
        catch (Exception ex)
        {
            System.out.println(ex.toString());
        }

        popup.add(new AbstractAction("Google Search")
        {
            public void actionPerformed(ActionEvent e)
            {
                BareBonesBrowserLaunch.openURL("http://google.com/search?q=" + name);
            }
        });

        if (0 < names.size())// && num < species.size())
        {
            popup.add(new AbstractAction(names.get(0))// + ", " + species.get(num))
            {
                public void actionPerformed(ActionEvent e)
                {
                    String toOpen = urls.get(0).replace(".txt", ".html");
                    System.out.println("opening URL " + toOpen);
                    BareBonesBrowserLaunch.openURL(toOpen);
                    //String function = getFunctionAndGOData(curNum);

                    //TraitInfoFrame tif = new TraitInfoFrame(null, function, names.get(curNum));
                    //tif.show();
                }
            });
        }
        
        if (1 < names.size())// && num < species.size())
        {
            popup.add(new AbstractAction(names.get(1))// + ", " + species.get(num))
            {
                public void actionPerformed(ActionEvent e)
                {
                    String toOpen = urls.get(1).replace(".txt", ".html");
                    System.out.println("opening URL " + toOpen);
                    BareBonesBrowserLaunch.openURL(toOpen);
                }
            });
        }
        
        if (2 < names.size())// && num < species.size())
        {
            popup.add(new AbstractAction(names.get(2))// + ", " + species.get(num))
            {
                public void actionPerformed(ActionEvent e)
                {
                    String toOpen = urls.get(2).replace(".txt", ".html");
                    System.out.println("opening URL " + toOpen);
                    BareBonesBrowserLaunch.openURL(toOpen);
                }
            });
        }
        
        if (3 < names.size())// && num < species.size())
        {
            popup.add(new AbstractAction(names.get(3))// + ", " + species.get(num))
            {
                public void actionPerformed(ActionEvent e)
                {
                    String toOpen = urls.get(3).replace(".txt", ".html");
                    System.out.println("opening URL " + toOpen);
                    BareBonesBrowserLaunch.openURL(toOpen);
                }
            });
        }
        
        if (4 < names.size())// && num < species.size())
        {
            popup.add(new AbstractAction(names.get(4))// + ", " + species.get(num))
            {
                public void actionPerformed(ActionEvent e)
                {
                    String toOpen = urls.get(4).replace(".txt", ".html");
                    System.out.println("opening URL " + toOpen);
                    BareBonesBrowserLaunch.openURL(toOpen);
                }
            });
        }
        
        if (5 < names.size())// && num < species.size())
        {
            popup.add(new AbstractAction(names.get(5))// + ", " + species.get(num))
            {
                public void actionPerformed(ActionEvent e)
                {
                    String toOpen = urls.get(5).replace(".txt", ".html");
                    System.out.println("opening URL " + toOpen);
                    BareBonesBrowserLaunch.openURL(toOpen);
                }
            });
        }
        
        if (6 < names.size())// && num < species.size())
        {
            popup.add(new AbstractAction(names.get(6))// + ", " + species.get(num))
            {
                public void actionPerformed(ActionEvent e)
                {
                    String toOpen = urls.get(6).replace(".txt", ".html");
                    System.out.println("opening URL " + toOpen);
                    BareBonesBrowserLaunch.openURL(toOpen);
                }
            });
        }
        
        if (7 < names.size())// && num < species.size())
        {
            popup.add(new AbstractAction(names.get(7))// + ", " + species.get(num))
            {
                public void actionPerformed(ActionEvent e)
                {
                    String toOpen = urls.get(7).replace(".txt", ".html");
                    System.out.println("opening URL " + toOpen);
                    BareBonesBrowserLaunch.openURL(toOpen);
                }
            });
        }
        
        if (8 < names.size())// && num < species.size())
        {
            popup.add(new AbstractAction(names.get(8))// + ", " + species.get(num))
            {
                public void actionPerformed(ActionEvent e)
                {
                    String toOpen = urls.get(8).replace(".txt", ".html");
                    System.out.println("opening URL " + toOpen);
                    BareBonesBrowserLaunch.openURL(toOpen);
                }
            });
        }
        
        if (9 < names.size())// && num < species.size())
        {
            popup.add(new AbstractAction(names.get(9))// + ", " + species.get(num))
            {
                public void actionPerformed(ActionEvent e)
                {
                    String toOpen = urls.get(9).replace(".txt", ".html");
                    System.out.println("opening URL " + toOpen);
                    BareBonesBrowserLaunch.openURL(toOpen);
                }
            });
        }

        if (names.size() == 0)
        {
            popup.add(new AbstractAction("No results found for this query in UniProt")
            {
                public void actionPerformed(ActionEvent e)
                {
                }
            });
        }

        final VisualizationViewer<Trait, Number> vv =
                (VisualizationViewer<Trait, Number>) e.getSource();
        if (popup.getComponentCount() > 0)
        {
            popup.show((Component) vv, e.getX(), e.getY());
        }

        //end label click action
        return ret;
    }

    public void mouseClicked(MouseEvent me)
    {
        if (me == null)
        {
            throw new NullPointerException();
        }

    }

    /**
     *
     * forwards press action to handlePopup
     * @param v
     * @param me
     */
    public void mousePressed(MouseEvent me)
    {
        if (me == null)
        {
            throw new NullPointerException();
        }
        //   handlePopup(me);
    }

    public void mouseExited(MouseEvent me)
    {
    }

    public void mouseEntered(MouseEvent me)
    {
    }

    /**
     *
     * forwards release action to handlePopup
     * @param v
     * @param me
     */
    public void mouseReleased(MouseEvent me)
    {
        if (me == null)
        {
            throw new NullPointerException();
        }
    }
}
