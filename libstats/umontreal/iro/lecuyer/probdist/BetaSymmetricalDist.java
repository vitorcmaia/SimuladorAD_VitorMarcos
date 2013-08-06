/*
 * Class:        BetaSymmetricalDist
 * Description:  Symmetrical beta distribution
 * Environment:  Java
 * Software:     SSJ 
 * Copyright (C) 2001  Pierre L'Ecuyer and Université de Montréal
 * Organization: DIRO, Université de Montréal
 * @author       Richard Simard
 * @since        April 2005

 * SSJ is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License (GPL) as published by the
 * Free Software Foundation, either version 3 of the License, or
 * any later version.

 * SSJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * A copy of the GNU General Public License is available at
 <a href="http://www.gnu.org/licenses">GPL licence site</a>.
 */

package umontreal.iro.lecuyer.probdist;

import umontreal.iro.lecuyer.util.*;
import umontreal.iro.lecuyer.functions.MathFunction;

/**
 * Specializes the class {@link BetaDist} to the case of a <SPAN
 * CLASS="textit">symmetrical</SPAN> <EM>beta</EM> distribution over the
 * interval <SPAN CLASS="MATH">[0, 1]</SPAN>, with shape parameters <SPAN
 * CLASS="MATH"><I>&#945;</I> = <I>&#946;</I></SPAN>. A faster inversion method
 * is implemented here for this special case. Because of the symmetry around
 * 1/2, four series are used to compute the <TT>cdf</TT>, two around <SPAN
 * CLASS="MATH"><I>x</I> = 0</SPAN> and two around <SPAN CLASS="MATH"><I>x</I> =
 * 1/2</SPAN>. Given <SPAN CLASS="MATH"><I>u</I></SPAN>, one then solves each
 * series for <SPAN CLASS="MATH"><I>x</I></SPAN> by using the Newton-Raphson
 * method which shows quadratic convergence when the starting iterate is close
 * enough to the solution <SPAN CLASS="MATH"><I>x</I></SPAN>.
 * 
 */
public class BetaSymmetricalDist extends BetaDist {
	private static final double PI_2 = 1.5707963267948966; // Pi/2
	private static final int MAXI = 11; // Max number of Newton iterations
	private static final int MAXIB = 50; // Max number of bisection iterations
	private static final int MAXJ = 2000; // Max number of terms in series
	private static final double EPSSINGLE = 1.0e-5;
	private static final double EPSBETA = 1.0e-10; // < 0.75 sqrt(DBL_EPSILON)
	private static final double SQPI_2 = 0.88622692545275801; // Sqrt(Pi)/2
	private static final double LOG_SQPI_2 = -0.1207822376352453; // Ln(Sqrt(Pi)/2)
	private static final double ALIM1 = 100000.0; // limit for normal
													// approximation
	private static final double LOG2 = 0.6931471805599453; // Ln(2)
	private static final double LOG4 = 1.3862943611198906; // Ln(4)
	private static final double INV2PI = 0.6366197723675813; // 2 / PI
	private double Ceta;
	private double logCeta;

	private static class Function implements MathFunction {
		protected int n;
		protected double sum;

		public Function(double sum, int n) {
			this.n = n;
			this.sum = sum;
		}

		public double evaluate(double x) {
			if (x <= 0.0)
				return 1.0e100;
			return (-2.0 * n * Num.digamma(x) + n * 2.0 * Num.digamma(2.0 * x) + sum);
		}
	}

	private static double belog(double x)
	/*
	 * Used in the Peizer and Pratt normal approximation of BetaSymCDF.
	 * 
	 * This is the function (1 - x*x + 2*x*log(x)) / ((1 - x)*(1 - x))
	 */
	{
		if (x > 1.0)
			return -belog(1.0 / x);
		if (x < 1.0e-200)
			return 1.0;
		if (x < 0.9)
			return (1.0 - x * x + 2.0 * x * Math.log(x))
					/ ((1.0 - x) * (1.0 - x));
		if (x == 1.0)
			return 0.0;

		/* For x near 1, use a series expansion to avoid loss of precision. */
		double term;
		final double EPS = 1.0e-12;
		final double Y = 1.0 - x;
		double ypow = 1.0;
		double sum = 0.0;
		int j = 2;
		do {
			ypow *= Y;
			term = ypow / (j * (j + 1));
			sum += term;
			j++;
		} while (Math.abs(term / sum) > EPS);

		return 2.0 * sum;
	}

