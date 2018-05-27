package hr.fer.zemris.java.custom.scripting.lexer;

/**
 * Razred koji implementira lexer kojim prozivodimo tokene koje kasnije
 * koristimo za stvaranje elemenata,a kasnije i cvorova. Konstruktoru je za
 * inicijalizaciju potreban String koji se pretvara u polje znakova kroz koje
 * lexer prolazi i generira leksicke jedinice ovisno o nacinu rada i ostalim
 * pravilima
 * 
 * @author Mihael
 *
 */
public class SmartScriptLexer {

	/**
	 * Polje znakova koje predstavljaju ulaz i materijal za izradu tokena
	 */
	private char[] input;

	/**
	 * Referenca na sljedeci clan niza kojeg moramo provjeriti
	 */
	private int arrayIndex;

	/**
	 * Referenca na trenutno stanje lexera
	 */
	private LexerStates state;

	/**
	 * Javni konstruktor koji pretvara zadani String u polje,postavlja stanje u
	 * osnovno te index na 0
	 * 
	 * @param input
	 *            - ulaz u obliku Stringa kojeg zelimo "razbiti" na tokene
	 */
	public SmartScriptLexer(String input) {
		super();

		if (input == null || input.length() == 0) {
			throw new LexerException("Duljina niza je 0 ili je sam argument null");
		}

		// this.input = cleanArray(input);
		this.input = input.toCharArray();

		arrayIndex = 0;
		state = LexerStates.NONTAG;
	}

	/**
	 * Metoda cijim se pozivanjem izraduje sljedeci token i vraca u obliku
	 * {@link Token}. U parseru se tada odlucuje sto ce ta vrijednost
	 * predstavljati,ovisno o nacinu rada
	 * 
	 * @return Token kao token
	 */
	public Token getToken() {
		if (arrayIndex + 1 < input.length) {
			char inputChar = input[arrayIndex];
			Token pom = null;

			if (inputChar == '{' && input[arrayIndex + 1] == '$') { // pocetak ili kraj funkcije

				if (!isEnd()) {
					pom = new Token(TypeToken.TAGSTART,
							new StringBuilder().append(inputChar).append(input[++arrayIndex]).toString());
					arrayIndex++;
				} else {

					pom = new Token(TypeToken.END, "{$END$}");
					arrayIndex += 7;
				}

			}

			else if (state == LexerStates.TAG) {
				pom = functionWork();
			}

			else {

				pom = new Token(TypeToken.TEXT, readText());
			}

			return pom;
		} else {
			return null;
		}
	}

	/**
	 * Metoda koja cita tekst ukoliko se lexer nalazi van tagova. Nakon sto se
	 * pojavi znak '{',metoda salje do tada procitani sadrzaj
	 * 
	 * @return String
	 */
	private String readText() {
		StringBuilder builder = new StringBuilder();
		char inputChar;

		while ((inputChar = input[arrayIndex]) != '{') {
			if (inputChar == '\\' && (input[arrayIndex + 1] == '\\' || input[arrayIndex + 1] == '{')) {
				builder.append(input[++arrayIndex]);
			} else {
				builder.append(inputChar);
			}

			if ((arrayIndex + 1) != input.length) {
				arrayIndex++;
			} else {
				break;
			}
		}

		return builder.toString();
	}

