package JSci.maths;

import JSci.GlobalSettings;

/**
* Arrays are faster than object, so this class is here to take full
* advantage of arrays without encapsulation.
* All methods are safe, that is, they create copies of arrays whenever
* necessary
* This makes for slower methods, but the programmer can always
* define his own methods optimized for performance.
* @author Daniel Lemire
* @author Mark Hale
*/
public final class ArrayMath extends AbstractMath {
        private ArrayMath() {}

        public static boolean isSquare(double[][] a) {
                for(int i=0; i<a.length; i++) {
                        if(a[i].length != a.length)
                                return false;
                }
                return true;
        }
        public static boolean isSquare(int[][] a) {
                for(int i=0; i<a.length; i++) {
                        if(a[i].length != a.length)
                                return false;
                }
                return true;
        }
        public static boolean isSquare(Object[][] a) {
                for(int i=0; i<a.length; i++) {
                        if(a[i].length != a.length)
                                return false;
                }
                return true;
        }

        /**
        * Applys a map to every component of an array.
        */
  public static double[] apply(Mapping m, double[] v) {
    double[] ans=new double[v.length];
    for(int k=0;k<v.length;k++) {
      ans[k]=m.map(v[k]);
    }
    return(ans);
  }

        /**
        * Applys a map to every component of an array.
        */
  public static double[][] apply(Mapping m, double[][] v) {
    double[][] ans=new double[v.length][];
    for(int k=0;k<v.length;k++) {
      ans[k]=apply(m,v[k]);
    }
    return(ans);
  }

        /**
        * Applys a map to every component of an array.
        */
  public static Complex[] apply(ComplexMapping m, Complex[] v) {
    Complex[] ans=new Complex[v.length];
    for(int k=0;k<v.length;k++) {
      ans[k]=m.map(v[k]);
    }
    return(ans);
  }

        /**
        * Applys a map to every component of an array.
        */
  public static Complex[][] apply(ComplexMapping m, Complex[][] v) {
    Complex[][] ans=new Complex[v.length][];
    for(int k=0;k<v.length;k++) {
      ans[k]=apply(m,v[k]);
    }
    return(ans);
  }
        /**
        * Renormalizes the array so that its L2 norm is 1
        * (up to computational errors).
        */
  public static double[] normalize(double[] v) {
    return(scalarMultiply(1.0/norm(v),v));
  }

        /**
        * Sets an array to the specified length scraping or
        * padding the beginning if necessary.
        */
        public static double[] setLengthFromEnd(double[] data, int length) {
                double[] ans = new double[length];
                int debut;
                if(length-data.length<0)
      debut = data.length-length;
                else
      debut = 0;
    System.arraycopy(data,debut,ans,-data.length+length+debut,data.length-debut);
                return(ans);
        }

        /**
        * Sets an array to the specified length scraping or
        * padding the end if necessary.
        */
        public static double[] setLengthFromBeginning(double[] data, int length) {
                double[] ans = new double[length];
                int debut;
                if(length-data.length<0)
      debut=data.length-length;
                else
      debut = 0;
    System.arraycopy(data,0,ans,0,data.length-debut);
                return(ans);
        }


        /**
        * Returns a copy of the array.
        */
        public static double[] copy(final double[] v) {
                double[] ans = new double[v.length];
    System.arraycopy(v,0,ans,0,v.length);
                return(ans);
        }
        /**
        * Returns a copy of the array.
        */
        public static double[][] copy(double[][] v) {
                double[][] ans = new double[v.length][];
    for (int k=0;k<v.length;k++)
      ans[k]=copy(v[k]);
                return(ans);
        }


        /**
        * Computes the variance.
        */
        public static double variance(double[] v) {
                final double m = mean(v);
                double ans=0.0;
                for(int i=0;i<v.length;i++)
                        ans+=(v[i]-m)*(v[i]-m);
                return ans/(v.length-1);
        }

        /**
        * Computes the covariance.
        */
        public static double covariance(double[] v1 , double[] v2) {
                if(v1.length!=v2.length)
                        throw new IllegalArgumentException("Arrays must have the same length : "+v1.length+", "+v2.length);
                final double m1 = mean(v1);
                final double m2 = mean(v2);
                double ans=0.0;
                for(int i=0;i<v1.length;i++)
                        ans+=(v1[i]-m1)*(v2[i]-m2);
                return ans/(v1.length-1);
        }

        /**
        * Computes the (linear) correlation between two arrays.
        * Squaring this result and multiply by 100 gives you
        * the percentage of correlation.
        */
  public static double correlation(double[] v1, double[] v2) {
    double denom=Math.sqrt(variance(v1)*variance(v2));
    if(denom!=0)
      return(covariance(v1,v2)/denom);
    else {
      if((variance(v1)==0) &&(variance(v2)==0))
        return(1.0);
      else return(0.0);  // impossible to correlate a null signal with another
    }
  }

        /**
        * Computes the mean.
        */
        public static double mean(double[] v) {
    if(v.length==0)
      throw new IllegalArgumentException("Nothing to compute! The array must have at least one element.");
                return(mass(v)/(double)v.length);
        }

        /**
        * Computes the standard deviation of an array.
        */
        public static double standardDeviation(double[] v) {
                return(Math.sqrt(variance(v)));
        }


        /**
        * Returns a sorted array from the minimum to the maximum value.
        */
        public static double[] sortMinToMax(double[] v) {
    double[] ans=copy(v);
    quickSortMinToMax(ans, 0, ans.length - 1);
    return(ans);
        }

        /**
        * Returns a sorted array from the maximum to the minimum value.
        */
        public static double[] sortMaxToMin(double[] v) {
    double[] ans=copy(v);
    quickSortMaxToMin(ans, 0, ans.length - 1);
    return(ans);
        }

        /**
        * Gives the percentile of an array.
	* @param v an unsorted array
        * @param p percentile, must be between 0 and 1
        * @exception IllegalArgumentException if parameter.
        * p is not between 0 and 1.
        */
        public static double percentile(double[] v, double p) {
		return percentile(v, p, false);
        }
        public static double percentile(double[] v, double p, boolean isSorted) {
                if((p<0) || (p>1)) {
                        throw new IllegalArgumentException("Percentile must be between 0 and 1 : "+p);
                }
                final double[] ans = (isSorted ? v : sortMinToMax(v));
                final int pos = (int) Math.floor(p*(ans.length-1));
                final double dif = p*(ans.length-1) - Math.floor(p*(ans.length-1));
                if(pos == (ans.length-1))
                        return(ans[ans.length-1]);
                else
                        return(ans[pos]*(1.0-dif) + ans[pos+1]*dif);
        }

