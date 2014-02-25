package javastat.util;

/**
 * <p>Title: javastat</p>
 * <p>Description: JAVA programs for statistical computations</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: Tung Hai University</p>
 * @version 1.4
 */

import java.util.*;

import javastat.util.*;

/*
 *  This class is based on DistLib.
 *  DistLib : A C Library of Special Functions
 *  Copyright (C) 1998 Ross Ihaka
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 */

public class StatRandom
{

   /**
    * The random sample.
    */

    public double[] randomSample;

    private static double q[] = {0.6931471805599453, 0.9333736875190459,
                                 0.9888777961838675, 0.9984959252914960,
                                 0.9998292811061389, 0.9999833164100727,
                                 0.9999985691438767, 0.9999998906925558,
                                 0.9999999924734159, 0.9999999995283275,
                                 0.9999999999728814, 0.9999999999985598,
                                 0.9999999999999289, 0.9999999999999968,
                                 0.9999999999999999, 1.0000000000000000};

    private static double[] al = {0.0, 0.0, 0.0,
                                  0.69314718055994530941723212145817,
                                  1.79175946922805500081247735838070,
                                  3.17805383034794561964694160129705,
                                  4.78749174278204599424770093452324,
                                  6.57925121201010099506017829290394,
                                  8.52516136106541430016553103634712};

    /**
     * Default StatRandom constructor.
     */

    public StatRandom()
    {
        this.randomSample=new double[]{this.normal()};
    }

    private double afc(int i)
    {
        double di, value;
        if (i < 0)
        {
            System.out.println(
                    "rhyper.c: afc(i)+ i=%d < 0 -- SHOULD NOT HAPPEN!\n" + i);
            return -1;
        }
        else if (i <= 7)
        {
            value = al[i + 1];
        }
        else
        {
            di = i;
            value = (di + 0.5) * java.lang.Math.log(di) - di +
                    0.08333333333333 / di
                    - 0.00277777777777 / di / di / di + 0.9189385332;
        }

        return value;
    }

    /**
     * Returns the next pseudorandom, uniformly distributed double value
     * between lower limit and upper limit from this random number
     * generator's sequence.
     * @param min the lower limit.
     * @param max the upper limit.
     * @return the next pseudorandom, uniformly distributed double value
     *         between lower limit and upper limit from this random number
     */

    public double uniform(double min,
                          double max)
    {
        return min + (max - min) * new Random().nextDouble();
    }

    /**
     * Returns the next pseudorandom, uniformly distributed double value
     * between 0.0 limit and 1.0 from this random number
     * generator's sequence.
     * @return the next pseudorandom, uniformly distributed double value
     *         between 0.0 limit and 1.0 from this random number
     *         generator's sequence.
     */

    public double uniform()
    {
        return uniform(0.0, 1.0);
    }

    /**
     * Returns the next pseudorandom, beta distributed double
     * value with specified values of shape parameters from this random number
     * generator's sequence.
     * @param aa the shape parameter.
     * @param bb the shape parameter.
     * @return the next pseudorandom, beta distributed double value with
     *         specified values of shape parameters from this random number
     *         generator's sequence.
     */