	/**
	 * Constructs a <TT>BetaSymmetricalDist</TT> object with parameters
	 * 
	 * <SPAN CLASS="MATH"><I>&#945;</I> = <I>&#946;</I> =</SPAN> <TT>alpha</TT>,
	 * over the unit interval <SPAN CLASS="MATH">(0, 1)</SPAN>.
	 * 
	 */
	public BetaSymmetricalDist(double alpha) {
		super(alpha, alpha);
		setParams(alpha, 14);
	}

	/**
	 * Same as <TT>BetaSymmetricalDist (alpha)</TT>, but using approximations of
	 * roughly <TT>d</TT> decimal digits of precision when computing the
	 * distribution, complementary distribution, and inverse functions.
	 * 
	 */
	public BetaSymmetricalDist(double alpha, int d) {
		super(alpha, alpha, d);
		setParams(alpha, d);
	}

	@SuppressWarnings("deprecation")
	public double cdf(double x) {
		return calcCdf(alpha, x, decPrec, logFactor, logBeta, logCeta, Ceta);
	}

	@SuppressWarnings("deprecation")
	public double barF(double x) {
		return calcCdf(alpha, 1.0 - x, decPrec, logFactor, logBeta, logCeta,
				Ceta);
	}

	@SuppressWarnings("deprecation")
	public double inverseF(double u) {
		return calcInverseF(alpha, u, decPrec, logFactor, logBeta, logCeta,
				Ceta);
	}

	/**
	 * Returns the density evaluated at <SPAN CLASS="MATH"><I>x</I></SPAN>.
	 * 
	 */
	public static double density(double alpha, double x) {
		return density(alpha, alpha, 0.0, 1.0, x);
	}

	/**
	 * Same as {@link #cdf(double,double,int,double) cdf}&nbsp;
	 * <TT>(alpha, alpha, d, x)</TT>.
	 * 
	 */
	public static double cdf(double alpha, int d, double x) {
		return calcCdf(alpha, x, d, Num.DBL_MIN, 0.0, 0.0, 0.0);
	}

	/**
	 * Returns the complementary distribution function.
	 * 
	 */
	public static double barF(double alpha, int d, double x) {
		return cdf(alpha, d, 1.0 - x);
	}

	/**
	 * Returns the inverse distribution function evaluated at <SPAN
	 * CLASS="MATH"><I>u</I></SPAN>, for the symmetrical beta distribution over
	 * the interval <SPAN CLASS="MATH">[0, 1]</SPAN>, with shape parameters
	 * <SPAN CLASS="MATH">0 &lt; <I>&#945;</I> = <I>&#946;</I></SPAN> =
	 * <TT>alpha</TT>. Uses four different hypergeometric series to compute the
	 * distribution <SPAN CLASS="MATH"><I>u</I> = <I>F</I>(<I>x</I>)</SPAN> (for
	 * the four cases <SPAN CLASS="MATH"><I>x</I></SPAN> close to 0 and <SPAN
	 * CLASS="MATH"><I>&#945;</I> &lt; 1</SPAN>, <SPAN
	 * CLASS="MATH"><I>x</I></SPAN> close to 0 and <SPAN
	 * CLASS="MATH"><I>&#945;</I> &gt; 1</SPAN>, <SPAN
	 * CLASS="MATH"><I>x</I></SPAN> close to 1/2 and <SPAN
	 * CLASS="MATH"><I>&#945;</I> &lt; 1</SPAN>, and <SPAN
	 * CLASS="MATH"><I>x</I></SPAN> close to 1/2 and <SPAN
	 * CLASS="MATH"><I>&#945;</I> &gt; 1</SPAN>), which are then solved by
	 * Newton's method for the solution of equations. For <SPAN
	 * CLASS="MATH"><I>&#945;</I> &gt; 100000</SPAN>, uses a normal
	 * approximation given in.
	 * 
	 */
	public static double inverseF(double alpha, double u) {
		return calcInverseF(alpha, u, 14, Num.DBL_MIN, 0.0, 0.0, 0.0);
	}

	/*----------------------------------------------------------------------*/