        /**
        * Computes the median of an array.
        * If the array contains an even number of elements,
        * then the mean of the lower and upper medians is returned.
        */
        public static double median(double[] v) {
                if(v.length == 3) {
                        return median3(copy(v));
                } else if(v.length == 5) {
                        return median5(copy(v));
                } else if(v.length == 7) {
                        return median7(copy(v));
                } else if(v.length % 2 == 1) { // odd
                        return quickSelect(copy(v), false);
                } else { // even
                        double[] tmp = copy(v);
                        double lowerMedian = quickSelect(tmp, false);
                        double upperMedian = quickSelect(tmp, true);
                        return (lowerMedian+upperMedian)/2.0;
                }
        }
        /*
        * http://ndevilla.free.fr/median/median/index.html
        * optmed.c
        * @author N. Devillard
        */
        private static void sortInPlace(final double[] v, final int i, final int j) {
                if(v[i] > v[j]) {
                        final double tmp = v[i];
                        v[i] = v[j];
                        v[j] = tmp;
                }
        }
        private static double median3(double[] v) {
                sortInPlace(v,0,1);
                sortInPlace(v,1,2);
                sortInPlace(v,0,1);
                return v[1];
        }
        private static double median5(double[] v) {
                sortInPlace(v,0,1);
                sortInPlace(v,3,4);
                sortInPlace(v,0,3);
                sortInPlace(v,1,4);
                sortInPlace(v,1,2);
                sortInPlace(v,2,3);
                sortInPlace(v,1,2);
                return v[2];
        }
        private static double median7(double[] v) {
                sortInPlace(v,0,5);
                sortInPlace(v,0,3);
                sortInPlace(v,1,6);
                sortInPlace(v,2,4);
                sortInPlace(v,0,1);
                sortInPlace(v,3,5);
                sortInPlace(v,2,6);
                sortInPlace(v,2,3);
                sortInPlace(v,3,6);
                sortInPlace(v,4,5);
                sortInPlace(v,1,4);
                sortInPlace(v,1,3);
                sortInPlace(v,3,4);
                return v[3];
        }
        /*
        * http://ndevilla.free.fr/median/median/index.html
        * quickselect.c
        * @author N. Devillard
        */
        private static double quickSelect(double[] v, boolean upperMedian) {
                int low=0, high=v.length-1;
                final int median = upperMedian ? high/2+1 : high/2;
                int middle, ll, hh;

                for (;;) {
                        if (high <= low) { /* One element only */
                                return v[median];
                        }

                        if (high == low + 1) {  /* Two elements only */
                                if (v[low] > v[high])
                                        swap(v, low, high);
                                return v[median];
                        }

                /* Find median of low, middle and high items; swap into position low */
                        middle = (low + high) / 2;
                        if (v[middle] > v[high]) swap(v, middle, high);
                        if (v[low] > v[high]) swap(v, low, high);
                        if (v[middle] > v[low]) swap(v, middle, low);

                /* Swap low item (now in position middle) into position (low+1) */
                        swap(v, middle, low+1);

                /* Nibble from each end towards middle, swapping items when stuck */
                        ll = low + 1;
                        hh = high;
                        for (;;) {
                                do ll++; while (v[low] > v[ll]);
                                do hh--; while (v[hh]  > v[low]);

                                if (hh < ll)
                                        break;

                                swap(v, ll, hh);
                        }

                /* Swap middle item (in position low) back into correct position */
                        swap(v, low, hh);

                /* Re-set active partition */
                        if (hh <= median)
                                low = ll;
                        if (hh >= median)
                                high = hh - 1;
                }
        }

        /**
        * Checks if two arrays are equal within a tolerance.
        */
        public static boolean equals(double[] a, double[] b) {
                if(a.length!=b.length)
                        return(false);
                for(int i=0;i<a.length;i++) {
                        if(Math.abs(a[i]-b[i])>GlobalSettings.ZERO_TOL)
                                return(false);
                }
                return(true);
        }

        /**
        * Takes the absolute value of each component of an array.
        */
        public static double[] abs(double[] v) {
                double[] ans=new double[v.length];
                for(int i=0;i<v.length;i++) {
                        ans[i]=Math.abs(v[i]);
                }
                return(ans);
        }

        /**
        * Takes the absolute value of each component of an array.
        */
        public static double[][] abs(double[][] v) {
                double[][] ans=new double[v.length][];
                for(int i=0;i<v.length;i++) {
                        ans[i]=abs(v[i]);
                }
                return(ans);
        }

        /**
        * Returns the maximum of an array.
        */
        public static double max(double[] v) {
                double max=v[0];
                for(int i=1;i<v.length;i++) {
                        if(max<v[i]) {
                                max=v[i];
                        }
                }
                return(max);
        }

        /**
        * Returns the maximum of an array.
        */
        public static double max(double[][] v) {
                double max=max(v[0]);
                for(int i=1;i<v.length;i++) {
                        if(max<max(v[i])) {
                                max=max(v[i]);
                        }
                }
                return(max);
        }

        /**
        * Returns the minimum of an array.
        */
        public static double min(double[] v) {
                double min=v[0];
                for(int i=1;i<v.length;i++) {
                        if(min>v[i]) {
                                min=v[i];
                        }
                }
                return(min);
        }

        /**
        * Returns the minimum of an array.
        */
        public static double min(double[][] v) {
                double min=min(v[0]);
                for(int i=1;i<v.length;i++) {
                        if(min>min(v[i])) {
                                min=min(v[i]);
                        }
                }
                return(min);
        }

        /**
        * Returns the componentwise modulus of
        * an array of Complex numbers.
        */
  public static double[] mod(Complex[] v) {
    double[] ans=new double[v.length];
    for(int k=0;k<v.length;k++) {
      ans[k]=v[k].mod();
    }
    return(ans);
  }