    public double beta(double aa,
                       double bb)
    {
        int qsame;
        double expmax = 0.0;
        double a = 0.0, b = 0.0, delta = 0.0, r = 0.0, s = 0.0, t = 0.0,
                u1 = 0.0, u2 = 0.0, v = 0.0, w = 0.0, y = 0.0, z = 0.0,
                alpha = 0.0, beta = 0.0, gamma = 0.0, k1 = 0.0, k2 = 0.0,
                olda = -1.0, oldb = -1.0;
        Random rng = new Random();
        if (expmax == 0.0)
        {
            expmax = java.lang.Math.log(Double.MAX_VALUE);
        }
        qsame = ((olda == aa) && (oldb == bb)) ? 1 : 0;
        if (!(qsame == 1))
        {
            if (aa > 0.0 && bb > 0.0)
            {
                olda = aa;
                oldb = bb;
            }
            else
            {
                throw new java.lang.ArithmeticException("Math Error: DOMAIN");
            }
        }
        deliver:
        {
            if (Math.min(aa, bb) <= 1.0)
            {
                if (!(qsame == 1))
                {
                    a = Math.max(aa, bb);
                    b = Math.min(aa, bb);
                    alpha = a + b;
                    beta = 1.0 / b;
                    delta = 1.0 + a - b;
                    k1 = delta * (0.0138889 + 0.0416667 * b) /
                         (a * beta - 0.777778);
                    k2 = 0.25 + (0.5 + 0.25 / delta) * b;
                }
                for (; ; )
                {
                    u1 = rng.nextDouble();
                    u2 = rng.nextDouble();
                    if (u1 < 0.5)
                    {
                        y = u1 * u2;
                        z = u1 * y;
                        if (0.25 * u2 + z - y >= k1)
                        {
                            continue;
                        }
                    }
                    else
                    {
                        z = u1 * u1 * u2;
                        if (z <= 0.25)
                        {
                            break;
                        }
                        if (z >= k2)
                        {
                            continue;
                        }
                    }
                    v = beta * java.lang.Math.log(u1 / (1.0 - u1));
                    if (v <= expmax)
                    {
                        w = a * java.lang.Math.exp(v);
                    }
                    else
                    {
                        w = Double.MAX_VALUE;
                    }
                    if (alpha * (java.lang.Math.log(alpha / (b + w)) + v) -
                        1.3862944 >= java.lang.Math.log(z))
                    {
                        break deliver;
                    }
                }
                v = beta * java.lang.Math.log(u1 / (1.0 - u1));
                if (v <= expmax)
                {
                    w = a * java.lang.Math.exp(v);
                }
                else
                {
                    w = Double.MAX_VALUE;
                }
            }
            else
            {
                if (!(qsame == 1))
                {
                    a = Math.min(aa, bb);
                    b = Math.max(aa, bb);
                    alpha = a + b;
                    beta = java.lang.Math.sqrt((alpha - 2.0) /
                                               (2.0 * a * b - alpha));
                    gamma = a + 1.0 / beta;
                }
                do
                {
                    u1 = rng.nextDouble();
                    u2 = rng.nextDouble();
                    v = beta * java.lang.Math.log(u1 / (1.0 - u1));
                    if (v <= expmax)
                    {
                        w = a * java.lang.Math.exp(v);
                    }
                    else
                    {
                        w = Double.MAX_VALUE;
                    }
                    z = u1 * u1 * u2;
                    r = gamma * v - 1.3862944;
                    s = a + r - w;
                    if (s + 2.609438 >= 5.0 * z)
                    {
                        break;
                    }
                    t = java.lang.Math.log(z);
                    if (s > t)
                    {
                        break;
                    }
                }
                while (r + alpha * java.lang.Math.log(alpha / (b + w)) < t);
            }
        }

        return (aa != a) ? b / (b + w) : w / (b + w);
    }

    /**
     * Returns the next pseudorandom, Cauchy distributed double
     * value with specified values of shape parameters from this random number
     * generator's sequence.
     * @param location the location parameter.
     * @param scale the scale parameter.
     * @return the next pseudorandom, Cauchy distributed double value with
     *         specified values of shape parameters from this random number
     *         generator's sequence.
     */

    public double cauchy(double location,
                         double scale)
    {
        if (Double.isInfinite(location) ||
            Double.isInfinite(scale) ||
            scale < 0)
        {
            throw new java.lang.ArithmeticException("Math Error: DOMAIN");
        }

        return location + scale * java.lang.Math.tan(
                BasicStatistics.PI * new Random().nextDouble());
    }

