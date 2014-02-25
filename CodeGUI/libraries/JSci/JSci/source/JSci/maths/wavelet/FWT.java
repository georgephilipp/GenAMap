package JSci.maths.wavelet;

/*****************************
* Abstract class for using very fast, in-place,
* implementations of the Fast Wavelet Transform.
* See for example the FastDaubechies2 or
* FastSymmlet8 classes.
* @author Daniel Lemire
********************************/
public abstract class FWT {
  public abstract void transform(float[] v);
  public abstract void invTransform(float[] v);
}