        /**
        * Returns the componentwise modulus of
        * an array of Complex numbers.
        */
  public static double[][] mod(Complex[][] v) {
    double[][] ans=new double[v.length][];
    for(int k=0;k<v.length;k++) {
      ans[k]=mod(v[k]);
    }
    return(ans);
  }

  public static double sumModSqrs(Complex[] v) {
    double ans = 0.0;
    for(int k=0; k<v.length; k++) {
      ans += v[k].modSqr();
    }
    return(ans);
  }
  public static double sumModSqrs(Complex[][] v) {
    double ans = 0.0;
    for(int k=0; k<v.length; k++) {
      ans += sumModSqrs(v[k]);
    }
    return(ans);
  }

        /**
        * Computes the L2 norm of an array (Euclidean norm or "length").
        */
        public static double norm(double[] data) {
                return(Math.sqrt(sumSquares(data)));
        }

        /**
        * Sums the squares of all components;
        * also called the energy of the array.
        */
        public static double sumSquares(double[] data) {
                double ans=0.0;
                for(int k=0;k<data.length;k++) {
                        ans+=data[k]*data[k];
                }
                return(ans);
        }

        /**
        * Sums the squares of all components;
        * also called the energy of the array.
        */
        public static double sumSquares(double[][] data) {
                double ans=0.0;
                for(int k=0;k<data.length;k++) {
      for(int l=0;l<data[k].length;l++) {
                          ans+=data[k][l]*data[k][l];
      }
                }
                return(ans);
        }

        /**
        * Computes the scalar product of two array as if they were vectors.
        * @exception IllegalArgumentException if the don't have the same length
        */
        public static double scalarProduct(double[] w0,double[] w1) {
                if (w0.length!=w1.length) {
                        throw new IllegalArgumentException("Arrays must have the same length : "+w0.length+", "+w1.length);
                }
    if(w0.length==0)
      throw new IllegalArgumentException("Nothing to compute! Arrays must have at least one element.");
                double sortie=0.0;
                for(int k=0;k<w0.length;k++){
                        sortie+=w0[k]*w1[k];
                }
                return(sortie);
        }

        /**
        * Computes the scalar product of two array as if they were matrices.
        * @exception IllegalArgumentException if the don't have the same length
        */
        public static double scalarProduct(double[][] w0,double[][] w1) {
                if (w0.length!=w1.length) {
                        throw new IllegalArgumentException("Arrays must have the same length : "+w0.length+", "+w1.length);
                }
    if(w0.length==0)
      throw new IllegalArgumentException("Nothing to compute! Arrays must have at least one element.");
                double sortie=0.0;
                for(int k=0;k<w0.length;k++){
                        sortie=sortie+scalarProduct(w0[k],w1[k]);
                }
                return(sortie);
        }


        /**
        * Extracts a sub-array (will invert the resulting array if k0 > k1).
        * @param k0 location of the first component
        * @param k1 location of the last component
        */
        public static double[] extract(int k0, int k1, double[] invect) {
                if ((k0<0)||(k1<0)||(k0>invect.length-1)||(k1>invect.length-1)) {
                        throw new IllegalArgumentException("The parameters are incorrect : "+k0+", "+k1+", "+invect.length);
                }
                if(k1>k0) {
                        double[] ans=new double[k1-k0+1];
      System.arraycopy(invect,k0,ans,0,k1-k0+1);
      return(ans);
                }
    double[] ans=new double[-k1+k0+1];
    for(int k=k1;k<=k0;k++) {
      ans[k0-k]=invect[k];
    }
    return(ans);
        }

        /**
        * Inverts an array from left to right.
        */
        public static double [] invert (double[] v) {
                double[] w=new double[v.length];
                for(int k=0;k<v.length;k++) {
                        w[v.length-1-k]=v[k];
                }
                return(w);
        }

        /**
        * Fills in with zero to get to the desired length;
        * original array with be at the specified position.
        * @param n0 length of the new array.
        * @param pos position of the old array.
        */
        public static double[] padding(int n0, int pos, double[] v) {
                if ((v.length+pos>n0)||(pos<0)) {
                        throw new IllegalArgumentException("Array is to long for this : "+n0+", "+pos+", "+v.length);
                }
                double[] w=new double[n0];
    System.arraycopy(v,0,w,pos,v.length);
                return(w);
        }



        /**
        * Adds to an array w, a times v where a is a scalar.
        * Since v can be smaller then w, we must specified the position at which v will be added.
        * @param a scalar.
        * @param p position.
        */
        public static double[] add(double[]w, double a, double[] v,int p) {
    if(v.length>w.length) {
      throw new IllegalArgumentException("Second array must be shorter or equal to the first one : "+w.length+", "+v.length);
    }
                double[] ans=copy(w);
                for(int k=p;k<p+v.length;k++) {
                        ans[k]+=a*v[k-p];
                }
                return(ans);
        }

        /**
        * Adds a scalar to every element in the array.
        */
  public static double[] add(double[] w, double a) {
    double[] ans=copy(w);
    for(int k=0;k<ans.length;k++) {
      ans[k]+=a;
    }
    return(ans);
  }

        /**
        * Takes the transpose of an array (like the matrix operation).
        * @exception IllegalArgumentException if the array is not a matrix
        */
        public static double[][] transpose (double[][] M) {
                double[][] Mt=new double[M[0].length][M.length];
                for(int i=0;i<M.length;i++) {
      if(M[i].length!=M[0].length) {
        throw new IllegalArgumentException("The array is not a matrix.");
      }
                        for(int j=0;j<M[0].length;j++) {
                                Mt[j][i]=M[i][j];
                        }
                }
                return(Mt);
        }

  /**
  * Generates an array going for a to b
  * with steps of size step. If it can't
  * get to the value b in a finite number
  * of steps, it gets as close as possible
  * (a can be larger or smaller than b)
  * @param step size of steps, must be positive.
  * @param a first value of array.
  * @param b last value of array.
  * @exception IllegalArgumentException if step is negative of if a=b.
  */
  public static double[] range(double a, double b, double step) {
    if( step<=0.0) {
      throw new IllegalArgumentException("The argument step should be positive: "+step+" < 0");
    }
    if( a==b) {
      double[] ans=new double[1];
      ans[0]=a;
      return(ans);
    }
    int sizeOfArray=(new Double(Math.abs(a-b)/step)).intValue()+1;
    double[] ans=new double[sizeOfArray];
    ans[0]=a;
    if(a>b) {
      step=-step;
    }
    for(int k=1;k<sizeOfArray;k++) {
      ans[k]=ans[k-1]+step;
    }
    return(ans);
  }