    /**
     * Returns the next pseudorandom, exponential distributed double
     * value with mean 1 from this random number generator's sequence.
     * @return the next pseudorandom, exponential distributed double
     *         value with mean 1 from this random number generator's sequence.
     */

    public double exponential()
    {
        double a = 0.0, u, ustar, umin;
        int i;
        Random rng = new Random();
        u = rng.nextDouble();
        for (; ; )
        {
            u = u + u;
            if (u > 1.0)
            {
                break;
            }
            a = a + q[0];
        }
        u = u - 1.0;
        if (u <= q[0])
        {
            return a + u;
        }
        i = 0;
        ustar = rng.nextDouble();
        umin = ustar;
        do
        {
            ustar = rng.nextDouble();
            if (ustar < umin)
            {
                umin = ustar;
            }
            i = i + 1;
        }
        while (u > q[i]);

        return a + umin * q[0];
    }

    /**
     * Returns the next pseudorandom, exponential distributed double
     * value with the specified mean from this random number generator's
     * sequence.
     * @param scale the mean of the distribution.
     * @return the next pseudorandom, exponential distributed double
     *         value with the specified mean from this random number generator's
     *         sequence.

     */

    public double exponential(double scale)
    {
        if (Double.isInfinite(scale) || scale <= 0.0)
        {
            throw new java.lang.ArithmeticException("Math Error: DOMAIN");
        }

        return scale * exponential();
    }

    /**
     * Returns the next pseudorandom, Gaussian (normal) distributed double
     * value with mean 0 and variance 1 from this random number generator's
     * sequence.
     * @return the next pseudorandom, Gaussian (normal) distributed double
     *         value with mean 0 and variance 1 from this random number
     *         generator's sequence.
     */

    public double normal()
    {
        return new Random().nextGaussian();
    }

    /**
     * Returns the next pseudorandom, Gaussian (normal) distributed double
     * value with specified mean and standard deviation from this random number
     * generator's sequence.
     * @param mu the mean.
     * @param sigma the standard deviation.
     * @return the next pseudorandom, Gaussian (normal) distributed double
     *         value with specified mean and standard deviation from this random
     *         number generator's sequence.
     */

    public double normal(double mu,
                         double sigma)
    {
        return mu + sigma * normal();
    }

    /**
     * Returns the next pseudorandom, Gamma distributed double
     * value with specified location and scale parameter from this random number
     * generator's sequence.
     * @param alpha the location parameter.
     * @param beta the scale parameter, where the mean of the distribution is
     *             alpha / beta.
     * @return the next pseudorandom, Gamma distributed double
     *         value with specified mean and scale parameter from this random
     *         number generator's sequence.
     */

    public double gamma(double alpha,
                        double beta)
    {
        return randomGamma(alpha, 1.0 / beta);
    }


    /**
     * Returns the next pseudorandom, Gamma distributed double
     * value with specified mean and scale parameter from this random number
     * generator's sequence.
     * @param a the location parameter.
     * @param scale the scale parameter.
     * @return the next pseudorandom, Gamma distributed double
     *         value with specified mean and scale parameter from this random
     *         number generator's sequence.
     */

