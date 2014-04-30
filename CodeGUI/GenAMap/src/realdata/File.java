package realdata;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Algorithms will often need to transfer parameter files and other information
 * over to the cluster in order for the machine to have the knowledge of how to
 * run. In order to accomplish this task, this class is set up in order
 * to transfer file to the remote machine. 
 * @author rcurtis
 */
public class File
{
    /**
     * Takes a list of destination files and goes through each one, sending it through
     * the URL post to the cluster.
     * @param jobname the name of the job that is about to be run on teh cluster
     * @param files the files that need to be transfered over
     * @param destFiles the names of the files once the arrive to the cluster
     * @return the name of the job, along with other information. 
     * @throws Exception
     */
    public static String transferFilesToDistributedProcessor(
            String jobname, ArrayList<String> files, ArrayList<String> destFiles) throws Exception
    {
        String data;
        ArrayList<String> lines = new ArrayList<String>();
        String line = null;
        String res = null;
        try
        {
            line  = "";
            for(int i = 0; i < files.size(); i ++)
            {
                //encode some parms.
                data = URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(Data1.getInstance().mysqlusername, "UTF-8");
                data += "&" + URLEncoder.encode("pwd", "UTF-8") + "=" + URLEncoder.encode(Data1.getInstance().mysqlpassword, "UTF-8");
                data += "&" + URLEncoder.encode("dir", "UTF-8") + "=" + URLEncoder.encode(jobname, "UTF-8");
                data += "&" + URLEncoder.encode("file", "UTF-8")+ "=" + URLEncoder.encode(destFiles.get(i), "UTF-8");

                String s = "";

                FileInputStream fstream = new FileInputStream(files.get(i));
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));

                while((line=br.readLine())!=null)
                {
                    s += line + "\n";
                }
                in.close();

                data += "&" + URLEncoder.encode("text", "UTF-8") + "=" + URLEncoder.encode(s,"UTF-8");

                //now make the connection and write out the data
                URL url = new URL(Data1.getInstance().getTransferWebsiteAddress());
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.close();
                //finally, get the response ...
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                boolean ok = false;
                while ((line = rd.readLine()) != null)
                {
                    line = line.trim();
                    res = line;
                    if(!ok)
                    {
                        if(line.equals("OK"))
                        {
                            ok = true;
                            continue;
                        }
                        else
                        {
                            throw new Exception (line);
                        }
                    }

                }
                rd.close();
            }
        }
        catch (Exception ex)
        {
            return null;
        }
        return res  + jobname;
    }
}
