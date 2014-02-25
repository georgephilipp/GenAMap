package JSci.doclet;

import java.util.Map;

public class PlanetMathTaglet extends NoosphereTaglet {
	public static void register(Map taglets) {
		register(taglets, new PlanetMathTaglet());
	}

	public PlanetMathTaglet() {
		super("jsci.planetmath", "PlanetMath");
	}
}