  /**
  * Generates an array going for a to b
  * inclusively with steps of size 1. a can be
  * smaller or larger than b.
  */
  public static double[] range(double a, double b) {
    return(range(a,b,1.0));
  }
  /**
  * Generates an array going for 0 to b
  * with steps of size 1. 0 can be
  * smaller or larger than b.
  */
  public static double[] range(double b) {
    return(range(0,b));
  }

  /**
  * Adds the two arrays together (componentwise).
  * @exception IllegalArgumentException if the
  * two arrays don't have the same length.
  */
        public static double[] add (double[] a, double[] b) {
                if (a.length!=b.length) {
                        throw new IllegalArgumentException("To add two arrays, they must have the same length : "+a.length+", "+b.length);
                }
                double[] ans=copy(a);
                for(int i=0;i<a.length;i++) {
                        ans[i]+=b[i];
                }
                return(ans);
        }
  /**
  * Subtracts the two arrays together (componentwise)
  * @exception IllegalArgumentException if the
  * two arrays don't have the same length.
  */
        public static double[] subtract (double[] a, double[] b) {
                if (a.length!=b.length) {
                        throw new IllegalArgumentException("To add two arrays, they must have the same length : "+a.length+", "+b.length);
                }
                double[] ans=copy(a);
                for(int i=0;i<a.length;i++) {
                        ans[i]-=b[i];
                }
                return(ans);
        }


        /**
        * Multiplies every component of an array by a scalar.
        */
        public static double[] scalarMultiply(double a, double[] v) {
                double[] ans=new double[v.length];
                for(int k=0;k<v.length;k++) {
                        ans[k]=a*v[k];
                }
                return(ans);
        }


  /**
  * Returns a comma delimited string representing the value of the array.
  */
  public static String toString(double[] array) {
          StringBuffer buf=new StringBuffer(array.length);
          String sep = "";
          for(int i=0;i<array.length;i++) {
                  buf.append(sep);
                  buf.append(array[i]);
                  sep = ",";
          }
          return buf.toString();
  }

  /**
  * Returns a comma delimited string representing the value of the array.
  */
  public static String toString(double[][] array) {
          StringBuffer buf=new StringBuffer();
          for(int k=0;k<array.length;k++) {
            buf.append(toString(array[k]));
            buf.append(System.getProperty("line.separator"));
          }
          return buf.toString();
  }

        /**
        * Returns the sum of the elements of the array.
        */
        public static double mass(double[] v) {
                double somme=0.0;
                for(int k=0;k<v.length;k++) {
                        somme+=v[k];
                }
                return(somme);
        }

        /**
        * Fast scalar multiplication for sparse arrays.
        */
        public static double[] scalarMultiplyFast(double a, double[] v) {
                if(a==0.0) return(new double[v.length]);
                double[] ans=new double[v.length];
                for(int k=0;k<v.length;k++) {
                        if((a!=0.0) && (v[k]!=0)) {
                                ans[k]=v[k]*a;
                        } else ans[k]=0.0;
                }
                return(ans);
        }

        /**
        * Prints an array to screen.
        */
        public static void print(double[] v) {
                for(int k=0;k<v.length;k++) {
                        System.out.println("array ["+k+"] = "+v[k]);
                }
        }

        /**
        * Prints an array to screen.
        */
        public static void print(double[][] v) {
                for(int k=0;k<v.length;k++) {
      for(int l=0;l<v[k].length;l++) {
                          System.out.println("array ["+k+"]["+l+"] = "+v[k][l]);
      }
                }
        }
        /**
        * Sets an array to the specified length scraping or
        * padding the beginning if necessary.
        */
        public static int[] setLengthFromEnd(int[] data, int length) {
                int[] ans=new int[length];
                int debut;
                if(length-data.length<0)
      debut=data.length-length;
                else
      debut=0;
    System.arraycopy(data,debut,ans,-data.length+length+debut,data.length-debut);
                return(ans);
        }

        /**
        * Sets an array to the specified length scraping or
        * padding the end if necessary.
        */
        public static int[] setLengthFromBeginning (int[] data, int length) {
                int[] ans=new int[length];
                int debut;
                if(length-data.length<0)
      debut=data.length-length;
                else
      debut=0;
    System.arraycopy(data,0,ans,0,data.length-debut);
                return(ans);
        }


        /**
        * Returns a copy of the array.
        */
        public static int[] copy(int[] v) {
                int[] ans=new int[v.length];
    System.arraycopy(v,0,ans,0,v.length);
                return(ans);
        }

        /**
        * Returns a copy of the array.
        */
        public static int[][] copy(int[][] v) {
                int[][] ans=new int[v.length][];
    for (int k=0;k<v.length;k++)
      ans[k]=copy(v[k]);
                return(ans);
        }

        /**
        * Computes the variance.
        */
        public static double variance(int[] v) {
                final double m = mean(v);
                double ans=0.0;
                for(int i=0;i<v.length;i++)
                        ans+=(v[i]-m)*(v[i]-m);
                return ans/(v.length-1);
        }


  /**
  * Computes the covariance.
  */
        public static double covariance(int[] v1 , int[] v2) {
                if(v1.length!=v2.length)
                        throw new IllegalArgumentException("Arrays must have the same length : "+v1.length+", "+v2.length);
                final double m1 = mean(v1);
                final double m2 = mean(v2);
                double ans=0.0;
                for(int i=0;i<v1.length;i++)
                        ans+=(v1[i]-m1)*(v2[i]-m2);
                return ans/(v1.length-1);
        }

        /**
        * Computes the (linear) correlation between two arrays.
        * Squaring this result and multiply by 100 gives you
        * the percentage of correlation.
        */
  public static double correlation (int[] v1, int[] v2) {
    double denom=Math.sqrt(variance(v1)*variance(v2));
    if(denom!=0)
      return(covariance(v1,v2)/denom);
    else {
      if((variance(v1)==0) &&(variance(v2)==0))
        return(1.0);
      else return(0.0);  // impossible to correlate a null signal with another
    }
  }

