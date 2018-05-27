package hr.fer.zemris.java.custom.scripting.parser;

import hr.fer.zemris.java.custom.collections.ArrayIndexedCollection;
import hr.fer.zemris.java.custom.collections.ObjectStack;
import hr.fer.zemris.java.custom.scripting.elems.Element;
import hr.fer.zemris.java.custom.scripting.elems.ElementConstantDouble;
import hr.fer.zemris.java.custom.scripting.elems.ElementConstantInteger;
import hr.fer.zemris.java.custom.scripting.elems.ElementFunction;
import hr.fer.zemris.java.custom.scripting.elems.ElementOperator;
import hr.fer.zemris.java.custom.scripting.elems.ElementString;
import hr.fer.zemris.java.custom.scripting.elems.ElementVariable;
import hr.fer.zemris.java.custom.scripting.lexer.LexerException;
import hr.fer.zemris.java.custom.scripting.lexer.SmartScriptLexer;
import hr.fer.zemris.java.custom.scripting.lexer.Token;
import hr.fer.zemris.java.custom.scripting.lexer.TypeToken;
import hr.fer.zemris.java.custom.scripting.nodes.DocumentNode;
import hr.fer.zemris.java.custom.scripting.nodes.EchoNode;
import hr.fer.zemris.java.custom.scripting.nodes.ForLoopNode;
import hr.fer.zemris.java.custom.scripting.nodes.Node;
import hr.fer.zemris.java.custom.scripting.nodes.TextNode;

/**
 * Javni razred koji implementira parser podataka. Pozivanjem lexera dolazimo do
 * dijelova sadrzaja niza(tokena) i u koracima sastavljamo strukturirano stablo
 * 
 * @author Mihael
 *
 */
public class SmartScriptParser {
	/**
	 * Referenca na Lexer koji ce nam proizvesti tokene iz zadanog niza
	 */
	SmartScriptLexer lexer;

	/**
	 * Refernca na stog na kojem ce se stavljati cvorovi (nodes)
	 */
	private ObjectStack stack;

	/**
	 * Javni konstruktor koji inicijalizira Lexer s nizom kojeg zelimo parsirati
	 * 
	 * @param input
	 *            - niz koji zelimo parsirati
	 * @throws NullPointerException
	 *             - ako je ulaz null
	 */
	public SmartScriptParser(String input) {
		if (input == null) {
			throw new NullPointerException("Naziv je null!");
		}

		try {
			lexer = new SmartScriptLexer(input);
			getTokens();
		} catch (LexerException e) {
			throw new SmartScriptParserException(e.getMessage(), e);
		}
	}

	/**
	 * Metoda koja poziva proizvodnju tokena od Lexera sve dok ne primi null
	 * vrijednost(kraj). Ako se u funkciji nalaze tagovi(FOR,ECHO),metoda poziva
	 * specificnu metodu za razradu i stvaranje strukture
	 */
	private void getTokens() {
		Token token;
		stack = new ObjectStack();
		stack.push(new DocumentNode()); // stavljamo na stog dokument

		while ((token = lexer.getToken()) != null) {
			if (token.getType() == TypeToken.TAGSTART) {
				ArrayIndexedCollection collection = new ArrayIndexedCollection(2);

				lexer.setState();
				token = checkID(lexer.getToken()); // identifikator
				collection.add(token);

				while ((token = lexer.getToken()) != null && token.getType() != TypeToken.TAGEND) {
					collection.add(token);
				}

				if (token == null) {
					break;
				}

				lexer.setState();

				tagFunction(collection);

			}

			else if (token.getType() == TypeToken.END) {
				endTag();
			}

			else {
				TextNode node = new TextNode((String) token.getValue());

				addNode(node);
			}
		}

		checkNumberOfNonEmptyLoops();
	}

	/**
	 * Metoda provjerava da li su nakon zavrsetka programa svi neprazni tagovi
	 * zatvoreni
	 * 
	 * @throws SmartScriptParserException
	 *             - ako je ostalo jedan ili vise neprazni tag
	 */
	private void checkNumberOfNonEmptyLoops() {
		if (stack.size() != 1) {
			throw new SmartScriptParserException(
					"U programu je nedovoljno oznaka koje zatvaraju neprazne tagove! Na stogu je " + stack.size());
		}
	}

