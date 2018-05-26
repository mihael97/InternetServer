package hr.fer.zemris.java.custom.scripting.exec;

import hr.fer.zemris.java.custom.scripting.exec.exceptions.ValueWrapperException;

/**
 * Razred predstavlja 'wrapper' podataka koji se koriste u
 * {@link ObjectMultistack}. Vrijednost može biti Double,Integer,String ili null
 * 
 * @author Mihael
 *
 */
public class ValueWrapper {
	/**
	 * Vrijednost wrappera
	 */
	private Object value;

	/**
	 * Javni konstruktor za inicijalizaciju vrijednosti
	 * 
	 * @param value
	 *            - vrijednost
	 * @throws ValueWrapperException
	 *             - ako tip podataka nije podržan
	 */
	public ValueWrapper(Object value) {
		this.value = value;
	}

	/**
	 * Metoda zbraja argument sa pohranjenom vrijednošću
	 * 
	 * @param incValue
	 *            - argument za zbrajanje
	 */
	public void add(Object incValue) {
		calculate(incValue, "add");
	}

	/**
	 * Metoda oduzima argument od pohranjene vrijednosti
	 * 
	 * @param decValue
	 *            - vrijednost koju želimo oduzeti
	 */
	public void subtract(Object decValue) {
		calculate(decValue, "subtract");
	}

	/**
	 * Metoda množi argument s pohranjenom vrijednošću
	 * 
	 * @param mulValue
	 *            - skalar kojim množimo pohranjenu vrijednost
	 */
	public void multiply(Object mulValue) {
		calculate(mulValue, "multiply");
	}

	/**
	 * Metoda dijeli pohranjenu vrijednost sa argumentom
	 * 
	 * @param divValue
	 *            - nazivnik -
	 */
	public void divide(Object divValue) {
		calculate(divValue, "divide");
	}

	/**
	 * Metoda uspoređuje trenutno pohranjenu vrijednost i argument. Ako je
	 * pohranjena vrijednost manja,vraćen će biti negativan broj. Ako je argument
	 * veći od pohranjenog broja,povratna vrijednost biti će manja od nule. Inače se
	 * vraća nula(vrijednosti jednake)
	 * 
	 * @param withValue
	 *            - argument korišten za usporedbu
	 * @return cijeli broj(ovisi o odnosu veličine argumenta i pohranjene
	 *         vrijednosti)
	 */
	public int numCompare(Object withValue) {
		if (value == null && withValue == null)
			return 0;

		Object first = cast(withValue);
		Object second = cast(value);

		if (first instanceof Double || second instanceof Double) {
			return ((Double) second).compareTo((Double) first);
		}

		return ((Integer) second).compareTo((Integer) first);
	}

	/**
	 * Metoda vraća trenutno pohranjenu vrijednost
	 * 
	 * @return trenutna vrijendnost
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Metoda postavlja trenutnu vrijednost na neku drugu
	 * 
	 * @param value
	 *            - nova vrijednost
	 * @throws ValueWrapperException
	 *             - ako tip argumenta nije podržan
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * Metoda vraća sljedeću pohranjenu vrijednost ovisno o operaciji
	 * 
	 * @param argument
	 *            - argument
	 * @param string
	 *            - operacija koju ćemo izvršiti
	 * 
	 * @throws ValueWrapperException
	 *             - ako operacija nije podržana
	 * @throws ArithmeticException
	 *             - ako se dijeli sa nulom
	 */
	private void calculate(Object argument, String string) {
		checkType(argument);
		Object number1 = cast(argument);
		Object number2 = cast(value);

		switch (string.trim().toLowerCase()) {
		case "add":
			if (number1 instanceof Double || number2 instanceof Double) {

				double[] array = getDoubles(number1, number2);
				value = Double.valueOf(array[0] + array[1]);

			} else {
				value = Integer.valueOf((Integer) number1 + (Integer) number2);
			}
			break;
		case "subtract":
			if (number1 instanceof Double || number2 instanceof Double) {
				double[] array = getDoubles(number1, number2);
				value = Double.valueOf(array[1] - array[0]);
			} else {
				value = Integer.valueOf((Integer) number2 - (Integer) number1);
			}
			break;
		case "multiply":
			if (number1 instanceof Double || number2 instanceof Double) {
				double[] array = getDoubles(number1, number2);
				value = Double.valueOf(array[0] * array[1]);
			} else {
				value = Integer.valueOf((Integer) number1 * (Integer) number2);
			}
			break;
		case "divide":
			if (number1 instanceof Double || number2 instanceof Double) {
				double[] array = getDoubles(number1, number2);

				if (array[1] == 0) {
					throw new ArithmeticException("Number can't be divided with zero!");
				}

				value = Double.valueOf(array[1] / array[0]);

			} else {
				Integer first = (Integer) number1;

				if (first.intValue() == 0) {
					throw new ArithmeticException("Number can't be divided with zero!");
				}

				value = Integer.valueOf((Integer) number2 / first);
			}
			break;
		default:
			throw new ValueWrapperException("Invalid operation!");
		}
	}

	/**
	 * Metoda pretvara dani argument u jedan od podržanih tipova. Ako je vrijednost
	 * null,vraća se novi Integer s vrijednošću 0. Ako je argument string,ovisno o
	 * tome sadrži li 'E' ili '.' pretvoriti će se u Double,odnosno String. Povratni
	 * objekti ako su argumenti Integer ili Double istog su tipa
	 * 
	 * @param number
	 *            - argument kojeg pretvaramo
	 * 
	 * @return pretvorena vrijednost
	 * @throws ValueWrapperException
	 *             - ako se argument ne može pretvoriti
	 */
	private Object cast(Object number) {

		if (number == null) {
			return Integer.valueOf(0);
		}

		try {
			System.out.println(String.valueOf(number));
			if (number instanceof Double) {
				return Double.valueOf(Double.parseDouble(String.valueOf(number)));
			}

			if (number instanceof Integer) {
				return Integer.valueOf(Integer.parseInt(String.valueOf(number)));
			}

			String casted = String.valueOf(number);

			if (casted.contains(".") || casted.contains("E"))
				return Double.valueOf(Double.parseDouble(casted));

			return Integer.valueOf(Integer.parseInt(casted));
		} catch (NumberFormatException e) {
			throw new ValueWrapperException("Value can't be casted to any supported type!");
		}

	}

	/**
	 * Metoda provjerava je li tip argumenta podržan. Podržani tipovi su
	 * Double,Integer,String i null
	 * 
	 * @param argument
	 *            - argument čiji tip želimo provjeriti
	 * @throws ValueWrapperException
	 *             - ako tip argumenta nije podržan
	 */
	private void checkType(Object argument) {
		if (!(argument == null || argument instanceof Double || argument instanceof Integer
				|| argument instanceof String)) {
			throw new ValueWrapperException(
					"Argument must be instance of Double,Integer or String. Also it can be null!");
		}
	}

	/**
	 * Metoda vraća Double vrijednost dva broj u obliku polja
	 * 
	 * @param number1
	 *            - prvi argument
	 * @param number2
	 *            - drugi argument
	 * @return polje doublea
	 */
	private double[] getDoubles(Object number1, Object number2) {
		double[] array = new double[2];

		array[0] = (number1 instanceof Double) ? ((Double) number1).doubleValue()
				: Double.valueOf(((Integer) number1).intValue());

		array[1] = (number2 instanceof Double) ? ((Double) number2).doubleValue()
				: Double.valueOf(((Integer) number2).intValue());

		return array;
	}
}