        /**
        * Computes the mean.
        */
        public static double mean(int[] v) {
    if(v.length==0)
      throw new IllegalArgumentException("Nothing to compute! The array must have at least one element.");
                return(mass(v)/(double)v.length);
        }

        /**
        * Returns the standard deviation of an array.
        */
        public static double standardDeviation(int[] v) {
                return(Math.sqrt(variance(v)));
        }


        /**
        * Returns a sorted array from the minimum to the maximum value.
        */
        public static int[] sortMinToMax(int[] v) {
    int[] ans=copy(v);
    quickSortMinToMax(ans, 0, ans.length - 1);
    return(ans);
        }

        /**
        * Returns a sorted array from the maximum to the minimum value.
        */
        public static int[] sortMaxToMin(int[] v) {
    int[] ans=copy(v);
    quickSortMaxToMin(ans, 0, ans.length - 1);
    return(ans);
        }

  /**
  * Returns a comma delimited string representing the value of the array.
  */
  public static String toString(int[] array) {
          StringBuffer buf=new StringBuffer(array.length);
          String sep = "";
          for(int i=0;i<array.length;i++) {
                  buf.append(sep);
                  buf.append(array[i]);
                  sep = ",";
          }
          return buf.toString();
  }

  /**
  * Returns a comma delimited string representing the value of the array.
  */
  public static String toString(int[][] array) {
          StringBuffer buf=new StringBuffer();
          for(int k=0;k<array.length;k++) {
            buf.append(toString(array[k]));
            buf.append(System.getProperty("line.separator"));
          }
          return buf.toString();
  }

        /**
        * Gives the percentile of an array.
	* @param v an unsorted array
        * @param p percentile, must be between 0 and 1
        * @exception IllegalArgumentException if parameter
        * p is not between 0 and 1.
        */
        public static double percentile(int[] v, double p) {
		return percentile(v, p, false);
	}
	/**
	* @param isSorted true if the array is already sorted into ascending numerical order.
	*/
        public static double percentile(int[] v, double p, boolean isSorted) {
                if((p<0) || (p>1)) {
                        throw new IllegalArgumentException("Percentile must be between 0 and 1 : "+p);
                }
                final int[] ans = (isSorted ? v : sortMinToMax(v));
                final int pos = (int) Math.floor(p*(ans.length-1));
                final double dif = p*(ans.length-1)-Math.floor(p*(ans.length-1));
                if(pos == (ans.length-1))
                        return(ans[ans.length-1]);
                else
                        return(ans[pos]*(1.0-dif)+ans[pos+1]*dif);
        }

        /**
        * Computes the median of an array.
        * If the array contains an even number of elements,
        * the lower median is returned.
        */
        public static int median(int[] v) {
                if(v.length == 3) {
                        return median3(copy(v));
                } else if(v.length == 5) {
                        return median5(copy(v));
                } else if(v.length == 7) {
                        return median7(copy(v));
                } else {
                        return quickSelect(copy(v));
                }
        }
        /*
        * http://ndevilla.free.fr/median/median/index.html
        * optmed.c
        * @author N. Devillard
        */
        private static void sortInPlace(final int[] v, final int i, final int j) {
                if(v[i] > v[j]) {
                        final int tmp = v[i];
                        v[i] = v[j];
                        v[j] = tmp;
                }
        }
        private static int median3(int[] v) {
                sortInPlace(v,0,1);
                sortInPlace(v,1,2);
                sortInPlace(v,0,1);
                return v[1];
        }
        private static int median5(int[] v) {
                sortInPlace(v,0,1);
                sortInPlace(v,3,4);
                sortInPlace(v,0,3);
                sortInPlace(v,1,4);
                sortInPlace(v,1,2);
                sortInPlace(v,2,3);
                sortInPlace(v,1,2);
                return v[2];
        }
        private static int median7(int[] v) {
                sortInPlace(v,0,5);
                sortInPlace(v,0,3);
                sortInPlace(v,1,6);
                sortInPlace(v,2,4);
                sortInPlace(v,0,1);
                sortInPlace(v,3,5);
                sortInPlace(v,2,6);
                sortInPlace(v,2,3);
                sortInPlace(v,3,6);
                sortInPlace(v,4,5);
                sortInPlace(v,1,4);
                sortInPlace(v,1,3);
                sortInPlace(v,3,4);
                return v[3];
        }
        /*
        * http://ndevilla.free.fr/median/median/index.html
        * quickselect.c
        * @author N. Devillard
        */
        private static int quickSelect(int[] v) {
                int low=0, high=v.length-1;
                final int median=high/2;
                int middle, ll, hh;

                for (;;) {
                        if (high <= low) { /* One element only */
                                return v[median];
                        }

                        if (high == low + 1) {  /* Two elements only */
                                if (v[low] > v[high])
                                        swap(v, low, high);
                                return v[median];
                        }

                /* Find median of low, middle and high items; swap into position low */
                        middle = (low + high) / 2;
                        if (v[middle] > v[high]) swap(v, middle, high);
                        if (v[low] > v[high]) swap(v, low, high);
                        if (v[middle] > v[low]) swap(v, middle, low);

                /* Swap low item (now in position middle) into position (low+1) */
                        swap(v, middle, low+1);

                /* Nibble from each end towards middle, swapping items when stuck */
                        ll = low + 1;
                        hh = high;
                        for (;;) {
                                do ll++; while (v[low] > v[ll]);
                                do hh--; while (v[hh]  > v[low]);

                                if (hh < ll)
                                        break;

                                swap(v, ll, hh);
                        }

                /* Swap middle item (in position low) back into correct position */
                        swap(v, low, hh);

                /* Re-set active partition */
                        if (hh <= median)
                                low = ll;
                        if (hh >= median)
                                high = hh - 1;
                }
        }

        /**
        * Use java.util.Arrays instead.
        * Check if two arrays are equal.
        * @deprecated Use java.util.Arrays instead.
        */
        public static boolean equals(int[] a, int[] b) {
                if(a.length!=b.length) {
                        return(false);
                }
                for(int i=0;i<a.length;i++) {
                        if(a[i]!=b[i]) {
                                return(false);
                        }
                }
                return(true);
        }

