package JSci.instruments;

/** An interface for objects that can look for the position of some objects
 in an image, and measure their coordinates */
public interface ParticleTracker extends ImageFilter {
    /** Sets the object that wants to receive the data collected
	by the particle tracker.
     @param ptl the object that receives the data; each time a new 
     image is processed, ptl.receivePosition() is called.
    */
    void setListener(ParticleTrackerListener ptl);
}