    private double randomGamma(double a,
                              double scale)
    {
        double aa = 0.0, sqrt32 = 5.656854, a1 = 0.3333333, a2 = -0.250003,
               a3 = 0.2000062, a4 = -0.1662921, a5 = 0.1423657,
               a6 = -0.1367177,
               a7 = 0.1233795, e1 = 1.0, e2 = 0.4999897, e3 = 0.166829,
               e4 = 0.0407753, e5 = 0.010293, q1 = 0.04166669, q2 = 0.02083148,
               q3 = 0.00801191, q4 = 0.00144121, q5 = -7.388e-5, q6 = 2.4511e-4,
               q7 = 2.424e-4, aaa = 0.0, s = 0.0, d = 0.0, s2 = 0.0,
               q0 = 0.0, b = 0.0, si = 0.0, c = 0.0;
        double p, t, x, u, r, v, w, e, q, ret_val;
        if (a < 1.0)
        {
            aa = 0.0;
            b = 1.0 + 0.36787944117144232159 * a;
            while (true)
            {
                p = b * new Random().nextDouble();
                if (p >= 1.0)
                {
                    ret_val = -java.lang.Math.log((b - p) / a);
                    if (exponential() >=
                        (1.0 - a) * java.lang.Math.log(ret_val))
                    {
                        break;
                    }
                }
                else
                {
                    ret_val = java.lang.Math.exp(java.lang.Math.log(p) / a);
                    if (exponential() >= ret_val)
                    {
                        break;
                    }
                }
            }
            return scale * ret_val;
        }
        if (a != aa)
        {
            aa = a;
            s2 = a - 0.5;
            s = java.lang.Math.sqrt(s2);
            d = sqrt32 - s * 12.0;
        }
        t = normal(0.0, 1.0);
        x = s + 0.5 * t;
        ret_val = x * x;
        if (t >= 0.0)
        {
            return scale * ret_val;
        }
        u = uniform(0.0, 1.0);
        if (d * u <= t * t * t)
        {
            return scale * ret_val;
        }
        if (a != aaa)
        {
            aaa = a;
            r = 1.0 / a;
            q0 = ((((((q7 * r + q6) * r + q5) * r + q4)
                    * r + q3) * r + q2) * r + q1) * r;
            if (a <= 3.686)
            {
                b = 0.463 + s + 0.178 * s2;
                si = 1.235;
                c = 0.195 / s - 0.079 + 0.16 * s;
            }
            else if (a <= 13.022)
            {
                b = 1.654 + 0.0076 * s2;
                si = 1.68 / s + 0.275;
                c = 0.062 / s + 0.024;
            }
            else
            {
                b = 1.77;
                si = 0.75;
                c = 0.1515 / s;
            }
        }
        if (x > 0.0)
        {
            v = t / (s + s);
            if (java.lang.Math.abs(v) <= 0.25)
            {
                q = q0 + 0.5 * t * t * ((((((a7 * v + a6) * v + a5) * v + a4) *
                                          v + a3) * v + a2) * v + a1) * v;
            }
            else
            {
                q = q0 - s * t + 0.25 * t * t + (s2 + s2) *
                    java.lang.Math.log(1.0 + v);
            }
            if (java.lang.Math.log(1.0 - u) <= q)
            {
                return scale * ret_val;
            }
        }
        while (true)
        {
            e = exponential();
            u = uniform(0.0, 1.0);
            u = u + u - 1.0;
            if (u < 0.0)
            {
                t = b - si * e;
            }
            else
            {
                t = b + si * e;
            }
            if (t >= -0.71874483771719)
            {
                v = t / (s + s);
                if (java.lang.Math.abs(v) <= 0.25)
                {
                    q = q0 + 0.5 * t * t * ((((((a7 * v + a6) * v + a5) *
                                               v + a4) * v + a3)
                                             * v + a2) * v + a1) * v;
                }
                else
                {
                    q = q0 - s * t + 0.25 * t * t + (s2 + s2) *
                        java.lang.Math.log(1.0 + v);
                }
                if (q > 0.0)
                {
                    if (q <= 0.5)
                    {
                        w = ((((e5 * q + e4) * q + e3)
                              * q + e2) * q + e1) * q;
                    }
                    else
                    {
                        w = java.lang.Math.exp(q) - 1.0;
                    }
                    if (c * java.lang.Math.abs(u) <=
                        w * java.lang.Math.exp(e - 0.5 * t * t))
                    {
                        break;
                    }
                }
            }
        }
        x = s + 0.5 * t;

        return scale * x * x;
    }

    /**
     * Returns the next pseudorandom, chi-square distributed double
     * value with specified degrees of freedom from this random number
     * generator's sequence.
     * @param df the degrees of freedom.
     * @return the next pseudorandom, chi-square distributed double
     *         value with specified degrees of freedom from this random number
     *         generator's sequence.
     */

