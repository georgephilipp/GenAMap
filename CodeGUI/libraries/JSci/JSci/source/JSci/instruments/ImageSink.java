package JSci.instruments;

/** An object that reads frames */

public interface ImageSink extends Control {

    /** set the object that sends the <code>Image</code>s.
     * @param fs the object that sends the frames
     */
    void setSource(ImageSource fs);

    /** This method is called by the ImageSource to send a Image 
     * @param f the new frame that must be displayed
     */
    void receive(Image f);
}
