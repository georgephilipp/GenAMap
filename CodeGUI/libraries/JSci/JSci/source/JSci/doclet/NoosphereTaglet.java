package JSci.doclet;

import com.sun.tools.doclets.*;
import com.sun.javadoc.*;
import java.util.Map;

public class NoosphereTaglet implements Taglet {
        private final String name;
	private final String desc;

	protected static void register(Map taglets, Taglet taglet) {
		String name = taglet.getName();
                Taglet old = (Taglet) taglets.get(name);
                if(old != null)
                        taglets.remove(name);
                taglets.put(name, taglet);
	}

	public NoosphereTaglet(String name, String desc) {
		this.name = name;
		this.desc = desc;
	}
        public String getName() {
                return name;
        }
        public boolean inConstructor() {
                return true;
        }
        public boolean inField() {
                return true;
        }
        public boolean inMethod() {
                return true;
        }
        public boolean inOverview() {
                return true;
        }
        public boolean inPackage() {
                return true;
        }
        public boolean inType() {
                return true;
        }
        public boolean isInlineTag() {
                return false;
        }
        public String toString(Tag tag) {
                return "<dt><b>"+desc+" references:</b></dt><dd>"+createPMlink(tag.text())+"</dd>";
        }
        public String toString(Tag[] tags) {
                if(tags.length == 0)
                        return null;

                StringBuffer buf = new StringBuffer("<dt><b>"+desc+" references:</b></dt><dd>");
                for(int i=0; i<tags.length; i++) {
                        if(i > 0)
                                buf.append(", ");
                        buf.append(createPMlink(tags[i].text()));
                }
                return buf.append("</dd>").toString();
        }
        private String createPMlink(String cname) {
		if(cname.indexOf(' ') != -1)
			throw new IllegalArgumentException("Invalid canonical name: "+cname);
		String host = desc+".org";
                return "<a href=\"http://"+host+"/?op=getobj&from=objects&name="+cname+"\">"+cname+"</a>";
        }
}
