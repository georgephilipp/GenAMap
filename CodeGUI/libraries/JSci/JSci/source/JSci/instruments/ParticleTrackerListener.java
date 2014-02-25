package JSci.instruments;

/** Interface for an object that is interested to receive the coordinates
    measured by a ParticleTracker. The object can be registered to a
    ParticleTracker.*/
public interface ParticleTrackerListener {
    /** This method is called by the ParticleTracker whenever it measures
	the coordinates of the objects in the image.
	@param time the timestamp of the image
	@param n the number of the object (can be different from the index)
	@param x the x coordinates of the templates
	@param y the y coordinates of the templates
	@param z the z coordinates of the templates; can be null if no z measure is made
     */
    void receivePosition(long time,int[] n,double[] x,double[] y,double[] z);
}