    public double chisquare(double df)
    {
        if (Double.isInfinite(df) || df <= 0.0)
        {
            throw new java.lang.ArithmeticException("Math Error: DOMAIN");
        }

        return gamma(df / 2.0, 2.0);
    }

    /**
     * Returns the next pseudorandom, f distributed double value with
     * specified degrees of freedom for the numerator and denominator
     * from this random number generator's sequence.
     * @param df1 the degrees of freedom for the numerator.
     * @param df2 the degrees of freedom for the denominator.
     * @return the next pseudorandom, f distributed double value with
     *         specified degrees of freedom for the numerator and denominator
     *         from this random number generator's sequence.
     */

    public double f(double df1,
                    double df2)
    {
        double v1, v2;
        if (Double.isNaN(df1) || Double.isNaN(df2) || df1 <= 0.0 || df2 <= 0.0)
        {
            throw new java.lang.ArithmeticException("Math Error: DOMAIN");
        }
        v1 = !Double.isInfinite(df1) ? (chisquare(df1) / df1) : normal();
        v2 = !Double.isInfinite(df2) ? (chisquare(df2) / df2) : normal();

        return v1 / v2;
    }

    /**
     * Returns the next pseudorandom, hypergeometric distributed double value
     * from this random number generator's sequence.
     * @param nn1in the number of red balls in the urn.
     * @param nn2in the number of black balls in the urn.
     * @param kkin the number of balls drawn from an urn with nnlin red and
     *             nn2in black balls.
     * @return the next pseudorandom, hypergeometric distributed double value
     *         from this random number generator's sequence.
     */