	private static double series1(double alpha, double x, double epsilon)
	/*
	 * Compute the series for F(x). This series is used for alpha < 1 and x
	 * close to 0.
	 */
	{
		int j;
		double sum, term;
		double poc = 1.0;
		sum = 1.0 / alpha;
		j = 1;
		do {
			poc *= x * (j - alpha) / j;
			term = poc / (j + alpha);
			sum += term;
			++j;
		} while ((term > sum * epsilon) && (j < MAXJ));

		return sum * Math.pow(x, alpha);
	}

	/*------------------------------------------------------------------------*/

	private static double series2(double alpha, double y, double epsilon)
	/*
	 * Compute the series for G(y). y = 0.5 - x. This series is used for alpha <
	 * 1 and x close to 1/2.
	 */
	{
		int j;
		double term, sum;
		double poc;
		final double z = 4.0 * y * y;

		/* Compute the series for G(y) */
		poc = sum = 1.0;
		j = 1;
		do {
			poc *= z * (j - alpha) / j;
			term = poc / (2 * j + 1);
			sum += term;
			++j;
		} while ((term > sum * epsilon) && (j < MAXJ));

		return sum * y;
	}

	/*------------------------------------------------------------------------*/

	private static double series3(double alpha, double x, double epsilon)
	/*
	 * Compute the series for F(x). This series is used for alpha > 1 and x
	 * close to 0.
	 */
	{
		int j;
		double sum, term;
		final double z = -x / (1.0 - x);

		sum = term = 1.0;
		j = 1;
		do {
			term *= z * (j - alpha) / (j + alpha);
			sum += term;
			++j;
		} while ((Math.abs(term) > sum * epsilon) && (j < MAXJ));

		return sum * x;
	}

	/*------------------------------------------------------------------------*/

	private static double series4(double alpha, double y, double epsilon)
	/*
	 * Compute the series for G(y). y = 0.5 - x. This series is used for alpha >
	 * 1 and x close to 1/2.
	 */
	{
		int j;
		double term, sum;
		final double z = 4.0 * y * y;

		term = sum = 1.0;
		j = 1;
		do {
			term *= z * (j + alpha - 0.5) / (0.5 + j);
			sum += term;
			++j;
		} while ((term > sum * epsilon) && (j < MAXJ));

		return sum * y;
	}

	/*------------------------------------------------------------------------*/

	private static double Peizer(double alpha, double x)
	/*
	 * Normal approximation of Peizer and Pratt
	 */
	{
		final double y = 1.0 - x;
		double z;
		z = Math.sqrt((1.0 - y * belog(2.0 * x) - x * belog(2.0 * y))
				/ ((2.0 * alpha - 5.0 / 6.0) * x * y))
				* (2.0 * x - 1.0) * (alpha - 1.0 / 3.0 + 0.025 / alpha);

		return NormalDist.cdf01(z);
	}

	/*-------------------------------------------------------------------------*/

	private static double inverse1(double alpha, // Shape parameter
			double bu, // u * Beta(alpha, alpha)
			int d // Digits of precision
	)
	/*
	 * This method is used for alpha < 1 and x close to 0.
	 */
	{
		int i, j;
		double x, xnew, poc, sum, term;
		final double EPSILON = EPSARRAY[d];

		// First term of series
		x = Math.pow(bu * alpha, 1.0 / alpha);

		// If T1/T0 is very small, neglect all terms of series except T0
		term = alpha * (1.0 - alpha) * x / (1.0 + alpha);
		if (term < EPSILON)
			return x;

		x = bu * alpha / (1.0 + term);
		xnew = Math.pow(x, 1.0 / alpha); // Starting point of Newton's iterates

		i = 0;
		do {
			++i;
			x = xnew;

			/* Compute the series for F(x) */
			poc = 1.0;
			sum = 1.0 / alpha;
			j = 1;
			do {
				poc *= x * (j - alpha) / j;
				term = poc / (j + alpha);
				sum += term;
				++j;
			} while ((term > sum * EPSILON) && (j < MAXJ));
			sum *= Math.pow(x, alpha);

			/* Newton's method */
			term = (sum - bu) * Math.pow(x * (1.0 - x), 1.0 - alpha);
			xnew = x - term;

		} while ((Math.abs(term) > EPSILON) && (i <= MAXI));

		return xnew;
	}

	/*----------------------------------------------------------------------*/