	/**
	 * Metoda koja provjerava sljedi li tag za zavrsetak nepraznog taga
	 * 
	 * @return true ako sljedi,inace false
	 */
	private boolean isEnd() {
		String end = "{$END$}";
		int index = arrayIndex;
		int i = 0;
		int length = input.length;

		while ((index < length)) {
			char c = input[index++];
			if (c != ' ') {
				if (end.charAt(i) == Character.toUpperCase(c)) {
					if (++i == 6) {
						break;
					}
				} else {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Metoda koja racuna sljedeci token u slucaju da se lexer nalazi u stanju kada
	 * vrijede druga leksicka pravila(stanje TAG)
	 * 
	 * @return {@link Token} kao sljedeci token
	 */
	private Token functionWork() {
		cleanSpaces();
		char inputChar;
		StringBuilder pom = new StringBuilder();

		while ((inputChar = input[arrayIndex]) != ' ') {
			if (inputChar == '$') {
				if (pom.length() == 0) {
					pom.append(inputChar).append('}');
					arrayIndex += 2;
				}

				break;
			} else if (inputChar == '=') {
				pom.append(inputChar);
				arrayIndex++;
				break;
			} else if (inputChar == '\"') {
				if (pom.length() == 0) {
					pom.append(readString());
				}

				break;
			} else if (inputChar == '@') {
				if (pom.length() == 0) {
					pom.append(input[arrayIndex++]);
					continue;
				}

				pom = new StringBuilder(pom.toString().trim());
				break;
			} else if (Character.isWhitespace(inputChar)) {
				if (pom.length() != 0) {
					break;
				}

				pom.append(inputChar);
			} else {

				pom.append(inputChar);
			}
			arrayIndex++;
		}

		if (inputChar == ' ')
			arrayIndex++;

		return makeObject(pom.toString());
	}

	/**
	 * Metoda koja cita tekst do pojavljivanje sljedeceg znaka '"'
	 * 
	 * @return tekst u obliku Stringa
	 */
	private String readString() {
		char inputChar = input[arrayIndex++];
		StringBuilder builder = new StringBuilder().append(inputChar);

		while ((inputChar = input[arrayIndex]) != '\"') {
			if (inputChar == '\\' && (arrayIndex + 1) != input.length
					&& (input[arrayIndex + 1] == 'r' || input[arrayIndex + 1] == 'n')) {
				builder.append(input[arrayIndex + 1] == 'r' ? "\r" : "\n");
				arrayIndex += 2;
				continue;
			} else {
				builder.append(inputChar);
			}

			if ((arrayIndex + 1) == input.length) {
				break;
			} else {
				arrayIndex++;
			}
		}

		builder.append(inputChar);
		arrayIndex++;
		return builder.toString();
	}

	/**
	 * Metoda koja stvara prikladan {@link Token} od procitanog sadrzaja
	 * 
	 * @param string
	 *            - procitani sadrzaj
	 * @return {@link Token} - referenca na token koji se prosljedjuje parseru
	 */
	private Token makeObject(String string) {
		Token forReturn = null;
		System.out.println(string);

		if (string == null) {
			throw new NullPointerException();
		}

		else if (string.equals("$}")) {
			forReturn = new Token(TypeToken.TAGEND, string);
		}

		else if (string.startsWith("\"")) {
			forReturn = new Token(TypeToken.STRING, string);
		}

		else if (Character.isDigit(string.charAt(0))
				|| string.charAt(0) == '-' && string.length() != 1 && Character.isDigit(string.charAt(1))) {
			// drugi dio uvjeta ako je negativan broj
			try {
				if (string.contains(".")) {
					forReturn = new Token(TypeToken.DOUBLE, Double.parseDouble(string));
				} else {
					forReturn = new Token(TypeToken.INTEGER, Integer.parseInt(string));
				}
			} catch (NumberFormatException e) {
				System.out.println("Ne moze se parsirati " + string);
				throw new LexerException("Nemoguce parsirati!");
			}
		}

		else if (isOperator(string.trim())) {
			forReturn = new Token(TypeToken.OPERATOR, string.trim());
		}

		else if (isFunction(string)) {
			forReturn = new Token(TypeToken.FUNCTION, string.substring(1, string.length())); // micemo @ iz znaka
		}

		else {
			forReturn = new Token(TypeToken.VARIABLE, string);
		}

		return forReturn;
	}

	/**
	 * Metoda provjerava je li funkcija formula,tj da li zapocinje s '@'
	 * 
	 * @param string
	 *            - ulaz kojeg zelimo projeriti
	 * @return true ako zapocinje,inace false
	 */
	private boolean isFunction(String string) {
		// TODO Auto-generated method stub
		return string.startsWith("@");
	}

	/**
	 * Metoda koja postavlja stanje lexera ovisno o izrazu koji se pojavi na
	 * ulazu(niz FOR aktivira nacin rada FORLOOP,a niz '=' aktivira echo nacin.
	 * Inace je nacin NONTAG
	 */
	public void setState() {
		if (state == LexerStates.NONTAG) {
			state = LexerStates.TAG;
		} else {
			state = LexerStates.NONTAG;
		}
	}

	/**
	 * Metoda koja "brise"(preskace) nepotrebne praznine prije bitnih dijelova
	 */
	private void cleanSpaces() {
		// TODO Auto-generated method stub
		while (input[arrayIndex] == ' ') {
			arrayIndex++;
		}
	}

	/**
	 * Metoda vraca trenutno stanje u kojem se lexer nalazi(TAG ili NONTAG)
	 * 
	 * @return {@link LexerStates} - trenutno stanje
	 */
	public LexerStates getState() {
		// TODO Auto-generated method stub
		return state;
	}

	/**
	 * Metoda provjerava da li je zadani argument operator(+,-,/,^)
	 *
	 * @param operator
	 *            - operator u obliku string koji zelimo provjeriti
	 * @return true ako je,inace false
	 */
	private boolean isOperator(String operator) {
		if (operator.equals("+") || operator.equals("-") || operator.equals("/") || operator.equals("^")
				|| operator.equals("*")) {
			return true;
		}

		return false;
	}

	/**
	 * Metoda koja provjerava nalazi li se u input leksicke zabrane,tj pojavljivanja
	 * znakova u rasporedu koji nije dozvoljen
	 * 
	 * @param string
	 *            - ulazni niz zadan preko argumenta konstruktora
	 * @return - "cisti niz",niz od kojeg kasnije krecemo u leksicku analizu i
	 *         proizvodnju tokena
	 * 
	 * @throws LexerException
	 *             - ako se nadu znakovi u nedozvoljenom rasporedu
	 */
	private char[] cleanArray(String string) {
		StringBuilder builder = new StringBuilder();

		char[] array = string.toCharArray();
		boolean tags = false;

		for (int i = 0, length = string.length(); i < length; i++) {

			if (tags == false) {
				if (array[i] == '\\') {
					if (((i + 1) != length && (array[i + 1] == '{' || array[i + 1] == '\\'))) {
						builder.append(array[i]);
						i++;
					} else {
						throw new LexerException("Pogreska kod dijela '\\' na indexu " + i);
					}
				} else if (array[i] == '{') {
					if ((i + 1) != length && array[i + 1] == '$') {
						tags = true;
					} else {
						throw new LexerException("Pogreska kod '{' na indexu " + i);
					}

				}

			} else {
				if (array[i] == '\\' && (i + 1) != length) {
					if (array[i + 1] == '\\') {
						builder.append(array[i]);
						i++;
					} else if (array[i + 1] == '\"') {
						builder.append(array[i]);
						i++;
					} else {
						throw new LexerException("Pogreska kod '\\' na indexu " + i);
					}
				} else if (array[i] == '}') {
					if ((i - 1) != 0 && array[i - 1] == '$') {
						tags = false;
					} else {
						throw new LexerException("Pogreska kod '}' na indexu " + i);
					}
				} else if (array[i] == '{') {
					throw new LexerException("Tag unutar taga!");
				}

			}

			builder.append(array[i]);

		}

		return builder.toString().toCharArray();
	}

}