        /**
        * Takes the absolute value of each component of an array.
        */
        public static int[] abs(int[] v) {
                int[] ans=new int[v.length];
                for(int i=0;i<v.length;i++) {
                        ans[i]=Math.abs(v[i]);
                }
                return(ans);
        }

        /**
        * Takes the absolute value of each component of an array.
        */
        public static int[][] abs(int[][] v) {
                int[][] ans=new int[v.length][];
                for(int i=0;i<v.length;i++) {
                        ans[i]=abs(v[i]);
                }
                return(ans);
        }

        /**
        * Returns the maximum of an array.
        */
        public static int max(int[] v) {
                int max=v[0];
                for(int i=1;i<v.length;i++) {
                        if(max<v[i]) {
                                max=v[i];
                        }
                }
                return(max);
        }

        /**
        * Returns the maximum of an array.
        */
        public static int max(int[][] v) {
                int max=max(v[0]);
                for(int i=1;i<v.length;i++) {
                        if(max<max(v[i])) {
                                max=max(v[i]);
                        }
                }
                return(max);
        }

        /**
        * Returns the minimum of an array.
        */
        public static int min(int[] v) {
                int min=v[0];
                for(int i=1;i<v.length;i++) {
                        if(min>v[i]) {
                                min=v[i];
                        }
                }
                return(min);
        }

        /**
        * Returns the minimum of an array.
        */
        public static int min(int[][] v) {
                int min=min(v[0]);
                for(int i=1;i<v.length;i++) {
                        if(min>min(v[i])) {
                                min=min(v[i]);
                        }
                }
                return(min);
        }

        /**
        * Computes the L2 norm of an array (Euclidean norm or "length").
        */
        public static double norm (int[] data) {
                return(Math.sqrt(sumSquares(data)));
        }

        /**
        * Sums the squares of all components;
        * also called the energy of the array.
        */
        public static int sumSquares (int[] data) {
                int ans=0;
                for(int k=0;k<data.length;k++) {
                        ans+=data[k]*data[k];
                }
                return(ans);
        }

        /**
        * Sums the squares of all components;
        * also called the energy of the array.
        */
        public static int sumSquares (int[][] data) {
                int ans=0;
                for(int k=0;k<data.length;k++) {
      for(int l=0;l<data[k].length;l++) {
                          ans+=data[k][l]*data[k][l];
      }
                }
                return(ans);
        }

        /**
        * Computes the scalar product of two array
        * as if they were vectors.
        * @exception IllegalArgumentException if the
        * don't have the same length.
        */
        public static int scalarProduct (int[] w0,int[] w1) {
                if (w0.length!=w1.length) {
                        throw new IllegalArgumentException("Arrays must have the same length : "+w0.length+", "+w1.length);
                }
    if(w0.length==0)
      throw new IllegalArgumentException("Nothing to compute! Arrays must have at least one element.");
                int sortie=0;
                for(int k=0;k<w0.length;k++){
                        sortie=sortie+w0[k]*w1[k];
                }
                return(sortie);
        }

        /**
        * Computes the scalar product of two array
        * as if they were matrices.
        * @exception IllegalArgumentException if the
        * don't have the same length.
        */
        public static double scalarProduct (int[][] w0,int[][] w1) {
                if (w0.length!=w1.length) {
                        throw new IllegalArgumentException("Arrays must have the same length : "+w0.length+", "+w1.length);
                }
    if(w0.length==0)
      throw new IllegalArgumentException("Nothing to compute! Arrays must have at least one element.");
                double sortie=0.0;
                for(int k=0;k<w0.length;k++){
                        sortie=sortie+scalarProduct(w0[k],w1[k]);
                }
                return(sortie);
        }


        /**
        * Extracts a sub-array (will invert the
        * resulting array if k0 > k1).
        * @param k0 location of the first component.
        * @param k1 location of the last component.
        */
        public static  int[] extract(int k0, int k1, int[] invect) {
                if ((k0<0)||(k1<0)||(k0>invect.length-1)||(k1>invect.length-1)) {
                        throw new IllegalArgumentException("The parameters are incorrect : "+k0+", "+k1+", "+invect.length);
                }
                if(k1>k0) {
                        int[] ans=new int[k1-k0+1];
      System.arraycopy(invect,k0,ans,0,k1-k0+1);
                        return(ans);
                }
                        int[] ans=new int[-k1+k0+1];
                        for(int k=k1;k<=k0;k++) {
                                ans[k0-k]=invect[k];
                        }
                        return(ans);
        }

        /**
        * Inverts an array from left to right.
        */
        public static int [] invert (int[] v) {
                int[] w=new int[v.length];
                for(int k=0;k<v.length;k++) {
                        w[v.length-1-k]=v[k];
                }
                return(w);
        }

        /**
        * Fills in with zeroes to get to the specified length;
        * original array with be at the specified position
        * @param n0 length of the new array.
        * @param pos position of the old array.
        */
        public static int[] padding(int n0, int pos, int[] v) {
                if ((v.length+pos>n0)||(pos<0)) {
                        throw new IllegalArgumentException("The array is too long for this : "+n0+", "+pos+", "+v.length);
                }
                int[] w=new int[n0];
    System.arraycopy(v,0,w,pos,v.length);
                return(w);
        }


        /**
        * Adds to an array w, a times v where a is a scalar.
        * Since v can be smaller then w, we must specified the position at which v will be added.
        * @param a scalar.
        * @param p position.
        * @param w longer array.
        * @param v shorter array.
        * @exception IllegalArgumentException if the second array
        * is not shorter than the first one.
        */
        public static int[] add(int[]w, double a, int[] v,int p) {
    if(v.length>w.length) {
      throw new IllegalArgumentException("Second array must be shorter or equal to the first one : "+w.length+", "+v.length);
    }
                int[] ans=copy(w);
                for(int k=p;k<p+v.length;k++) {
                        ans[k]+=a*v[k-p];
                }
                return(ans);
        }

  /**
  * Adds a scalar to every element in the array.
  */
  public static int[] add(int[] w, int a) {
    int[] ans=copy(w);
    for(int k=0;k<ans.length;k++) {
      ans[k]+=a;
    }
    return(ans);
  }