	private static double inverse2(double alpha, // Shape parameter
			double w, // (0.5 - u)B/pow(4, 1 - alpha)
			int d // Digits of precision
	)
	/*
	 * This method is used for alpha < 1 and x close to 1/2.
	 */
	{
		int i, j;
		double term, y, ynew, z, sum;
		double poc;
		final double EPSILON = EPSARRAY[d];

		term = (1.0 - alpha) * w * w * 4.0 / 3.0;
		/* If T1/T0 is very small, neglect all terms of series except T0 */
		if (term < EPSILON)
			return 0.5 - w;

		ynew = w / (1 + term); /* Starting point of Newton's iterates */
		i = 0;
		do {
			++i;
			y = ynew;
			z = 4.0 * y * y;

			// Compute the series for G(y)
			poc = sum = 1.0;
			j = 1;
			do {
				poc *= z * (j - alpha) / j;
				term = poc / (2 * j + 1);
				sum += term;
				++j;
			} while ((term > sum * EPSILON) && (j < MAXJ));

			sum *= y;

			// Newton's method
			ynew = y - (sum - w) * Math.pow(1.0 - z, 1.0 - alpha);

		} while ((Math.abs(ynew - y) > EPSILON) && (i <= MAXI));

		return 0.5 - ynew;
	}

	/*---------------------------------------------------------------------*/

	private static double bisect(double alpha, // Shape parameter
			double logBua, // Ln(alpha * u * Beta(alpha, alpha))
			double a, // x is presumed in [a, b]
			double b, int d // Digits of precision
	)
	/*
	 * This method is used for alpha > 1 and u very close to 0. It will almost
	 * never be called, if at all.
	 */
	{
		int i, j;
		double z, sum, term;
		double x, xprev;
		final double EPSILON = EPSARRAY[d];

		if (a >= 0.5 || a > b) {
			a = 0.0;
			b = 0.5;
		}

		x = 0.5 * (a + b);
		i = 0;
		do {
			++i;
			z = -x / (1 - x);

			/* Compute the series for F(x) */
			sum = term = 1.0;
			j = 1;
			do {
				term *= z * (j - alpha) / (j + alpha);
				sum += term;
				++j;
			} while ((Math.abs(term / sum) > EPSILON) && (j < MAXJ));
			sum *= x;

			/* Bisection method */
			term = Math.log(x * (1.0 - x));
			z = logBua - (alpha - 1.0) * term;
			if (z > Math.log(sum))
				a = x;
			else
				b = x;
			xprev = x;
			x = 0.5 * (a + b);

		} while ((Math.abs(xprev - x) > EPSILON) && (i < MAXIB));

		return x;
	}

	/*---------------------------------------------------------------------*/

	private static double inverse3(double alpha, // Shape parameter
			double logBua, // Ln(alpha * u * Beta(alpha, alpha))
			int d // Digits of precision
	)
	/*
	 * This method is used for alpha > 1 and x close to 0.
	 */
	{
		int i, j;
		double z, x, w, xnew, sum = 0., term;
		double eps = EPSSINGLE;
		final double EPSILON = EPSARRAY[d];
		// For alpha <= 100000 and u < 1.0/(2.5 + 2.25*sqrt(alpha)), X0 is
		// always
		// to the right of the solution, so Newton is certain to converge.
		final double X0 = 0.497;

		/* Compute starting point of Newton's iterates */
		w = logBua / alpha;
		x = Math.exp(w);
		term = (Math.log1p(-x) + logBua) / alpha;
		z = Math.exp(term);
		if (z >= 0.25)
			xnew = X0;
		else if (z > 1.0e-6)
			xnew = (1.0 - Math.sqrt(1.0 - 4.0 * z)) / 2.0;
		else
			xnew = z;

		i = 0;
		do {
			++i;
			if (xnew >= 0.5)
				xnew = X0;
			x = xnew;

			sum = Math.log(x * (1.0 - x));
			w = logBua - (alpha - 1.0) * sum;
			if (Math.abs(w) >= Num.DBL_MAX_EXP * Num.LN2) {
				xnew = X0;
				continue;
			}
			w = Math.exp(w);
			z = -x / (1 - x);

			/* Compute the series for F(x) */
			sum = term = 1.0;
			j = 1;
			do {
				term *= z * (j - alpha) / (j + alpha);
				sum += term;
				++j;
			} while ((Math.abs(term / sum) > eps) && (j < MAXJ));
			sum *= x;

			/* Newton's method */
			term = (sum - w) / alpha;
			xnew = x - term;
			if (Math.abs(term) < 32.0 * EPSSINGLE)
				eps = EPSILON;

		} while ((Math.abs(xnew - x) > sum * EPSILON)
				&& (Math.abs(xnew - x) > EPSILON) && (i <= MAXI));

		/*
		 * If Newton has not converged with enough precision, call bisection
		 * method. It is very slow, but will be called very rarely.
		 */
		if (i >= MAXI && Math.abs(xnew - x) > 10.0 * EPSILON)
			return bisect(alpha, logBua, 0.0, 0.5, d);
		return xnew;
	}