    public double hypergeometric(double nn1in,
                                 double nn2in,
                                 double kkin)
    {
        int ks = -1, n1s = -1, n2s = -1, k = 0, n1 = 0, n2 = 0, minjx = 0,
                maxjx = 0, m = 0;
        int i, ix, nn1, nn2, kk;
        double con = 57.56462733, deltal = 0.0078, deltau = 0.0034,
               scale = 1e25, tn = 0.0, w = 0.0, p3 = 0.0, p1 = 0.0, xl = 0.0,
               p2 = 0.0, lamdl = 0.0, xr = 0.0, lamdr = 0.0, a = 0.0;
        double d, e, f, g, p, r, s, t, u, v, y, y1, de, dg, gl, kl, ub, nk, dr,
               nm, gu, kr, ds, dt, ym, yn, yk, xm, xn, xk, alv;
        boolean setup1, setup2, reject;
        Random rng = new Random();
        if (Double.isInfinite(nn1in) || Double.isInfinite(nn2in) ||
            Double.isInfinite(kkin))
        {
            throw new java.lang.ArithmeticException("Math Error: DOMAIN");
        }
        nn1 = (int) java.lang.Math.floor(nn1in + 0.5);
        nn2 = (int) java.lang.Math.floor(nn2in + 0.5);
        kk = (int) java.lang.Math.floor(kkin + 0.5);
        if (nn1 < 0 || nn2 < 0 || kk < 0 || kk > nn1 + nn2)
        {
            throw new java.lang.ArithmeticException("Math Error: DOMAIN");
        }
        reject = true;
        setup1 = false;
        setup2 = false;
        if (nn1 != n1s || nn2 != n2s)
        {
            setup1 = true;
            setup2 = true;
        }
        else if (kk != ks)
        {
            setup2 = true;
        }
        if (setup1)
        {
            n1s = nn1;
            n2s = nn2;
            tn = nn1 + nn2;
            if (nn1 <= nn2)
            {
                n1 = nn1;
                n2 = nn2;
            }
            else
            {
                n1 = nn2;
                n2 = nn1;
            }
        }
        if (setup2)
        {
            ks = kk;
            if (kk + kk >= tn)
            {
                k = (int) (tn) - kk;
            }
            else
            {
                k = kk;
            }
        }
        if (setup1 || setup2)
        {
            m = (int) ((k + 1.0) * (n1 + 1.0) / (tn + 2.0));
            minjx = Math.max(0, k - n2);
            maxjx = Math.min(n1, k);
        }
        if (minjx == maxjx)
        {
            ix = maxjx;
            if (kk + kk >= tn)
            {
                if (nn1 > nn2)
                {
                    ix = kk - nn2 + ix;
                }
                else
                {
                    ix = nn1 - ix;
                }
            }
            else
            {
                if (nn1 > nn2)
                {
                    ix = kk - ix;
                }
            }
            return ix;
        }
        else if (m - minjx < 10)
        {
            if (setup1 || setup2)
            {
                if (k < n2)
                {
                    w = java.lang.Math.exp(con + afc(n2) + afc(n1 + n2 - k)
                                           - afc(n2 - k) - afc(n1 + n2));
                }
                else
                {
                    w = java.lang.Math.exp(con + afc(n1) + afc(k)
                                           - afc(k - n2) - afc(n1 + n2));
                }
            }
            L10: while (true)
            {
                p = w;
                ix = minjx;
                u = rng.nextDouble() * scale;
                L20: while (true)
                {
                    if (u > p)
                    {
                        u = u - p;
                        p = p * (n1 - ix) * (k - ix);
                        ix = ix + 1;
                        p = p / ix / (n2 - k + ix);
                        if (ix > maxjx)
                        {
                            continue L10;
                        }
                        continue L20;
                    }
                    break L10;
                }
            }
        }
        else
        {
            if (setup1 || setup2)
            {
                s = java.lang.Math.sqrt((tn - k) * k * n1 * n2 / (tn - 1) / tn /
                                        tn);
                d = (int) (1.5 * s) + .5;
                xl = m - d + .5;
                xr = m + d + .5;
                a = afc(m) + afc(n1 - m) + afc(k - m)
                    + afc(n2 - k + m);
                kl = java.lang.Math.exp(a - afc((int) (xl)) -
                                        afc((int) (n1 - xl))
                                        - afc((int) (k - xl))
                                        - afc((int) (n2 - k + xl)));
                kr = java.lang.Math.exp(a - afc((int) (xr - 1))
                                        - afc((int) (n1 - xr + 1))
                                        - afc((int) (k - xr + 1))
                                        - afc((int) (n2 - k + xr - 1)));
                lamdl = -java.lang.Math.log(xl * (n2 - k + xl) / (n1 - xl + 1)
                                            / (k - xl + 1));
                lamdr = -java.lang.Math.log((n1 - xr + 1) * (k - xr + 1)
                                            / xr / (n2 - k + xr));
                p1 = d + d;
                p2 = p1 + kl / lamdl;
                p3 = p2 + kr / lamdr;
            }
            L30: while (true)
            {
                u = rng.nextDouble() * p3;
                v = rng.nextDouble();
                if (u < p1)
                {
                    ix = (int) (xl + u);
                }
                else if (u <= p2)
                {
                    ix = (int) (xl + java.lang.Math.log(v) / lamdl);
                    if (ix < minjx)
                    {
                        continue L30;
                    }
                    v = v * (u - p1) * lamdl;
                }
                else
                {
                    ix = (int) (xr - java.lang.Math.log(v) / lamdr);
                    if (ix > maxjx)
                    {
                        continue L30;
                    }
                    v = v * (u - p2) * lamdr;
                }
                if (m < 100 || ix <= 50)
                {
                    f = 1.0;
                    if (m < ix)
                    {
                        for (i = m + 1; i <= ix; i++)
                        {
                            f = f * (n1 - i + 1) * (k - i + 1) /
                                (n2 - k + i) / i;
                        }
                    }
                    else if (m > ix)
                    {
                        for (i = ix + 1; i <= m; i++)
                        {
                            f = f * i * (n2 - k + i) / (n1 - i) / (k - i);
                        }
                    }
                    if (v <= f)
                    {
                        reject = false;
                    }
                }
                else
                {
                    y = ix;
                    y1 = y + 1.0;
                    ym = y - m;
                    yn = n1 - y + 1.0;
                    yk = k - y + 1.0;
                    nk = n2 - k + y1;
                    r = -ym / y1;
                    s = ym / yn;
                    t = ym / yk;
                    e = -ym / nk;
                    g = yn * yk / (y1 * nk) - 1.0;
                    dg = 1.0;
                    if (g < 0.0)
                    {
                        dg = 1.0 + g;
                    }
                    gu = g * (1.0 + g * ( -0.5 + g / 3.0));
                    gl = gu - .25 * (g * g * g * g) / dg;
                    xm = m + 0.5;
                    xn = n1 - m + 0.5;
                    xk = k - m + 0.5;
                    nm = n2 - k + xm;
                    ub = y * gu - m * gl + deltau
                         + xm * r * (1. + r * ( -0.5 + r / 3.0))
                         + xn * s * (1. + s * ( -0.5 + s / 3.0))
                         + xk * t * (1. + t * ( -0.5 + t / 3.0))
                         + nm * e * (1. + e * ( -0.5 + e / 3.0));
                    alv = java.lang.Math.log(v);
                    if (alv > ub)
                    {
                        reject = true;
                    }
                    else
                    {
                        dr = xm * (r * r * r * r);
                        if (r < 0.0)
                        {
                            dr = dr / (1.0 + r);
                        }
                        ds = xn * (s * s * s * s);
                        if (s < 0.0)
                        {
                            ds = ds / (1.0 + s);
                        }
                        dt = xk * (t * t * t * t);
                        if (t < 0.0)
                        {
                            dt = dt / (1.0 + t);
                        }
                        de = nm * (e * e * e * e);
                        if (e < 0.0)
                        {
                            de = de / (1.0 + e);
                        }
                        if (alv < ub - 0.25 * (dr + ds + dt + de)
                            + (y + m) * (gl - gu) - deltal)
                        {
                            reject = false;
                        }
                        else
                        {
                            if (alv <= (a - afc(ix) - afc(n1 - ix)
                                        - afc(k - ix) - afc(n2 - k + ix)))
                            {
                                reject = false;
                            }
                            else
                            {
                                reject = true;
                            }
                        }
                    }
                }
                if (reject)
                {
                    continue L30;
                }
                break L30;
            }
        }
        if (kk + kk >= tn)
        {
            if (nn1 > nn2)
            {
                ix = kk - nn2 + ix;
            }
            else
            {
                ix = nn1 - ix;
            }
        }
        else
        {
            if (nn1 > nn2)
            {
                ix = kk - ix;
            }
        }

        return ix;
    }