  /**
  * Generates an array going for a to b
  * with steps of size 1. a can be
  * smaller or larger than b.
  */
  public static int[] range(int a, int b) {
    return(range(a,b,1));
  }
  /**
  * Generates an array going for 0 to b
  * with steps of size 1. 0 can be
  * smaller or larger than b.
  */
  public static int[] range(int b) {
    return(range(0,b));
  }

  /**
  * Generates an array going for a to b
  * with steps of size step. If it can't
  * get to the value b in a finite number
  * of steps, it gets as close as possible
  * (a can be larger or smaller than b)
  * @param step size of steps, must be positive
  * @param a first value of array
  * @param b last value of array
  * @exception IllegalArgumentException if step is
  * negative or if a=b.
  */
  public static int[] range(int a, int b, int step) {
    if( step<=0) {
      throw new IllegalArgumentException("The argument step should be positive: "+step+" < 0");
    }
    if( a==b) {
      int[] ans=new int[1];
      ans[0]=a;
      return(ans);
    }
    int sizeOfArray=(new Double(Math.abs(a-b)/step)).intValue();
    int[] ans=new int[sizeOfArray];
    ans[0]=a;
    if(a>b) {
      step=-step;
    }
    for(int k=1;k<sizeOfArray;k++) {
      ans[k]=ans[k-1]+step;
    }
    return(ans);
  }

  /**
  * Takes the transpose of an array (like the matrix
  * operation).
  * @exception IllegalArgumentException if the array
  * is not a matrix
  */
        public static int[][] transpose (int[][] M) {
                int[][] Mt=new int[M[0].length][M.length];
                for(int i=0;i<M.length;i++) {
      if(M[i].length!=M[0].length) {
        throw new IllegalArgumentException("The array is not a matrix.");
      }
                        for(int j=0;j<M[0].length;j++) {
                                Mt[j][i]=M[i][j];
                        }
                }
                return(Mt);
        }

        /**
        * Adds the two arrays together (componentwise).
        * @exception IllegalArgumentException if the
        * two arrays don't have the same length.
        */
        public static int[] add (int[] a, int[] b) {
                if (a.length!=b.length) {
                        throw new IllegalArgumentException("To add two arrays, they must have the same length : "+a.length+", "+b.length);
                }
                int[] ans=copy(a);
                for(int i=0;i<a.length;i++) {
                        ans[i]+=b[i];
                }
                return(ans);
        }
        /**
        * Subtracts the two arrays together (componentwise).
        * @exception IllegalArgumentException if the
        * two arrays don't have the same length.
        */
        public static int[] subtract (int[] a, int[] b) {
                if (a.length!=b.length) {
                        throw new IllegalArgumentException("To add two arrays, they must have the same length : "+a.length+", "+b.length);
                }
                int[] ans=copy(a);
                for(int i=0;i<a.length;i++) {
                        ans[i]-=b[i];
                }
                return(ans);
        }

        /**
        * Returns the sum of the elements of the array.
        */
        public static int mass(int[] v) {
                int somme=0;
                for(int k=0;k<v.length;k++) {
                        somme+=v[k];
                }
                return(somme);
        }

        /**
        * Multiplies every component of an array by a scalar.
        */
        public static double[] scalarMultiply(double a, int[] v) {
                double[] ans=new double[v.length];
                for(int k=0;k<v.length;k++) {
                        ans[k]=v[k]*a;
                }
                return(ans);
        }

        /**
        * Fast scalar multiplication for sparse arrays.
        */
        public static double[] scalarMultiplyFast(double a, int[] v) {
                if(a==0.0) return (new double[v.length]);
                double[] ans=new double[v.length];
                for(int k=0;k<v.length;k++) {
                        if((a!=0) && (v[k]!=0)) {
                                ans[k]=v[k]*a;
                        } else ans[k]=0.0;
                }
                return(ans);
        }

        /**
        * Prints an array to screen.
        */
        public static void print(int[] v) {
                for(int k=0;k<v.length;k++) {
                        System.out.println("array ["+k+"] = "+v[k]);
                }
        }
        /**
        * Prints an array to screen.
        */
        public static void print(int[][] v) {
                for(int k=0;k<v.length;k++) {
      for(int l=0;l<v[k].length;l++) {
                          System.out.println("array ["+k+"]["+l+"] = "+v[k][l]);
      }
                }
        }

   /**
* This is a generic version of C.A.R Hoare's Quick Sort
    * algorithm.  This will handle arrays that are already
    * sorted, and arrays with duplicate keys.<BR>
    *
    * If you think of a one dimensional array as going from
    * the lowest index on the left to the highest index on the right
    * then the parameters to this function are lowest index or
    * left and highest index or right.  The first time you call
    * this function it will be with the parameters 0, a.length - 1.
    * (taken out of a code by James Gosling and Kevin A. Smith provided
    * with Sun's JDK 1.1.7)
    *
    * @param a       an integer array
    * @param lo0     left boundary of array partition (inclusive).
    * @param hi0     right boundary of array partition (inclusive).
        * @deprecated
    */
   private static void quickSortMinToMax(int a[], int lo0, int hi0)
   {
      int lo = lo0;
      int hi = hi0;
      int mid;

      if ( hi0 > lo0)
      {

         /* Arbitrarily establishing partition element as the midpoint of
          * the array.
          */
         mid = a[ (int) Math.round(( lo0 + hi0 ) / 2.0) ];

         // loop through the array until indices cross
         while( lo <= hi )
         {
            /* find the first element that is greater than or equal to
             * the partition element starting from the left Index.
             */
             while( ( lo < hi0 )  && ( a[lo] < mid ))
                 ++lo;

            /* find an element that is smaller than or equal to
             * the partition element starting from the right Index.
             */
             while( ( hi > lo0 )  && ( a[hi] > mid ))
                 --hi;

            // if the indexes have not crossed, swap
            if( lo <= hi )
            {
               swap(a, lo, hi);
               ++lo;
               --hi;
            }
         }

         /* If the right index has not reached the left side of array
          * must now sort the left partition.
          */
         if( lo0 < hi )
            quickSortMinToMax( a, lo0, hi );

         /* If the left index has not reached the right side of array
          * must now sort the right partition.
          */
         if( lo < hi0 )
            quickSortMinToMax( a, lo, hi0 );

      }
   }