	/*---------------------------------------------------------------------*/

	private static double inverse4(double alpha, // Shape parameter
			double logBva, // Ln(B) + Ln(1/2 - u) + (alpha - 1)*Ln(4)
			int d // Digits of precision
	)
	/*
	 * This method is used for alpha > 1 and x close to 1/2.
	 */
	{
		int i, j;
		double term, sum, y, ynew, z;
		double eps = EPSSINGLE;
		final double EPSILON = EPSARRAY[d];

		ynew = Math.exp(logBva); // Starting point of Newton's iterates
		i = 0;
		do {
			++i;
			y = ynew;

			/* Compute the series for G(y) */
			z = 4.0 * y * y;
			term = sum = 1.0;
			j = 1;
			do {
				term *= z * (j + alpha - 0.5) / (0.5 + j);
				sum += term;
				++j;
			} while ((term > sum * eps) && (j < MAXJ));
			sum *= y * (1.0 - z);

			/* Newton's method */
			term = Math.log1p(-z);
			term = sum - Math.exp(logBva - (alpha - 1.0) * term);
			ynew = y - term;
			if (Math.abs(term) < 32.0 * EPSSINGLE)
				eps = EPSILON;

		} while ((Math.abs(ynew - y) > EPSILON)
				&& (Math.abs(ynew - y) > sum * EPSILON) && (i <= MAXI));

		return 0.5 - ynew;
	}

	/*---------------------------------------------------------------------*/

	private static double PeizerInverse(double alpha, double u) {
		/* Inverse of the normal approximation of Peizer and Pratt */
		double t1, t3, xprev;
		final double C2 = alpha - 1.0 / 3.0 + 0.025 / alpha;
		final double z = NormalDist.inverseF01(u);
		double x = 0.5;
		double y = 1.0 - x;
		int i = 0;

		do {
			i++;
			t1 = (2.0 * alpha - 5.0 / 6.0) * x * y;
			t3 = 1.0 - y * belog(2.0 * x) - x * belog(2.0 * y);
			xprev = x;
			x = 0.5 + 0.5 * z * Math.sqrt(t1 / t3) / C2;
			y = 1.0 - x;
		} while (i <= MAXI && Math.abs(x - xprev) > EPSBETA);

		return x;
	}

	/*---------------------------------------------------------------------*/

	private static void CalcB4(double alpha, double[] bc, double epsilon) {
		double temp;
		double pB = 0.0;
		double plogB = 0.0;
		double plogC = 0.0;

		/* Compute Beta(alpha, alpha) or Beta(alpha, alpha)*4^(alpha-1). */

		if (alpha <= EPSBETA) {
			/* For a -> 0, B(a,a) = (2/a)*(1 - 1.645*a^2 + O(a^3)) */
			pB = 2.0 / alpha;
			plogB = Math.log(pB);
			plogC = plogB + (alpha - 1.0) * LOG4;

		} else if (alpha <= 1.0) {
			temp = Num.lnGamma(alpha);
			plogB = 2.0 * temp - Num.lnGamma(2.0 * alpha);
			plogC = plogB + (alpha - 1.0) * LOG4;
			pB = Math.exp(plogB);

		} else if (alpha <= 10.0) {
			plogC = Num.lnGamma(alpha) - Num.lnGamma(0.5 + alpha) + LOG_SQPI_2;
			plogB = plogC - (alpha - 1.0) * LOG4;
			pB = Math.exp(plogB);

		} else if (alpha <= 200.0) {
			/* Convergent series for Gamma(x + 0.5) / Gamma(x) */
			double term = 1.0;
			double sum = 1.0;
			int i = 1;
			while (term > epsilon * sum) {
				term *= (i - 1.5) * (i - 1.5) / (i * (alpha + i - 1.5));
				sum += term;
				i++;
			}
			temp = SQPI_2 / Math.sqrt((alpha - 0.5) * sum);
			plogC = Math.log(temp);
			plogB = plogC - (alpha - 1.0) * LOG4;
			pB = Math.exp(plogB);

		} else {
			/* Asymptotic series for Gamma(a + 0.5) / (Gamma(a) * Sqrt(a)) */
			double z = 1.0 / (8.0 * alpha);
			temp = 1.0 + z
					* (-1.0 + z * (0.5 + z * (2.5 - z * (2.625 + 49.875 * z))));
			/* This is 4^(alpha - 1)*B(alpha, alpha) */
			temp = SQPI_2 / (Math.sqrt(alpha) * temp);
			plogC = Math.log(temp);
			plogB = plogC - (alpha - 1.0) * LOG4;
			pB = Math.exp(plogB);
		}
		bc[0] = pB;
		bc[1] = plogB;
		bc[2] = plogC;
	}

