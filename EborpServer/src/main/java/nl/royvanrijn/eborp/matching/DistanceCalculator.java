package nl.royvanrijn.eborp.matching;

public class DistanceCalculator {

	public static void main(String[] args) {
		System.out.println(new DistanceCalculator().calculateDistance(-71, 2457));
	}

	public double calculateDistance(double levelInDb, double freqInMHz)    {
		   double exp = (27.55 - (20 * Math.log10(freqInMHz)) + Math.abs(levelInDb)) / 20.0;
		   return Math.pow(10.0, exp);
		}
}