    /**
     * Returns the next pseudorandom, logistic distributed double
     * value with specified values of shape parameters from this random number
     * generator's sequence.
     * @param location the location parameter.
     * @param scale the scale parameter.
     * @return the next pseudorandom, logistic distributed double value with
     *         specified values of shape parameters from this random number
     *         generator's sequence.
     */

    public double logistic(double location,
                           double scale)
    {
        double u;
        if (Double.isInfinite(location) || Double.isInfinite(scale))
        {
            throw new java.lang.ArithmeticException("Math Error: DOMAIN");
        }
        u = new Random().nextDouble();

        return location + scale * java.lang.Math.log(u / (1.0 - u));
    }

    /**
     * Returns the next pseudorandom, lognormal distributed double
     * value with specified mean and standard deviation from this random number
     * generator's sequence.
     * @param logmean the mean of the distribution of the log of the random
     *                variable.
     * @param logsd the standard deviation the distribution of the log of the
     *              random variable.
     * @return the next pseudorandom, lognormal distributed double
     *         value with specified mean and standard deviation from this random
     *         number generator's sequence.
     */

    public double lognormal(double logmean,
                            double logsd)
    {
        if (Double.isInfinite(logmean) || Double.isInfinite(logsd) ||
            logsd <= 0.0)
        {
            throw new java.lang.ArithmeticException("Math Error: DOMAIN");
        }

        return java.lang.Math.exp(normal(logmean, logsd));
    }

