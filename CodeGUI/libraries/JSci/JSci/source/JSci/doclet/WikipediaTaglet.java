package JSci.doclet;

import java.util.Map;

public class WikipediaTaglet extends WikiTaglet {
	public static void register(Map taglets) {
		register(taglets, new WikipediaTaglet());
	}

	public WikipediaTaglet() {
		super("jsci.wikipedia", "Wikipedia");
	}
}
