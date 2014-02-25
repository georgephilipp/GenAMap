package JSci.instruments;

/** An object that delivers frames (typically, a framegrabber) */

public interface ImageSource extends Control,Dimensions {

    /** set the object that must read the frames through receive() method.
     * @param fs the object that reads the frames
     */
    void setSink(ImageSink fs);
    
}