	/*---------------------------------------------------------------------*/

	private static double calcInverseF(double alpha, double u, int d,
			double logFact, double logBeta, double logCeta, double Ceta) {
		if (alpha <= 0.0)
			throw new IllegalArgumentException("alpha <= 0");
		if (u > 1.0 || u < 0.0)
			throw new IllegalArgumentException("u not in [0,1]");
		if (u == 0.0)
			return 0.0;
		if (u == 1.0)
			return 1.0;
		if (u == 0.5)
			return 0.5;

		// Case alpha = 1 is the uniform law
		if (alpha == 1.0)
			return u;

		// Case alpha = 1/2 is the arcsin law
		double temp;
		if (alpha == 0.5) {
			temp = Math.sin(u * PI_2);
			return temp * temp;
		}

		if (alpha > ALIM1)
			return PeizerInverse(alpha, u);

		boolean isUpper; // True if u > 0.5
		if (u > 0.5) {
			isUpper = true;
			u = 1.0 - u;
		} else
			isUpper = false;

		double x;
		double C = 0.0, B = 0.0, logB = 0.0, logC = 0.0;

		if (logFact == Num.DBL_MIN) {
			double[] bc = new double[] { 0.0, 0.0, 0.0 };
			CalcB4(alpha, bc, EPSARRAY[d]);
			B = bc[0];
			logB = bc[1];
			logC = bc[2];
			C = Math.exp(logC);
		} else {
			B = 1.0 / Math.exp(logFact);
			logB = logBeta;
			logC = logCeta;
			C = Ceta;
		}

		if (alpha <= 1.0) {
			// First term of integrated series around 1/2
			double y0 = C * (0.5 - u);
			if (y0 > 0.25)
				x = inverse1(alpha, B * u, d);
			else
				x = inverse2(alpha, y0, d);

		} else {
			if (u < 1.0 / (2.5 + 2.25 * Math.sqrt(alpha))) {
				double logBua = logB + Math.log(u * alpha);
				x = inverse3(alpha, logBua, d);
			} else {
				// logBva = Ln(Beta(a,a) * (0.5 - u)*pow(4, a - 1)
				double logBva = logC - LOG2 + Math.log1p(-2.0 * u);

				x = inverse4(alpha, logBva, d);
			}
		}

		if (isUpper)
			return 1.0 - x - Num.DBL_EPSILON;
		else
			return x;
	}

	/*---------------------------------------------------------------------*/

