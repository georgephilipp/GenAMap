package JSci.maths;

/**
* The Fourier math library.
* This class cannot be subclassed or instantiated because all methods are static.
* Use <code>sort(transform(sort(...)))</code> for the discrete analogue of the continuous Fourier transform,
* and <code>sort(inverseTransform(sort(...)))</code> for the inverse transform.
* @jsci.planetmath FourierTransform
* @version 0.9
* @author Mark Hale
*/
public final class FourierMath extends AbstractMath implements NumericalConstants {
        private FourierMath() {}

        /**
        * Fourier transform (2Pi convention).
        * @param data an array containing the positive time part of the signal
        * followed by the negative time part.
        * @return an array containing positive frequencies in ascending order
        * followed by negative frequencies in ascending order.
        * @author Don Cross
        */
        public static Complex[] transform(final Complex data[]) {
                final int N=data.length;
                if(!isPowerOf2(N))
                        throw new IllegalArgumentException("The number of samples must be a power of 2.");

                final double arrayRe[]=new double[N];
                final double arrayIm[]=new double[N];

                final int numBits=numberOfBitsNeeded(N);
// Simultaneous data copy and bit-reversal ordering into output
                for(int i=0;i<N;i++) {
                        final int j=reverseBits(i,numBits);
                        arrayRe[j]=data[i].real();
                        arrayIm[j]=data[i].imag();
                }
// FFT
                fft(arrayRe, arrayIm, TWO_PI);

                final Complex answer[]=new Complex[N];
                for(int i=0;i<N;i++)
                        answer[i]=new Complex(arrayRe[i],arrayIm[i]);
                return answer;
        }
        /**
        * Fourier transform (2Pi convention).
        * @param dataReal an array containing the positive real time part of the signal
        * followed by the negative real time part.
        * @param dataImag an array containing the positive imaginary time part of the signal
        * followed by the negative imaginary time part.
        * @return an array containing positive frequencies in ascending order
        * followed by negative frequencies in ascending order.
        * @author Don Cross
        */
        public static Complex[] transform(final double dataReal[], final double dataImag[]) {
                final int N=dataReal.length;
                if(!isPowerOf2(N))
                        throw new IllegalArgumentException("The number of samples must be a power of 2.");

                final double arrayRe[]=new double[N];
                final double arrayIm[]=new double[N];

                final int numBits=numberOfBitsNeeded(N);
// Simultaneous data copy and bit-reversal ordering into output
                for(int i=0;i<N;i++) {
                        final int j=reverseBits(i,numBits);
                        arrayRe[j]=dataReal[i];
                        arrayIm[j]=dataImag[i];
                }
// FFT
                fft(arrayRe, arrayIm, TWO_PI);

                final Complex answer[]=new Complex[N];
                for(int i=0;i<N;i++)
                        answer[i]=new Complex(arrayRe[i],arrayIm[i]);
                return answer;
        }
        /**
        * Fourier transform (2Pi convention).
        * @param data an array containing the positive time part of the signal
        * followed by the negative time part.
        * @return an array containing positive frequencies in ascending order
        * followed by negative frequencies in ascending order.
        */
        public static Complex[] transform(final double data[]) {
                final int N=data.length;
                if(!isPowerOf2(N))
                        throw new IllegalArgumentException("The number of samples must be a power of 2.");

                final double arrayRe[]=new double[N];
                final double arrayIm[]=new double[N];

                final int numBits=numberOfBitsNeeded(N);
// Simultaneous data copy and bit-reversal ordering into output
                for(int i=0;i<N;i++) {
                        final int j=reverseBits(i,numBits);
                        arrayRe[j]=data[i];
                }
// FFT
                fft(arrayRe, arrayIm, TWO_PI);

                final Complex answer[]=new Complex[N];
                for(int i=0;i<N;i++)
                        answer[i]=new Complex(arrayRe[i],arrayIm[i]);
                return answer;
        }
        /**
        * Inverse Fourier transform (-2Pi convention).
        * @param data an array containing positive frequencies in ascending order
        * followed by negative frequencies in ascending order.
        * @return an array containing the positive time part of the signal
        * followed by the negative time part.
        * @author Don Cross
        */
        public static Complex[] inverseTransform(final Complex data[]) {
                final int N=data.length;
                if(!isPowerOf2(N))
                        throw new IllegalArgumentException("Data length must be a power of 2.");

                final double arrayRe[]=new double[N];
                final double arrayIm[]=new double[N];

                final int numBits=numberOfBitsNeeded(N);
// Simultaneous data copy and bit-reversal ordering into output
                for(int i=0;i<N;i++) {
                        final int j=reverseBits(i,numBits);
                        arrayRe[j]=data[i].real();
                        arrayIm[j]=data[i].imag();
                }
// inverse FFT
                fft(arrayRe, arrayIm, -TWO_PI);
// Normalize
                final Complex answer[]=new Complex[N];
                final double denom=N;
                for(int i=0;i<N;i++)
                        answer[i]=new Complex(arrayRe[i]/denom,arrayIm[i]/denom);
                return answer;
        }
        /**
        * Inverse Fourier transform (-2Pi convention).
        * @param dataReal an array containing positive real frequencies in ascending order
        * followed by negative real frequencies in ascending order.
        * @param dataImag an array containing positive imaginary frequencies in ascending order
        * followed by negative imaginary frequencies in ascending order.
        * @return an array containing the positive time part of the signal
        * followed by the negative time part.
        * @author Don Cross
        */
        public static Complex[] inverseTransform(final double dataReal[], final double dataImag[]) {
                final int N=dataReal.length;
                if(!isPowerOf2(N))
                        throw new IllegalArgumentException("Data length must be a power of 2.");

                final double arrayRe[]=new double[N];
                final double arrayIm[]=new double[N];

                final int numBits=numberOfBitsNeeded(N);
// Simultaneous data copy and bit-reversal ordering into output
                for(int i=0;i<N;i++) {
                        final int j=reverseBits(i,numBits);
                        arrayRe[j]=dataReal[i];
                        arrayIm[j]=dataImag[i];
                }
// inverse FFT
                fft(arrayRe, arrayIm, -TWO_PI);
// Normalize
                final Complex answer[]=new Complex[N];
                final double denom=N;
                for(int i=0;i<N;i++)
                        answer[i]=new Complex(arrayRe[i]/denom,arrayIm[i]/denom);
                return answer;
        }
        /**
        * Inverse Fourier transform (-2Pi convention).
        * @param data an array containing positive frequencies in ascending order
        * followed by negative frequencies in ascending order.
        * @return an array containing the positive time part of the signal
        * followed by the negative time part.
        */
        public static Complex[] inverseTransform(final double data[]) {
                final int N=data.length;
                if(!isPowerOf2(N))
                        throw new IllegalArgumentException("Data length must be a power of 2.");

                final double arrayRe[]=new double[N];
                final double arrayIm[]=new double[N];

                final int numBits=numberOfBitsNeeded(N);
// Simultaneous data copy and bit-reversal ordering into output
                for(int i=0;i<N;i++) {
                        final int j=reverseBits(i,numBits);
                        arrayRe[j]=data[i];
                }
// inverse FFT
                fft(arrayRe, arrayIm, -TWO_PI);
// Normalize
                final Complex answer[]=new Complex[N];
                final double denom=N;
                for(int i=0;i<N;i++)
                        answer[i]=new Complex(arrayRe[i]/denom,arrayIm[i]/denom);
                return answer;
        }
        /**
        * Common FFT code.
        * @param twoPi TWO_PI for transform, -TWO_PI for inverse transform.
        */
        private static void fft(double arrayRe[], double arrayIm[], final double twoPi) {
                final int N=arrayRe.length;
                int blockEnd=1;
                for(int blockSize=2; blockSize<=N; blockSize<<=1) {
                        final double deltaAngle = twoPi/blockSize;
                        double alpha = Math.sin(0.5*deltaAngle);
                        alpha = 2.0*alpha*alpha;
                        final double beta = Math.sin(deltaAngle);
                        for(int i=0; i<N; i+=blockSize) {
                                double angleRe=1.0;
                                double angleIm=0.0;
                                for(int j=i,n=0; n<blockEnd; j++,n++) {
                                        final int k = j+blockEnd;
                                        // tmp = angle*array[k]
                                        double tmpRe = angleRe*arrayRe[k]-angleIm*arrayIm[k];
                                        double tmpIm = angleRe*arrayIm[k]+angleIm*arrayRe[k];
                                        arrayRe[k] = arrayRe[j]-tmpRe;
                                        arrayIm[k] = arrayIm[j]-tmpIm;
                                        arrayRe[j] += tmpRe;
                                        arrayIm[j] += tmpIm;
                                        // angle = angle - (a-bi)*angle
                                        tmpRe = alpha*angleRe + beta*angleIm;
                                        tmpIm = alpha*angleIm - beta*angleRe;
					angleRe -= tmpRe;
					angleIm -= tmpIm;
                                }
                        }
                        blockEnd=blockSize;
                }
        }
        /**
        * Returns true if x is a power of 2.
        * @author Don Cross
        */
        private static boolean isPowerOf2(final int x) {
                final int BITS_PER_WORD=32;
                for(int i=1,y=2; i<BITS_PER_WORD; i++,y<<=1) {
                        if(x==y)
                                return true;
                }
                return false;
        }
        /**
        * Number of bits needed.
        * @author Don Cross
        */
        private static int numberOfBitsNeeded(final int pwrOf2) {
                if(pwrOf2<2)
                        throw new IllegalArgumentException();
                for(int i=0;;i++) {
                        if((pwrOf2&(1<<i))>0)
                                return i;
                }
        }
        /**
        * Reverse bits.
        * @author Don Cross
        */
        private static int reverseBits(int index,final int numBits) {
                int i,rev;
                for(i=rev=0;i<numBits;i++) {
                        rev=(rev<<1)|(index&1);
                        index>>=1;
                }
                return rev;
        }

        /**
        * Sorts the output from the Fourier transfom methods into
        * ascending frequency/time order.
        */
        public static Complex[] sort(final Complex output[]) {
                final Complex ret[]=new Complex[output.length];
                final int Nby2=output.length/2;
                for(int i=0;i<Nby2;i++) {
                        ret[Nby2+i]=output[i];
                        ret[i]=output[Nby2+i];
                }
                return ret;
        }
        public static double[] sort(final double input[]) {
                final double ret[]=new double[input.length];
                final int Nby2=input.length/2;
                for(int i=0;i<Nby2;i++) {
                        ret[Nby2+i]=input[i];
                        ret[i]=input[Nby2+i];
                }
                return ret;
        }
}