   /** This is a generic version of C.A.R Hoare's Quick Sort
    * algorithm.  This will handle arrays that are already
    * sorted, and arrays with duplicate keys.<BR>
    *
    * If you think of a one dimensional array as going from
    * the lowest index on the left to the highest index on the right
    * then the parameters to this function are lowest index or
    * left and highest index or right.  The first time you call
    * this function it will be with the parameters 0, a.length - 1.
    * (taken out of a code by James Gosling and Kevin A. Smith provided
    * with Sun's JDK 1.1.7)
    *
    * @param a       an integer array
    * @param lo0     left boundary of array partition (inclusive).
    * @param hi0     right boundary of array partition (inclusive).
    */
   private static void quickSortMaxToMin(int a[], int lo0, int hi0)
   {
      int lo = lo0;
      int hi = hi0;
      int mid;

      if ( hi0 > lo0)
      {

         /* Arbitrarily establishing partition element as the midpoint of
          * the array.
          */
         mid = a[(int) Math.round( ( lo0 + hi0 ) / 2.0) ];

         // loop through the array until indices cross
         while( lo <= hi )
         {
            /* find the first element that is greater than or equal to
             * the partition element starting from the left Index.
             */
             while( ( lo < hi0 )  && ( a[lo] > mid ))
                 ++lo;

            /* find an element that is smaller than or equal to
             * the partition element starting from the right Index.
             */
             while( ( hi > lo0 )  && ( a[hi] < mid ))
                 --hi;

            // if the indexes have not crossed, swap
            if( lo <= hi )
            {
               swap(a, lo, hi);
               ++lo;
               --hi;
            }
         }

         /* If the right index has not reached the left side of array
          * must now sort the left partition.
          */
         if( lo0 < hi )
            quickSortMaxToMin( a, lo0, hi );

         /* If the left index has not reached the right side of array
          * must now sort the right partition.
          */
         if( lo < hi0 )
            quickSortMaxToMin( a, lo, hi0 );

      }
   }

   /**
* This is a generic version of C.A.R Hoare's Quick Sort
    * algorithm.  This will handle arrays that are already
    * sorted, and arrays with duplicate keys.<BR>
    *
    * If you think of a one dimensional array as going from
    * the lowest index on the left to the highest index on the right
    * then the parameters to this function are lowest index or
    * left and highest index or right.  The first time you call
    * this function it will be with the parameters 0, a.length - 1.
    * (taken out of a code by James Gosling and Kevin A. Smith provided
    * with Sun's JDK 1.1.7)
    *
    * @param a       a double array
    * @param lo0     left boundary of array partition (inclusive).
    * @param hi0     right boundary of array partition (inclusive).
        * @deprecated
    */
   private static void quickSortMinToMax(double a[], int lo0, int hi0)
   {
      int lo = lo0;
      int hi = hi0;
      double mid;

      if ( hi0 > lo0)
      {

         /* Arbitrarily establishing partition element as the midpoint of
          * the array.
          */
         mid = a[(int) Math.round( ( lo0 + hi0 ) / 2.0) ];

         // loop through the array until indices cross
         while( lo <= hi )
         {
            /* find the first element that is greater than or equal to
             * the partition element starting from the left Index.
             */
             while( ( lo < hi0 )  && ( a[lo] < mid ))
                 ++lo;

            /* find an element that is smaller than or equal to
             * the partition element starting from the right Index.
             */
             while( ( hi > lo0 )  && ( a[hi] > mid ))
                 --hi;

            // if the indexes have not crossed, swap
            if( lo <= hi )
            {
               swap(a, lo, hi);
               ++lo;
               --hi;
            }
         }

         /* If the right index has not reached the left side of array
          * must now sort the left partition.
          */
         if( lo0 < hi )
            quickSortMinToMax( a, lo0, hi );

         /* If the left index has not reached the right side of array
          * must now sort the right partition.
          */
         if( lo < hi0 )
            quickSortMinToMax( a, lo, hi0 );

      }
   }

   /**
* This is a generic version of C.A.R Hoare's Quick Sort
    * algorithm.  This will handle arrays that are already
    * sorted, and arrays with duplicate keys.<BR>
    *
    * If you think of a one dimensional array as going from
    * the lowest index on the left to the highest index on the right
    * then the parameters to this function are lowest index or
    * left and highest index or right.  The first time you call
    * this function it will be with the parameters 0, a.length - 1.
    * (taken out of a code by James Gosling and Kevin A. Smith provided
    * with Sun's JDK 1.1.7)
    *
    * @param a       a double array
    * @param lo0     left boundary of array partition (inclusive).
    * @param hi0     right boundary of array partition (inclusive).
    */
   private static void quickSortMaxToMin(double a[], int lo0, int hi0)
   {
      int lo = lo0;
      int hi = hi0;
      double mid;

      if ( hi0 > lo0)
      {

         /* Arbitrarily establishing partition element as the midpoint of
          * the array.
          */
         mid = a[ (int) Math.round(( lo0 + hi0 ) / 2.0) ];

         // loop through the array until indices cross
         while( lo <= hi )
         {
            /* find the first element that is greater than or equal to
             * the partition element starting from the left Index.
             */
             while( ( lo < hi0 )  && ( a[lo] > mid ))
                 ++lo;

            /* find an element that is smaller than or equal to
             * the partition element starting from the right Index.
             */
             while( ( hi > lo0 )  && ( a[hi] < mid ))
                 --hi;

            // if the indexes have not crossed, swap
            if( lo <= hi )
            {
               swap(a, lo, hi);
               ++lo;
               --hi;
            }
         }

         /* If the right index has not reached the left side of array
          * must now sort the left partition.
          */
         if( lo0 < hi )
            quickSortMaxToMin( a, lo0, hi );

         /* If the left index has not reached the right side of array
          * must now sort the right partition.
          */
         if( lo < hi0 )
            quickSortMaxToMin( a, lo, hi0 );

      }
   }
   /**
   * Used by the quick sort and quick select algorithms.
   */
   private static void swap(final int a[], final int i, final int j)
   {
      final int T;
      T = a[i];
      a[i] = a[j];
      a[j] = T;
   }
   /**
   * Used by the quick sort and quick select algorithms.
   */
   private static void swap(final double a[], final int i, final int j)
   {
      final double T;
      T = a[i];
      a[i] = a[j];
      a[j] = T;

   }
}