	private static double calcCdf(double alpha, double x, int d,
			double logFact, double logBeta, double logCeta, double Ceta) {
		double temp, u, logB = 0.0, logC = 0.0, C = 0.0;
		boolean isUpper; /* True if x > 0.5 */
		double B = 0.0; /* Beta(alpha, alpha) */
		double x0;
		final double EPSILON = EPSARRAY[d]; // Absolute precision

		if (alpha <= 0.0)
			throw new IllegalArgumentException("alpha <= 0");

		if (x <= 0.0)
			return 0.0;
		if (x >= 1.0)
			return 1.0;
		if (x == 0.5)
			return 0.5;
		if (alpha == 1.0)
			return x; /* alpha = 1 is the uniform law */
		if (alpha == 0.5) /* alpha = 1/2 is the arcsin law */
			return INV2PI * Math.asin(Math.sqrt(x));

		if (alpha > ALIM1)
			return Peizer(alpha, x);

		if (x > 0.5) {
			x = 1.0 - x;
			isUpper = true;
		} else
			isUpper = false;

		if (logFact == Num.DBL_MIN) {
			double[] bc = new double[3];
			bc[0] = B;
			bc[1] = logB;
			bc[2] = logC;
			CalcB4(alpha, bc, EPSILON);
			B = bc[0];
			logB = bc[1];
			logC = bc[2];
			C = Math.exp(logC);
		} else {
			B = 1.0 / Math.exp(logFact);
			logB = logBeta;
			logC = logCeta;
			C = Ceta;
		}

		if (alpha <= 1.0) {
			/*
			 * For x = x0, both series use the same number of terms to get the
			 * required precision
			 */
			if (x > 0.25) {
				temp = -Math.log(alpha);
				if (alpha >= 1.0e-6)
					x0 = 0.25 + 0.005 * temp;
				else
					x0 = 0.13863 + .01235 * temp;
			} else
				x0 = 0.25;

			if (x <= x0)
				u = (series1(alpha, x, EPSILON)) / B;
			else
				u = 0.5 - (series2(alpha, 0.5 - x, EPSILON)) / C;

		} else { /* 1 < alpha < ALIM1 */
			if (alpha < 400.0)
				x0 = 0.5 - 0.9 / Math.sqrt(4.0 * alpha);
			else
				x0 = 0.5 - 1.0 / Math.sqrt(alpha);
			if (x0 < 0.25)
				x0 = 0.25;

			if (x <= x0) {
				temp = (alpha - 1.0) * Math.log(x * (1.0 - x)) - logB;
				u = series3(alpha, x, EPSILON) * Math.exp(temp) / alpha;

			} else {
				final double y = 0.5 - x;
				if (y > 0.05) {
					temp = Math.log(1.0 - 4.0 * y * y);
				} else {
					u = 4.0 * y * y;
					temp = -u
							* (1.0 + u
									* (0.5 + u
											* (1.0 / 3.0 + u
													* (0.25 + u
															* (0.2 + u
																	* (1.0 / 6.0 + u * 1.0 / 7.0))))));
				}
				temp = alpha * temp - logC;
				u = 0.5 - (series4(alpha, y, EPSILON)) * Math.exp(temp);
			}
		}

		if (isUpper)
			return 1.0 - u;
		else
			return u;
	}

	public double getMean() {
		return 0.5;
	}

	public double getVariance() {
		return BetaSymmetricalDist.getVariance(alpha);
	}

	public double getStandardDeviation() {
		return BetaSymmetricalDist.getStandardDeviation(alpha);
	}

	/**
	 * Estimates the parameter <SPAN CLASS="MATH"><I>&#945;</I></SPAN> of the
	 * symmetrical beta distribution over the interval [0, 1] using the maximum
	 * likelihood method, from the <SPAN CLASS="MATH"><I>n</I></SPAN>
	 * observations <SPAN CLASS="MATH"><I>x</I>[<I>i</I>]</SPAN>, <SPAN
	 * CLASS="MATH"><I>i</I> = 0, 1,&#8230;, <I>n</I> - 1</SPAN>. The estimate
	 * is returned in element 0 of the returned array.
	 * 
	 * @param x
	 *            the list of observations to use to evaluate parameters
	 * 
	 * @param n
	 *            the number of observations to use to evaluate parameters
	 * 
	 * @return returns the parameter [ <SPAN CLASS="MATH">hat(&alpha;)</SPAN>]
	 * 
	 */
	public static double[] getMLE(double[] x, int n) {
		if (n <= 0)
			throw new IllegalArgumentException("n <= 0");

		double var = 0.0;
		double sum = 0.0;
		for (int i = 0; i < n; i++) {
			var += ((x[i] - 0.5) * (x[i] - 0.5));
			if (x[i] > 0.0 && x[i] < 1.0)
				sum += Math.log(x[i] * (1.0 - x[i]));
			else
				sum -= 709.0;
		}
		var /= (double) n;

		Function f = new Function(sum, n);

		double[] parameters = new double[1];
		double alpha0 = (1.0 - 4.0 * var) / (8.0 * var);

		double a = alpha0 - 5.0;
		if (a <= 0.0)
			a = 1e-15;

		parameters[0] = RootFinder.brentDekker(a, alpha0 + 5.0, f, 1e-5);

		return parameters;
	}