	/**
	 * Metoda provjerava nalazi li se ime taga medu dopustenim imenima
	 * 
	 * @param token
	 * @return string ako se nalazi,inace iznimka
	 * 
	 * @throws SmartScriptParserException
	 *             - ako se ime taga ne moze prepoznati
	 */
	private Token checkID(Token token) {

		if (!token.getValue().toString().trim().equals("=")
				&& !token.getValue().toString().toUpperCase().trim().equals("FOR")) {
			throw new SmartScriptParserException("Nemoguce prepoznati ime taga!");
		}

		return token;
	}

	/**
	 * Metoda prima kolekiciju s tokenima dobivenim od lexera i poziva odgovarajucu
	 * metodu(za FOR petlju ili ECHO) koje stvaraju cvorove
	 * 
	 * @param collection
	 *            - kolekcija elemnata
	 */
	private void tagFunction(ArrayIndexedCollection collection) {
		// TODO Auto-generated method stub
		Token element = (Token) collection.get(0);

		switch (element.toString().toUpperCase()) {
		case "=":
			echo(collection);

			break;
		case "FOR":
			FORLoop(collection);

			break;
		default:
			break;
		}
	}

	/**
	 * Metoda koja dodaje dijete zadnjem dodanom objektu na stogu
	 *
	 * @param stack
	 *            - referenca na stog
	 * @param node
	 *            - cvor koji zelimo dodati kao dijete
	 */
	private void addNode(Node node) {
		// TODO Auto-generated method stub
		Node fromStack = (Node) stack.pop();
		fromStack.addChildNode(node);
		stack.push(fromStack);
	}

	/**
	 * Metoda prima kolekciju {@link Token} ako se lexer nalazio u ECHO nacinu rada.
	 * Iz dobivenih elemenata stvaraju se {@link Element} koji kasnije tvore cvor
	 *
	 * @param collection
	 *            - kolekcija elemenat dobivena od tokena
	 * 
	 **/
	private void echo(ArrayIndexedCollection collection) {
		ArrayIndexedCollection forReturn = new ArrayIndexedCollection(collection.size());

		for (int i = 1; i < collection.size(); i++) {
			Element ele = makeElement((Token) collection.get(i));
			if (ele != null)
				forReturn.add(ele);
		}

		EchoNode node = new EchoNode(copyArray(forReturn.toArray(), collection.size()));

		addNode(node);
	}

	/**
	 * Metoda koja clanove polja objekata prebacuje u polje Elemenata zbog
	 * prihvatljivijeg formata
	 *
	 * @param array
	 *            - polje objekata koje je bilo pohranjeno u
	 *            {@link ArrayIndexedCollection}
	 * @param size
	 *            - velicina objekata,kolicina
	 * @return - polje {@link Element} koje se salju u konstruktor
	 */
	private Element[] copyArray(Object[] array, int size) {
		Element[] forReturn = new Element[size];
		int index = 0;

		for (Object obj : array) {
			if (obj != null) {
				forReturn[index++] = (Element) obj;
			} else {
				break;
			}
		}

		return forReturn;
	}

	/**
	 * Metoda koja analizira podatke dobivene od lexera i u konacnici inicijalizira
	 * {@link ForLoopNode} koji ce biti dio stabla ForLoopNode moze imati 3 ili 4
	 * argumenta,inace se baca iznimka o prevelikom/premalom broju tokena izlexera
	 *
	 * @param collection
	 *            - kolekcija elemenata ako se lexer nalazio u FOR petlji
	 * @return {@link ForLoopNode} koji ce biti dio strukture
	 *
	 * @throws SmartScriptParserException
	 *             - ako je jedan od argumenata funkcija ili ako ima nedozvoljen
	 *             broj argumenata
	 */
	private void FORLoop(ArrayIndexedCollection collection) {

		if (collection.size() > 5) {
			throw new SmartScriptParserException("Previse argumenata!");

		} else if (collection.size() < 4) {
			throw new SmartScriptParserException("Premalo argumenata!");

		}

		String value = collection.get(1).toString();
		ElementVariable variable = new ElementVariable(checkName(value));
		Element startExpression = makeElement((Token) collection.get(2));
		Element endExpression = makeElement((Token) collection.get(3));
		Element stepExpression = null;

		if (collection.size() == 5) {
			stepExpression = makeElement((Token) collection.get(4));
		}

		// argument ne moze biti funkcija
		if (startExpression instanceof ElementFunction || endExpression instanceof ElementFunction
				|| stepExpression instanceof ElementFunction) {
			throw new SmartScriptParserException("Jedan od argumenata je funkcija u FOR petlji!");
		}

		ForLoopNode node = new ForLoopNode(variable, startExpression, endExpression, stepExpression);
		addNode(node);
		stack.push(node);
	}