    /**
     * Returns the next pseudorandom, t distributed double
     * value with specified degrees of freedom from this random number
     * generator's sequence.
     * @param df the degrees of freedom.
     * @return the next pseudorandom, t distributed double
     *         value with specified degrees of freedom from this random number
     *         generator's sequence.
     */

    public double t(double df)
    {
        if (Double.isNaN(df) || df <= 0.0)
        {
            throw new java.lang.ArithmeticException("Math Error: DOMAIN");
        }
        if (Double.isInfinite(df))
        {
            return normal();
        }
        else
        {
            return normal() / java.lang.Math.sqrt(chisquare(df) / df);
        }
    }

    /**
     * Returns the next pseudorandom, Weibull distributed double
     * value with specified values of shape and scale parameters from this
     * random number generator's sequence.
     * @param shape the shape parameter.
     * @param scale the scale parameter.
     * @return the next pseudorandom, Weibull distributed double
     *         value with specified values of shape and scale parameters from
     *         this random number generator's sequence.
     */

    public double weibull(double shape,
                          double scale)
    {
        if (Double.isInfinite(shape) || Double.isInfinite(scale) ||
            shape <= 0.0 || scale <= 0.0)
        {
            throw new java.lang.ArithmeticException("Math Error: DOMAIN");
        }

        return scale * java.lang.Math.pow(
                -java.lang.Math.log(new Random().nextDouble()), 1.0 / shape);
    }

    /**
     * Returns a random sample given the specified type of
     * the distribution and sample size.
     * @param distribution the enum in the class DistributionType.
     * @param parameter the parameters for the distribution of interest.
     * @param sampleSize the sample size.
     * @return the random sample.
     */

    public double[] random(DistributionType distribution,
                           double[] parameter,
                           int sampleSize)
    {
        randomSample = new double[sampleSize];
        for(int i = 0; i < sampleSize; i++)
        {
            switch (distribution)
            {
                case UNIFORM:
                    randomSample[i] = uniform(parameter[0], parameter[1]);
                    break;
                case NORMAL:
                    randomSample[i] = normal(parameter[0], parameter[1]);
                    break;
                case BETA:
                    randomSample[i] = beta(parameter[0], parameter[1]);
                    break;
                case CAUCHY:
                    randomSample[i] = cauchy(parameter[0], parameter[1]);
                    break;
                case CHISQUARE:
                    randomSample[i] = chisquare(parameter[0]);
                    break;
                case EXPONENTIAL:
                    randomSample[i] = exponential(parameter[0]);
                    break;
                case F:
                    randomSample[i] = f(parameter[0], parameter[1]);
                    break;
                case GAMMA:
                    randomSample[i] = gamma(parameter[0], parameter[1]);
                    break;
                case HYPERGEOMETRIC:
                    randomSample[i] = hypergeometric(parameter[0], parameter[1],
                                                     parameter[2]);
                    break;
                case LOGISTIC:
                    randomSample[i] = logistic(parameter[0], parameter[1]);
                    break;
                case LOGNORMAL:
                    randomSample[i] = lognormal(parameter[0], parameter[1]);
                    break;
                case T:
                    randomSample[i] = t(parameter[0]);
                    break;
                case WEIBULL:
                    randomSample[i] = weibull(parameter[0], parameter[1]);
                    break;
                default:
                    throw new IllegalArgumentException
                            ("Input distribution function can not be found.");
            }
        }

        return randomSample;
    }

}
