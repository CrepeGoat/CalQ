package awqatty.b.FunctionDictionary.FunctionForms;

import java.util.List;


public class FunctionNCK implements FunctionForm {
	//*

	long holdVarAsTemp(long var, long setVarToNewValue) {
		return var;
	}
	private long gcd(long A, long a) {
		// Ensure |A| >= |a|
		if (Math.abs(A)<Math.abs(a))
			A = holdVarAsTemp(a, a=A);
		// Euclidian method
		while (a!=0) {
			A = holdVarAsTemp(a, a=A%a);
		}
		return A;
	}
	@Override
	public Double calculate(List<Double> vlist) throws CalculationException {
		// Check for integer values
		if (Math.round(vlist.get(0)) != vlist.get(0)
				|| Math.round(vlist.get(1)) != vlist.get(1)
				|| vlist.get(0) < vlist.get(1)
				|| vlist.get(1) < 0)
			return Double.NaN;

		// Set constant vars
		long n = Math.round(vlist.get(0));
		long k = Math.round(Math.min(vlist.get(1), n-vlist.get(1)));
		double result=1;
		long gcd_tmp;
		// Calculates the product, \prod_(i=1)^k (n+1-i) / i
		for (long i=1; i<=k; ++i) {
			// It is guaranteed that for each iteration:
			//		- k<n-k -> i<=(n+1-i)
			//		- i divides result*(n+1-i)
			gcd_tmp = gcd(i,n+1-i);
			result /= i/gcd_tmp;
			result *= (n+1-i)/gcd_tmp;
		}
		// Get result
		return result;
	}
	/*/
	// Internal vars
	private long row[], row_tmp[];
	private long row_number;

	// Inner Functions
	long[] holdVarAsTemp(long[] var, long[] setVarToNewValue) {
		return var;
	}
	void incrementRowNumber() {
		++row_number;
		for (int i=(int)Math.min((long)(row.length-1),row_number);i>0;--i) {
			row[i]+=row[i-1];
		}
	}
	void doubleRowNumber() {
		// Move row to row_tmp as reference
		row = holdVarAsTemp(row_tmp,row_tmp=row);
		// Set initial values of row to 0
		for (int i=row.length-1; i>=0; --i) {
			row[i]=0;
		}
		// Convolute row_tmp onto itself and store in row
		for (int i = (int)Math.min((long)(row.length-1),row_number); i>=0; --i) {
			for (int j = (int)Math.min((long)(row.length-i-1),row_number); j>=0; --j) {
				row[i+j] += row_tmp[i]*row_tmp[j];
			}
		}
		// Set new row number
		row_number*=2;
	}

	@Override
	public Double calculate(List<Double> vlist) throws CalculationException {
		// Check for integer values
		if (Math.round(vlist.get(0)) != vlist.get(0)
				|| Math.round(vlist.get(1)) != vlist.get(1)
				|| vlist.get(0) < vlist.get(1)
				|| vlist.get(1) < 0)
			return Double.NaN;

		// Set constant vars
		final long n = Math.round(vlist.get(0));
		final int k = (int)Math.round(Math.min(vlist.get(1), n-vlist.get(1)));
		row = new long[k+1];
		row_tmp = new long[k+1];
		row[0]=1;
		row_number = 0;
		// (Optimal operation pattern is indicated by bits in
		int i = 63 - Long.numberOfLeadingZeros(n);
		while (i>=0) {
			doubleRowNumber();
			if ((1&(n>>i)) != 0) {
				incrementRowNumber();
			}
			--i;
		}
		// Cleanup
		row_tmp = null;
		// Get result
		return (double) holdVarAsTemp(row,row=null)[k];
	}
	//*/
}
