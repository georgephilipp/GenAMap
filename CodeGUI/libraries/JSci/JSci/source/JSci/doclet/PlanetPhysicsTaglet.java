package JSci.doclet;

import java.util.Map;

public class PlanetPhysicsTaglet extends NoosphereTaglet {
	public static void register(Map taglets) {
		register(taglets, new PlanetPhysicsTaglet());
	}

	public PlanetPhysicsTaglet() {
		super("jsci.planetphysics", "PlanetPhysics");
	}
}