	/**
	 * Ukoliko je kao token dosao End tag,metoda sa stoga skida jedan element i
	 * provjerava je li stog prazan. Ako je iznimka,jer to znaci da imamo previse
	 * tagova koji zatvaraju neprazne dijelove
	 * 
	 * @throws SmartScriptParserException
	 *             - ako je stog nakon skidanja prazan
	 */
	private void endTag() {
		// TODO Auto-generated method stub
		@SuppressWarnings("unused")
		Node node = (Node) stack.pop();

		if (stack.size() == 0) {
			throw new SmartScriptParserException("Stog je ostao prazan,previse {$END$} u odnosu na neprazne tagove!");
		}
	}

	/**
	 * Metoda koja ovisno o dobivenom Tokenu iz lexera stvara novi element koji ce
	 * poslije sastavljati cvorove. Metoda stvara elemente temeljem varijable Type
	 * koju svaki Token ima
	 *
	 * @param token
	 *            - token dobiven od lexera
	 *
	 * @return element koji nasljeduje {@link Element} ovisno o predanom argumentu
	 */
	private Element makeElement(Token token) {
		Element element = null;

		switch (token.getType()) {
		case INTEGER:
			element = new ElementConstantInteger((Integer) token.getValue());
			break;

		case DOUBLE:
			element = new ElementConstantDouble((Double) token.getValue());

			break;
		case STRING:
			element = new ElementString((String) token.getValue());

			break;
		case OPERATOR:
			element = new ElementOperator((String) token.getValue());

			break;
		case FUNCTION:
			element = new ElementFunction((String) token.getValue());

			break;
		case VARIABLE:
			if (((String) token.getValue()).trim().length() != 0)
				element = new ElementVariable(checkName(((String) token.getValue()).trim()));

			break;
		default:
			throw new SmartScriptParserException(
					"Nemoguce pretvoriti u niti jedan element! Razlog:" + token.getValue());

		}

		return element;
	}

	/**
	 * Metoda provjerava zadovoljavaju li imena varijabli i funkcija odredene
	 * leksicke kriterije. Kriteriji: naziv mora zapoceti sa slovom,a kasnije se
	 * mogu pojavljivati slova,brojevi i donje povlake("underscore") u neogranicenom
	 * broju
	 *
	 * @param value
	 *            - niz ciju validnost zelimo provjeriri
	 * @return - predani argument ako zadovoljava uvjete,inace iznimku
	 *
	 * @throws SmartScriptParserException
	 *             - ako niz ne zadovoljava kriterije
	 */
	private String checkName(String value) {
		char[] nameArray = value.toCharArray();
		if (value.length() != 0) {
			SmartScriptParserException exception = new SmartScriptParserException(
					"Ime varijable " + value + " se ne moze prihvatiti!");

			if (!Character.isLetter(nameArray[0])) {
				throw exception;
			} else {
				for (int i = 1; i < nameArray.length; i++) {
					char c = nameArray[i];
					if (!(Character.isLetter(c) || Character.isDigit(c) || c == '_')) {
						throw exception;
					}
				}
			}
		}
		return value;
	}

	/**
	 * Metoda koja vraca glavni cvor(Document Node) strukturiranog stabla. Ako
	 * stablo jos nije izradeno,tj nema dodanih cvorova,metoda vraca
	 * {@link SmartScriptParserException}
	 *
	 * @return {@link DocumentNode} ako on postoji,inace iznimka
	 *
	 * @throws SmartScriptParserException
	 *             ako je stog neinicijaliziran
	 */
	public DocumentNode getDocumentNode() {
		if (stack == null) {
			throw new SmartScriptParserException("Stog nije inicijaliziran!");
		} else {
			Node node = null;

			while (stack.size() != 0) {
				node = (Node) stack.pop();
			}

			return (DocumentNode) node;
		}
	}
}