	/**
	 * Creates a new instance of a symmetrical beta distribution with parameter
	 * <SPAN CLASS="MATH"><I>&#945;</I></SPAN> estimated using the maximum
	 * likelihood method based on the <SPAN CLASS="MATH"><I>n</I></SPAN>
	 * observations <SPAN CLASS="MATH"><I>x</I>[<I>i</I>]</SPAN>, <SPAN
	 * CLASS="MATH"><I>i</I> = 0, 1,&#8230;, <I>n</I> - 1</SPAN>.
	 * 
	 * @param x
	 *            the list of observations to use to evaluate parameters
	 * 
	 * @param n
	 *            the number of observations to use to evaluate parameters
	 * 
	 * 
	 */
	public static BetaSymmetricalDist getInstanceFromMLE(double[] x, int n) {
		double parameters[] = getMLE(x, n);
		return new BetaSymmetricalDist(parameters[0]);
	}

	/**
	 * Computes and returns the mean <SPAN CLASS="MATH"><I>E</I>[<I>X</I>] =
	 * 1/2</SPAN> of the symmetrical beta distribution with parameter <SPAN
	 * CLASS="MATH"><I>&#945;</I></SPAN>.
	 * 
	 * @return the mean of the symmetrical beta distribution <SPAN
	 *         CLASS="MATH"><I>E</I>[<I>X</I>] = 1/2</SPAN>
	 * 
	 */
	public static double getMean(double alpha) {
		if (alpha <= 0.0)
			throw new IllegalArgumentException("alpha <= 0");

		return 0.5;
	}

	/**
	 * Computes and returns the variance, <SPAN CLASS="MATH">Var[<I>X</I>] =
	 * 1/(8<I>&#945;</I> + 4)</SPAN>, of the symmetrical beta distribution with
	 * parameter <SPAN CLASS="MATH"><I>&#945;</I></SPAN>.
	 * 
	 * @return the variance of the symmetrical beta distribution
	 * 
	 *         <SPAN CLASS="MATH">Var[<I>X</I>] = 1/[4(2<I>&#945;</I> +
	 *         1)]</SPAN>
	 * 
	 */
	public static double getVariance(double alpha) {
		if (alpha <= 0.0)
			throw new IllegalArgumentException("alpha <= 0");

		return (1 / (8 * alpha + 4));
	}

	/**
	 * Computes and returns the standard deviation of the symmetrical beta
	 * distribution with parameter <SPAN CLASS="MATH"><I>&#945;</I></SPAN>.
	 * 
	 * @return the standard deviation of the symmetrical beta distribution
	 * 
	 */
	public static double getStandardDeviation(double alpha) {
		if (alpha <= 0.0)
			throw new IllegalArgumentException("alpha <= 0");

		return (1 / Math.sqrt(8 * alpha + 4));
	}

	@SuppressWarnings("deprecation")
	public void setParams(double alpha, double beta, double a, double b, int d) {
		// We don't want to calculate Beta, logBeta and logFactor twice.
		if (a >= b)
			throw new IllegalArgumentException("a >= b");
		this.decPrec = d;
		supportA = this.a = a;
		supportB = this.b = b;
		bminusa = b - a;
	}

	private void setParams(double alpha, int d) {
		if (alpha <= 0.0)
			throw new IllegalArgumentException("alpha <= 0");
		this.alpha = alpha;
		beta = alpha;

		double[] bc = new double[] { 0.0, 0.0, 0.0 };
		CalcB4(alpha, bc, EPSARRAY[d]);
		Beta = bc[0];
		logBeta = bc[1];
		logCeta = bc[2];
		Ceta = Math.exp(logCeta);
		if (Beta > 0.0)
			logFactor = -logBeta - (2.0 * alpha - 1) * Math.log(bminusa);
		else
			logFactor = 0.0;
	}

	/**
	 * Return a table containing the parameter of the current distribution.
	 * 
	 * 
	 */
	public double[] getParams() {
		double[] retour = { alpha };
		return retour;
	}

	public String toString() {
		return getClass().getSimpleName() + " : alpha = " + alpha;
	}

}
