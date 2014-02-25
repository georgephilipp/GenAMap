package JSci.maths;

import JSci.maths.analysis.RealFunction;
import JSci.maths.analysis.RealFunction2D;
import JSci.maths.analysis.RealFunction3D;

/**
* The numerical math library.
* This class cannot be subclassed or instantiated because all methods are static.
* @version 1.0
* @author Mark Hale
*/
public final class NumericalMath extends AbstractMath {
        private NumericalMath() {}

        /**
        * Calculates the roots of the quadratic equation
        * ax<sup>2</sup>+bx+c=0.
        * @return an array containing the two roots.
        */
        public static double[] solveQuadratic(final double a,final double b,final double c) {
                final double roots[]=new double[2];
                double q;
                if(b < 0.0)
                        q = (-b+Math.sqrt(b*b-4.0*a*c))/2.0;
                else
                        q = (-b-Math.sqrt(b*b-4.0*a*c))/2.0;
                roots[0] = q/a;
                roots[1] = c/q;
                return roots;
        }
        /**
        * Finds a root using the bisection method.
        * @param a lower bound.
        * @param b upper bound.
        */
        public static double bisection(Mapping func, double a, double b, final int maxIter, final double tol) throws MaximumIterationsExceededException {
                final int signa = ExtraMath.sign(func.map(a));
                final int signb = ExtraMath.sign(func.map(b));
                if(signa == signb)
                        throw new IllegalArgumentException("Bounds do not bracket a root.");
                double x;
                int n = 0;
                do {
                        x = (a + b)/2.0;
                        int signx = ExtraMath.sign(func.map(x));
                        if(signx == signa) {
                                a = x;
                        } else if(signx == signb) {
                                b = x;
                        } else {
                                a = x;
                                b = x;
                        }
                        if(++n > maxIter)
                                throw new MaximumIterationsExceededException("No convergence after "+maxIter+" iterations.", new MathDouble(x));
                } while(Math.abs(a-b) > tol);
                return x;
        }
        /**
        * Finds a root using the false position method.
        * @param a lower bound.
        * @param b upper bound.
        */
        public static double falsePosition(Mapping func, double a, double b, final int maxIter, final double tol) throws MaximumIterationsExceededException {
                double fa = func.map(a);
                double fb = func.map(b);
                final int signa = ExtraMath.sign(fa);
                final int signb = ExtraMath.sign(fb);
                if(signa == signb)
                        throw new IllegalArgumentException("Bounds do not bracket a root.");
                double x;
                double delta;
                int n = 0;
                do {
                        x = a - (b-a)*fa/(fb-fa);
                        double fx = func.map(x);
                        int signx = ExtraMath.sign(fx);
                        if(signx == signa) {
                                delta = x-a;
                                a = x;
                                fa = fx;
                        } else if(signx == signb) {
                                delta = b-x;
                                b = x;
                                fb = fx;
                        } else {
                                delta = 0.0;
                        }
                        if(++n > maxIter)
                                throw new MaximumIterationsExceededException("No convergence after "+maxIter+" iterations.", new MathDouble(x));
                } while(Math.abs(delta) > tol);
                return x;
        }
        /**
        * Finds a root using the Newton-Raphson method.
        * @param x initial guess.
        */
        public static double newtonRaphson(RealFunction func, double x, final int maxIter, final double tol) throws MaximumIterationsExceededException {
                RealFunction deriv = func.differentiate();
                double delta;
                int n = 0;
                do {
                        delta = -func.map(x)/deriv.map(x);
                        x += delta;
                        if(++n > maxIter)
                                throw new MaximumIterationsExceededException("No convergence after "+maxIter+" iterations.", new MathDouble(x));
                } while(Math.abs(delta) > tol);
                return x;
        }
        /**
        * Uses the Euler method to solve an ODE.
        * @param y an array to be filled with y values, set y[0] to initial condition.
        * @param func dy/dt as a function of y.
        * @param dt step size.
        * @return y.
        */
        public static double[] euler(final double y[],final Mapping func,final double dt) {
                for(int i=0;i<y.length-1;i++)
                        y[i+1]=y[i]+dt*func.map(y[i]);
                return y;
        }
        /**
        * Uses the Leap-Frog method to solve an ODE.
        * @param y an array to be filled with y values, set y[0], y[1] to initial conditions.
        * @param func dy/dt as a function of y.
        * @param dt step size.
        * @return y.
        */
        public static double[] leapFrog(final double y[],final Mapping func,final double dt) {
                final double two_dt = 2.0*dt;
                for(int i=1;i<y.length-1;i++)
                        y[i+1]=y[i-1]+two_dt*func.map(y[i]);
                return y;
        }
        /**
        * Uses the 2nd order Runge-Kutta method to solve an ODE.
        * @param y an array to be filled with y values, set y[0] to initial condition.
        * @param func dy/dt as a function of y.
        * @param dt step size.
        * @return y.
        */
        public static double[] rungeKutta2(final double y[],final Mapping func,final double dt) {
                final double dt2 = dt/2.0;
                for(int i=0;i<y.length-1;i++)
                        y[i+1] = y[i] + dt*func.map(y[i]+dt2*func.map(y[i]));
                return y;
        }
        /**
        * Uses the 2nd order Runge-Kutta method to solve an ODE.
        * @param y an array to be filled with y values, set y[0] to initial condition.
        * @param func dy/dt as a function of y and t.
        * @param t0 initial time.
        * @param dt step size.
        * @return y.
        */
        public static double[] rungeKutta2(final double y[],final RealFunction2D func,final double t0, final double dt) {
                final double dt2 = dt/2.0;
                double t = t0;
                for(int i=0;i<y.length-1;i++) {
                        y[i+1] = y[i] + dt*func.map(y[i]+dt2*func.map(y[i], t), t+dt2);
                        t += dt;
                }
                return y;
        }
        /**
        * Uses the 4th order Runge-Kutta method to solve an ODE.
        * @param y an array to be filled with y values, set y[0] to initial condition.
        * @param func dy/dt as a function of y.
        * @param dt step size.
        * @return y.
        */
        public static double[] rungeKutta4(final double y[],final Mapping func,final double dt) {
                double k1,k2,k3,k4;
                for(int i=0;i<y.length-1;i++) {
                        k1 = dt*func.map(y[i]);
                        k2 = dt*func.map(y[i]+k1/2.0);
                        k3 = dt*func.map(y[i]+k2/2.0);
                        k4 = dt*func.map(y[i]+k3);
                        y[i+1] = y[i] + (k1+k4)/6.0 + (k2+k3)/3.0;
                }
                return y;
        }
        /**
        * Uses the 4th order Runge-Kutta method to solve an ODE.
        * @param y an array to be filled with y values, set y[0] to initial condition.
        * @param func dy/dt as a function of y and t.
        * @param dt step size.
        * @return y.
        */
        public static double[] rungeKutta4(final double y[],final RealFunction2D func,final double t0, final double dt) {
                final double dt2 = dt/2.0;
                double t = t0;
                double k1,k2,k3,k4;
                for(int i=0;i<y.length-1;i++) {
                        k1 = dt*func.map(y[i], t);
                        k2 = dt*func.map(y[i]+k1/2.0, t+dt2);
                        k3 = dt*func.map(y[i]+k2/2.0, t+dt2);
                        k4 = dt*func.map(y[i]+k3, t+dt);
                        y[i+1] = y[i] + (k1+k4)/6.0 + (k2+k3)/3.0;
                        t += dt;
                }
                return y;
        }
        /**
        * Uses the Runge-Kutta-Nystrom method to solve a 2nd order ODE.
        * @param y an array to be filled with y values, set y[0] to initial condition.
        * @param dy an array to be filled with dy/dt values, set dy[0] to initial condition.
        * @param func y'' as a function of y, y' and t.
        * @param t0 initial time.
        * @param dy0 initial dy/dt.
        * @param dt step size.
        * @return y.
        */
        public static double[] rungeKuttaNystrom(final double y[],final double dy[],final RealFunction3D func,final double t0, final double dt) {
                final double dt2 = dt/2.0;
                double t = t0;
                double k1,k2,k3,k4;
                for(int i=0;i<y.length-1;i++) {
                        k1 = dt*func.map(y[i], dy[i], t);
                        k2 = dt*func.map(y[i]+dy[i]*dt2+k1*dt2/4.0, dy[i]+k1/2.0, t+dt2);
                        k3 = dt*func.map(y[i]+dy[i]*dt2+k1*dt2/4.0, dy[i]+k2/2.0, t+dt2);
                        k4 = dt*func.map(y[i]+dy[i]*dt+k3*dt2/2.0, dy[i]+k3, t+dt);
                        y[i+1] = y[i] + dt*(dy[i] + (k1+k2+k3)/6);
                        dy[i+1] = dy[i] + (k1+2*k2+2*k3+k4)/6;
                        t += dt;
                }
                return y;
        }
        /**
        * Numerical integration using the trapezium rule.
        * @param N the number of strips to use.
        * @param func a function.
        * @param a the first ordinate.
        * @param b the last ordinate.
        */
        public static double trapezium(final int N,final Mapping func,final double a,final double b) {
                double A=0.0,x=a,h=(b-a)/N;
                for(int i=0;i<N;i++) {
                        A+=func.map(x)+func.map(x+h);
                        x+=h;
                }
                return A*h/2.0;
        }
        /**
        * Numerical integration using Simpson's rule.
        * @param N the number of strip pairs to use.
        * @param func a function.
        * @param a the first ordinate.
        * @param b the last ordinate.
        */
        public static double simpson(final int N,final Mapping func,final double a,final double b) {
                double Ao=0.0,Ae=0.0,x=a;
                final double h=(b-a)/(2*N);
                for(int i=0;i<N-1;i++) {
                        Ao+=func.map(x+h);
                        Ae+=func.map(x+2*h);
                        x+=2.0*h;
                }
                Ao+=func.map(x+h);
                return h/3.0*(func.map(a)+4.0*Ao+2.0*Ae+func.map(b));
        }
        /**
        * Numerical integration using the Richardson extrapolation.
        * @param N the number of strip pairs to use (lower value).
        * @param func a function.
        * @param a the first ordinate.
        * @param b the last ordinate.
        */
        public static double richardson(final int N,final Mapping func,final double a,final double b) {
                double Aa,Aao=0.0,Aae=0.0,Ab,Abo=0.0,Abe=0.0,x=a;
                final double ha=(b-a)/(2*N);
                final double hb=ha/2.0;
                for(int i=0;i<N-1;i++) {
                        Aao+=func.map(x+ha);
                        Aae+=func.map(x+2.0*ha);
                        Abo+=func.map(x+hb);
                        Abe+=func.map(x+2*hb);
                        Abo+=func.map(x+3*hb);
                        Abe+=func.map(x+4*hb);
                        x+=2.0*ha;
                }
                Aao+=func.map(x+ha);
                Abo+=func.map(x+hb);
                Abe+=func.map(x+2.0*hb);
                Abo+=func.map(x+3.0*hb);
                Aa=ha/3.0*(func.map(a)+4.0*Aao+2.0*Aae+func.map(b));
                Ab=hb/3.0*(func.map(a)+4.0*Abo+2.0*Abe+func.map(b));
                return (16.0*Ab-Aa)/15.0;
        }
        /**
        * Numerical integration using the Gaussian integration formula (4 points).
        * @param N the number of strips to use.
        * @param func a function.
        * @param a the first ordinate.
        * @param b the last ordinate.
        */
        public static double gaussian4(final int N,final Mapping func,double a,final double b) {
                int n,i;
                double A=0.0;
                final double h=(b-a)/N;
                final double h2=h/2.0;
                final double zeros[]=new double[4];
                final double coeffs[]=new double[4];
                zeros[2]=0.339981043584856264802665759103;
                zeros[3]=0.861136311594052575223946488893;
                zeros[0]=-zeros[3];
                zeros[1]=-zeros[2];
                coeffs[0]=coeffs[3]=0.347854845137453857373063949222;
                coeffs[1]=coeffs[2]=0.652145154862546142626936050778;
                for(n=0;n<N;n++) {
                        for(i=0;i<zeros.length;i++)
                                A+=coeffs[i]*func.map(a+(zeros[i]+1)*h2);
                        a+=h;
                }
                return A*h2;
        }
        /**
        * Numerical integration using the Gaussian integration formula (8 points).
        * @param N the number of strips to use.
        * @param func a function.
        * @param a the first ordinate.
        * @param b the last ordinate.
        */
        public static double gaussian8(final int N,final Mapping func,double a,final double b) {
                int n,i;
                double A=0.0;
                final double h=(b-a)/N;
                final double h2=h/2.0;
                final double zeros[]=new double[8];
                final double coeffs[]=new double[8];
                zeros[4]=0.183434642495649804939476142360;
                zeros[5]=0.525532409916328985817739049189;
                zeros[6]=0.796666477413626739591553936476;
                zeros[7]=0.960289856497536231683560868569;
                zeros[0]=-zeros[7];
                zeros[1]=-zeros[6];
                zeros[2]=-zeros[5];
                zeros[3]=-zeros[4];
                coeffs[0]=coeffs[7]=0.101228536290376259152531354310;
                coeffs[1]=coeffs[6]=0.222381034453374470544355994426;
                coeffs[2]=coeffs[5]=0.313706645877887287337962201987;
                coeffs[3]=coeffs[4]=0.362683783378361982965150449277;
                for(n=0;n<N;n++) {
                        for(i=0;i<zeros.length;i++)
                                A+=coeffs[i]*func.map(a+(zeros[i]+1)*h2);
                        a+=h;
                }
                return A*h2;
        }
        /**
        * Numerical differentiation.
        * @param N the number of points to use.
        * @param func a function.
        * @param a the first ordinate.
        * @param b the last ordinate.
        */
        public static double[] differentiate(final int N,final Mapping func,final double a,final double b) {
                final double diff[]=new double[N];
                double x=a;
                final double dx=(b-a)/N;
                final double dx2=dx/2.0;
                for(int i=0;i<N;i++) {
                        diff[i]=(func.map(x+dx2)-func.map(x-dx2))/dx;
                        x+=dx;
                }
                return diff;
        }
        /**
        * Numerical differentiation in multiple dimensions.
        * @param func a function.
        * @param x coordinates at which to differentiate about.
        * @param dx step size.
        * @return an array M<sub>ij</sub>=df<sup>i</sup>/dx<sub>j</sub>.
        */
        public static double[][] differentiate(final MappingND func,final double x[],final double dx[]) {
                final double xplus[]=new double[x.length];
                final double xminus[]=new double[x.length];
                System.arraycopy(x,0,xplus,0,x.length);
                System.arraycopy(x,0,xminus,0,x.length);
                xplus[0]+=dx[0];
                xminus[0]-=dx[0];
                double funcdiff[]=ArrayMath.scalarMultiply(0.5/dx[0],ArrayMath.subtract(func.map(xplus),func.map(xminus)));
                final double diff[][]=new double[funcdiff.length][x.length];
                for(int i=0;i<funcdiff.length;i++)
                        diff[i][0]=funcdiff[i];
                for(int i,j=1;j<x.length;j++) {
                        System.arraycopy(x,0,xplus,0,x.length);
                        System.arraycopy(x,0,xminus,0,x.length);
                        xplus[j]+=dx[j];
                        xminus[j]-=dx[j];
                        funcdiff=ArrayMath.scalarMultiply(0.5/dx[j],ArrayMath.subtract(func.map(xplus),func.map(xminus)));
                        for(i=0;i<funcdiff.length;i++)
                                diff[i][j]=funcdiff[i];
                }
                return diff;
        }
        /**
        * The Metropolis algorithm.
        * @param list an array to be filled with values distributed according to func, set list[0] to initial value.
        * @param func distribution function.
        * @param dx step size.
        * @return list.
        */
        public static double[] metropolis(final double list[],final Mapping func,final double dx) {
                for(int i=0;i<list.length-1;i++) {
                        list[i+1]=list[i]+dx*(2.0*Math.random()-1.0);
                        if(func.map(list[i+1])/func.map(list[i]) < Math.random())
                                list[i+1]=list[i];
                }
                return list;
        }
}

